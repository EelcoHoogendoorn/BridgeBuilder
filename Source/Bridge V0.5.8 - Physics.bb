; 20030428 PGF Workaround for Null G_FirstRailSegment
;              Fixed bug in TestPhysics() - Wrong medium when X < 0 or X > levelwidth
;              Added GridDisplay toggle
; 20030918 EH  Got the traincollisions working pretty much the way i want them to be, without any
;			   any bugs as far as ive noticed.
;			   Fixed bug in TestPhysics() - C_BoltTypeFixedRail was being simulated.
; --------------------------------------------------------------------------------------------------------------------------------
;
; Physics Module
;
; --------------------------------------------------------------------------------------------------------------------------------

; Constants
Const C_Gravity#	= 9.81
Const C_BoltMass#	= 0.01
Const C_BoltVolume#	= 0.01

Const C_WaterMass	= 1

Const C_AirMass#	= 0.001

Const MediumTypeAir			= 0
Const MediumTypeWater		= 1
Const MediumTypeGround		= 2
Dim MediumMass#(2)
MediumMass(MediumTypeAir)	= .01
MediumMass(MediumTypeWater)	= 1
MediumMass(MediumTypeGround)= 5

Const C_TestPhysicsSteps = 20	; Default number of steps
Global G_TestPhysicsSteps = C_TestPhysicsSteps

Global G_FirstRailSegment.T_Segment
Global G_nRails

Global G_TestComplete	; Flag for completion of test objective
Global G_TestCompleteCount

; Train
Type T_Wheel
	Field Wheel.T_Segment
	Field Rail.T_Segment
	Field Contact#
	Field LContact#
	Field Drive#
End Type

Global trainengine.T_Bolt
Global trainend.T_Bolt
Global trainlength=5
Global TrainMass=3600
Global trainpower=trainlength*TrainMass/10


;Functions
Function TestInitialise()

	Local B.T_Bolt
	Local S.T_Segment
	Local Mass#,Volume#
	
	If C_FunctionTrace Then FunctionEntree("TestInitialise")
	
	G_Mode = C_ModeTest
	G_TestComplete = False
	
	G_PhysicsClock = 0
	G_PhysicsCycle = 0
	
	G_WindSpeed = G_WindSpeedStart
	
	; Default - turn off StressGraph
	G_StressGraphDisplay = False
	
	; Initialise Bolts
	i = 1
	For B.T_Bolt = Each T_Bolt
		B\Id	= i
		
		; Reset mass
		B\Mass = C_BoltMass
		B\Volume = C_BoltVolume
		
		B\PX 	= B\OriginalX
		B\PY 	= B\OriginalY
		
		B\LPX	= B\PX
		B\LPY	= B\PY
		
		; Reset Speed
		B\SX	= 0
		B\SY	= 0
		B\S		= 0

		B\LSX	= 0
		B\LSY	= 0

		B\FX	= 0
		B\FY	= 0
		
		B\LFX	= 0
		B\LFY	= 0
		
		i = i + 1
		
		; Force on the StressGraph if any bolt is selected
		If B\Selected
			G_StressGraphDisplay = True
		EndIf
	Next
		
	; Initialise Segments
	i = 1
	G_FirstRailSegment = Null
	G_nRails = 0

	; Sort: Bridge - Rails - Train (Train Segments are added later)
	For S.T_Segment = Each T_Segment
		If S\SegmentType = C_SegmentTypeRail Or S\SegmentType = C_SegmentTypeFixedRail Then
			If G_FirstRailSegment = Null Then
				G_FirstRailSegment = S
			Else
				Insert S After Last T_Segment
			End If
			G_nRails = G_nRails + 1
		Else
			If G_FirstRailSegment <> Null
				Insert S Before G_FirstRailSegment
			EndIf
		End If
		;If KeyHit(1) Then End

		; Force on the StressGraph if any segment is selected
		If S\Selected
			G_StressGraphDisplay = True
		EndIf
	Next
	
	For S.T_Segment = Each T_Segment
		S\Id = i
		S\Status = C_SegmentStatusOK
		
		; Calculate mass for the Segment and Bolt
		S\Length = Sqr((S\Bolt1\OriginalX - S\Bolt2\OriginalX) ^ 2 + (S\Bolt1\OriginalY - S\Bolt2\OriginalY) ^ 2)

		Mass = S\Length * S\Material\Mass
		S\Bolt1\Mass = S\Bolt1\Mass + Mass
		S\Bolt2\Mass = S\Bolt2\Mass + Mass

		Volume = S\Length		;*thickness^2 in future?
		S\Bolt1\Volume = S\Bolt1\Volume + Volume 
		S\Bolt2\Volume = S\Bolt2\Volume + Volume 

		S\Stress = 0
		S\LStress = 0

		; Align rails  (this should be obsolete as soon as i have switched the trainphysics to use vectors)
		If S\SegmentType = C_SegmentTypeRail
			If S\Bolt1\PX > S\Bolt2\PX Then
				T.T_Bolt = S\Bolt1
				S\Bolt1 = S\Bolt2
				S\Bolt2 = T
			End If
		End If
		
		i = i + 1
	Next

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function TestFinalise()
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("TestFinalise")
	
	; Delete any Bolts and Segments created for the train
	TrainStop()
	
	; Restore Bolts	
	For B.T_Bolt = Each T_Bolt
		B\PX 	= B\OriginalX
		B\PY 	= B\OriginalY
		
		; Reset Speed
		B\SX	= 0
		B\LSX	= 0
		B\SY	= 0
		B\LSY	= 0
		B\S		= 0
		
		;reset mass
		B\Mass = C_BoltMass
	Next
	
	; Initialise Segments
	;#### incomplete
	;For S.T_Segment = Each T_Segment
	;	;S\Status = C_SegmentStatusOK
	;	
	;	; Calculate mass for the Segment and Bolt
	;	Mass = S\Length * S\Material\Mass
	;	S\Bolt1\Mass = S\Bolt1\Mass + Mass
	;	S\Bolt2\Mass = S\Bolt2\Mass + Mass
	;	
	;	; Align rails
	;	If S\SegmentType = C_SegmentTypeRail
	;		If S\Bolt1\PX > S\Bolt2\PX Then
	;			T.T_Bolt = S\Bolt1
	;			S\Bolt1 = S\Bolt2
	;			S\Bolt2 = T
	;		End If
	;	End If
	;Next

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function TestControls()
	
	If C_FunctionTrace Then FunctionEntree("TestControls")
	
	ViewZoomControls()
	ViewPanControls()	
	
	; This gives X,Y of mouse in real world coordinates
	G_CursorFreeX = 1.0 * (G_MouseX - G_ScreenCentreX) / G_GridSize / G_ZoomFactor + G_CameraX
	G_CursorFreeY = 1.0 * (G_ScreenCentreY - G_MouseY) / G_GridSize / G_ZoomFactor + G_CameraY
	
	; This snaps the mouse to nearest grid coordinate
	G_CursorX = Int(G_CursorFreeX / G_GridSnap) * G_GridSnap
	G_CursorY = Int(G_CursorFreeY / G_GridSnap) * G_GridSnap
	
	; Mouse selection
	If G_Mouse1
		; This initiates the action
		;
		SelectMaybeClear()
		; If not extending the selection then clear any previous selection
		If KeyDown(C_Key_Left_Control) = False And KeyDown(C_Key_Right_Control) = False
			SelectClear()
		EndIf
		
		Select C_EditToolSelect	; ####G_EditTool
			Case C_EditToolSelect
				G_Point1X = G_CursorX
				G_Point1Y = G_CursorY
				G_EditStep = C_EditStepP2
			Default
		End Select
	EndIf
	
	If G_Mouse1Down
		; This updates the action
		Select G_EditStep
			Case C_EditStepP1
				G_Point1X = G_CursorX
				G_Point1Y = G_CursorY
			Case C_EditStepP2
				G_Point2X = G_CursorX
				G_Point2Y = G_CursorY
			Case C_EditStepP3
				G_Point3X = G_CursorX
				G_Point3Y = G_CursorY
			Case C_EditStepP4
				G_Point4X = G_CursorX
				G_Point4Y = G_CursorY
		End Select
		
		If G_Point2X <= G_Point1X
			G_SelectModeType = C_SelectModeTypeCrossing
		Else
			G_SelectModeType = C_SelectModeTypeContains
		EndIf
		
		If G_EditStep <> C_EditStepZero
			Select C_EditToolSelect	; ####G_EditTool
				Case C_EditToolSelect
					SelectUpdate()
				Default
			End Select
		EndIf
	Else
		; This completes the action
		If G_EditStep = C_EditStepP2
			If 1	; ####G_EditTool = C_EditToolSelect
				SelectMaybeConfirm()
				G_EditStep = C_EditStepDone
			EndIf
		EndIf
	EndIf
	
	; Train toggle
	If KeyHit(C_Key_T)
		TrainTest = Not TrainTest

		If TrainTest
			TrainStart()
		Else
			TrainStop()
		EndIf
		
	EndIf

	; Grid toggle
	If KeyHit(C_Key_Period)
		G_GridDisplay = Not G_GridDisplay
	EndIf			

	; Stress display toggle
	If KeyHit(C_Key_V)
		G_StressColourDisplay = Not G_StressColourDisplay
	EndIf
	
	; Stress graph toggle
	If KeyHit(C_Key_Comma)
		G_StressGraphDisplay = Not G_StressGraphDisplay
	EndIf
	
	If KeyDown(C_Key_0)
		G_TestPhysicsSteps = 0
	ElseIf KeyDown(C_Key_1)
		G_TestPhysicsSteps = 1
	Else
		G_TestPhysicsSteps = C_TestPhysicsSteps
	EndIf
	
	FlushKeys
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function TestPhysics(nSteps)

	Local iStep
	Local S.T_Segment
	Local B.T_Bolt
;	Local Angle#
;	Local CosA#, SinA#
	Local R1FX#,R1FY#,R2FX#,R2FY#			;Use resultant force vector to limit dereferencing when applying all forces.
	Local TimeStart

	If C_FunctionTrace Then FunctionEntree("TestPhysics")
	
	TimeStart = MilliSecs()
	
	G_PhysicsCycle = G_PhysicsCycle + 1
	
	If G_StressGraphDisplay
		SetBuffer ImageBuffer(G_StressGraphs(G_StressGraph))
		G_StressGraph = 1 - G_StressGraph
		GrabImage G_StressGraphs(G_StressGraph), G_TestPhysicsSteps, 0
		
		SetBuffer ImageBuffer(G_StressGraphs(G_StressGraph))
		DrawBlock G_StressGraphBlank, G_ScreenWidth - G_TestPhysicsSteps, 0
	EndIf
		
	G_SoundBreak = False	; Flag used to request SoundUpdate() to play the breaking sound
	
	; Dynamic-precision physics update.:P 
	For iStep = 1 To nSteps

;		If G_DebugTrace Then DebugLog "Cycle" + RSet$(G_PhysicsCycle, 4) +" Step" + RSet$(iStep, 3)
		
		; all forces applied by the segments
		For S.T_Segment = Each T_Segment
;			If G_DebugTraceSegment Then DebugTraceSegment("A", S)
		
			If S\Status = C_SegmentStatusOK
				R1FX = 0
				R1FY = 0
				R2FX = 0
				R2FY = 0

				; Precalc
				RX# = S\Bolt2\PX - S\Bolt1\PX
				RY# = S\Bolt2\PY - S\Bolt1\PY
				
				RLength# = Sqr(RX * RX + RY * RY)
				If RLength = 0
					; Could happen with compressible cables
					RLength = 0.001
				EndIf
				
				NX# =  RY / RLength
				NY# = -RX / RLength
	
				; Resistance
				If S\Bolt1\Medium = MediumTypeAir Then
					R1# = ((S\Bolt1\SX - G_WindSpeed) * NX - S\Bolt1\SY * NY)
					R1 = R1 * Abs(R1) * RLength * MediumMass(MediumTypeAir) / 10
					R2# = ((S\Bolt2\SX - G_WindSpeed) * NX - S\Bolt2\SY * NY)
					R2 = R2 * Abs(R2) * RLength * MediumMass(MediumTypeAir) / 10
				Else
					R1# = (S\Bolt1\SX * NX - S\Bolt1\SY * NY)
					R1 = R1 * Abs(R1) * RLength * MediumMass(S\Bolt1\Medium) / 10
					R2# = (S\Bolt2\SX * NX - S\Bolt2\SY * NY)
					R2 = R2 * Abs(R2) * RLength * MediumMass(S\Bolt2\Medium) / 10
				End If
				
				R1FX = R1FX - NX * R1
				R1FY = R1FY + NY * R1
				R2FX = R2FX - NX * R2
				R2FY = R2FY + NY * R2
				
				
;				If G_DebugTraceSegment Then DebugTraceSegment("B", S)
			
			
				; NOTE: Damping AND Stress are to be replaced by something entirely different to overcome the wobbling effect
			
				; Damping
				DS# = (S\Stress - S\LStress) * S\Length
				Force# = Sqr(Abs(DS)) * Sgn(DS) * S\Material\Damping

;				If G_DebugTraceSegment Then DebugTraceSegment("C", S)
				
				; Stress
				S\LStress = S\Stress
				
				; PGF - Attempt at pre-stressing cables
				If S\Material\Cable ; And KeyDown(C_Key_F5) ;Debugging
					S\Stress = (Rlength * 1.005 - S\Length) / S\Length
				Else
					S\Stress = (Rlength - S\Length) / S\Length
				EndIf
				Force# = Force + S\Stress * S\Material\Strength
				
				; PGF - Special handling for cables
				If S\Material\Cable And RLength < S\Length
					Force = Force / 1000
				EndIf
				
				R1FX = R1FX - NY * Force
				R1FY = R1FY - NX * Force
				R2FX = R2FX + NY * Force
				R2FY = R2FY + NX * Force
							
;				If G_DebugTraceSegment Then DebugTraceSegment("D", S)
				
				; Train-Rails Collision
				If TrainTest And S\SegmentType = C_SegmentTypeRail
					For W.T_Wheel = Each T_Wheel
					
						DX# = S\Bolt1\PX - W\Wheel\Bolt1\PX
						DY# = S\Bolt1\PY - W\Wheel\Bolt1\PY

						WX# = W\Wheel\Bolt2\PX - W\Wheel\Bolt1\PX
						WY# = W\Wheel\Bolt2\PY - W\Wheel\Bolt1\PY
						
						CP# = WX * RY - WY * RX
						; If not parralel
;						If (CP<-.0001 Or CP>.0001) Then
						If CP<-1 Then
							; Calc Depth
							RP# = (DX * wY - DY * wX) / CP

							; If Wheel above rail
							If RP>0 And RP<1 Then

								WP# = (DX * rY - DY * rX) / CP

								; If Wheel on rail
								If WP>-0.1 And WP<0 Then
									; YAY, weve intersected!
									
									FX# = NX * (2 * WP - W\LContact) * 100000
									FY# = NY * (2 * WP - W\LContact) * 100000

									If W\Drive>0 Then
										FX = FX - NY * WP * W\Drive
										FY = FY + NX * WP * W\Drive
									End If
									
									R1FX = R1FX + FX * (1 - RP)
									R1FY = R1FY + FY * (1 - RP)
									R2FX = R2FX + FX * RP
									R2FY = R2FY + FY * RP
									
									W\Wheel\Bolt1\FX = W\Wheel\Bolt1\FX - FX
									W\Wheel\Bolt1\FY = W\Wheel\Bolt1\FY - FY

									W\LContact = WP
								End If
							End If
							
						End If

					Next
					
				End If
				
						
				; Add resultants of this segment to its bolts
				S\Bolt1\FX = S\Bolt1\FX + R1FX
				S\Bolt1\FY = S\Bolt1\FY + R1FY
				S\Bolt2\FX = S\Bolt2\FX + R2FX
				S\Bolt2\FY = S\Bolt2\FY + R2FY

				; Breaking
				If S\Stress > S\Material\MaxTension Or (S\Material\Cable = False And -S\Stress > S\Material\MaxPressure)
					S\Status = C_SegmentStatusBroken
					G_SoundBreak = True
				End If
				
				; StressGraphs
				If G_StressGraphDisplay And S\Selected
					;SetBuffer ImageBuffer(G_StressGraphs(G_StressGraph))
;					C# = S\Stress / (S\Material\Elasticity * S\Length)
					If S\Stress > 0 Then			
						C = S\Stress / S\Material\MaxTension * 128
						CR = 128 - C : CG = 128 + C : CB = 128 - Abs(C)
					Else
						If S\Material\Cable
							C = S\Stress / S\Material\MaxPressure * 128 / 1000
							If C < -127
								C = -127
							EndIf
							CR = 128 - C : CG = 128 + C : CB = 128 + Abs(C)	; Cable becomes purple under compression
						Else
							C = S\Stress / S\Material\MaxPressure * 128
							CR = 128 - C : CG = 128 + C : CB = 128 - Abs(C)	; Other materials show as red under compression
						EndIf
					End If
					Color CR, CG, CB
					
					;If S\Stress > 0 Then
					;	C#  = S\Stress / S\Material\MaxTension
					;Else
					;	C#  = S\Stress / S\Material\MaxPressure
					;End If
					C2 = C * C_StressGraphAxis / 128
					Plot G_ScreenWidth - G_TestPhysicsSteps + iStep - 1, C_StressGraphAxis - C2
				EndIf
				
			EndIf
		Next
		

		; Apply all the forces to the bolts
		For b.T_Bolt = Each T_Bolt
			
;			If G_DebugTraceBolt And B\Id = G_DebugTraceBoltId Then DebugTraceBolt("A", B)

			If B\BoltType <> C_BoltTypeFixed
				; Select medium this bolt is in
				If B\PX => 0 And B\PX < G_LevelWidth Then
					If Height(B\PX) - B\PY > 0 Then
						B\Medium = MediumTypeGround
					Else If	B\PY <= G_WaterLevel Then
						B\Medium = MediumTypeWater
					Else
						B\Medium = MediumTypeAir
					End If
				Else
					If B\PY > 0 Then B\Medium = MediumTypeAir Else B\Medium = MediumTypeGround
				End If

				; Do forces applied by ground. (might need to be tuned)
				If B\Medium = MediumTypeGround Then
					FBPX=Floor(B\PX)
					If FBPX < 0 Or FBPX >= G_LevelWidth - 1
						h# = 0
						d# = 0 - B\PY
					Else
						;height of slope
						h# = (height(FBPX+1) - height(FBPX))
						;interpolate depth
						d# = (Height(FBPX) - B\PY) * (1-(B\PX - FBPX)) + (Height(FBPX+1) - B\PY) * (B\PX - FBPX)
					EndIf
				
					; Normal force
					f# = Sqr(1.0 + h * h)	;to normalize slope vector
					B\FX = B\FX - (h/f)   * B\Volume * d * 8
					B\FY = B\FY - (1.0/f) * B\Volume * d * 8
					
					; Very heavy fricton
					B\FX = B\FX - B\SX * B\Volume * d * 30
					B\FY = B\FY - B\SY * B\Volume * d * 30
					
				End If
				
				; Force of displaced water volume
				If B\Medium = MediumTypeWater Then B\FY = B\FY - B\Volume * MediumMass(MediumTypeWater) * C_Gravity / 100
								
				; Add gravity
				B\FY = B\FY + B\Mass * C_Gravity / 100

				; Accelerate
				B\SX = B\SX + B\FX / B\Mass / 4 / 20	; nSteps
				B\SY = B\SY + B\FY / B\Mass / 4 / 20	; nSteps
				B\S  = Sqr(B\SX^2 + B\SY^2)
				
				; and move
				B\PX = B\PX + B\SX / 4 / 20	; nSteps
				B\PY = B\PY - B\SY / 4 / 20	; nSteps

				;for debug purposes	(filter exeptions)		
;				If B\PX < -1000 Or B\PX > 1000
;					;Stop
;				EndIf
			End If

			; Store current data to be viewed next loop as the last loop's data.
			B\Lpx = B\PX
			B\Lpy = B\PY
			B\Lsx = B\SX
			B\Lsy = B\SY
			B\Lfx = B\FX
			B\Lfy = B\FY

;			If G_DebugTraceBolt And B\Id = G_DebugTraceBoltId Then DebugTraceBolt("B", B)
			
			; Reset forces
			B\FX = 0
			B\FY = 0
			
		Next	
	Next
	
	If TrainTest
		; Check for train all passing TrainStopX
		G_TestComplete = True
		For W.T_Wheel = Each T_Wheel
			If G_TrainStartX < G_TrainStopX
				If W\Wheel\Bolt1\PX < G_TrainStopX
					G_TestComplete = False
				EndIf
			Else
				If W\Wheel\Bolt1\PX > G_TrainStopX
					G_TestComplete = False
				EndIf
			EndIf
		Next
		
		If G_TestComplete
			TrainStop()
			G_TestCompleteCount = 0
		EndIf
	EndIf

	G_PhysicsTime = MilliSecs() - TimeStart
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function TrainStart()
	
	Local BaseX# = G_TrainStartX
	Local BaseY# = G_TrainStartY
	
	If C_FunctionTrace Then FunctionEntree("TrainStart")
	
	If G_TrainStopX >= G_TrainStartX
		DX = 1
	Else
		DX = -1
	EndIf
	
	For C = 0 To G_TrainLength - 1
		
		If C <> 0
			X = -5
		EndIf
		
		b1.T_Bolt = New T_Bolt
		b1\PX = BaseX - (35 * C) * DX
		b1\PY = BaseY
		b1\SX = 2
		b1\BoltType = C_BoltTypeRegular
		b1\DynamicId = 1
		b1\Mass = TrainMass
		
		b2.T_Bolt = New T_Bolt
		If C = 0
			b1\PX = BaseX + 10 * DX
		Else
			b1\PX = BaseX - (35 * C) * DX
		EndIf
		b2\PX = BaseX - (35 * C) * DX + X
		b2\PY = BaseY + 15
		b2\SX = 2
		b2\BoltType = C_BoltTypeRegular
		b2\DynamicId = 1
		b2\Mass = TrainMass
		
		b3.T_Bolt = New T_Bolt
		b3\PX = BaseX - (35 * C + 30) * DX
		b3\PY = BaseY
		b3\SX = 2
		b3\BoltType = C_BoltTypeRegular
		b3\DynamicId = 1
		b3\Mass = TrainMass
		
		b4.T_Bolt = New T_Bolt
		b4\PX = BaseX - (35 * C + 30) * DX - X
		b4\PY = BaseY + 15
		b4\SX = 2
		b4\BoltType = C_BoltTypeRegular
		b4\DynamicId = 1
		b4\Mass = TrainMass
		
		s1.T_Segment=New T_Segment
		s1\bolt1=b1
		s1\bolt2=b3
		s1\SegmentType = C_SegmentTypeRegular
		s1\DynamicId = 1
		s1\material=G_MaterialSteelBeam
		s1\length=Sqr((s1\bolt1\PX-s1\bolt2\PX)^2+(s1\bolt1\PY-s1\bolt2\PY)^2)
		
		s2.T_Segment=New T_Segment
		s2\bolt1=b2
		s2\bolt2=b4
		s2\SegmentType = C_SegmentTypeRegular
		s2\DynamicId = 1
		s2\material=G_MaterialSteelBeam
		s2\length=Sqr((s2\bolt1\PX-s2\bolt2\PX)^2+(s2\bolt1\PY-s2\bolt2\PY)^2)	
		
		s3.T_Segment=New T_Segment
		s3\bolt1=b1
		s3\bolt2=b4
		s3\SegmentType = C_SegmentTypeRegular
		s3\DynamicId = 1
		s3\material=G_MaterialSteelBeam
		s3\length=Sqr((s3\bolt1\PX-s3\bolt2\PX)^2+(s3\bolt1\PY-s3\bolt2\PY)^2)
		
		s4.T_Segment=New T_Segment
		s4\bolt1=b3
		s4\bolt2=b2
		s4\SegmentType = C_SegmentTypeRegular
		s4\DynamicId = 1
		s4\material=G_MaterialSteelBeam
		s4\length=Sqr((s4\bolt1\PX-s4\bolt2\PX)^2+(s4\bolt1\PY-s4\bolt2\PY)^2)

		s5.T_Segment=New T_Segment
		s5\bolt1=b1
		s5\bolt2=b2
		s5\SegmentType = C_SegmentTypeRegular
		s5\DynamicId = 1
		s5\material=G_MaterialSteelBeam
		s5\length=Sqr((s5\bolt1\PX-s5\bolt2\PX)^2+(s5\bolt1\PY-s5\bolt2\PY)^2)
		w1.T_Wheel=New T_Wheel
		w1\wheel=s5
		
		s6.T_Segment=New T_Segment
		s6\bolt1=b3
		s6\bolt2=b4
		s6\SegmentType = C_SegmentTypeRegular
		s6\DynamicId = 1
		s6\material=G_MaterialSteelBeam
		s6\length=Sqr((s6\bolt1\PX-s6\bolt2\PX)^2+(s6\bolt1\PY-s6\bolt2\PY)^2)
		w2.T_Wheel=New T_Wheel
		w2\wheel=s6
		
		If c > 0 Then 
			s.T_Segment=New T_Segment
			S\Bolt1=b1
			S\Bolt2=l.T_Bolt
			S\Length=Sqr((S\Bolt1\PX-S\Bolt2\PX)^2+(S\Bolt1\PY-S\Bolt2\PY)^2)
			S\Material=G_MaterialSteelBeam;cable
			S\SegmentType = C_SegmentTypeRegular
			S\DynamicId = 1
		Else
			trainengine=b1
		End If
		If C = G_TrainLength - 1 Then trainend=b4
		l=b3
		
		If C = 0 Then
			w1\drive = 20000
			w2\drive = 20000
		End If
	Next
	
	For b.t_bolt=Each t_bolt
		If b\DynamicId = 1 Then 
			b\py=b\py+1.5
			b\volume=80
		End If
	Next
	
	traintest=True
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function TrainStop()
	
	If C_FunctionTrace Then FunctionEntree("TrainStop")
	
	For B.T_Bolt = Each T_Bolt
		If B\DynamicId <> 0 Then Delete b
	Next
	
	For S.T_Segment = Each T_Segment
		If S\DynamicId <> 0 Then Delete S
	Next
	
	Delete Each T_Wheel
	
	TrainTest = False
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

;Function findrail.T_Segment(px,py)
;
;	For s.T_Segment=Each T_Segment
;		If S\SegmentType=rail Then
;			b1#=ATan2(S\Bolt1\PY-S\Bolt2\PY,S\Bolt1\PX-S\Bolt2\PX)-ATan2(py-S\Bolt1\PY,px-S\Bolt1\PX)
;
;			lb1#=Sqr((S\Bolt1\PX-px)^2+(S\Bolt1\PY-py)^2)
;			lb2#=Sqr((S\Bolt2\PX-px)^2+(S\Bolt2\PY-py)^2)
;			p#=lb1*-Cos(b1)
;			If p>0 And p<S\Length Then
;				h#=lb1*-Sin(b1)
;				If Abs(h)<1 Then Return s
;			Else If p<0 Then
;				If lb1<1 Then Return s
;			Else If p>S\Length
;				If lb2<1 Then Return s
;			End If
;		End If
;	Next
;
;End Function

;Function setuptest()
;
;	;align rails
;	For s.T_Segment=Each T_Segment
;		If S\SegmentType=rail Then
;			If S\Bolt1\PX>S\Bolt2\PX Then
;				t.T_Bolt=S\Bolt1
;				S\Bolt1=S\Bolt2
;				S\Bolt2=t
;			End If
;		End If
;	Next
;	
;	savebridge(tempbackup)
;
;	For b.T_Bolt=Each T_Bolt
;		;reset speed
;		B\SX=0
;		B\Lsx=0
;		B\SY=0
;		B\Lsy=0
;		;reset mass
;		B\Mass=boltmass
;	Next
;	
;	For s.T_Segment=Each T_Segment
;		mass=S\Length*S\Material\Mass
;		S\Bolt1\Mass=S\Bolt1\Mass+mass
;		S\Bolt2\Mass=S\Bolt2\Mass+mass
;	Next
;
;	mode=test
;
;End Function