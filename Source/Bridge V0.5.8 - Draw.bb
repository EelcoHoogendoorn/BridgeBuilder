; --------------------------------------------------------------------------------------------------------------------------------
;
; Draw Module
;
; --------------------------------------------------------------------------------------------------------------------------------

Function GridDraw()

	Local Level
	
	If C_FunctionTrace Then FunctionEntree("GridDraw")
	
	G_GridSnap = 0	; Null value
	
	For Level = 0 To 8
		GridDrawLevel(Level)
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function GridDrawLevel(Level)
	
	Local	Factor#
	Local	C
	Local	X, Y, Temp
	Local	OffsetX#, OffsetY#
	Local	SX#, SX1#, SX2#
	Local	SY#, SY1#, SY2#
	
	Factor = G_GridDivisions ^ Level * G_GridSize * G_ZoomFactor
	If Factor >= 5
		;  This grid is big enough to be visible
		
		; Set colour
		C = 25 * (Level + 2)
		Color C / 4, C, C / 4
		;C = 15 * (Level + 1)
		;Color C / 2, C, C / 2
		
		;  Draw vertical grid lines
		SY1 = 0
		SY2 = G_ScreenHeight
		
		X = G_CameraX / G_GridDivisions ^ Level
		Temp = X
		
		;  - Going from centre to left
		OffsetX = G_ScreenCentreX - G_CameraX * G_GridSize * G_ZoomFactor
		Repeat
			SX = OffsetX + X * Factor
			If SX < 0 Then Exit
			
			Rect SX, SY1, 1, SY2
			X = X - 1
		Forever
		
		;  - Going from centre to right
		X = Temp + 1
		Repeat
			SX = OffsetX + X * Factor
			If SX > G_ScreenWidth Then Exit
			
			Rect SX, SY1, 1, SY2
			X = X + 1
		Forever
		  
		; Draw horizontal grid lines
		SX1 = 0
		SX2 = G_ScreenWidth
		
		;  - Going from centre to top
		Y = G_CameraY / G_GridDivisions ^ Level
		Temp = Y
		
		OffsetY = G_ScreenCentreY + G_CameraY * G_GridSize * G_ZoomFactor
		Repeat
			SY = OffsetY - Y * Factor
			If SY < 0 Then Exit
			
			Rect SX1, SY, SX2, 1
			Y = Y + 1
		Forever
		
		;  - Going from centre to bottom
		Y = Temp - 1
		Repeat
			SY = OffsetY - Y * Factor
			If SY >= G_ScreenHeight Then Exit
			
			Rect SX1, SY, SX2, 1
			Y = Y - 1
		Forever
		
		; Check if this is the lowest visible level and if so set the snap size
		If G_GridSnap = 0
			G_GridSnap = (G_GridDivisions ^ Level)
		Else
			; Draw grid points at levels above the lowest
			
			; #### Incomplete
		EndIf
		
	EndIf
	
End Function

Function GroundInitialise()
	
	If C_FunctionTrace Then FunctionEntree("GroundInitialise")
	
	G_GroundYMin = Height(0)
	G_GroundYMax = Height(0)
	
	For X = 1 To G_LevelWidth - 1
		If Height(X) < G_GroundYMin
			G_GroundYMin = Height(X)
		EndIf
		
		If Height(X) > G_GroundYMax
			G_GroundYMax = Height(X)
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function GroundDraw()
	; W prefix = World coordinate
	; S prefic = Screen coordinate
	Local WXMin# = 0
	Local WXMax# = G_LevelWidth - 1
	
	If C_FunctionTrace Then FunctionEntree("GroundDraw")
	
	GroundInitialise()	; Later will only call when necessary
	
	; Work out where the left and right edges are
	SLeft = G_ScreenCentreX + (0 - G_CameraX) * G_GridSize * G_ZoomFactor
	If SLeft < 0
		SLeft = 0
	EndIf
	
	SRight = G_ScreenCentreX + (G_LevelWidth - 1 - G_CameraX) * G_GridSize * G_ZoomFactor
	If SRight >= G_ScreenWidth
		SRight = G_ScreenWidth - 1
	EndIf
	
	; Work out various vertical positions
	SGroundY = G_ScreenCentreY - (G_GroundLevel - G_CameraY) * G_GridSize * G_ZoomFactor
	If SGroundY < 0
		SGroundY = 0
	ElseIf SGroundY >= G_ScreenHeight
		; Nothing to draw
		Return
	EndIf
	
	SWaterY = G_ScreenCentreY - (G_WaterLevel - G_CameraY) * G_GridSize * G_ZoomFactor
	If SWaterY < 0 Then SWaterY = 0
	
	SDeepestY = G_ScreenCentreY - (G_GroundYMin - G_CameraY) * G_GridSize * G_ZoomFactor
	If SDeepestY >= G_ScreenHeight
		SDeepestY = G_ScreenHeight - 1
	EndIf
	
	; Draw visible blocks
	Color 160, 128, 64
	
	; Left block
	If SLeft > 0
		Rect 0, SGroundY, SLeft, G_ScreenHeight - SGroundY + 1, True
	EndIf
	
	; Right block
	If SRight + 1 < G_ScreenWidth - 1
		Rect SRight + 1, SGroundY, G_ScreenWidth - SRight + 1, G_ScreenHeight - SGroundY + 1, True
	EndIf
	
	; Center block from the deepest point of water downwards
	If SDeepestY + 1 < G_ScreenHeight - 1
		Rect SLeft, SDeepestY + 1, SRight - SLeft + 1, G_ScreenHeight - SDeepestY, True
	EndIf
	
	; Now the centre block where the water and ground meet
	ImageW = SRight - SLeft + 1
	ImageH = SDeepestY - SGroundY + 1

	If ImageH <= 0 Or ImageW <= 0
		; Nothing to draw
	Else
		; See whether the cached image is OK to be used
		If G_GroundImage = 0
			UseCacheImage = False
		ElseIf ImageW = 0 Or ImageH = 0
			UseCacheImage = False
		ElseIf G_GroundImageWidth <> ImageW Or G_GroundImageHeight <> ImageH
			UseCacheImage = False
		ElseIf G_GroundImageZoomFactor <> G_ZoomFactor
			UseCacheImage = False
		ElseIf G_GroundImageCameraX <> G_CameraX Or G_GroundImageCameraY <> G_CameraY
			UseCacheImage = False
		Else
			UseCacheImage = True
		EndIf
		
		If UseCacheImage
			G_GroundCacheUsed = G_GroundCacheUsed + 1
			; Can use cached drawing from last time
			DrawImage G_GroundImage, SLeft, SGroundY
			;Color 0, 255, 0
			;Rect SLeft, SGroundY, ImageW, ImageH, False 
		Else
			; Cached image (if any) is not valid
			
			G_GroundCacheUsed = G_GroundCacheUsed - 1
			
			If G_GroundImage <> 0 And G_GroundImageWidth = ImageW And G_GroundImageHeight = ImageH
				; Reuse same image buffer
				SetBuffer ImageBuffer(G_GroundImage)
				OffsetX = 0
				OffsetY = SGroundY
				ClsColor 0, 0, 0	; Ensure the background is transparent
				Cls
			Else
				; Free old image buffer
				FreeImage G_GroundImage
				G_GroundImage = 0
				
				If ImageW * ImageH < 200000
					; Small enough to use image caching
					G_GroundImage = CreateImage(ImageW, ImageH)
					If G_GroundImage = 0
						Stop
					EndIf
					SetBuffer ImageBuffer(G_GroundImage)
					OffsetX = 0
					OffsetY = SGroundY
				Else
					; Will draw directly to BackBuffer
					SetBuffer BackBuffer()
					OffsetX = SLeft
					OffsetY = 0
				EndIf
			EndIf
			
			; Draw to the buffer that has been determined
			Local WX#, WY#
			
			For SX = 0 To ImageW - 1
				WX = (SX + SLeft - G_ScreenCentreX) / (G_GridSize * G_ZoomFactor) + G_CameraX
				
				If WX < 0
					WX = 0
				ElseIf WX > G_LevelWidth
					WX = G_LevelWidth
				EndIf
				
				WX1 = Floor(WX)
				If WX1 < G_LevelWidth
					WX2 = WX1 + 1
				Else
					WX2 = WX1
				EndIf
				
				WY1# = Height(WX1)
				WY2# = Height(WX2)
				
				WY = WY1 + (WY2 - WY1) * (WX - WX1) 
				
				YDepth = G_ScreenCentreY - (WY - G_CameraY) * G_GridSize * G_ZoomFactor
				
				If SWaterY < YDepth
					Color 50, 50, 255
					Rect SX + OffsetX, SWaterY - OffsetY, 1, YDepth - SWaterY + 1
				EndIf
				
				Color 160, 128, 64
				Rect SX + OffsetX, YDepth - OffsetY, 1, SDeepestY - YDepth + 1
			Next
			
			If G_GroundImage <> 0
				SetBuffer BackBuffer()
				DrawImage G_GroundImage, SLeft, SGroundY
			EndIf
			
			; Show when cache image not used
			;Color 255, 0, 0
			;Rect SLeft, SGroundY, ImageW, ImageH, False 
		EndIf
		
;		If G_GroundImage <> 0
;			DrawImage G_GroundImage, 0, 200
;		EndIf
		
	EndIf
	
	G_GroundImageWidth		= ImageW
	G_GroundImageHeight		= ImageH
	G_GroundImageZoomFactor	= G_ZoomFactor
	G_GroundImageCameraX	= G_CameraX
	G_GroundImageCameraY	= G_CameraY

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function BridgeDrawFull()
	Local X1#, Y1#, X2#, Y2#
	Local C#
	Local S.T_Segment
	Local B.T_Bolt
	
	If C_FunctionTrace Then FunctionEntree("BridgeDrawFull")
	
	For S.T_Segment = Each T_Segment
		If G_Mode = C_ModeEdit
			X1 = S\Bolt1\OriginalX
			Y1 = S\Bolt1\OriginalY
			
			X2 = S\Bolt2\OriginalX
			Y2 = S\Bolt2\OriginalY
			
			If S\Shadow
				Color 100, 100, 100
			ElseIf S\Selected Or S\SelectedMaybe
				Color 255, 255, 100
			Else
				If S\Locked
					Color S\Material\R / 2, S\Material\G / 2, S\Material\B / 2
				Else
					Color S\Material\R, S\Material\G, S\Material\B
				EndIf
			EndIf
		Else
			X1 = S\Bolt1\PX
			Y1 = S\Bolt1\PY
						
			X2 = S\Bolt2\PX
			Y2 = S\Bolt2\PY
			If G_StressColourDisplay
				If S\Stress>0 Then			
					C  = S\Stress / S\Material\MaxTension * 128
				Else
					C  = S\Stress / S\Material\MaxPressure * 128
				End If
				Color 128 - C, 128 + C, 128 - Abs(C)
			ElseIf S\SegmentType = C_SegmentTypeTrain
				Color 0, 255, 0
			Else
				If S\Locked ;S\SegmentType = C_SegmenTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
					Color S\Material\R / 2, S\Material\G / 2, S\Material\B / 2
				Else
					Color S\Material\R, S\Material\G, S\Material\B
				End If
			EndIf
		EndIf

;		If X1 < 0 Or Y1 < -5 Or X2 < 0 Or Y2 < -5
;			DebugText("Bad point" + S1X + ", " + S1Y + ", " + S2X + ", " + S2Y )
;			;Stop
;		EndIf

		If G_Mode = C_ModeEdit Or (G_Mode = C_ModeTest And S\Status = C_SegmentStatusOK)
			S1X = G_ScreenCentreX + (X1 - G_CameraX) * G_GridSize * G_ZoomFactor
			S1Y = G_ScreenCentreY - (Y1 - G_CameraY) * G_GridSize * G_ZoomFactor
			
			S2X = G_ScreenCentreX + (X2 - G_CameraX) * G_GridSize * G_ZoomFactor
			S2Y = G_ScreenCentreY - (Y2 - G_CameraY) * G_GridSize * G_ZoomFactor
			
;			If S1X = 0 Or S1Y = 0 Or S2X = 0 Or S2Y = 0
;				DebugText("0 coord" + S1X + ", " + S1Y + ", " + S2X + ", " + S2Y )
;				
;			EndIf
		
			Line S1X, S1Y, S2X, S2Y
		EndIf
	Next
	
	For B.T_Bolt = Each T_Bolt
		If G_Mode = C_ModeEdit
			X1 = B\OriginalX
			Y1 = B\OriginalY
			
			If B\Selected Or B\SelectedMaybe
				Color 100, 255, 255
;			ElseIf B\Id = G_DebugTraceBoltId
;				Color 255, 255, 0
			Else
				Color 255, 0, 0
			EndIf
		Else
			X1 = B\PX
			Y1 = B\PY
			Color 255, 0, 0
		EndIf

		S1X = G_ScreenCentreX + (X1 - G_CameraX) * G_GridSize * G_ZoomFactor
		S1Y = G_ScreenCentreY - (Y1 - G_CameraY) * G_GridSize * G_ZoomFactor
		
		Oval S1X - 2, S1Y - 2, 5, 5, False
	Next

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function BridgeDrawFull2()
	;Transform bolt positions & determine sectors
	;Clip segments, select color & draw segment
	;Draw bolts
	Local X1#,Y1#,X2#,Y2#,DX#,DY#
	Local VSX#=1024,VSY#=768
	Local Thickness#=G_zoomfactor*15
	
	If C_FunctionTrace Then FunctionEntree("BridgeDrawFull2")
	
	For B.T_Bolt = Each T_Bolt
		; Transform to screencoordinates
		If G_Mode = C_ModeEdit
			X1 = B\OriginalX
			Y1 = B\OriginalY
		Else
			X1 = B\PX
			Y1 = B\PY
		EndIf
		
		B\TX = G_ScreenCentreX + (X1 - G_CameraX) * G_GridSize * G_ZoomFactor
		B\TY = G_ScreenCentreY - (Y1 - G_CameraY) * G_GridSize * G_ZoomFactor
		
		;Determine sector
		B\Sector=0
		If (B\TX)<0 Then
			B\Sector=(B\Sector Or %000100)
		Else If (B\TX)=>VSX
			B\Sector=(B\Sector Or %000001)
		Else 
			B\Sector=(B\Sector Or %000010)
		End If

		If (B\TY)<0 Then
			B\Sector=(B\Sector Or %100000)
		Else If (B\TY)=>VSY
			B\Sector=(B\Sector Or %001000)
		Else 
			B\Sector=(B\Sector Or %010000)
		End If
	Next
	
	;Draw Segments
	LockBuffer
	For S.T_Segment = Each T_Segment
		If G_Mode = C_ModeEdit Or (G_Mode = C_ModeTest And S\Status = C_SegmentStatusOK)

			; Get color
			If G_Mode = C_ModeEdit			
				If S\Shadow
					CR = 100 : CG = 100 : CB = 100
				ElseIf G_BreakDisplay And S\Status = C_SegmentStatusBroken
					If S\Stress < 0
						CR = 255 : CG = 0 : CB = 0
					Else
						CR = 0 : CG = 255 : CB = 0
					EndIf
				ElseIf S\Selected Or S\SelectedMaybe
					CR = 255 : CG = 255 : CB = 100
				Else
					If S\Locked
						CR = S\Material\R / 2 : CG = S\Material\G / 2 : CB = S\Material\B / 2
					Else
						CR = S\Material\R : CG = S\Material\G : CB = S\Material\B
					EndIf
				EndIf
			Else
				If G_StressColourDisplay
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
				ElseIf S\SegmentType = C_SegmentTypeTrain
					CR = 0 : CG = 255 : CB = 0
				Else
					If S\Locked ;S\SegmentType = C_SegmenTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
						CR = S\Material\R / 2 : CG = S\Material\G / 2 : CB = S\Material\B / 2
					Else
						CR = S\Material\R : CG = S\Material\G : CB = S\Material\B
					End If
				EndIf
			EndIf
		
			; Precalc & dereference
			X1 = S\Bolt1\TX
			Y1 = S\Bolt1\TY
			X2 = S\Bolt2\TX
			Y2 = S\Bolt2\TY

			DX = X2 - X1
			DY = Y2 - Y1
		
			;Clip
			If (S\Bolt1\Sector=%010010) And (S\Bolt2\Sector=%010010) Then 
				If S\SegmentType = C_SegmentTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
					DrawRailAA X1,Y1,X2,Y2,CR,CG,CB,Thickness
				Else
					DrawSegmentAA X1,Y1,X2,Y2,CR,CG,CB,Thickness
				EndIf
			Else If (S\Bolt1\Sector=S\Bolt2\Sector) Then
			Else If (((S\Bolt1\Sector Xor S\Bolt2\Sector) And %000111)=False) And ((S\Bolt1\Sector And %000010)=False)
			Else If (((S\Bolt1\Sector Xor S\Bolt2\Sector) And %111000)=False) And ((S\Bolt1\Sector And %010000)=False)
			Else If (S\Bolt1\Sector=%010010) And (S\Bolt2\Sector<>%010010) Then 
				dpx#=-dy*VSX
				dpy#=-dx*VSY
				
				u#=y1
				d#=y1-VSY
				l#=x1
				r#=x1-VSX

				pm#=1
				
				pu#=(u*vsx)/dpx
				If pu=>0 And pu<pm Then pm=pu
				
				pd#=(d*vsx)/dpx
				If pd=>0 And pd<pm Then pm=pd

				pl#=(l*vsy)/dpy
				If pl=>0 And pl<pm Then pm=pl

				pr#=(r*vsy)/dpy
				If pr=>0 And pr<pm Then pm=pr
				
				If S\SegmentType = C_SegmentTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
					DrawRailAA x1,y1,x1+dx*pm,y1+dy*pm,CR,CG,CB,Thickness
				Else
					DrawSegmentAA x1,y1,x1+dx*pm,y1+dy*pm,CR,CG,CB,Thickness
				EndIf
			Else If (S\Bolt2\Sector=%010010) And (S\Bolt1\Sector<>%010010) Then
				dpx#=vsx*dy
				dpy#=vsy*dx
				
				u#=y2
				d#=y2-vsy
				l#=x2
				r#=x2-vsx

				pm#=1
				
				pu#=(u*vsx)/dpx
				If pu=>0 And pu<pm Then pm=pu
				
				pd#=(d*vsx)/dpx
				If pd=>0 And pd<pm Then pm=pd

				pl#=(l*vsy)/dpy
				If pl=>0 And pl<pm Then pm=pl

				pr#=(r*vsy)/dpy
				If pr=>0 And pr<pm Then pm=pr
				If S\SegmentType = C_SegmentTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
					DrawRailAA x2,y2,x2-dx*pm,y2-dy*pm,CR,CG,CB,Thickness
				Else
					DrawSegmentAA x2,y2,x2-dx*pm,y2-dy*pm,CR,CG,CB,Thickness
				EndIf
			Else
				dpx#=-vsx*dy
				dpy#=-vsy*dx
				
				u#=y1
				d#=y1-vsy
				l#=x1
				r#=x1-vsx
				
				pu#=(u*vsx)/dpx
				bu#=(dx*u-dy*l)/dpx

				pd#=(d*vsx)/dpx
				bd#=-(dx*d-dy*r)/dpx

				pl#=(l*vsy)/dpy
				bl#=-(dx*u-dy*l)/dpy
		
				pr#=(r*vsy)/dpy
				br#=(dx*d-dy*r)/dpy

				p=0
				If bu=>0 And bu=<1 Then 
					p0#=pu
					p=1
				End If
				If bd=>0 And bd=<1 Then
					If p=1 Then 
						p1#=pd
						p=2
					End If
					If p=0 Then 
						p0=pd
						p=1
					End If
				End If
				If bl=>0 And bl=<1 Then
					If p=1 Then 
						p1=pl
						p=2
					End If
					If p=0 Then 
						p0=pl
						p=1
					End If
				End If
				If br=>0 And br=<1 Then
					If p=1 Then 
						p1=pr
						p=2
					End If
				End If

				If p=2
					If S\SegmentType = C_SegmentTypeRail Or S\SegmentType = C_SegmentTypeFixedRail
						DrawRailAA x1+dx*p0,y1+dy*p0,x1+dx*p1,y1+dy*p1,CR,CG,CB,Thickness
					Else
						DrawSegmentAA x1+dx*p0,y1+dy*p0,x1+dx*p1,y1+dy*p1,CR,CG,CB,Thickness
					EndIf
				EndIf
			End If
		End If
		
	Next
	UnlockBuffer
	
	;Draw Bolts
	If G_Mode = C_ModeEdit Or G_Mode = C_ModeTest
		
		Local BoltWidth = Int(Thickness * 1.45)	; Need to be a bit more than sqr(2) * width
		Local BoltOffset = (BoltWidth - 0.999) * 0.5
		
		Local HoleWidth
		If (BoltWidth And $1) = 0
			HoleWidth = Int(Thickness * 0.75) And $FFFFFFFE
		Else
			HoleWidth = Int(Thickness * 0.75) Or $1
		EndIf
		
		If HoleWidth > BoltWidth - 2
			HoleWidth = BoltWidth - 2
		EndIf
		Local HoleOffset = (HoleWidth - 0.999) * 0.5
		
		For B.T_Bolt = Each T_Bolt
			
			If G_Mode = C_ModeEdit
				X1 = B\TX
				Y1 = B\TY
				If B\Selected Or B\SelectedMaybe
					Color 100, 255, 255
				Else
					Color 255, 0, 0
				EndIf
			Else
				X1 = B\PX
				Y1 = B\PY
				Color 255, 0, 0
			EndIf
			
			If B\BoltType = C_BoltTypeFixed
				Rect B\TX - BoltOffset, B\TY - BoltOffset, BoltWidth, BoltWidth, True
			Else
				Oval B\TX - BoltOffset, B\TY - BoltOffset, BoltWidth, BoltWidth, True
			EndIf
			
			If HoleWidth > 0
				Color 0,0,0
				Oval B\TX - HoleOffset, B\TY - HoleOffset, HoleWidth, HoleWidth, True
			EndIf
		
		Next
	End If
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function DrawSegment(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	col=(r Shl 16) Or (g Shl 8) Or b
	thickness=thickness-1
	thickness2#=thickness/2+.001
	ex#=x1-x0
	ey#=y1-y0
	
	ax#=Abs(ex)
	ay#=Abs(ey)
	Theta# = ATan2(ay, ax)
	
	If ax>ay Then
		; Correct thickness
		thickness = thickness / Cos(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		ix0=Int(x0)
		ix1=Int(x1)

		;slope
		dy#=ey/ex

		If ex<0 Then
			;swap values
			tmpi=ix0
			ix0=ix1
			ix1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		y0 = y0 - (x0-Float(ix0)) * dy
		y1 = y1 - (x1-Float(ix1)) * dy

		;AA offset
		o1#=-((y0-thickness2-.5)-Floor(y0-thickness2))
		o2#=((y0+thickness2-.5)-Floor(y0+thickness2))
		
		;initial coords
		x	= ix0
		fy# = y0
		
		While x=<ix1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer y (start)
			iy=Int(fy-thickness2)
 
			WritePixel x,iy-2,0
			WritePixel x,iy-1, (Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For iy=iy To Int(fy+thickness2)
				WritePixel x,iy,col
			Next
			WritePixel x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			WritePixel x,iy+1,0
			
			;increase float y
			fy=fy+dy

			;increase AA
			o1=o1-dy
			o2=o2+dy

			x=x+1
		Wend
		
	Else
		; Correct thickness
		thickness = thickness / Sin(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		iy0=Int(y0)
		iy1=Int(y1)
		
		;slope
		dx#=ex/ey
		
		If ey<0 Then
			;swap values
			tmpi=iy0
			iy0=iy1
			iy1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		x0 = x0 - (y0-Float(iy0)) * dx
		x1 = x1 - (y1-Float(iy1)) * dx
		
		;AA offset
		o1#=-((x0-thickness2-.5)-Floor(x0-thickness2))
		o2#=((x0+thickness2-.5)-Floor(x0+thickness2))
		
		;initial coords
		y	= iy0
		fx# = x0
		
		While y=<iy1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer x (start)
			ix=Int(fx-thickness2)

			WritePixel ix-2,y,0
			WritePixel ix-1,y,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For ix=ix To Int(fx+thickness2)
				WritePixel ix,y,col
			Next
			WritePixel ix,y,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			WritePixel ix+1,y,0
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
End Function

Function DrawSegment2(x0#,y0#,ex#,ey#,l#,r,g,b,thickness#)
	col=(r Shl 16) Or (g Shl 8) Or b
	thickness=thickness-1
	thickness2#=thickness/2+.001
	ex#=ex*l
	ey#=ex*l
	x1#=x0+ex
	y1#=y0+ey
	
	ax#=Abs(ex)
	ay#=Abs(ey)
	
	If ax>ay Then
		
		;integer endpoints
		ix0=Int(x0)
		ix1=Int(x1)

		;slope
		dy#=ey/ex

		If ex<0 Then
			;swap values
			tmpi=ix0
			ix0=ix1
			ix1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		y0 = y0 - (x0-Float(ix0)) * dy
		y1 = y1 - (x1-Float(ix1)) * dy

		;AA offset
		o1#=-((y0-thickness2+.5)-Floor(y0-thickness2))
		o2#=((y0+thickness2+.5)-Floor(y0+thickness2))
		
		;initial coords
		x	= ix0
		fy# = y0
		
		While x=<ix1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer y (start)
			iy=Int(fy-thickness2)

			WritePixel x,iy-1,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For iy=iy To Int(fy+thickness2)
				WritePixel x,iy,col
			Next
			WritePixel x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			
			;increase float y
			fy=fy+dy

			;increase AA
			o1=o1-dy
			o2=o2+dy

			x=x+1
		Wend
		
	Else
		;integer endpoints
		iy0=Int(y0)
		iy1=Int(y1)

		;slope
		dx#=ex/ey

		If ey<0 Then
			;swap values
			tmpi=iy0
			iy0=iy1
			iy1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		x0 = x0 - (y0-Float(iy0)) * dx
		x1 = x1 - (y1-Float(iy1)) * dx

		;AA offset
		o1#=-((x0-thickness2+.5)-Floor(x0-thickness2))
		o2#=((x0+thickness2+.5)-Floor(x0+thickness2))
		
		;initial coords
		y	= iy0
		fx# = x0
		
		While y=<iy1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer x (start)
			ix=Int(fx-thickness2)

			WritePixel ix-1,y,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For ix=ix To Int(fx+thickness2)
				WritePixel ix,y,col
			Next
			WritePixel ix,y,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If

End Function

; PGF - Special with AA
Function DrawSegmentAA(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	
	If KeyDown(C_Key_F6)
		DrawSegment(x0, y0, x1, y1, R, G, B, Thickness)
		Return
	EndIf
	
	col=(r Shl 16) Or (g Shl 8) Or b

	ex#=x1-x0
	ey#=y1-y0
	
	ax#=Abs(ex)
	ay#=Abs(ey)
	Theta# = ATan2(ay, ax)
	
	If ax>ay Then
		; Correct thickness
		thickness = thickness / Cos(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		ix0=Int(x0)
		ix1=Int(x1)

		;slope
		dy#=ey/ex
		
		If ex<0 Then
			;swap values
			tmpi=ix0
			ix0=ix1
			ix1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		y0 = y0 - (x0-Float(ix0)) * dy
		y1 = y1 - (x1-Float(ix1)) * dy

		;AA offset
		o1#=-((y0-thickness2-.5)-Floor(y0-thickness2))
		o2#=((y0+thickness2-.5)-Floor(y0+thickness2))
		
		;initial coords
		x	= ix0
		fy# = y0
		
		While x=<ix1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer y (start)
			iy=Int(fy-thickness2)
 
			;Writepixel x,iy-2,0
			
			V = ReadPixel(x, iy-1)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel x,iy-1, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)
			
			For iy=iy To Int(fy+thickness2)
				WritePixel x,iy,col
			Next

			V = ReadPixel(x, iy)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel x,iy, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			;Writepixel x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			;Writepixel x,iy+1,0
			
			;increase float y
			fy=fy+dy

			;increase AA
			o1=o1-dy
			o2=o2+dy

			x=x+1
		Wend
		
	Else
		; Correct thickness
		thickness = thickness / Sin(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		iy0=Int(y0)
		iy1=Int(y1)

		;slope
		dx#=ex/ey

		If ey<0 Then
			;swap values
			tmpi=iy0
			iy0=iy1
			iy1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		x0 = x0 - (y0-Float(iy0)) * dx
		x1 = x1 - (y1-Float(iy1)) * dx

		;AA offset
		o1#=-((x0-thickness2-.5)-Floor(x0-thickness2))
		o2#=((x0+thickness2-.5)-Floor(x0+thickness2))
		
		;initial coords
		y	= iy0
		fx# = x0
		
		While y=<iy1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer x (start)
			ix=Int(fx-thickness2)

			;Writepixel ix-2,y,0
			
			V = ReadPixel(ix-1, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel ix-1,y, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)

			For ix=ix To Int(fx+thickness2)
				WritePixel ix,y,col
			Next

			V = ReadPixel(ix, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel ix,y, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			
			;Writepixel ix+1,y,0
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
End Function

; Version of DrawSegmentAA that draws special rails which are white on top side
Function DrawRailAA(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	
	Local Col = R Shl 16 + G Shl 8 + B
	
	; Special rail colour
	Local RR = 250
	Local RG = 250
	Local RB = 250
	Local RCol = RR Shl 16 + RG Shl 8 + RB
	
	If thickness < 4
		R = RR
		G = RG
		B = RB
		Col = RCol
	EndIf
		
	ex#=x1-x0
	ey#=y1-y0
	
	ax#=Abs(ex)
	ay#=Abs(ey)
	Theta# = ATan2(ay, ax)
	
	If ax>ay Then
		; Correct thickness
		thickness = thickness / Cos(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		ix0=Int(x0)
		ix1=Int(x1)

		;slope
		dy#=ey/ex
		
		If ex<0 Then
			;swap values
			tmpi=ix0
			ix0=ix1
			ix1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		y0 = y0 - (x0-Float(ix0)) * dy
		y1 = y1 - (x1-Float(ix1)) * dy

		;AA offset
		o1#=-((y0-thickness2-.5)-Floor(y0-thickness2))
		o2#=((y0+thickness2-.5)-Floor(y0+thickness2))
		
		;initial coords
		x	= ix0
		fy# = y0
		
		While x=<ix1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer y (start)
			iy=Int(fy-thickness2)
 
			;Writepixel x,iy-2,0
			
			V = ReadPixel(x, iy-1)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			;WritePixel x,iy-1, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)
			WritePixel x,iy-1, (Int(o1*RR + (1-o1) * VR) Shl 16) Or (Int(o1*RG + (1-o1) * VG) Shl 8) Or Int(o1*RB + (1-o1) * VB)
			
			While iy < (fy - thickness2 / 2)
				WritePixel x, iy, RCol
				iy = iy + 1
			Wend
			
			If iy < Int(fy + thickness2)
				WritePixel x,iy, (Col And $FEFEFE) Shr 1 + (RCol And $FEFEFE) Shr 1
				iy = iy + 1
			EndIf
			
			While iy < Int(fy + thickness2)
				WritePixel x,iy,col
				iy = iy + 1
			Wend

			V = ReadPixel(x, iy)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel x,iy, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			;Writepixel x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			;Writepixel x,iy+1,0
			
			;increase float y
			fy=fy+dy

			;increase AA
			o1=o1-dy
			o2=o2+dy

			x=x+1
		Wend
		
	Else
		; Correct thickness
		thickness = thickness / Sin(Theta)
		thickness2#=thickness/2+.001
		
		;integer endpoints
		iy0=Int(y0)
		iy1=Int(y1)

		;slope
		dx#=ex/ey

		If ey<0 Then
			;swap values
			tmpi=iy0
			iy0=iy1
			iy1=tmpi
			
			tmpf#=y0
			y0=y1
			y1=tmpf
			
			tmpf#=x0
			x0=x1
			x1=tmpf
		End If
		
		;extrapolate yvals
		x0 = x0 - (y0-Float(iy0)) * dx
		x1 = x1 - (y1-Float(iy1)) * dx

		;AA offset
		o1#=-((x0-thickness2-.5)-Floor(x0-thickness2))
		o2#=((x0+thickness2-.5)-Floor(x0+thickness2))
		
		;initial coords
		y	= iy0
		fx# = x0
		
		While y=<iy1

			;AA bounds
			If o1=>1.0 Then o1=o1-1.0
			If o1<0.0 Then o1=o1+1.0
			If o2=>1.0 Then o2=o2-1.0
			If o2<0.0 Then o2=o2+1.0
			
			;integer x (start)
			ix=Int(fx-thickness2)

			;Writepixel ix-2,y,0
			
			V = ReadPixel(ix-1, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			;WritePixel ix-1,y, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)
			WritePixel ix-1,y, (Int(o1*RR + (1-o1) * VR) Shl 16) Or (Int(o1*RG + (1-o1) * VG) Shl 8) Or Int(o1*RB + (1-o1) * VB)

			While ix < fx - thickness2 / 2
				WritePixel ix, y, RCol
				ix = ix + 1
			Wend
			
			While ix < Int(fx+thickness2)
				WritePixel ix, y, Col
				ix = ix + 1
			Wend

			V = ReadPixel(ix, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel ix,y, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			
			;Writepixel ix+1,y,0
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
End Function

; Draw a miniature version suitable for output to the save file as a thumbnail
;   - This replicates a lot of ViewZoom(), GroundDraw() and BridgeDrawFull() but with enough differences
;     that I didn't go back and generalise those routines and also worry about saving the global
;     paramaters
;   - An image is returned and it should later be freed
;   
Function BridgeDrawMini()
	Local X1#, Y1#, X2#, Y2#
	Local MinX#, MinY#, MaxX#, MaxY#
	Local CentreX#, CentreY#
	Local W#, H#
	Local Aspect#
	Local ZoomFactor#
	
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("BridgeDrawMini")
	
	If G_ThumbnailImage = 0
		G_ThumbnailImage = CreateImage(G_ThumbnailWidth, G_ThumbnailHeight)
	EndIf
	
	SetBuffer ImageBuffer(G_ThumbnailImage)
	
	; Erase the image
	Color 0, 0, 0
	Rect 0, 0, G_ThumbnailWidth, G_ThumbnailHeight, True
	
	; Find extents of drawing
	Initial = True
	For S = Each T_Segment
		If S\SegmentType = C_SegmentTypeRegular
			X1 = S\Bolt1\OriginalX
			Y1 = S\Bolt1\OriginalY
			X2 = S\Bolt2\OriginalX
			Y2 = S\Bolt2\OriginalY
			
			If Initial
				Initial = False
				MinX = X1
				MinY = Y1
				MaxX = X1
				MaxY = Y1
			Else
				If X1 < MinX Then MinX = X1
				If Y1 < MinY Then MinY = Y1
				If X1 > MaxX Then MaxX = X1
				If Y1 > MaxY Then MaxY = Y1
			EndIf
			
			If X2 < MinX Then MinX = X2
			If Y2 < MinY Then MinY = Y2
			If X2 > MaxX Then MaxX = X2
			If Y2 > MaxY Then MaxY = Y2
			
		EndIf
	Next
	
	If MinX = 0 And MinY = 0 And MaxX = 0 And MaxY = 0
		MaxX = 600 * 2
		MaxY = 240 * Y
	Else
		If MinY > -10 Then MinY = -10
		If MaxY < 10 Then MaxY = 10
	EndIf
	
	CentreX = (MinX + MaxX) / 2
	CentreY = (MinY + MaxY) / 2
	
	W = (MaxX - MinX) * 1.1		; Allow 10% wider / taller
	H = (MaxY - MinY) * 1.1
	
	If W = 0
		If H = 0
			W = 10
			H = 10
		Else
			W = H
		EndIf
	Else
		If H = 0
			H = W / 2
		EndIf
	EndIf
	
	Aspect = Float(H) / W
	
	If Aspect > (Float(G_ThumbnailHeight) / G_ThumbnailWidth)
		; Height controls the setting
		ZoomFactor = Float(G_ThumbnailHeight) / (H * G_GridSize)
	Else
		; Width controls the setting
		ZoomFactor = Float(G_ThumbnailWidth) / (W * G_GridSize)
	EndIf
	
	; Draw the ground and water
	
	YGround = G_ThumbnailHeight / 2 + CentreY * G_GridSize * ZoomFactor
	If YGround >= G_ThumbnailHeight
		; Nothing to draw
	Else
		If YGround < 0
			YGround = 0
		EndIf
		
		XLeft = G_ThumbnailWidth / 2 + (0 - CentreX) * G_GridSize * ZoomFactor
		If XLeft < 0 Then XLeft = 0
		
		XRight = G_ThumbnailWidth / 2 + (1200 - 1 - CentreX) * G_GridSize * ZoomFactor
		If XRight >= G_ThumbnailWidth
			XRight = G_ThumbnailWidth - 1
		ElseIf XRight < 0
			XRight = 0
		EndIf
		
		YWater = G_ThumbnailHeight / 2 - (G_WaterLevel - CentreY) * G_GridSize * ZoomFactor
		If YWater < 0 Then YWater = 0
		
		If YGround < G_ThumbnailHeight
			Color 160, 128, 64
			Rect 0, YGround, XLeft, G_ThumbnailHeight - 1, True
			Rect XRight, YGround, G_ThumbnailWidth - 1, G_ThumbnailHeight - 1, True
		EndIf
	
		For X = XLeft To XRight
			XWorld = (X - G_ThumbnailWidth / 2) / (G_GridSize * ZoomFactor) + CentreX
			If XWorld < 0
				XWorld = 0
			Else If XWorld > 1200
				XWorld = 1200
			EndIf
			YWorld = Height(XWorld)
			YDepth = G_ThumbnailHeight / 2 - (YWorld - CentreY) * G_GridSize * ZoomFactor
			If Y0 < G_ThumbnailHeight
				;Color 0, 0, 0
				If YWater < YDepth
					Color 50, 50, 255
					;Line X, YWater, X, YDepth - 1
					Rect X, YWater, 1, YDepth - YWater + 1
				EndIf
	
				Color 160, 128, 64
				;Line X, YDepth, X, G_ScreenHeight - 1
				Rect X, YDepth, 1, G_ThumbnailHeight - 1 - YDepth + 1
			EndIf
		Next
	EndIf
		
	; Draw the bridge
	For S.T_Segment = Each T_Segment
		X1 = S\Bolt1\OriginalX
		Y1 = S\Bolt1\OriginalY
		
		X2 = S\Bolt2\OriginalX
		Y2 = S\Bolt2\OriginalY
		
		Color S\Material\R, S\Material\G, S\Material\B

		S1X = G_ThumbnailWidth / 2 + (X1 - CentreX) * G_GridSize * ZoomFactor
		S1Y = G_ThumbnailHeight / 2 - (Y1 - CentreY) * G_GridSize * ZoomFactor
		
		S2X = G_ThumbnailWidth / 2 + (X2 - CentreX) * G_GridSize * ZoomFactor
		S2Y = G_ThumbnailHeight / 2 - (Y2 - CentreY) * G_GridSize * ZoomFactor
			
		Line S1X, S1Y, S2X, S2Y
	Next
	
	For B.T_Bolt = Each T_Bolt
		X1 = B\OriginalX
		Y1 = B\OriginalY
		
		Color 255, 0, 0

		S1X = G_ThumbnailWidth / 2 + (X1 - CentreX) * G_GridSize * ZoomFactor
		S1Y = G_ThumbnailHeight / 2 - (Y1 - CentreY) * G_GridSize * ZoomFactor
		
		Plot S1X, S1Y
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function CursorDraw()
	Local	SX, S1X, S2X
	Local	SY, S1Y, S2Y
		
	Local MS.T_MSegment
	Local MB.T_MBolt
	Local Dist#
	
	If C_FunctionTrace Then FunctionEntree("CursorDraw")
	
	If 1 ;G_Mode = G_ModeEdit
		SX = G_ScreenCentreX + (G_CursorX - G_CameraX) * G_GridSize * G_ZoomFactor
		SY = G_ScreenCentreY - (G_CursorY - G_CameraY) * G_GridSize * G_ZoomFactor
		
		Color 255, 128, 128
		Oval SX - 4, SY - 4, 9, 9, False
		
		If G_EditStep <> C_EditStepZero
			CursorDrawBox()
			If G_EditTool = C_EditToolMacroParabola
				MacroDraw()
			EndIf
			
			; Special for Build Multiple
			If G_EditTool = C_EditToolBuildMultiple
				MS = Last T_MSegment
				If MS <> Null
					MB = MS\Bolt2
					
					S1X = G_ScreenCentreX + (MB\OriginalX - G_CameraX) * G_GridSize * G_ZoomFactor
					S1Y = G_ScreenCentreY - (MB\OriginalY - G_CameraY) * G_GridSize * G_ZoomFactor
					
					Dist = Dist(G_CursorX, G_CursorY, MB\OriginalX, MB\OriginalY)
					If Dist < C_SegmentLengthMinimum Or Dist > C_SegmentLengthMaximum
						Color 255, 100, 100
					ElseIf G_MaterialPrimary\QtyAvailable <> -1 And G_MaterialPrimary\QtyUsed + G_MaterialPrimary\QtyMacro + Dist > G_MaterialPrimary\QtyAvailable
						Color 150, 0, 0
					ElseIf G_MaterialCostUsed + G_MaterialCostMacro + Dist * G_MaterialPrimary\CostPerUnit > G_MaterialBudget
						Color 255, 0, 0
					Else
						Color 100, 255, 100
					EndIf
					Line Sx, SY, S1X, S1Y
				EndIf
			EndIf

			Color 255, 128, 128
			Oval SX - 4, SY - 4, 9, 9, False
		EndIf
	EndIf

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function CursorDrawBox()
	
	If C_FunctionTrace Then FunctionEntree("CursorDrawBox")
	
	S1X = G_ScreenCentreX + (G_Point1X - G_CameraX) * G_GridSize * G_ZoomFactor
	S1Y = G_ScreenCentreY - (G_Point1Y - G_CameraY) * G_GridSize * G_ZoomFactor
	
	S2X = G_ScreenCentreX + (G_Point2X - G_CameraX) * G_GridSize * G_ZoomFactor
	S2Y = G_ScreenCentreY - (G_Point2Y - G_CameraY) * G_GridSize * G_ZoomFactor
	
	If S1X < S2X
		S2X = S2X - S1X + 1
	ElseIf S1X = S2X
		S2X = 1
	Else
		S2X = S1X - S2X + 1
		S1X = S1X - S2X + 1
	EndIf
	
	If S1Y < S2Y
		S2Y = S2Y - S1Y + 1
	ElseIf S1Y = S2Y
		S2Y = 1
	Else
		S2Y = S1Y - S2Y + 1
		S1Y = S1Y - S2Y + 1
	EndIf
	
	If G_SelectModeType = C_SelectModeCrossing
		Color 150, 100, 100
	Else
		Color 100, 150, 100
	EndIf
	
	Rect S1X, S1Y, S2X, S2Y, False
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MouseInitialise()

	If C_FunctionTrace Then FunctionEntree("MouseInitialise")
	
	MoveMouse G_ScreenCentreX * 0.9, G_ScreenCentreY * 0.9
	G_MouseImage = GW_LoadImage("Images\", "Mouse Image.bmp")
	MaskImage G_MouseImage, 255, 255, 255

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MouseDraw()

	If C_FunctionTrace Then FunctionEntree("MouseDraw")
	
	DrawImage G_MouseImage, G_MouseX, G_MouseY
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MaterialInfoDraw()
	Local Msg$

	Color 200, 200, 250
	MaterialInfoDrawOne(G_MaterialPrimary, 10)
	If G_MaterialPrimary <> G_MaterialSecondary
		MaterialInfoDrawOne(G_MaterialSecondary, 160)
	EndIf
	
	If G_MaterialCostMacro = 0
		Msg = "Budget: $" + Int(G_MaterialCostUsed) + " of $" + Int(G_MaterialBudget)
	Else
		Msg = "Budget: $(" + Int(G_MaterialCostUsed) + "+" + Int(G_MaterialCostMacro) + ") of " + Int(G_MaterialBudget)
	EndIf
	
	Text 10, 4, Msg
	
	Local TL = StringWidth(Msg)
	Local TX, TY
	
	TX = 10 + L + 20
	If TX < 310
		TX = 310
	EndIf	
	
	If G_MaterialSelectedQty <> 0
		Text TX, 4, "Selected:"
		Text TX, 16, "  Length " + G_MaterialSelectedQty
		Text TX, 28, "  Mass   " + G_MaterialSelectedMass
		Text TX, 40, "  Cost   " + G_MaterialSelectedCost   
	EndIf
	
End Function

Function MaterialInfoDrawOne(M.T_Material, TX)
	Local TY = 20 + M\Icon\Height + 4
	Local Lmt$

	DrawBlock M\Icon\ImageNormal, TX, 20
	
	If M\QtyAvailable = -1
		Lmt = ""
	Else
		Lmt = " of " + M\QtyAvailable
	EndIf
	
	If M\QtyMacro = 0
		Text TX, TY, Int(M\QtyUsed * 100) / 100.0 + Lmt
	Else
		Text TX, TY, Int(M\QtyUsed * 100) / 100.0 + "+" + Int(M\QtyMacro * 100) / 100.0 + Lmt
	EndIf

End Function	

Function RegionDraw()
	Local R.T_Region

	If C_FunctionTrace Then FunctionEntree("RegionDraw")
	
	If G_RegionDefault = C_RegionTypeIn
		ColR = 0
		ColG = 25
		ColB = 0
	Else
		ColR = 25
		ColG = 0
		ColB = 0
	EndIf
	
	; Draw the default region colour on the entire screen
	ClsColor ColR, ColG, ColB
	Cls
	
	; Draw the first level which is opposite in sense to the default
	For R = Each T_Region
		If R\RegionType <> G_RegionDefault
			X = G_ScreenCentreX + (R\EdgeLeft - G_CameraX) * G_GridSize * G_ZoomFactor
			Y = G_ScreenCentreY - (R\EdgeTop - G_CameraY) * G_GridSize * G_ZoomFactor
			W = G_ScreenCentreX + (R\EdgeRight - G_CameraX) * G_GridSize * G_ZoomFactor - X + 1
			H = G_ScreenCentreY - (R\EdgeBottom - G_CameraY) * G_GridSize * G_ZoomFactor - Y + 1
			
			Color ColG, ColR, ColB		; Inverse Red/Green
			Rect X, Y, W, H, True
		EndIf
	Next
		
	; Draw the second level which is the same in sense to the default
	For R = Each T_Region
		If R\RegionType = G_RegionDefault
			X = G_ScreenCentreX + (R\EdgeLeft - G_CameraX) * G_GridSize * G_ZoomFactor
			Y = G_ScreenCentreY - (R\EdgeTop - G_CameraY) * G_GridSize * G_ZoomFactor
			W = G_ScreenCentreX + (R\EdgeRight - G_CameraX) * G_GridSize * G_ZoomFactor - X + 1
			H = G_ScreenCentreY - (R\EdgeBottom - G_CameraY) * G_GridSize * G_ZoomFactor - Y + 1
			
			Color ColR, ColG, ColB		; Correct Red/Blue
			Rect X, Y, W, H, True
		EndIf
	Next
		
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function RegionDrawOutline()
	Local R.T_Region

	If C_FunctionTrace Then FunctionEntree("RegionDrawOutline")
	
	If G_RegionDefault = C_RegionTypeIn
		ColR = 0
		ColG = 200
		ColB = 0
	Else
		ColR = 200
		ColG = 0
		ColB = 0
	EndIf
	
	; Draw the first level which is opposite in sense to the default
	; Inverse Red/Green
	Color ColG, ColR, ColB
	For R = Each T_Region
		If R\RegionType <> G_RegionDefault
			X = G_ScreenCentreX + (R\EdgeLeft - G_CameraX) * G_GridSize * G_ZoomFactor
			Y = G_ScreenCentreY - (R\EdgeTop - G_CameraY) * G_GridSize * G_ZoomFactor
			W = G_ScreenCentreX + (R\EdgeRight - G_CameraX) * G_GridSize * G_ZoomFactor - X + 1
			H = G_ScreenCentreY - (R\EdgeBottom - G_CameraY) * G_GridSize * G_ZoomFactor - Y + 1
			
			Rect X, Y, W, H, False
		EndIf
	Next
		
	; Draw the second level which is the same in sense to the default
	; Correct Red/Blue
	Color ColR, ColG, ColB
	For R = Each T_Region
		If R\RegionType = G_RegionDefault
			X = G_ScreenCentreX + (R\EdgeLeft - G_CameraX) * G_GridSize * G_ZoomFactor
			Y = G_ScreenCentreY - (R\EdgeTop - G_CameraY) * G_GridSize * G_ZoomFactor
			W = G_ScreenCentreX + (R\EdgeRight - G_CameraX) * G_GridSize * G_ZoomFactor - X + 1
			H = G_ScreenCentreY - (R\EdgeBottom - G_CameraY) * G_GridSize * G_ZoomFactor - Y + 1
			
			Rect X, Y, W, H, False
		EndIf
	Next
	
	Rect 0, 0, G_ScreenWidth, G_ScreenHeight, False
		
	If C_FunctionTrace Then FunctionEgress()
	
End Function