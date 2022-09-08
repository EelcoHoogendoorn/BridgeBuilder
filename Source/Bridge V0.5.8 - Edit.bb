; --------------------------------------------------------------------------------------------------------------------------------
;
; Edit Module
;
; --------------------------------------------------------------------------------------------------------------------------------

Function EditInitialise()
	
	If C_FunctionTrace Then FunctionEntree("EditInitialise")
	
	G_Mode = C_ModeEdit
	If G_EditTool = 0
		G_EditTool = C_EditToolSelect
	EndIf
	G_EditStep = C_EditStepZero
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function EditControls()
	
	If C_FunctionTrace Then FunctionEntree("EditControls")

	ViewZoomControls()
	
	If G_IconTest\Status = C_IconStatusSelected
		PanelIconSelect(G_ToolPanel, G_IconSelect)
	EndIf
	
	; This gives X,Y of mouse in real world coordinates
	G_CursorFreeX = 1.0 * (G_MouseX - G_ScreenCentreX) / G_GridSize / G_ZoomFactor + G_CameraX
	G_CursorFreeY = 1.0 * (G_ScreenCentreY - G_MouseY) / G_GridSize / G_ZoomFactor + G_CameraY
	
	; This snaps the mouse to nearest grid coordinate
	G_CursorX = Int(G_CursorFreeX / G_GridSnap) * G_GridSnap
	G_CursorY = Int(G_CursorFreeY / G_GridSnap) * G_GridSnap
	
	If G_ToolPanelMoving Or (G_ToolPanel\Status = C_PanelStatusShow And G_MouseX >= G_ToolPanel\EdgeLeft And G_MouseX <= G_ToolPanel\EdgeRight And G_MouseY >= G_ToolPanel\EdgeTop And G_MouseY <= G_ToolPanel\EdgeBottom)
		EditControlsToolPanel()
	ElseIf G_EditModeSuper And (G_SuperPanelMoving Or (G_SuperPanel\Status = C_PanelStatusShow And G_MouseX >= G_SuperPanel\EdgeLeft And G_MouseX <= G_SuperPanel\EdgeRight And G_MouseY >= G_SuperPanel\EdgeTop And G_MouseY <= G_SuperPanel\EdgeBottom))
		EditControlsSuperPanel()
	Else
		ViewPanControls()
		If G_EditModeSuper
			If EditControlsSuper() = False
				EditControlsNormal()
			EndIf
		Else
			EditControlsNormal()
		EndIf
	EndIf
	
	If G_EditTool = C_EditToolStretch
		MacroUpdateLengths()
	EndIf
	
	; Check for Right-Mouse click
	If G_Mouse2
		G_Point1X = G_CursorX
		G_Point1Y = G_CursorY
		G_Point2X = G_CursorX
		G_Point2Y = G_CursorY
		
		SelectClear()
		SelectMaybeClear()
		SelectUpdate()
		SelectMaybeConfirm()
		SelectDelete()
	EndIf
	
	; Keyboard commands
	EditControlsKeys()
	
	If C_FunctionTrace Then FunctionEgress()

End Function

Function EditControlsToolPanel()
	Local M.T_Material
	
	If C_FunctionTrace Then FunctionEntree("EditControlsToolPanel")

	G_MousePanelOver = G_ToolPanel
	If G_ToolPanelMoving
		If G_Mouse1Down
			PanelMove(G_ToolPanel, G_ToolPanel\EdgeLeft - G_ToolPanelMoveX + G_MouseX, G_ToolPanel\EdgeTop - G_ToolPanelMoveY + G_MouseY)
			G_ToolPanelMoveX = G_MouseX
			G_ToolPanelMoveY = G_MouseY
		Else
			G_ToolPanelMoving = False
		EndIf
	EndIf
	
	PanelIconScan(G_ToolPanel)
	If G_MouseIconOver = Null
		If G_Mouse1
			G_ToolPanelMoving = True
			G_ToolPanelMoveX = G_MouseX
			G_ToolPanelMoveY = G_MouseY
		EndIf
		
		If G_Mouse1Down And G_ToolPanelMoving
			PanelMove(G_ToolPanel, G_ToolPanel\EdgeLeft - G_ToolPanelMoveX + G_MouseX, G_ToolPanel\EdgeTop - G_ToolPanelMoveY + G_MouseY)
			G_ToolPanelMoveX = G_MouseX
			G_ToolPanelMoveY = G_MouseY
		EndIf
	Else
		If G_Mouse1
			If G_MouseIconOver\Status = C_IconStatusNormal
				PanelIconSelect(G_ToolPanel, G_MouseIconOver)
			EndIf
			
			If G_MouseIconOver = G_IconSelect
				G_EditTool = C_EditToolSelect
			ElseIf G_MouseIconOver = G_IconCopy
				G_EditTool = C_EditToolCopy
				EditMousePosition(G_Point2X, G_Point2Y)
				SelectCopy()
			ElseIf G_MouseIconOver = G_IconMove
				G_EditTool = C_EditToolMove
				EditMousePosition(G_Point2X, G_Point2Y)
				SelectMove()
			ElseIf G_MouseIconOver = G_IconStretch
				G_EditTool = C_EditToolStretch
				EditMousePosition(G_Point2X, G_Point2Y)
				SelectStretch()
			ElseIf G_MouseIconOver = G_IconFlipX
				SelectFlipX()
			ElseIf G_MouseIconOver = G_IconFlipY
				SelectFlipY()
			ElseIf G_MouseIconOver = G_IconRotate
				G_EditTool = C_EditToolRotate
				EditMousePosition(G_Point2X, G_Point2Y)
				SelectRotate()
			ElseIf G_MouseIconOver = G_IconGroup
				G_EditTool = C_EditToolGroup
				SelectGroup()
				; Immediately changes to Select tool.  Needs an indication that it worked but how ?
				G_EditTool = C_EditToolSelect
				PanelIconSelect(G_ToolPanel, G_IconSelect)
			ElseIf G_MouseIconOver = G_IconReplace
				G_EditTool = C_EditToolReplace
				SelectReplace()
				; Immediately changes to Select tool.  Needs an indication that it worked but how ?
				G_EditTool = C_EditToolSelect
				PanelIconSelect(G_ToolPanel, G_IconSelect)
			ElseIf G_MouseIconOver = G_IconBuild
				G_EditTool = C_EditToolBuild
			ElseIf G_MouseIconOver = G_IconBuildMultiple
				G_EditTool = C_EditToolBuildMultiple
			ElseIf G_MouseIconOver = G_IconMacroLine
				G_EditTool = C_EditToolMacroLine
			ElseIf G_MouseIconOver = G_IconMacroBeam
				G_EditTool = C_EditToolMacroBeam
			ElseIf G_MouseIconOver = G_IconMacroArch
				G_EditTool = C_EditToolMacroArch
			ElseIf G_MouseIconOver = G_IconMacroCircle
				G_EditTool = C_EditToolMacroCircle
			ElseIf G_MouseIconOver = G_IconMacroTower
				G_EditTool = C_EditToolMacroTower
			ElseIf G_MouseIconOver = G_IconTest
				TestInitialise()
				; Immediately jump out and let mainline take care of things
				Goto EditControlsToolPanelReturn
			ElseIf G_MouseIconOver = G_IconLoad
				G_Mode = C_ModeLoad
			ElseIf G_MouseIconOver = G_IconSave
				G_Mode = C_ModeSave
			ElseIf G_MouseIconOver = G_IconNew
				If BridgeClear(True)
					BridgeNew()
				EndIf
				; Immediately changes to Select tool.  Needs an indication that it worked but how ?
				G_EditTool = C_EditToolSelect
				PanelIconSelect(G_ToolPanel, G_IconSelect)
			ElseIf G_MouseIconOver = G_IconExit
				G_Mode = C_ModeExit
			Else
				For M = Each T_Material
					If M\Icon = G_MouseIconOver
						;G_MaterialPrimary\Icon\Status = C_IconStatusNormal
						;M\Icon\Status = C_IconStatusSelected
						G_MaterialPrimary = M
						G_MaterialSecondary = M
					EndIf
				Next
			EndIf
		EndIf
		
		If G_Mouse2
			For M = Each T_Material
				If M\Icon = G_MouseIconOver
					G_MaterialSecondary = M
				EndIf
			Next
		EndIf
	EndIf

.EditControlsToolPanelReturn
	
	If C_FunctionTrace Then FunctionEgress()

End Function

Function EditControlsSuperPanel()
	Local M.T_Material
	
	If C_FunctionTrace Then FunctionEntree("EditControlsSuperPanel")

	G_MousePanelOver = G_SuperPanel
	If G_SuperPanelMoving
		If G_Mouse1Down
			PanelMove(G_SuperPanel, G_SuperPanel\EdgeLeft - G_SuperPanelMoveX + G_MouseX, G_SuperPanel\EdgeTop - G_SuperPanelMoveY + G_MouseY)
			G_SuperPanelMoveX = G_MouseX
			G_SuperPanelMoveY = G_MouseY
		Else
			G_SuperPanelMoving = False
		EndIf
	EndIf
	
	PanelIconScan(G_SuperPanel)
	If G_MouseIconOver = Null
		If G_Mouse1
			G_SuperPanelMoving = True
			G_SuperPanelMoveX = G_MouseX
			G_SuperPanelMoveY = G_MouseY
		EndIf
		
		If G_Mouse1Down And G_SuperPanelMoving
			PanelMove(G_SuperPanel, G_SuperPanel\EdgeLeft - G_SuperPanelMoveX + G_MouseX, G_SuperPanel\EdgeTop - G_SuperPanelMoveY + G_MouseY)
			G_SuperPanelMoveX = G_MouseX
			G_SuperPanelMoveY = G_MouseY
		EndIf
	Else
		If G_Mouse1
			If G_MouseIconOver\Status = C_IconStatusNormal
				PanelIconSelect(G_SuperPanel, G_MouseIconOver)
			EndIf
			
			If G_MouseIconOver = G_SuperIconLock
				SelectSetLock()
			ElseIf G_MouseIconOver = G_SuperIconUnlock
				SelectSetUnlock()
			ElseIf G_MouseIconOver = G_SuperIconNormal
				SelectSetNormal()
			ElseIf G_MouseIconOver = G_SuperIconRail
				SelectSetRail()
			ElseIf G_MouseIconOver = G_SuperIconFreeBolt
				SelectSetFree()
			ElseIf G_MouseIconOver = G_SuperIconFixedBolt
				SelectSetFixed()
			EndIf
		EndIf
		
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function EditControlsSuper()
	Local AnyControls		; Indicates if any scenario controls are active for return
	Local C.T_Control
	
	If C_FunctionTrace Then FunctionEntree("EditControlsSuper")
	
	AnyControls = False
	
	If G_Mouse1
		
		; See if any of the controls has been selected
		C = First T_Control
		While C <> Null
			If G_CursorX = C\PosX And G_CursorY = C\PosY
				Exit
			EndIf
			C = After C
		Wend
		
		If C <> Null
			AnyControls = True
		EndIf
		
		G_ControlSelected = C
	EndIf
	
	If G_Mouse1Down
		; Force GroundDraw() to ignore the cached image
		G_GroundImageCameraX = G_CameraX + 1
		
		C = G_ControlSelected	; Just to save typing !
		If C <> Null
			If C\PosX <> G_CursorX Or C\PosY <> G_CursorY
				C\PosX = G_CursorX
				C\PosY = G_CursorY
				
				Select C\ControlType
					Case C_ControlTypeWaterLevel
						If C\PosY > G_GroundLevel
							C\PosY = G_GroundLevel
						EndIf
						G_WaterLevel = C\PosY
						
						; Adjust the water depth control as well
						G_ControlWaterDepth\PosX = C\PosX
						If G_WaterDepth > G_WaterLevel
							G_WaterDepth = G_WaterLevel
							G_ControlWaterDepth\PosY = C\PosY
							
							; Recreate the terrain
							SetHeight()
						EndIf
						
					Case C_ControlTypeWaterDepth
						If C\PosY > G_WaterLevel
							C\PosY = G_WaterLevel
						EndIf
						G_WaterDepth = C\PosY
					
						; Adjust the water level control as well
						G_ControlWaterLevel\PosX = C\PosX
						
						; Recreate the terrain
						SetHeight()
						
					Case C_ControlTypeShoreLeft
						C\PosY = G_GroundLevel
						If C\PosX > G_ShoreRight
							C\PosX = G_ShoreRight
						EndIf
						G_ShoreLeft = C\PosX
						
						; Adjust right shore control as well
						G_ControlShoreRight\PosY = C\PosY
						
						; Recreate the terrain
						SetHeight()
						
					Case C_ControlTypeShoreRight
						C\PosY = G_GroundLevel
						If C\PosX < G_ShoreLeft
							C\PosX = G_ShoreLeft
						EndIf
						G_ShoreRight = C\PosX
						
						; Adjust left shore control as well
						G_ControlShoreLeft\PosY = C\PosY
						
						; Recreate the terrain
						SetHeight()
						
					Case C_ControlTypeWind
						C\PosY = 500
						G_WindSpeedStart = C\PosX / 10
					
					Case C_ControlTypeTrainStart
						If C\PosY < G_GroundLevel
							C\PosY = G_GroundLevel
						EndIf
						G_TrainStartX = C\PosX
						G_TrainStartY = C\PosY
						
						; Adjust the train length and stop controls as well
						G_ControlTrainLength\PosX = C\PosX - G_TrainLength
						G_ControlTrainLength\PosY = C\PosY
						G_ControlTrainStop\PosY = C\PosY
						
					Case C_ControlTypeTrainLength
						C\PosY = G_TrainStartY
						If C\PosX >= G_TrainStartX
							C\PosX = G_TrainStartX - 1
						EndIf
						G_TrainLength = G_TrainStartX - C\PosX
						
					Case C_ControlTypeTrainStop
						If C\PosY < G_GroundLevel
							C\PosY = G_GroundLevel
						EndIf
						G_TrainStopX = C\PosX
						G_TrainStartY = C\PosY
						
						; Adjust the train length and stop controls as well
						G_ControlTrainLength\PosY = C\PosY
						G_ControlTrainStart\PosY = C\PosY
						
					Default
				End Select
			EndIf
			
			AnyControls = True
		EndIf
	Else
		G_ControlSelected = Null
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return AnyControls
End Function

Function EditControlsNormal()
	Local MS1.T_MSegment, MS2.T_MSegment
	Local MB1.T_MBolt, MB2.T_MBolt
	
	If C_FunctionTrace Then FunctionEntree("EditControlsNormal")
	
	;ViewPanControls()
	
	If G_MousePanelOver <> Null
		; Mouse has moved off the panel - Reset
		If G_MouseIconOver <> Null
			G_MouseIconOver\HoverTime = 0
			G_MouseIconOver = Null
		EndIf
		G_MousePanelOver = Null
	EndIf
	
	If G_CursorX <> G_CursorOldX Or G_CursorY <> G_CursorOldY
		If G_Channel3 = 0
			;_Channel3 = PlaySound(G_Sound3)
		EndIf
		
		G_CursorOldX = G_CursorX
		G_CursorOldY = G_CursorY
	EndIf
	
	; New select method	
	If G_Mouse1
		; This generally initiates the action
		;
		; For BuildMultiple it may or may not be an intermediate step
		;
		; For Copy/Move/Stretch it completes the action
		;
		If G_EditTool = C_EditToolCopy Or G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch
			SelectComplete()
		ElseIf G_EditTool = C_EditToolRotate
			G_Point1X = G_CursorX
			G_Point1Y = G_CursorY
			G_EditStep = C_EditStepP2
		Else
			SelectMaybeClear()
			; If not extending the selection then clear any previous selection
			If KeyDown(C_Key_Left_Control) = False And KeyDown(C_Key_Right_Control) = False
				SelectClear()
			EndIf
		
			Select G_EditTool
				Case C_EditToolSelect
					G_Point1X = G_CursorX
					G_Point1Y = G_CursorY
					G_EditStep = C_EditStepP2
				Case C_EditToolBuild
					G_Point1X = G_CursorX
					G_Point1Y = G_CursorY
					G_EditStep = C_EditStepP2
				Case C_EditToolBuildMultiple
					MS1 = Last T_MSegment
					
					; Check for click on same point and terminate
					If MS1 <> Null And G_CursorX = G_Point2X And G_CursorY = G_Point2Y
						SelectComplete()
					Else
						MS2 = New T_MSegment
						MS2\SegmentType = C_SegmentTypeRegular
						MS2\Locked = False
						
						If MS1 = Null
							MS2\Bolt1 = New T_MBolt
							MS2\Bolt1\BoltType = C_BoltTypeRegular
							MS2\Bolt1\Locked = False
							G_Point1X = G_CursorX
							G_Point1Y = G_CursorY
						Else
							MS2\Bolt1 = MS1\Bolt2
							G_Point1X = MS1\Bolt2\OriginalX
							G_Point1Y = MS1\Bolt2\OriginalY
						EndIf
						
						MS2\Bolt2 = New T_MBolt
						MS2\Bolt2\BoltType = C_BoltTypeRegular
						MS2\Bolt2\Locked = False
						
						G_EditStep = C_EditStepP2
					EndIf
				Case C_EditToolDemolish
				Case C_EditToolMacroLine, C_EditToolMacroBeam, C_EditToolMacroArch, C_EditToolMacroCircle, C_EditToolMacroTower
					G_Point1X = G_CursorX
					G_Point1Y = G_CursorY
					G_EditStep = C_EditStepP2
				Default
			End Select
		EndIf
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
			Select G_EditTool
				Case C_EditToolSelect
					SelectUpdate()
				Case C_EditToolBuild
					MacroUpdateBuild()
				Case C_EditToolBuildMultiple
					MacroUpdateBuildMultiple()
				Case C_EditToolRotate
					SelectRotateUpdate()
				Case C_EditToolDemolish
				Case C_EditToolMacroLine
					MacroUpdateLine()
				Case C_EditToolMacroBeam
					MacroUpdateBeam()
				Case C_EditToolMacroArch
					MacroUpdateArch()
				Case C_EditToolMacroCircle
					MacroUpdateCircle()
				Case C_EditToolMacroTower
					MacroUpdateTower()
				Default
			End Select
		EndIf
	Else
		; This completes the action
		If G_EditStep = C_EditStepP2
			If G_EditTool = C_EditToolSelect
				SelectMaybeConfirm()
				G_EditStep = C_EditStepDone
			ElseIf G_EditTool = C_EditToolBuildMultiple
				MacroUpdateBuildMultiple()
			Else
				SelectComplete()
				;MacroPlace()
				;MacroClear()
				;G_EditStep = C_EditStepDone
			EndIf
		EndIf
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function EditControlsKeys()
	
	If C_FunctionTrace Then FunctionEntree("EditControlsKeys")
	
	; Super edit mode
	If KeyHit(C_Key_F8)
		G_EditModeSuper = Not G_EditModeSuper
	EndIf
	
	; Build
	If KeyHit(C_Key_A)
		G_EditTool = C_EditToolBuild
		PanelIconSelect(G_ToolPanel, G_IconBuild)
	EndIf
	
	; Select
	If KeyHit(C_Key_Z)
		G_EditTool = C_EditToolSelect
		PanelIconSelect(G_ToolPanel, G_IconSelect)
	EndIf
	
	; Copy
	If KeyHit(C_Key_C)
		SelectCopy()
		PanelIconSelect(G_ToolPanel, G_IconCopy)
	EndIf
	
	; Move
	If KeyHit(C_Key_V)
		SelectMove()
		PanelIconSelect(G_ToolPanel, G_IconMove)
	EndIf
	
	; Stretch
	If KeyHit(C_Key_B)
		SelectStretch()
		PanelIconSelect(G_ToolPanel, G_IconStretch)
	EndIf
	
	; Group
	If KeyHit(C_Key_G)
		SelectGroup()
	EndIf
	
	; Delete
	If KeyHit(C_Key_Delete)
		SelectDelete()
	EndIf
	
	; Flip horizontally
	If KeyHit(C_Key_X)
		SelectFlipX()
	EndIf
	
	; Flip vertically
	If KeyHit(C_Key_Y)
		SelectFlipY()
	EndIf
	
	; Rotate
	If KeyHit(C_Key_O)
		SelectRotate()
	EndIf

	; Replace material
	If KeyHit(C_Key_R)
		SelectReplace()
	EndIf
	
	; New bridge
	If KeyHit(C_Key_N)
		If BridgeClear(True)
			BridgeNew()
		EndIf
		; Immediately changes to Select tool.  Needs an indication that it worked but how ?
		G_EditTool = C_EditToolSelect
		PanelIconSelect(G_ToolPanel, G_IconSelect)
	EndIf
	
	; Macro place
	If KeyHit(C_Key_Enter)
		SelectComplete()
	EndIf
	
	; Material selector
	If KeyHit(C_Key_M)
		MaterialSelectNext()
	EndIf
	
	; Tool Panel toggle
	If KeyHit(C_Key_Tab)
		If G_ToolPanel\Status = C_PanelStatusHide
			G_ToolPanel\Status = C_PanelStatusShow
		Else
			G_ToolPanel\Status = C_PanelStatusHide
		EndIf
	EndIf
	
	; Prototype panel resizing controls
	If KeyHit(C_Key_Left_Bracket)
		If G_ToolPanelToolsWide > 1
			G_ToolPanelToolsWide = G_ToolPanelToolsWide - 1
			G_ToolPanel\MaxWidth = 1 + (1 + G_ToolPanelIconWidth + 1) * G_ToolPanelToolsWide + 1
			G_ToolPanel\MaxHeight = 1 + 20 + 1 + (1 + G_ToolPanelIconHeight + 1) * G_ToolPanelToolsHigh + 1

			PanelIconArrange(G_ToolPanel)
			PanelRender(G_ToolPanel)
		EndIf
		
		PanelVisibility(G_ToolPanel)
	EndIf
	
	If KeyHit(C_Key_Right_Bracket)
		If G_ToolPanelToolsWide < 12
			G_ToolPanelToolsWide = G_ToolPanelToolsWide + 1
			G_ToolPanel\MaxWidth = 1 + (1 + G_ToolPanelIconWidth + 1) * G_ToolPanelToolsWide + 1
			G_ToolPanel\MaxHeight = 1 + 20 + 1 + (1 + G_ToolPanelIconHeight + 1) * G_ToolPanelToolsHigh + 1

			PanelIconArrange(G_ToolPanel)
			PanelRender(G_ToolPanel)
		EndIf
		
		PanelVisibility(G_ToolPanel)
	EndIf
	
	; Grid toggle
	If KeyHit(C_Key_Period)
		G_GridDisplay = Not G_GridDisplay
	EndIf			

	; Stress graph toggle
	If KeyHit(C_Key_Comma)
		G_StressGraphDisplay = Not G_StressGraphDisplay
	EndIf
	
	; Broken segment display
	If KeyDown(C_Key_W)
		G_BreakDisplay = True
	Else
		G_BreakDisplay = False
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

; Move mouse to WX, WY (World coordinates)
Function EditMousePosition(WX, WY)
	Local SX, SY
	
	If C_FunctionTrace Then FunctionEntree("EditMousePosition")
	
	SX = G_ScreenCentreX + (WX - G_CameraX) * G_GridSize * G_ZoomFactor
	SY = G_ScreenCentreY - (WY - G_CameraY) * G_GridSize * G_ZoomFactor
	
	If SX < 0 Or SX > G_ScreenWidth
		SX = G_ScreenCentreX
	EndIf
	
	If SY < 0 Or SY > G_ScreenHeight
		SY = G_ScreenCentreY
	EndIf
	
	MoveMouse SX, SY
	
	G_MouseX = SX
	G_MouseY = SY
	
	TempX = 1.0 * (G_MouseX - G_ScreenCentreX) / G_GridSize / G_ZoomFactor + G_CameraX
	TempY = 1.0 * (G_ScreenCentreY - G_MouseY) / G_GridSize / G_ZoomFactor + G_CameraY
	
	G_CursorX = Int(TempX / G_GridSnap) * G_GridSnap
	G_CursorY = Int(TempY / G_GridSnap) * G_GridSnap
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectUpdate()
	Local MinX#, MaxX#, MinY#, MaxY#
	Local DX#, DY#, D#, MinBoltD#, MinSegmentD#
	Local BX#, BY#, B1X#, B1Y#, B2X#, B2Y#
	Local B.T_Bolt, ClosestB.T_Bolt
	Local S.T_Segment, ClosestS.T_Segment
	Local SelectGroupId
	
	If C_FunctionTrace Then FunctionEntree("SelectUpdate")
	
	;If KeyDown(C_Key_F1)
	;	Stop
	;EndIf
	
	If G_Point1X <= G_Point2X
		MinX = G_Point1X
		MaxX = G_Point2X
	Else
		MinX = G_Point2X
		MaxX = G_Point1X
	EndIf
	
	If G_Point1Y <= G_Point2Y
		MinY = G_Point1Y
		MaxY = G_Point2Y
	Else
		MinY = G_Point2Y
		MaxY = G_Point1Y
	EndIf
	
	SelectGroupId = -1
	
	; Select Bolt(s)
	If MinX = MaxX And MinY = MaxY
		; Point selection
		G_SelectModeType = C_SelectModeTypeCrossing

		; Use the actual mouse coordinates (ie. not snapped to the grid)
		MinX = G_CursorFreeX
		MinY = G_CursorFreeY
		
		; Find nearest Bolt
		ClosestB = Null
		MinBoltD = 0
		For B.T_Bolt = Each T_Bolt
			B\SelectedMaybe = False
			
			; Choose which coordinates to check
			If G_Mode = C_ModeTest
				BX = B\PX
				BY = B\PY
			Else
				BX = B\OriginalX
				BY = B\OriginalY
			EndIf
			
			; Check for exact hit on snapped cursor
			If G_Point1X = BX And G_Point1Y = BY
				ClosestB = B
				MinBoltD = 0
			Else
				; - not exact so calculate distance
				DX = Abs(MinX - BX)
				If DX < MinBoltD Or ClosestB = Null
					DY = Abs(MinY - BY)
					If DY < MinBoltD Or ClosestB = Null
				    	D = Sqr(DX ^ 2 + DY ^ 2)
						If D < MinBoltD Or ClosestB = Null
							ClosestB = B
							MinBoltD = D
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		
		; Find nearest Segment
		ClosestS = Null
		MinSegmentD = 0
		For S = Each T_Segment
			S\SelectedMaybe = False
			
			; Choose which coordinates to check
			If G_Mode = C_ModeTest
				B1X = S\Bolt1\PX
				B1Y = S\Bolt1\PY
				B2X = S\Bolt2\PX
				B2Y = S\Bolt2\PY
			Else
				B1X = S\Bolt1\OriginalX
				B1Y = S\Bolt1\OriginalY
				B2X = S\Bolt2\OriginalX
				B2Y = S\Bolt2\OriginalY
			EndIf
			
			If 2=1
				; Midpoint of Segment.  Original method which was fairly efficient but not totally usable
				DX = Abs(MinX - (B1X + B2X) / 2)
				If DX < MinSegmentD Or ClosestS = Null
					DY = Abs(MinY - ( B1Y + B2Y) / 2)
					If DY < MinSegmentD Or ClosestS = Null
						D = Sqr(DX ^ 2 + DY ^ 2)
						If D < MinSegmentD Or ClosestS = Null
							ClosestS = S
							MinSegmentD = D
						EndIf
					EndIf
				EndIf
			Else
				; Perpendicular distance to line - Less efficient but allows more intuitive operation
				D = DistPointToSegment(MinX, MinY, B1X, B1Y, B2X, B2Y)
				If D < MinSegmentD Or ClosestS = Null
					ClosestS = S
					MinSegmentD = D
				EndIf
			EndIf
		Next
		
		If MinBoltD < 100 Or MinSegmentD < 100
			; Choose whether the nearest Bolt or Segment should be selected
			If ClosestB <> Null
				If ClosestS <> Null
					If MinBoltD < 0.75 Or MinBoltD < 1.5 * MinSegmentD
						ClosestB\SelectedMaybe = (ClosestB\Locked = False Or G_EditModeSuper)
						ClosestS = Null
						SelectGroupId = ClosestB\GroupId
					Else
						ClosestS\SelectedMaybe = (ClosestS\Locked = False Or G_EditModeSuper)
						SelectGroupId = ClosestS\GroupId
					EndIf
				Else
					ClosestB\SelectedMaybe = (ClosestB\Locked = False Or G_EditModeSuper)
					SelectGroupId = ClosestB\GroupId
				EndIf
			ElseIf ClosestS <> Null
				ClosestS\SelectedMaybe = (ClosestS\Locked = False Or G_EditModeSuper)
				SelectGroupId = ClosestS\GroupId
			EndIf
		EndIf
	Else
		; Box selection
		For B = Each T_Bolt
			; Choose which coordinates to check
			If G_Mode = C_ModeTest
				BX = B\PX
				BY = B\PY
			Else
				BX = B\OriginalX
				BY = B\OriginalY
			EndIf
			
			B\SelectedMaybe = (BX >= MinX And BX <= MaxX And BY >= MinY And BY <= MaxY)
		Next
	EndIf
	
	; Select Segment(s)
	If ClosestS = Null
		For S = Each T_Segment
			;If S\SegmentType <> C_SegmentTypeFixed And S\SegmentType <> C_SegmentTypeFixedRail And S\SegmentType <> C_SegmentTypeRail
				If G_SelectModeType = C_SelectModeTypeCrossing
					S\SelectedMaybe = (S\Bolt1\SelectedMaybe Or S\Bolt2\SelectedMaybe) And (S\Locked = False Or G_EditModeSuper)
				Else
					S\SelectedMaybe = (S\Bolt1\SelectedMaybe And S\Bolt2\SelectedMaybe) And (S\Locked = False Or G_EditModeSuper)
				EndIf
			;EndIf
		Next
	Else
		If MinSegmentD < 100
			ClosestS\Bolt1\SelectedMaybe = True
			ClosestS\Bolt2\SelectedMaybe = True
		EndIf
	EndIf
	
	; Check for group selection
	If  (KeyDown(C_Key_Left_Alt) Or KeyDown(C_Key_Right_Alt)) And SelectGroupId <> -1
		For S = Each T_Segment
			If S\GroupId = SelectGroupId
				S\SelectedMaybe = (S\Locked = False Or G_EditModeSuper)
			EndIf
		Next
		
		For B = Each T_Bolt
			If B\GroupId = SelectGroupId
				B\SelectedMaybe = (B\Locked = False Or G_EditModeSuper)
			EndIf
		Next
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectClear()
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectClear")
	
	For B.T_Bolt = Each T_Bolt
		B\Selected = False
		B\SelectedMaybe = False
	Next
	
	For S.T_Segment = Each T_Segment
		S\Selected = False
		S\SelectedMaybe = False
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectMaybeClear()
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectMaybeClear")
	
	For B.T_Bolt = Each T_Bolt
		B\SelectedMaybe = False
	Next
	
	For S.T_Segment = Each T_Segment
		S\SelectedMaybe = False
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectMaybeConfirm()
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectMaybeConfirm")
	
	For B.T_Bolt = Each T_Bolt
		B\Selected = B\Selected Or B\SelectedMaybe
	Next
	
	For S.T_Segment = Each T_Segment
		S\Selected = S\Selected Or S\SelectedMaybe
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectCopy()
	Local B.T_Bolt
	Local S.T_Segment
	
	Local MB.T_MBolt
	Local MS.T_MSegment

	If C_FunctionTrace Then FunctionEntree("SelectCopy")
	
	G_EditTool = C_EditToolCopy
	
	G_RelativeX = G_CursorX
	G_RelativeY = G_CursorY
	
	MacroClear()
	
	; Clear the Trace flag on all bolts
	For B = Each T_Bolt
		B\Trace = False
	Next
	
	; Set the Trace flag on each bolt to which a selected segment is attached
	For S = Each T_Segment
		If S\Selected
			S\Bolt1\Trace = True
			S\Bolt2\Trace = True
			S\Shadow = True
		EndIf
	Next
	
	; Create the macro bolts
	For B = Each T_Bolt
		If B\Trace
			MB = New T_MBolt
			MB\BoltType = B\BoltType
			MB\Locked = B\Locked
			MB\OriginalX = B\OriginalX
			MB\OriginalY = B\OriginalY
			MB\Movable = True
			B\MacroBolt = MB
		EndIf
	Next
	
	; Create the macro segments and attach to the macro bolts
	For S = Each T_Segment
		If S\Selected
			MS = New T_MSegment
			MS\SegmentType = S\SegmentType
			MS\Locked = S\Locked
			MS\Material = S\Material
			MS\Bolt1 = S\Bolt1\MacroBolt
			MS\Bolt2 = S\Bolt2\MacroBolt
		EndIf
	Next
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectMove()
	
	If C_FunctionTrace Then FunctionEntree("SelectMove")
	
	SelectCopy()
	
	G_EditTool = C_EditToolMove
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectStretch()
	Local B.T_Bolt
	Local S.T_Segment
	
	Local MB.T_MBolt
	Local MS.T_MSegment

	If C_FunctionTrace Then FunctionEntree("SelectStretch")
	
	G_EditTool = C_EditToolStretch
	
	G_RelativeX = G_CursorX
	G_RelativeY = G_CursorY
	
	MacroClear()
	
	; Clear the Trace flag on all bolts
	For B = Each T_Bolt
		B\Trace = False
	Next
	
	; Set the Trace flag on each bolt to which a selected segment is attached
	For S = Each T_Segment
		If S\Selected
			S\Bolt1\Trace = True
			S\Bolt2\Trace = True
			S\Shadow = True
		EndIf
	Next
	
	; Create the macro bolts
	For B = Each T_Bolt
		If B\Trace
						
			B\MacroBolt = MB
			MB = New T_MBolt
			MB\BoltType = B\BoltType
			MB\Locked = B\Locked
			MB\OriginalX = B\OriginalX
			MB\OriginalY = B\OriginalY
			MB\Movable = B\Selected
			B\MacroBolt = MB
		EndIf
	Next
	
	; Create the macro segments and attach to the macro bolts
	For S = Each T_Segment
		If S\Selected
			MS = New T_MSegment
			MS\SegmentType = S\SegmentType
			MS\Locked = S\Locked
			MS\Material = S\Material
			MS\Bolt1 = S\Bolt1\MacroBolt
			MS\Bolt2 = S\Bolt2\MacroBolt
		EndIf
	Next
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectComplete()
	
	If C_FunctionTrace Then FunctionEntree("SelectComplete")
	
	If G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch Or G_EditTool = C_EditToolRotate
		SelectDelete()
	EndIf
	MacroPlace()
	MacroClear()
	
	If G_EditTool <> C_EditToolBuild And G_EditTool <> C_EditToolBuildMultiple And G_EditTool <> C_EditToolMacroLine And G_EditTool <> C_EditToolMacroBeam And G_EditTool <> C_EditToolMacroArch And G_EditTool <> C_EditToolMacroCircle And G_EditTool <> C_EditToolMacroTower
		; Save this code ? If G_EditTool = C_EditToolGroup Or G_EditTool = C_EditToolReplace Or G_EditTool = C_EditToolFlipX Or G_EditTool = C_EditToolFlipY Or G_EditTool = C_EditToolRotate
		; Immediately changes to Select tool.  Needs an indication that it worked but how ?
		G_EditTool = C_EditToolSelect
		PanelIconSelect(G_ToolPanel, G_IconSelect)
	EndIf
	
	G_Point1X = G_CursorX
	G_Point1Y = G_CursorY
	G_Point2X = G_CursorX
	G_Point2Y = G_CursorY
	
	G_EditStep = C_EditStepZero
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectDelete()
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectDelete")
	
	; Clear the Trace flag on all bolts
	For B = Each T_Bolt
		B\Trace = False
	Next
	
	; Run through the segments and:
	;   - Delete those that are selected
	;   - Set the trace flag on those bolts connected to segments that are not to be deleted
	;
	For S = Each T_Segment
		If S\Selected
			Delete S
			G_FileChanged = True
		Else
			S\Bolt1\Trace = True
			S\Bolt2\Trace = True
		EndIf
	Next
	
	; Delete the bolts that have not been traced 
	For B = Each T_Bolt
		If Not B\Trace
			Delete B
			G_FileChanged = True
		EndIf
	Next
	
	;SelectUpdate()
	
	G_EditStep = C_EditStepZero
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectGroup()
	Local S.T_Segment
	Local B.T_Bolt
	
	If C_FunctionTrace Then FunctionEntree("SelectGroup")
	
	G_GroupId = G_GroupId + 1
	For S = Each T_Segment
		If S\Selected
			S\GroupId = G_GroupId
			G_FileChanged = True
		EndIf
	Next
	
	For B = Each T_Bolt
		If B\Selected
			B\GroupId = G_GroupId
			G_FileChanged = True
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectReplace()
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectReplace")
	
	For S = Each T_Segment
		If S\Selected And S\Material <> G_MaterialPrimary
			; Check material and budget limits
			If G_MaterialPrimary\QtyAvailable <> -1 And G_MaterialPrimary\QtyUsed + S\Length > G_MaterialPrimary\QtyAvailable
				; Not enough material available
				G_SoundStatus = C_SoundStatusError
			ElseIf G_MaterialCostUsed + S\Length * (G_MaterialPrimary\CostPerUnit - S\Material\CostPerUnit) > G_MaterialBudget
				; Not enough money
				G_SoundStatus = C_SoundStatusError
			Else
				G_MaterialCostUsed = G_MaterialCostUsed + S\Length * (G_MaterialPrimary\CostPerUnit - S\Material\CostPerUnit)
				S\Material\QtyUsed = S\Material\QtyUsed - S\Length
				S\Material\CostUsed = S\Material\QtyUsed * S\Material\CostPerUnit
				
				S\Material = G_MaterialPrimary
				
				S\Material\QtyUsed = S\Material\QtyUsed + S\Length
				S\Material\CostUsed = S\Material\QtyUsed * S\Material\CostPerUnit
				
				G_FileChanged = True
				
				If G_SoundStatus = C_SoundStatusNone
					G_SoundStatus = C_SoundStatusDone
				EndIf
			EndIf
		EndIf
	Next

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectFlipX()
	Local MinX#, MinY#
	Local MB.T_MBolt
	Local Initial
	
	If C_FunctionTrace Then FunctionEntree("SelectFlipX")
	
	; Find the left and right edges
	Initial = True
	For MB = Each T_MBolt
		If Initial
			MinX = MB\OriginalX
			MaxX = MB\OriginalX
			Initial = False
		Else
			If MB\OriginalX < MinX Then MinX = MB\OriginalX
			If MB\OriginalX > MaxX Then MaxX = MB\OriginalX
		EndIf
	Next
	
	If Initial
		; No bolts to flip
		Goto SelectFlipXReturn
	EndIf
	
	; Flip the coordinates
	For MB = Each T_MBolt
		MB\OriginalX = MaxX - MB\OriginalX + MinX
	Next
	
.SelectFlipXReturn
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectFlipY()
	Local MinX#, MinY#
	Local MB.T_MBolt
	Local Initial
	
	If C_FunctionTrace Then FunctionEntree("SelectFlipY")
	
	; Find the top and bottom edges
	Initial = True
	For MB = Each T_MBolt
		If Initial
			MinY = MB\OriginalY
			MaxY = MB\OriginalY
			Initial = False
		Else
			If MB\OriginalY < MinY Then MinY = MB\OriginalY
			If MB\OriginalY > MaxY Then MaxY = MB\OriginalY
		EndIf
	Next
	
	If Initial
		; No bolts to flip
		Goto SelectFlipYReturn
	EndIf
	
	; Flip the coordinates
	For MB = Each T_MBolt
		MB\OriginalY = MaxY - MB\OriginalY + MinY
	Next
	
.SelectFlipYReturn
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectRotate()
	
	If C_FunctionTrace Then FunctionEntree("SelectRotate")
	
	; SelectCopy() is used to setup the macro structure
	SelectCopy()
	
	G_EditTool = C_EditToolRotate
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectRotateUpdate()
	Local B.T_Bolt
	Local MB.T_MBolt
	Local X#, Y#
	
	If C_FunctionTrace Then FunctionEntree("SelectRotateUpdate")
	
	Angle = ATan2(G_Point2Y - G_Point1Y, G_Point2X - G_Point1X)
	
	For B = Each T_Bolt
		If B\Selected
			MB = B\MacroBolt
			
			X = B\OriginalX - G_Point1X
			Y = B\OriginalY - G_Point1Y
			
			MB\OriginalX = G_Point1X + X * Cos(Angle) - Y * Sin(Angle)
			MB\OriginalY = G_Point1Y + X * Sin(Angle) + Y * Cos(Angle)
		EndIf
	Next
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetLock()
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectSetLock")
	
	For S = Each T_Segment
		If S\Selected
			S\Locked = True
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetUnlock()
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectSetUnlock")
	
	For S = Each T_Segment
		If S\Selected
			S\Locked = False
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetNormal()
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectSetNormal")
	
	For S = Each T_Segment
		If S\Selected
			S\SegmentType = C_SegmentTypeRegular
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetRail()
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SelectSetRail")
	
	For S = Each T_Segment
		If S\Selected
			S\SegmentType = C_SegmentTypeRail
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetFree()
	Local B.T_Bolt
	
	If C_FunctionTrace Then FunctionEntree("SelectSetFree")
	
	For B = Each T_Bolt
		If B\Selected
			B\BoltType = C_BoltTypeRegular
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SelectSetFixed()
	Local B.T_Bolt
	
	If C_FunctionTrace Then FunctionEntree("SelectSetFixed")
	
	For B = Each T_Bolt
		If B\Selected
			B\BoltType = C_BoltTypeFixed
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroPlace()
	Local S.T_Segment
	Local MS.T_MSegment
	
	Local B.T_Bolt
	Local MB.T_MBolt
	
	Local Material.T_Material
	
	Local NewX#, NewY#
	Local DX#, DY#, D#
	
	Local OffsetX#, OffsetY#
	
	If C_FunctionTrace Then FunctionEntree("MacroPlace")
	
	G_ConsistencyMergeBolts = 0
	G_ConsistencyMergeSegments = 0
	
	SelectClear()
	
	; Increment the Group Id
	G_GroupId = G_GroupId + 1
	
	If G_EditTool = C_EditToolCopy Or G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch
		OffsetX = G_CursorX - G_RelativeX
		OffsetY = G_CursorY - G_RelativeY
	Else
		OffsetX = 0
		OffsetY = 0
	EndIf
	
	; Clear the Trace flag on all bolts
	For MB = Each T_MBolt
		MB\Trace = False
	Next
	
	; Run through the segments and flag the bolts that will be added
	;
	For MS = Each T_MSegment
		If Not (MS\LengthError Or MS\BudgetError)
			MS\Bolt1\Trace = True
			MS\Bolt2\Trace = True
		EndIf
	Next
	
	; Check and if necessary place the bolts
	For MB = Each T_MBolt
		If MB\Trace
			If MB\Movable
				NewX = MB\OriginalX + OffsetX
				NewY = MB\OriginalY + OffsetY
			Else
				NewX = MB\OriginalX
				NewY = MB\OriginalY
			EndIf
			
			; See if there is already a bolt in the right place
			B = First T_Bolt
			While B <> Null
				DX = Abs(B\OriginalX - NewX)
				If DX < 0.75
					DY = Abs(B\OriginalY - NewY)
					If DY < 0.75
						D = Sqr(DX * DX + DY * DY)
						If D < 0.75
							Exit
						EndIf
					EndIf
				EndIf
				B = After B
			Wend
			
			If B = Null
				B = New T_Bolt
				B\BoltType = MB\BoltType
				B\Locked = MB\Locked
				B\OriginalX = NewX
				B\OriginalY = NewY
				
				G_FileChanged = True
			Else
				G_ConsistencyMergeBolts = G_ConsistencyMergeBolts + 1
			EndIf
			
			B\GroupId = G_GroupId
			B\Selected = True
			MB\RealBolt = B
		EndIf
	Next
	
	; Check and if necessary place the segments
	For MS = Each T_MSegment
		If Not (MS\LengthError Or MS\BudgetError)
		
			; Determine the material
			If MS\Material <> Null
				Material = MS\Material
			Else
				If MS\MaterialChoice = 2
					Material = G_MaterialSecondary
				Else
					Material = G_MaterialPrimary
				EndIf
			EndIf
			
			; Check for already existing segment
			S = First T_Segment
			While S <> Null
				If (MS\Bolt1\RealBolt = S\Bolt1 And MS\Bolt2\RealBolt = S\Bolt2) Or (MS\Bolt1\RealBolt = S\Bolt2 And MS\Bolt2\RealBolt = S\Bolt1)
					; Coincident segment
					G_ConsistencyMergeSegments = G_ConsistencyMergeSegments + 1
					
					; Existing segment is always chosen as the survivor but it inherits the new material if it is stronger
					If Material\Strength <= S\Material\Strength Or S\Locked
						Material = S\Material
					EndIf
					
					Exit
				EndIf
						
				S = After S
			Wend
			
			If S = Null
				S = New T_Segment
				S\GroupId = G_GroupId
				S\SegmentType = MS\SegmentType
				S\Locked = MS\Locked
				S\Bolt1 = MS\Bolt1\RealBolt
				S\Bolt2 = MS\Bolt2\RealBolt
				S\Material = Material
				S\Length = DistBoltToBolt(S\Bolt1, S\Bolt2)
				S\Selected = True
				G_FileChanged = True
			Else
				If Not S\Locked
					S\GroupId = G_GroupId
					S\SegmentType = MS\SegmentType
					S\Bolt1 = MS\Bolt1\RealBolt
					S\Bolt2 = MS\Bolt2\RealBolt
					S\Material = Material
					S\Selected = True
					G_FileChanged = True
				EndIf
			EndIf
		EndIf
	Next
	
	; Reset the Shadow flag on segments
	For S = Each T_Segment
		S\Shadow = False
	Next
	
	If G_SoundStatus = C_SoundStatusNone
		G_SoundStatus = C_SoundStatusWarn
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroDraw()
	Local X1#, Y1#, X2#, Y2#
	Local C#
	Local MS.T_MSegment
	Local MB.T_MBolt
	
	Local OffsetX#, OffsetY#
	Local NewX#, NewY#
	
	If C_FunctionTrace Then FunctionEntree("MacroDraw")
	
	If G_EditTool = C_EditToolCopy Or G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch
		OffsetX = G_CursorX - G_RelativeX
		OffsetY = G_CursorY - G_RelativeY
	Else
		OffsetX = 0
		OffsetY = 0
	EndIf
	
	For MS.T_MSegment = Each T_MSegment
		If MS\Bolt1\Movable
			X1 = MS\Bolt1\OriginalX + OffsetX
			Y1 = MS\Bolt1\OriginalY + OffsetY
		Else
			X1 = MS\Bolt1\OriginalX
			Y1 = MS\Bolt1\OriginalY
		EndIf
		
		If MS\Bolt2\Movable
			X2 = MS\Bolt2\OriginalX + OffsetX
			Y2 = MS\Bolt2\OriginalY + OffsetY
		Else
			X2 = MS\Bolt2\OriginalX
			Y2 = MS\Bolt2\OriginalY
		EndIf
		
		If MS\LengthError
			Color 255, 100, 100
		ElseIf MS\BudgetError
			Color 255, 0, 0
;			Color 150, 255, 150
		Else
			Color 100, 255, 100
		EndIf
		
		S1X = G_ScreenCentreX + (X1 - G_CameraX) * G_GridSize * G_ZoomFactor
		S1Y = G_ScreenCentreY - (Y1 - G_CameraY) * G_GridSize * G_ZoomFactor
		
		S2X = G_ScreenCentreX + (X2 - G_CameraX) * G_GridSize * G_ZoomFactor
		S2Y = G_ScreenCentreY - (Y2 - G_CameraY) * G_GridSize * G_ZoomFactor
		
		Line S1X, S1Y, S2X, S2Y
	Next
	
	For MB.T_MBolt = Each T_MBolt
		If MB\Movable
			X1 = MB\OriginalX + OffsetX
			Y1 = MB\OriginalY + OffsetY
		Else
			X1 = MB\OriginalX
			Y1 = MB\OriginalY
		EndIf
		
		Color 100, 255, 255
		
		S1X = G_ScreenCentreX + (X1 - G_CameraX) * G_GridSize * G_ZoomFactor
		S1Y = G_ScreenCentreY - (Y1 - G_CameraY) * G_GridSize * G_ZoomFactor
		
		Oval S1X - 2, S1Y - 2, 5, 5, False
	Next

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroClear()
	Local MB.T_MBolt
	Local MS.T_MSegment
	
	If C_FunctionTrace Then FunctionEntree("MacroClear")
	
	; Clean up unused segments
	For MS = Each T_MSegment
		Delete MS
	Next
	
	; Clean up unused bolts
	For MB.T_MBolt = Each T_MBolt
		Delete MB
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateBuild()
	Local MB1.T_MBolt, MB2.T_MBolt
	Local MS.T_MSegment
	Local DistX#, DistY#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateBuild")
	
	MacroClear()
	
	MB1 = New T_MBolt
	MB1\BoltType = C_BoltTypeRegular
	MB1\Locked = False
	MB1\OriginalX = G_Point1X
	MB1\OriginalY = G_Point1Y
	
	MB2 = New T_MBolt
	MB2\BoltType = C_BoltTypeRegular
	MB2\Locked = False
	MB2\OriginalX = G_Point2X
	MB2\OriginalY = G_Point2Y
	
	MS = New T_MSegment
	MS\SegmentType = C_SegmentTypeRegular
	Ms\Locked = False
	MS\Bolt1 = MB1
	MS\Bolt2 = MB2
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateBuildMultiple()
	Local MB1.T_MBolt, MB2.T_MBolt
	Local MS.T_MSegment
	Local DistX#, DistY#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateBuildMultiple")
	
	;MacroClear()
	
	MS = Last T_MSegment
	
	If MS <> Null
		MB1 = MS\Bolt1
		MB1\OriginalX = G_Point1X
		MB1\OriginalY = G_Point1Y
		
		MB2 = MS\Bolt2
		MB2\OriginalX = G_Point2X
		MB2\OriginalY = G_Point2Y
		
		MS\Length = Dist(G_Poiint1X, G_Point1Y, G_Point2X, G_Point2Y)
	Else
		;Stop
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateLine()
	Local MS.T_MSegment
	Local MB.T_MBolt
	
	Local Dist#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateLine")
	
	MacroClear()
	
	If G_Point1X <> G_Point2X Or G_Point1Y <> G_Point2Y
		Dist = Sqr((G_Point1X - G_Point2X)^2 + (G_Point1Y - G_Point2Y)^2)
		N = Ceil(Dist / C_SegmentLengthNormal)
		
		MB = New T_MBolt
		MB\OriginalX = G_Point1X
		MB\OriginalY = G_Point1Y
		
		For i = 1 To N
			MS = New T_MSegment
			MS\SegmentType = C_SegmentTypeRegular
			
			MS\Bolt1 = MB
			
			MB = New T_MBolt
			MB\OriginalX = G_Point1X + (G_Point2X - G_Point1X) * i / N
			MB\OriginalY = G_Point1Y + (G_Point2Y - G_Point1Y) * i / N
			
			MS\Bolt2 = MB
		Next
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateBeam()
	Local S.T_MSegment
	
	; Bolts
	Local O1.T_MBolt, O2.T_MBolt, O3.T_MBolt
	Local N1.T_MBolt, N2.T_MBolt, N3.T_MBolt
	
	Local Dist#

	If C_FunctionTrace Then FunctionEntree("MacroUpdateBeam")
	
	MacroClear()
	
	If G_Point1X <> G_Point2X Or G_Point1Y <> G_Point2Y
		Dist = Sqr((G_Point1X - G_Point2X)^2 + (G_Point1Y - G_Point2Y)^2)
		N = Ceil(Dist / C_SegmentLengthNormal)

		Width = Dist / 16
		If Width > C_SegmentLengthNormal
			Width = C_SegmentLengthNormal
		EndIf
		
		;If Width < C_SegmentLengthMinimum
		;	Width = C_SegmentLengthMinimum
		;EndIf
		
		O1 = New T_MBolt
		O1\OriginalX = G_Point1X
		O1\OriginalY = G_Point1Y
		
		For i = 1 To N
			
			; Create the bolts
			N1 = New T_MBolt
			N1\OriginalX = G_Point1X + (G_Point2X - G_Point1X) * i / N
			N1\OriginalY = G_Point1Y + (G_Point2Y - G_Point1Y) * i / N
			
			Angle = ATan2(N1\OriginalY - O1\OriginalY, N1\OriginalX - O1\OriginalX)
			
			N2 = New T_MBolt
			N2\OriginalX = (O1\OriginalX + N1\OriginalX) / 2 - Width / 2 * Cos(Angle + 90)
			N2\OriginalY = (O1\OriginalY + N1\OriginalY) / 2 - Width / 2 * Sin(Angle + 90)
			
			N3 = New T_MBolt
			N3\OriginalX = (O1\OriginalX + N1\OriginalX) / 2 - Width / 2 * Cos(Angle - 90)
			N3\OriginalY = (O1\OriginalY + N1\OriginalY) / 2 - Width / 2 * Sin(Angle - 90)
			
			; Determine materials
			If i = 1
				M1 = 1
			Else
				M1 = 2
			EndIf
			
			If i = N
				M2 = 1
			Else
				M2 = 2
			EndIf
			
			M3 = 1
			
			; Create the segments
			;S = NewMSegment(O1, N1, M3)
			S = NewMSegment(O1, N2, M1) 
			S = NewMSegment(O1, N3, M1)
			S = NewMSegment(N1, N2, M2)
			S = NewMSegment(N1, N3, M2)
			S = NewMSegment(N2, N3, M3)
			
			If i <> 1
				S = NewMSegment(O2, N2, M3)
				S = NewMSegment(O3, N3, M3)
			EndIf
			
			O1 = N1
			O2 = N2
			O3 = N3
		Next
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function NewMSegment.T_MSegment(B1.T_MBolt, B2.T_MBolt, M)
	Local S.T_MSegment
	
	S = New T_MSegment
	S\SegmentType = C_SegmentTypeRegular
	S\Bolt1 = B1
	S\Bolt2 = B2
	S\MaterialChoice = M
	
	Return S
End Function

Function MacroUpdateArchX()
	;Local MB1.T_MBolt, MB2.T_MBolt
	Local MS1.T_MSegment, MS2.T_MSegment, MS3.T_MSegment, MS4.T_MSegment, MS5.T_MSegment
	
	Local X, Y#
	Local X1, X2, XStep
	Local nSteps = 20
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateArch")
	
	MacroClear()
		
	If G_Point1X <> G_Point2X
		; There is some width so make the arch
		
		MidX = (G_Point1X + G_Point2X) / 2
		Scale# =  (G_Point2Y - G_Point1Y) / (MidX - G_Point2X) ^ 2
		
		If G_Point1X < G_Point2X
			X1 = G_Point1X
			X2 = G_Point2X
		Else
			X1 = G_Point2X
			X2 = G_Point1X
		EndIf
		
		MidX = (X1 + X2) / 2
		
		X = X1
		
		XStep = (X2 - X1) / nSteps
		If XStep < 1
			XStep = 1
		ElseIf XStep > 64
			XStep = 64
		EndIf
		
		MS2 = Null
	
		While True
			Y1 = G_Point2Y - (MidX - X) ^ 2 * Scale
			Y2 = Y1 + (X2 - X1) / nSteps * (0.25 + 1.5 * Abs(X - MidX - 0.0) / (X2 - X1))
			
			If MS2 = Null
				; Left edge - Just create a vertical segment
				MS1 = New T_MSegment
				MS1\SegmentType = C_SegmentTypeRegular
				
				MS1\Bolt1 = New T_MBolt
				MS1\Bolt1\OriginalX = X
				MS1\Bolt1\OriginalY = Y1
				MS1\Bolt1\Trace = True
				
				MS1\Bolt2 = New T_MBolt
				MS1\Bolt2\OriginalX = X
				MS1\Bolt2\OriginalY = Y2
				MS1\Bolt2\Trace = True
				
				MS2 = MS1
			Else
				; Create a vertical segment and link to previous vertical
				MS1 = New T_MSegment
				MS1\SegmentType = C_SegmentTypeRegular
				
				MS1\Bolt1 = New T_MBolt
				MS1\Bolt1\OriginalX = X
				MS1\Bolt1\OriginalY = Y1
				MS1\Bolt1\Trace = True
				
				MS1\Bolt2 = New T_MBolt
				MS1\Bolt2\OriginalX = X
				MS1\Bolt2\OriginalY = Y2
				MS1\Bolt2\Trace = True
				
				; Link back
				MS3 = New T_MSegment
				MS3\SegmentType = C_SegmentTypeRegular
				
				MS3\Bolt1 = MS1\Bolt1
				MS3\Bolt2 = MS2\Bolt1
				
				MS4 = New T_MSegment
				MS4\SegmentType = C_SegmentTypeRegular
				MS4\Bolt1 = MS1\Bolt2
				MS4\Bolt2 = MS2\Bolt2
				
				D1# = (MS1\Bolt1\OriginalX - MS2\Bolt2\OriginalX) ^ 2 + (MS1\Bolt1\OriginalY - MS2\Bolt2\OriginalY) ^ 2
				D2# = (MS1\Bolt2\OriginalX - MS2\Bolt1\OriginalX) ^ 2 + (MS1\Bolt2\OriginalY - MS2\Bolt1\OriginalY) ^ 2
				
				MS5 = New T_MSegment
				MS5\SegmentType = C_SegmentTypeRegular
				If D1 <= D2
					MS5\Bolt1 = MS1\Bolt1
					MS5\Bolt2 = MS2\Bolt2
				Else
					MS5\Bolt1 = MS1\Bolt2
					MS5\Bolt2 = MS2\Bolt1
				EndIf
				
				MS2 = MS1
			EndIf
			
			If X = X2
				Exit
			EndIf
			
			X = X + XStep
			If X > X2
				X = X2
			EndIf
		Wend
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateArch()
	Local NewMB1.T_MBolt, NewMB2.T_MBolt
	Local OldMB1.T_MBolt, OldMB2.T_MBolt
	Local MidMB1.T_MBolt, MidMB2.T_MBolt
	Local MS.T_MSegment
	
	Local MidX
	Local X, Y#
	
	Local X1, X2, XStep
	Local Y1, Y2
	Local nSteps = 20
	Local Scale#
	
	Local D1#, D2#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateArch")
	
	MacroClear()
		
	If G_Point1X <> G_Point2X
		; There is some width so make the arch
		
		MidX = (G_Point1X + G_Point2X) / 2
		
		If G_Point1X < G_Point2X
			X1 = G_Point1X
			X2 = G_Point2X
		Else
			X1 = G_Point2X
			X2 = G_Point1X
		EndIf
		
		MidX = (X1 + X2) / 2
		
		XStep = (X2 - X1) / nSteps
		If XStep < 1
			XStep = 1
		ElseIf XStep > 64
			XStep = 64
		EndIf
		
		; Step from left to middle
		X = X1
		Scale# =  (G_Point2Y - G_Point1Y) / (MidX - X) ^ 2
		While True
			Y1 = G_Point2Y - (MidX - X) ^ 2 * Scale
			Y2 = Y1 + (X2 - X1) / nSteps * (0.25 + 1.5 * Abs(X - MidX) / (X2 - X1))
			
			If Y2 = Y1 Then Y2 = Y1 + Sgn(G_Point2Y - G_Point1Y) 
			
			NewMB1 = New T_MBolt
			NewMB1\OriginalX = X
			NewMB1\OriginalY = Y1
			
			NewMB2 = New T_MBolt
			NewMB2\OriginalX = X
			NewMB2\OriginalY = Y2
			
			MS = New T_MSegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = NewMB1
			MS\Bolt2 = NewMB2
			MS\MaterialChoice = 1
			
			If X > X1
				; Not left edge - Create the links back
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NewMB1
				MS\Bolt2 = OldMB1
				MS\MaterialChoice = 1
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NewMB2
				MS\Bolt2 = OldMB2
				MS\MaterialChoice = 1
				
				D1 = (NewMB1\OriginalX - OldMB2\OriginalX) ^ 2 + (NewMB1\OriginalY - OldMB2\OriginalY) ^ 2
				D2 = (NewMB2\OriginalX - OldMB1\OriginalX) ^ 2 + (NewMB2\OriginalY - OldMB1\OriginalY) ^ 2
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				If D1 <= D2
					MS\Bolt1 = NewMB1
					MS\Bolt2 = OldMB2
				Else
					MS\Bolt1 = NewMB2
					MS\Bolt2 = OldMB1
				EndIf
				MS\MaterialChoice = 2
				
			EndIf
			
			OldMB1 = NewMB1
			OldMB2 = NewMB2
			
			If X + XStep <= MidX
				X = X + XStep
			ElseIf X = MidX
				MidMB1 = NewMB1
				MidMB2 = NewMB2
				Exit
			ElseIf X + XStep / 2 > MidX
				MidMB1 = NewMB1
				MidMB2 = NewMB2
				Exit
			Else
				X = MidX
			EndIf
		Wend
		
		; Step from right to middle
		X = X2
		Scale# =  (G_Point2Y - G_Point1Y) / (MidX - X) ^ 2
		While True
			Y1 = G_Point2Y - (MidX - X) ^ 2 * Scale
			Y2 = Y1 + (X2 - X1) / nSteps * (0.25 + 1.5 * Abs(X - MidX - 0.0) / (X2 - X1))
			
			If Y2 = Y1 Then Y2 = Y1 + Sgn(G_Point2Y - G_Point1Y)
			
			NewMB1 = New T_MBolt
			NewMB1\OriginalX = X
			NewMB1\OriginalY = Y1
			
			NewMB2 = New T_MBolt
			NewMB2\OriginalX = X
			NewMB2\OriginalY = Y2
			
			MS = New T_MSegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = NewMB1
			MS\Bolt2 = NewMB2
			MS\MaterialChoice = 1
			
			If X < X2
				; Not left edge - Create the links back
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NewMB1
				MS\Bolt2 = OldMB1
				MS\MaterialChoice = 1
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NewMB2
				MS\Bolt2 = OldMB2
				MS\MaterialChoice = 1
				
				D1 = (NewMB1\OriginalX - OldMB2\OriginalX) ^ 2 + (NewMB1\OriginalY - OldMB2\OriginalY) ^ 2
				D2 = (NewMB2\OriginalX - OldMB1\OriginalX) ^ 2 + (NewMB2\OriginalY - OldMB1\OriginalY) ^ 2
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				If D1 <= D2
					MS\Bolt1 = NewMB1
					MS\Bolt2 = OldMB2
				Else
					MS\Bolt1 = NewMB2
					MS\Bolt2 = OldMB1
				EndIf
				MS\MaterialChoice = 2
				
			EndIf
			
			OldMB1 = NewMB1
			OldMB2 = NewMB2
			
			If X - XStep >= MidX
				X = X - XStep
			ElseIf X = MidX
				Exit
			ElseIf X - XStep / 2 < MidX
				Exit
			Else
				X = MidX
			EndIf
		Wend
		
		If X <> MidX
			MS = New T_Msegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = MidMB1
			MS\Bolt2 = NewMB1
			MS\MaterialChoice = 1
			
			MS = New T_Msegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = MidMB2
			MS\Bolt2 = NewMB2
			MS\MaterialChoice = 1
			
			MS = New T_Msegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = MidMB1
			MS\Bolt2 = NewMB2
			MS\MaterialChoice = 2
			
			MS = New T_Msegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = MidMB2
			MS\Bolt2 = NewMB1
			MS\MaterialChoice = 2
		EndIf
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateCircle()
	Local MS.T_MSegment
	Local OMB1.T_MBolt, OMB2.T_MBolt, OMB3.T_MBolt, OMB4.T_MBolt
	Local NMB1.T_MBolt, NMB2.T_MBolt
	
	Local Length#, R1#, R2#
	Local Angle#, AngleStep#, AngleStart#

	If C_FunctionTrace Then FunctionEntree("MacroUpdateCircle")
	
	MacroClear()
	
	If G_Point1X <> G_Point2X Or G_Point1Y <> G_Point2Y
		R1 = Sqr((G_Point1X - G_Point2X)^2 + (G_Point1Y - G_Point2Y)^2)
		
		Length = R1 / 3
		If Length > C_SegmentLengthNormal
			Length = C_SegmentLengthNormal
		EndIf
		
		If R1 >= 4 * Length
			R2 = R1 - Length / 2
		Else
			R2 = R1 * 0.875
		EndIf
		
		N = Ceil(Pi * R1 / 2 / Length)
		
		AngleStep = 90.0 / N
		
		AngleStart = ATan2(G_Point2Y - G_Point1Y, G_Point2X - G_Point1X)
		
		Angle = 0
		
		While Abs(Angle) < 360
			
			NMB1 = New T_MBolt
			NMB1\OriginalX = G_Point1X + R1 * Cos(AngleStart + Angle)
			NMB1\OriginalY = G_Point1Y + R1 * Sin(AngleStart + Angle)
			
			NMB2 = New T_MBolt
			NMB2\OriginalX = G_Point1X + R2 * Cos(AngleStart + Angle)
			NMB2\OriginalY = G_Point1Y + R2 * Sin(AngleStart + Angle)
			
			MS = New T_MSegment
			MS\SegmentType = C_SegmentTypeRegular
			MS\Bolt1 = NMB1
			MS\Bolt2 = NMB2
			MS\MaterialChoice = 1
			
			If Angle = 0
				OMB3 = NMB1
				OMB4 = NMB2
			Else
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NMB1
				MS\Bolt2 = OMB1
				MS\MaterialChoice = 1
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NMB2
				MS\Bolt2 = OMB2
				MS\MaterialChoice = 1
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NMB1
				MS\Bolt2 = OMB2
				MS\MaterialChoice = 2
				
				MS = New T_MSegment
				MS\SegmentType = C_SegmentTypeRegular
				MS\Bolt1 = NMB2
				MS\Bolt2 = OMB1
				MS\MaterialChoice = 2
			EndIf
			
			OMB1 = NMB1
			OMB2 = NMB2
			
			Angle = Angle + AngleStep
		Wend
		
		MS = New T_MSegment
		MS\SegmentType = C_SegmentTypeRegular
		MS\Bolt1 = NMB1
		MS\Bolt2 = OMB3
		MS\MaterialChoice = 1
		
		MS = New T_MSegment
		MS\SegmentType = C_SegmentTypeRegular
		MS\Bolt1 = NMB2
		MS\Bolt2 = OMB4
		MS\MaterialChoice = 1
		
		MS = New T_MSegment
		MS\SegmentType = C_SegmentTypeRegular
		MS\Bolt1 = NMB1
		MS\Bolt2 = OMB4
		MS\MaterialChoice = 2
		
		MS = New T_MSegment
		MS\SegmentType = C_SegmentTypeRegular
		MS\Bolt1 = NMB2
		MS\Bolt2 = OMB3
		MS\MaterialChoice = 2
	EndIf
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

;  Array of macro bolts needed to store the rows during MacroUpdateTower()
;    (arrays must be declared outside of functions)
;
Dim MacroBolt.T_MBolt(1, 10)

Function MacroUpdateTower()
	Local MB1.T_MBolt, MB2.T_MBolt
	Local MS.T_MSegment
	Local DistX#, DistY#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateTower")
	
	MacroClear()
	
	;  Determine the width and number of segments horizontally
	W = Abs(G_Point2X - G_Point1X)
	sgnH = Sgn(G_Point2X - G_Point1X)
	If W > C_SegmentLengthMaximum
		cntH = W / C_SegmentLengthMaximum + 1
		BoxW = W / cntH
		If (BoxW * cntH <> W)
			cntH = cntH + 1
		EndIf
	Else
		cntH = 1
		BoxW = W
	EndIf
	
	If cntH > 10
		cntH = 10
	EndIf
	BoxW = BoxW * sgnH
	
	;  Determine the height and tnumber of segments vertically
	H = Abs(G_Point2Y - G_Point1Y)
	sgnV = Sgn(G_Point2Y - G_Point1Y)
	If H > C_SegmentLengthMaximum
		cntV = H / C_SegmentLengthMaximum + 1
		BoxH = H / cntV
		If (BoxH * cntV <> H)
			cntV = cntV + 1
		EndIf
	Else
		cntV = 1
		BoxH = H
	EndIf
	BoxH = BoxH * sgnV
	
	Toggle = 0
	For iV = 0 To cntV
		For iH = 0 To cntH
			If iH = cntH
				X = G_Point2X
			Else
				X = G_Point1X + iH * BoxW
			EndIf
			
			If iV = cntV
				Y = G_Point2Y
			Else
				Y = G_Point1Y + iV * BoxH
			EndIf
			
			MB1 = New T_MBolt
			MacroBolt(Toggle, iH) = MB1
			MB1\BoltType = C_BoltTypeRegular
			MB1\Locked = False

			MB1\OriginalX = X
			MB1\OriginalY = Y
			
			If iH <> 0
				NewMSegment(MB1, MacroBolt(Toggle, iH - 1), 1)
			EndIf
			
			If iV <> 0
				NewMSegment(MB1, MacroBolt(1 - Toggle, iH), 1)
			EndIf
			
			If iH <> 0 And iV <> 0
				MB2 = New T_MBolt
				MB2\BoltType = C_BoltTypeRegular
				MB2\Locked = False
				
				MB2\OriginalX = (MB1\OriginalX + MacroBolt(Toggle, iH - 1)\OriginalX) / 2
				MB2\OriginalY = (MB1\OriginalY + MacroBolt(1 - Toggle, iH)\OriginalY) / 2
				
				NewMSegment(MB2, MB1, 2)
				NewMSegment(MB2, MacroBolt(Toggle, iH - 1), 2)
				NewMSegment(MB2, MacroBolt(1 - Toggle, iH), 2)
				NewMSegment(MB2, MacroBolt(1 - Toggle, iH - 1), 2)
			EndIf
		Next
		Toggle = 1 - Toggle
	Next
	
	MacroUpdateLengths()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MacroUpdateLengths()
	Local DistX#, DistY#, Dist#
	Local MS.T_MSegment
	
	Local OffsetX#, OffsetY#
	Local X1#, Y1#, X2#, Y2#
	
	If C_FunctionTrace Then FunctionEntree("MacroUpdateLengths")
	
	If G_EditTool = C_EditToolCopy Or G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch
		OffsetX = G_CursorX - G_RelativeX
		OffsetY = G_CursorY - G_RelativeY
	Else
		OffsetX = 0
		OffsetY = 0
	EndIf
	
	For MS = Each T_MSegment
		If MS\Bolt1\Movable
			X1 = MS\Bolt1\OriginalX + OffsetX
			Y1 = MS\Bolt1\OriginalY + OffsetY
		Else
			X1 = MS\Bolt1\OriginalX
			Y1 = MS\Bolt1\OriginalY
		EndIf
		
		If MS\Bolt2\Movable
			X2 = MS\Bolt2\OriginalX + OffsetX
			Y2 = MS\Bolt2\OriginalY + OffsetY
		Else
			X2 = MS\Bolt2\OriginalX
			Y2 = MS\Bolt2\OriginalY
		EndIf
		
		MS\Length = 0	; Length is considered to be 0 unless it is within the acceptable range
		DistX = Abs(X1 - X2)
		If DistX > C_SegmentLengthMaximum
			MS\LengthError = True
		Else
			DistY = Abs(Y1 - Y2)
			If DistY > C_SegmentLengthMaximum
				MS\LengthError = True
			Else
				Dist = Sqr(DistX * DistX + DistY * DistY)
				If Dist > C_SegmentLengthMaximum Or Dist < C_SegmentLengthMinimum
					MS\LengthError = True
				Else
					MS\LengthError = False
					MS\Length = Dist
				EndIf
			EndIf
		EndIf
		
		If MS\LengthError And G_SoundStatus = C_SoundStatusNone
			G_SoundStatus = C_SoundStatusWarn
		EndIf
		
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ControlsInitialise()
	Local C.T_Control
	
	If C_FunctionTrace Then FunctionEntree("ControlsInitialise")
	
	Delete Each T_Control
	
	C = New T_Control
	C\ControlType = C_ControlTypeWaterLevel
	C\PosX = (G_ShoreLeft + G_ShoreRight) / 2
	C\PosY = G_WaterLevel
	G_ControlWaterLevel = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeWaterDepth
	C\PosX = (G_ShoreLeft + G_ShoreRight) / 2
	C\PosY = G_WaterDepth
	G_ControlWaterDepth = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeShoreLeft
	C\PosX = G_ShoreLeft
	C\PosY = G_GroundLevel
	G_ControlShoreLeft = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeShoreRight
	C\PosX = G_ShoreRight
	C\PosY = G_GroundLevel
	G_ControlShoreRight = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeWind
	C\PosX = G_WindSpeedStart * 10
	C\PosY = 500
	G_ControlWind = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeTrainStart
	C\PosX = G_TrainStartX
	C\PosY = G_TrainStartY
	G_ControlTrainStart = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeTrainLength
	C\PosX = G_ControlTrainStart\PosX - G_TrainLength
	C\PosY = G_ControlTrainStart\PosY
	G_ControlTrainLength = C
	
	C = New T_Control
	C\ControlType = C_ControlTypeTrainStop
	C\PosX = G_TrainStopX
	C\PosY = G_TrainStartY
	G_ControlTrainStop = C
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ControlsDraw()
	Local C.T_Control
	Local SX, SY
	Local X0, Y0	; Pixel coordinates of 0,0

	If C_FunctionTrace Then FunctionEntree("ControlsDraw")
	
	For C = Each T_Control
		SX = G_ScreenCentreX + (C\PosX - G_CameraX) * G_GridSize * G_ZoomFactor
		SY = G_ScreenCentreY - (C\PosY - G_CameraY) * G_GridSize * G_ZoomFactor
		
		X0 = G_ScreenCentreX - G_CameraX * G_GridSize * G_ZoomFactor
		Y0 = G_ScreenCentreY + G_CameraY * G_GridSize * G_ZoomFactor
		
		If C = G_ControlSelected
			FillFlag = True
		Else
			FillFlag = False
		EndIf
		
		Select C\ControlType
			Case C_ControlTypeWaterLevel
				Color 100, 150, 250
				Oval SX-10, SY-10, 20, 20, FillFlag
				Line SX-20, SY, SX-10, SY
				Line SX+10, SY, SX+20, SY
			Case C_ControlTypeWaterDepth
				Color 100, 150, 250
				Oval SX-10, SY-10, 20, 20, FillFlag
				Line SX-20, SY, SX-10, SY
				Line SX+10, SY, SX+20, SY
			Case C_ControlTypeShoreLeft
				Color 250, 150, 100
				Rect SX - 5, SY - 5, 11, 11, FillFlag
				Line SX, SY - 20, SX, SY + 20
				Line SX + 5, SY - 10, SX + 5, SY + 10
				Line SX + 15, SY, SX + 5, SY - 10
				Line SX + 15, SY, SX + 5, SY + 10
			Case C_ControlTypeShoreRight
				Color 250, 150, 100
				Rect SX - 5, SY - 5, 11, 11, FillFlag
				Line SX, SY - 20, SX, SY + 20
				Line SX - 5, SY - 10, SX - 5, SY + 10
				Line SX - 15, SY, SX - 5, SY - 10
				Line SX - 15, SY, SX - 5, SY + 10
			Case C_ControlTypeWind
				Color 150, 150, 150
				If C\PosX < 0
					DX = -1
				ElseIf C\PosX = 0
					DX = 0
				Else
					DX = 1
				EndIf
				
				Rect SX - 5, SY - 5, 11, 11, FillFlag
				Line SX, SY - 20, SX, SY + 20
				Line SX + 5 * DX, SY + 10, SX + 5 * DX, SY - 10
				Line SX + 5 * DX, SY + 10, SX + 15 * DX, SY
				Line SX + 5 * DX, SY - 10, SX + 15 * DX, SY

				; Extra line from X=0
				Line X0, SY, SX, SY
			Case C_ControlTypeTrainStart
				Color 0, 250, 0
				If C\PosX < G_ControlTrainStop\PosX
					DX = 1
				ElseIf C\PosX = G_ControlTrainStop\PosX
					DX = 0
				Else
					DX = -1
				EndIf
				
				Rect SX - 5, SY - 5, 11, 11, FillFlag
				Line SX, SY - 20, SX, SY + 20
				Line SX + 5 * DX, SY + 10, SX + 5 * DX, SY - 10
				Line SX + 5 * DX, SY + 10, SX + 15 * DX, SY
				Line SX + 5 * DX, SY - 10, SX + 15 * DX, SY
			Case C_ControlTypeTrainLength
				Color 0, 250, 0
				Rect SX - 5, SY - 5, 11, 11, FillFlag
			Case C_ControlTypeTrainStop
				Color 0, 250, 0
				Rect SX - 5, SY - 5, 11, 11, FillFlag
			Default
		End Select
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ConsistencyCheck()
	Local B1.T_Bolt, B2.T_Bolt
	Local S1.T_Segment, S2.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("ConsistencyCheck")
	
	G_ConsistencyMergeBolts = 0
	G_ConsistencyMergeSegments = 0
	
	; (1) - Check for coincident bolts
	;
	; NB : There is already a check in MacroPlace which should guarantee that no
	;      coincident bolt is ever created.  However it doesn't hurt to check again !
	;
	
	; Clear trace flag on all bolts
	For B1 = Each T_Bolt
		B1\Trace = False
		B1\TraceBolt = Null
	Next
	
	; Check each bolt against others
	For B1 = Each T_Bolt
		If B1\Trace = False
			; Only check bolts that haven't already been flagged for merging
			B2 = After B1
			While B2 <> Null
				If B2\Trace = False
					If B1\OriginalX = B2\OriginalX And B1\OriginalY = B2\OriginalY
						; Coincident bolt - Flag it and point to survivor
						B2\Trace = True
						B2\TraceBolt = B1
						G_ConsistencyMergeBolts = G_ConsistencyMergeBolts + 1
					EndIf
				EndIf
				B2 = After B2
			Wend
		EndIf
	Next
	
	; Delete the duplicate bolts
	If G_ConsistencyMergeBolts <> 0
		; Update segments to point to surviving bolt
		For S1 = Each T_Segment
			If S1\Bolt1\Trace
				S1\Bolt1 = S1\Bolt1\TraceBolt
			EndIf
			
			If S1\Bolt2\Trace
				S1\Bolt2 = S1\Bolt2\TraceBolt
			EndIf
		Next
		
		; Now delete the duplicate bolts
		For B1 = Each T_Bolt
			If B1\Trace
				Delete B1
			EndIf
		Next
	EndIf
	
	; (2) - Check for coincident segments
	;
	; NB : There is already a check in MacroPlace which should guarantee that no
	;      coincident segment is ever created.  However it doesn't hurt to check again !
	
	; Clear trace flag on all segments
	For S1 = Each T_Segment
		S1\Trace = False
	Next
	
	; Check each segment against others
	For S1 = Each T_Segment
		If S1\Bolt1 = S1\Bolt2
			; This situation should never happen now but it did arise in some files created long
			;   ago - before there was a check to prevent 0 length segments (?)
			S1\Trace = True
			G_ConsistencyMergeSegments = G_ConsistencyMergeSegments + 1
		Else
			If S1\Trace = False
				; Only check segments that haven't already been flagged for merging
				S2 = After S1
				While S2 <> Null
					If S2\Trace = False
						If (S1\Bolt1 = S2\Bolt1 And S1\Bolt2 = S2\Bolt2) Or (S1\Bolt1 = S2\Bolt2 And S1\Bolt2 = S2\Bolt1)
							; Coincident segment
							G_ConsistencyMergeSegments = G_ConsistencyMergeSegments + 1
							
							; S1 is always chosen as the survivor but it inherits the material from S2 if it is stronger
							If S2\Material\Strength > S1\Material\Strength
								S1\Material = S2\Material
							EndIf
							
							S2\Trace = True
						EndIf
					EndIf
							
					S2 = After S2
				Wend
			EndIf
		EndIf
	Next
	
	; Delete any flagged segments and trace the bolts so we'll know which survive later
	For S1 = Each T_Segment
		If S1\Trace
			Delete S1
		Else
			S1\Bolt1\Trace = True
			S1\Bolt2\Trace = True
		EndIf
	Next
	
	; Delete any non-linked bolts
	For B1 = Each T_Bolt
		If Not B1\Trace
			Delete B1
		EndIf
	Next
	
	; Inform the user of any problems found
	If G_ConsistencyMergeBolts <> 0
		DialogBox("Consistency check found " + G_ConsistencyMergeBolts + " bolts in error", C_DialogBoxAny)
	EndIf
	
	If G_ConsistencyMergeSegments <> 0
		DialogBox("Consistency check found " + G_ConsistencyMergeSegments + " segments in error", C_DialogBoxAny)
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function DistPointToSegment#(X0#, Y0#, P1X, P1Y, P2X, P2Y)	;S.T_Segment
	Local VX#, VY#
	Local WX#, WY#
	Local B#, C1#, C2#
	Local X#, Y#
	
	VX = P2X - P1X	;S\Bolt2\OriginalX - S\Bolt1\OriginalX
	VY = P2Y - P1Y	;S\Bolt2\OriginalY - S\Bolt1\OriginalY
	
	WX = X0 - P1X	;S\Bolt1\OriginalX
	WY = Y0 - P1Y	;S\Bolt1\OriginalY
	
	C1 = Dot(WX, WY, VX, VY)
	If C1 <= 0
		Return Dist(X0, Y0, P1X, P1Y)	;S\Bolt1\OriginalX, S\Bolt1\OriginalY)
	EndIf
	
	C2 = dot(VX, VY, VX, VY)
	If C2 < C1
		Return Dist(X0, Y0, P2X, P2Y)	;S\Bolt2\OriginalX, S\Bolt2\OriginalY)
	EndIf
	
	B = C1 / C2
	
	X = P1X + B * VX	;S\Bolt1\OriginalX 
	Y = P1Y + B * VY	;S\Bolt1\OriginalY
	
	Return Dist(X0, Y0, X, Y)
	
End Function

Function Dot#(X1#, Y1#, X2#, Y2#)
	Return X1 * X2 + Y1 * Y2
End Function

Function Dist#(X1#, Y1#, X2#, Y2#)
	Local DX = X1 - X2
	Local DY = Y1 - Y2
	Return Sqr(DX * DX + DY * DY)
End Function

Function DistBoltToBolt#(B1.T_Bolt, B2.T_Bolt)
	Local DX = B1\OriginalX - B2\OriginalX
	Local DY = B1\OriginalY - B2\OriginalY
	Return Sqr(DX * DX + DY * DY)
End Function