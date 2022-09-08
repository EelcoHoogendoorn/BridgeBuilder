; --------------------------------------------------------------------------------------------------------------------------------
;
; File Module
;
; --------------------------------------------------------------------------------------------------------------------------------

Function LoadMenu()
	; Returns True if a save is performed, False if it is cancelled
	
	Local Done
	
	If C_FunctionTrace Then FunctionEntree("LoadMenu")
	
	If G_LoadBackImage = 0
		G_LoadBackImage = GW_LoadImage("Images\", "Metal015.JPG")
	EndIf
	
	If G_LoadHeadImage = 0
		G_LoadHeadImage = GW_LoadImage("Images\", "Heading - Bridge Load.PNG")
	EndIf
	
	; Check if the current file has been changed and prompt to save it before loading
	If G_FileChanged
		Answer = DialogBox("Current file modified.  Save first ?", C_DialogBoxYNC)
		If Answer = C_DialogBoxYes
			If Not SaveMenu()
				Goto LoadMenuFailed
			EndIf
		ElseIf Answer = C_DialogBoxCancel
			Goto LoadMenuFailed
		EndIf
	EndIf
	
	LoadScanFiles(G_FilesPageStart)
	
	Done = False
	While Not Done
		If KeyHit(C_Key_Escape)
			Goto LoadMenuFailed
		Else
			Done = LoadControls()
		EndIf
		
		Flip

		LoadDraw()
		
	Wend
	
	G_Mode = C_ModeEdit
	G_EditTool = C_EditToolSelect
	PanelIconSelect(G_ToolPanel, G_IconSelect)
	FlushKeys
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return True

.LoadMenuFailed
	
	DialogBox("Load file cancelled", C_DialogBoxOK)
	
	G_Mode = C_ModeEdit
	G_EditTool = C_EditToolSelect
	PanelIconSelect(G_ToolPanel, G_IconSelect)

	If C_FunctionTrace Then FunctionEgress()
	
	Return False
	
End Function

Function LoadControls()
	Local Bridge
	
	If C_FunctionTrace Then FunctionEntree("LoadControls")
	
	G_MouseX = MouseX()
	G_MouseY = MouseY()
	
	If MouseHit(1) > 0
		G_Mouse1 = True
	Else
		G_Mouse1 = False
	EndIf
	G_Mouse1Down = MouseDown(1)
	
	G_Mouse2 = MouseDown(2)
	G_Mouse3 = MouseDown(3)
	
	If KeyHit(C_Key_SysReq) Or KeyHit(C_Key_F12)
		; There is no Print_Screen key and SysReq doesn't work ?
		If PrintScreen()
			DialogBox("Print Screen - Press any key to resume", C_DialogBoxAny)
		EndIf
	EndIf
	
	Bridge = -1
	
	If G_Mouse1
		Bridge = G_FilesPageStart + G_FilesMouseOver
	EndIf
	
	If KeyHit(C_Key_0)
		Bridge = 0
	ElseIf KeyHit(C_Key_1)
		Bridge = 1
	ElseIf KeyHit(C_Key_2)
		Bridge = 2
	ElseIf KeyHit(C_Key_3)
		Bridge = 3
	ElseIf KeyHit(C_Key_4)
		Bridge = 4
	ElseIf KeyHit(C_Key_5)
		Bridge = 5
	ElseIf KeyHit(C_Key_6)
		Bridge = 6
	ElseIf KeyHit(C_Key_7)
		Bridge = 7
	ElseIf KeyHit(C_Key_8)
		Bridge = 8
	ElseIf KeyHit(C_Key_9)
		Bridge = 9
	Else
		If KeyHit(C_Key_Home)
			G_FilesPageStart = 0
			LoadScanFiles(G_FilesPageStart)
		ElseIf KeyHit(C_Key_End)
			G_FilesPageStart = 100 - G_FilesPerPage
			LoadScanFiles(G_FilesPageStart)
		ElseIf KeyHit(C_Key_Page_Down)
			If G_FilesPageStart + G_FilesPerPage < 100
				G_FilesPageStart = G_FilesPageStart + G_FilesPerPage
				LoadScanFiles(G_FilesPageStart)
			EndIf
		ElseIf KeyHit(C_Key_Page_Up)
			If G_FilesPageStart - G_FilesPerPage >= 0
				G_FilesPageStart = G_FilesPageStart - G_FilesPerPage
			Else
				G_FilesPageStart = 0
			EndIf
			LoadScanFiles(G_FilesPageStart)
		EndIf			
	EndIf
	
	If Bridge = -1
		If C_FunctionTrace Then FunctionEgress()
	
		Return False
	Else
		If Bridge = 0
			BridgeClear(False)
			G_Mode = C_ModeEdit
		Else
			G_FileChanged = False
			;BridgeClear(True)
			If KeyDown(C_Key_K)
				;LoadBridgeOld(Bridge)
			Else
				LoadBridgeV4(Bridge)
			EndIf
			G_Mode = C_ModeEdit
		EndIf
		ViewFull(C_ViewModeAll)
		
		If C_FunctionTrace Then FunctionEgress()
	
		Return True
	EndIf
	
	FlushMouse()
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function LoadDraw()
	Local i
	Local BaseX, BaseY
	
	If C_FunctionTrace Then FunctionEntree("LoadDraw")
	
	SetBuffer BackBuffer()
	TileBlock G_LoadBackImage
	
	DrawImage G_LoadHeadImage, G_ScreenCentreX - ImageWidth(G_LoadHeadImage) / 2, 10

	G_FilesMouseOver = -1
	For i = 0 To G_FilesPerPage - 1
		If G_FilesPageStart + i > 99
			Exit
		EndIf
		
		BaseX = 10 + (i / G_FilesPerColumn) * G_FilesWidth
		BaseY = 120 + (i Mod G_FilesPerColumn) * G_FilesHeight
		
		G_Files(i)\EdgeLeft = BaseX
		G_Files(i)\EdgeRight = BaseX + G_ThumbnailWidth - 1
		G_Files(i)\EdgeTop = BaseY
		G_Files(i)\EdgeBottom = BaseY + G_ThumbnailHeight - 1
		
		If G_Files(i)\Status = C_FilestatusOK
			If G_MouseX >= G_Files(i)\EdgeLeft And G_MouseX <= G_Files(i)\EdgeRight And G_MouseY >= G_Files(i)\EdgeTop And G_MouseY <= G_Files(i)\EdgeBottom
				G_FilesMouseOver = i
				Color 50, 150, 50
				Rect BaseX - 5, BaseY - 5, G_ScreenWidth / (G_FilesPerPage / G_FilesPerColumn), G_ThumbnailHeight + 10, True
			EndIf
			Color 200, 200, 200
		Else
			Color 255, 100, 100
		EndIf
		
		Rect BaseX - 2, BaseY - 2, G_ThumbnailWidth + 4, G_ThumbnailHeight + 4, True
		
		DrawBlock G_Files(i)\Image, BaseX, BaseY
		
		Text BaseX + 140, BaseY, G_Files(i)\FileName
		
		DrawImage G_MouseImage, G_MouseX, G_MouseY
		
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function LoadScanFiles(StartN)
	Local FileName$
	Local Check$
	
	If C_FunctionTrace Then FunctionEntree("LoadScanFiles")
	
	For i = 0 To G_FilesPerPage - 1
		If i + StartN < 100
			; Check For potential file
			FileName = "Data\Bridge XZ" + Right("0" + (i + StartN), 2) + ".DAT"
			If G_Files(i) = Null
				G_Files(i) = New T_FileInfo
				G_Files(i)\Image = CreateImage(G_ThumbnailWidth, G_ThumbnailHeight)
			EndIf
			
			SetBuffer ImageBuffer(G_Files(i)\Image)
			
			G_Files(i)\Id = i + StartN
			G_Files(i)\FileName = FileName
	
			If FileType(FileName) = 1
				G_Files(i)\Status = C_FileStatusOK
				IFile = OpenFile(FileName)
				
				Check = ReadString$(IFile)
				If Check <> "Bridge Data File"
					GW_AbortErrorMessage("Invalid string in '" + FileName + "'  Expected 'Bridge Data File'  Found '" + Check + "'")
				EndIf
				
				Check = ReadString$(IFile)
				If Check <> "Version"
					GW_AbortErrorMessage("Invalid string in '" + FileName + "'  Expected 'Version'  Found '" + Check + "'")
				EndIf
				
				Version = ReadInt(IFile)
				If Version = 1
					IntSize = 2
				ElseIf Version = 2
					IntSize = 4
				ElseIf Version = 3
					IntSize = 4
				ElseIf Version = 4
					IntSize = 4
				Else
					GW_AbortErrorMessage("Invalid file version in '" + FileName + "'  Expected 1  Found " + Version)
				EndIf
				
				Check = ReadString$(IFile)
				If Check <> "Thumbnail"
					GW_AbortErrorMessage("Invalid string in '" + FileName + "'  Expected 'Thumbnail'  Found '" + Check + "'")
				EndIf
				
				Local W, H
				Local X, Y
				
				If IntSize = 2
					H = ReadShort(IFile)
					W = ReadShort(IFile)
				Else
					H = ReadInt(IFile)
					W = ReadInt(IFile)
				EndIf
				
				If H <> G_ThumbnailHeight
					GW_AbortErrorMessage("Invalid thumbnail height - " + H)
				EndIf
				
				If W <> G_ThumbnailWidth
					GW_AbortErrorMessage("Invalid thumbnail width - " + H)
				EndIf
				
				Buffer = ImageBuffer(G_Files(i)\Image)
				
				If 1 = 0
					; Straight pixels
					For Y = 0 To G_ThumbnailHeight - 1
						For X = 0 To G_ThumbnailWidth - 1
							V = ReadInt(IFile) And $FFFFFF
							WritePixel X, Y, V, Buffer
						Next
					Next
				Else
					; Run length encoded
					Y = 0
					X = 0
					
					While Y < H
						V = ReadInt(IFile)
						C = V And $FFFFFF
						N = V Shr 24
						
						While N > 0
							If N = 1
								WritePixel X, Y, C, Buffer
								X = X + 1
								N = 0
							ElseIf X + N <= W
								Color 0, 0, C
								Rect X, Y, N, 1
								X = X + N
								N = 0
							Else
								Color 0, 0, C
								Rect X, Y, W - X, 1
								N = N - W + X
								X = W
							EndIf
							
							If X >= W
								X = 0
								Y = Y + 1
							EndIf
						Wend
					Wend
				EndIf
				
				Color 0, 200, 0
				
				CloseFile IFile
				IFile = 0
			Else
				G_Files(i)\Status = C_FileStatusNone
				Color 200, 0, 0
				Rect 0, 0, G_ThumbnailWidth, G_ThumbnailHeight, False
				Color 0, 0, 0
				Rect 1, 1, G_ThumbnailWidth - 2, G_ThumbnailHeight - 2, True
			EndIf
		Else
			; Clear the entry
			G_Files(i)\Id = i + StartN
			G_Files(i)\FileName = ""
			G_Files(i)\Status = C_FileStatusNone
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function LoadBridgeV3(Id)
	Local nBolts, iBolt, iB1, iB2
	Local B.T_Bolt
	Local nSegments, iSegment
	Local S.T_Segment
	Local iMaterial
	Local M.T_Material
	
	Local IFileName$
	Local IFile
	Local Check$
	
	Local GroupIdFlag	; From V3, the Bolts and Segments have a GroupId
	
	If C_FunctionTrace Then FunctionEntree("LoadBridge")
	
	G_GroupId = -1		; Set GroupId to low value
	
	IFileName = "Data\Bridge XY" + Right("0" + Id, 2) + ".DAT"
	
	Select FileType(IFileName)
		Case 0
			GW_AbortErrorMessage("File name '" + IFileName + "' not found")
		Case 1
			; This is Ok
		Case 2
			GW_AbortErrorMessage("File name '" + IFileName + "' conflicts with directory name")
		Default
			GW_AbortErrorMessage("Unknown status in LoadBridge(" + Id + ")")
	End Select
	
	IFile = OpenFile(IFileName)
	
	BridgeClear(True)
	
	Check = ReadString$(IFile)
	If Check <> "Bridge Data File"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Bridge Data File'  Found '" + Check + "'")
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Version"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Version'  Found '" + Check + "'")
	EndIf
	
	Version = ReadInt(IFile)
	If Version = 1
		IntSize = 2
		GroupIdFlag = False
	ElseIf Version = 2
		IntSize = 4
		GroupIdFlag = False
	ElseIf Version = 3
		IntSize = 4
		GroupIdFlag = True
	Else
		GW_AbortErrorMessage("Invalid file version in '" + IFileName + "'  Expected 1-3  Found " + Version)
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Thumbnail"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Thumbnail'  Found '" + Check + "'")
	EndIf
	
	Local W, H
	Local X, Y
	
	If IntSize = 2
		H = ReadShort(IFile)
		W = ReadShort(IFile)
	Else
		H = ReadInt(IFile)
		W = ReadInt(IFile)
	EndIf
	
	If H <> G_ThumbnailHeight
		GW_AbortErrorMessage("Invalid thumbnail height - " + H)
	EndIf
	
	If W <> G_Thumbnailwidth
		GW_AbortErrorMessage("Invalid thumbnail width - " + H)
	EndIf
	
	If 1 = 0
		; Straight pixels
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				V = ReadInt(IFile) And $FFFFFF
				;WritePixel X, Y, V, Buffer
			Next
		Next
	Else
		; Run length encoded
		Y = 0
		X = 0
		
		While Y < H
			V = ReadInt(IFile)
			C = V And $FFFFFF
			N = V Shr 24
			
			While N > 0
				If N = 1
					;WritePixel X, Y, C, Buffer
					X = X + 1
					N = 0
				ElseIf X + N <= W
					;Color 0, 0, C
					;Rect X, Y, N, 1
					X = X + N
					N = 0
				Else
					;Color 0, 0, C
					;Rect X, Y, W - X, 1
					N = N - W + X
					X = W
				EndIf
				
				If X >= W
					X = 0
					Y = Y + 1
				EndIf
			Wend
		Wend
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Level"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Level'  Found '" + Check + "'")
	EndIf
	
	If IntSize = 2
		G_LevelWidth = ReadShort(IFile)
		Dim height(G_LevelWidth)
		G_WaterDepth = -(65536 - ReadShort(IFile))
		levelheight = ReadShort(IFile)
		G_WaterLevel = - (65536 - ReadShort(IFile))
	Else
		G_LevelWidth = ReadInt(IFile)
		Dim height(G_LevelWidth)
		G_WaterDepth = ReadInt(IFile)
		levelheight = ReadInt(IFile)
		G_WaterLevel = ReadInt(IFile)
		
		; Special to fix the reverse signed water depth
		If G_WaterDepth > G_WaterLevel
			G_WaterDepth = - G_WaterDepth
		EndIf
	EndIf
	
	For h=0 To G_LevelWidth-1
		height(h) = ReadFloat(IFile)
	Next
	
	ControlsInitialise()
	
	Check = ReadString$(IFile)
	If Check <> "Bolts"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Bolts'  Found '" + Check + "'")
	EndIf
	
	If IntSize = 2
		nBolts = ReadShort(IFile)
	Else
		nBolts = ReadInt(IFile)
	EndIf
	
	For iBolt = 1 To nBolts
		B.T_Bolt	= New T_Bolt
		
		B\Id		= iBolt
		
		If GroupIdFlag
			B\GroupId = ReadInt(IFile)
		Else
			B\GroupId = 1
		EndIf
		
		If B\GroupId > G_GroupId
			G_GroupId = B\GroupId
		EndIf
		
		B\OriginalX	= ReadFloat(IFile)
		B\OriginalY	= ReadFloat(IFile)
		
		B\BoltType	= ReadByte(IFile)
		B\DynamicId = 0		; Default
		B\ActionId = 0		; Default
	Next
	
	Check = ReadString$(IFile)
	If Check <> "Segments"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Segments'  Found '" + Check + "'")
	EndIf
	
	If IntSize = 2
		nSegments = ReadShort(IFile)
	Else
		nSegments = ReadInt(IFile)
	EndIf
	
	For iSegment = 1 To nSegments
		S.T_Segment	= New T_Segment
		
		S\Id = iSegment
		
		If GroupIdFlag
			S\GroupId = ReadInt(IFile)
		Else
			S\GroupId = 1
		EndIf
		
		If S\GroupId > G_GroupId
			G_GroupId = S\GroupId
		EndIf
		
		iB1 = ReadShort(IFile)
		For B = Each T_Bolt
			If B\Id = iB1
				S\Bolt1 = B
				Exit
			EndIf
		Next
			
		iB2 = ReadShort(IFile)
		For B = Each T_Bolt
			If B\Id = iB2
				S\Bolt2 = B
				Exit
			EndIf
		Next
		
		iMaterial = ReadByte(IFile)
		; Map to new materials
		;If iMaterial = 0
		;	iMaterial = 1
		;ElseIf iMaterial = 1
		;	iMaterial = 0
		;EndIf
		
		For M.T_Material = Each T_Material
			If M\Id = iMaterial
				S\Material = M
				Exit
			EndIf
		Next
		
		S\SegmentType = ReadByte(IFile)
		S\DynamicId = 0		; Default
		S\ActionId = 0		; Default
		S\Length = Sqr((S\Bolt1\OriginalX - S\Bolt2\OriginalX) ^ 2 + (S\Bolt1\OriginalY - S\Bolt2\OriginalY) ^ 2)
		
	Next
		
	CloseFile IFile
	
	G_FileChanged = False
	G_FileSlot = Id
	
	; Forced consistency check
	ConsistencyCheck()
	
	;GW_AbortErrorMessage("OK so far")


	; Fixup some version differences
	For B = Each T_Bolt
		If B\OriginalY = 0 And (B\OriginalX <= G_ShoreLeft Or B\OriginalX >= G_ShoreRight)
			B\BoltType = C_BoltTypeFixed
		EndIf
	Next
	
	For S = Each T_Segment
		If S\SegmentType = 0
			S\SegmentType = 1
		Else
			S\SegmentType = 2
		EndIf
		
		If S\Bolt1\OriginalY = 0 And S\Bolt2\OriginalY = 0
			S\Locked = True
		EndIf
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function LoadBridgeV4(Id)
	Local nBolts, iBolt, iB1, iB2
	Local B.T_Bolt
	Local nSegments, iSegment
	Local S.T_Segment
	Local iMaterial
	Local M.T_Material
	
	Local IFileName$
	Local IFile
	Local Check$
	
	Local GroupIdFlag	; From V3, the Bolts and Segments have a GroupId
	
	If C_FunctionTrace Then FunctionEntree("LoadBridge")
	
	G_GroupId = -1		; Set GroupId to low value
	
	IFileName = "Data\Bridge XZ" + Right("0" + Id, 2) + ".DAT"
	
	Select FileType(IFileName)
		Case 0
			GW_AbortErrorMessage("File name '" + IFileName + "' not found")
		Case 1
			; This is Ok
		Case 2
			GW_AbortErrorMessage("File name '" + IFileName + "' conflicts with directory name")
		Default
			GW_AbortErrorMessage("Unknown status in LoadBridge(" + Id + ")")
	End Select
	
	IFile = OpenFile(IFileName)
	
	BridgeClear(True)
	
	Check = ReadString$(IFile)
	If Check <> "Bridge Data File"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Bridge Data File'  Found '" + Check + "'")
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Version"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Version'  Found '" + Check + "'")
	EndIf
	
	Version = ReadInt(IFile)
	If Version <> 4
		GW_AbortErrorMessage("Invalid file version in '" + IFileName + "'  Expected 1-3  Found " + Version)
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Thumbnail"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Thumbnail'  Found '" + Check + "'")
	EndIf
	
	Local W, H
	Local X, Y
	
	If IntSize = 2
		H = ReadShort(IFile)
		W = ReadShort(IFile)
	Else
		H = ReadInt(IFile)
		W = ReadInt(IFile)
	EndIf
	
	If H <> G_ThumbnailHeight
		GW_AbortErrorMessage("Invalid thumbnail height - " + H)
	EndIf
	
	If W <> G_Thumbnailwidth
		GW_AbortErrorMessage("Invalid thumbnail width - " + H)
	EndIf
	
	If 1 = 0
		; Straight pixels
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				V = ReadInt(IFile) And $FFFFFF
				;WritePixel X, Y, V, Buffer
			Next
		Next
	Else
		; Run length encoded
		Y = 0
		X = 0
		
		While Y < H
			V = ReadInt(IFile)
			C = V And $FFFFFF
			N = V Shr 24
			
			While N > 0
				If N = 1
					;WritePixel X, Y, C, Buffer
					X = X + 1
					N = 0
				ElseIf X + N <= W
					;Color 0, 0, C
					;Rect X, Y, N, 1
					X = X + N
					N = 0
				Else
					;Color 0, 0, C
					;Rect X, Y, W - X, 1
					N = N - W + X
					X = W
				EndIf
				
				If X >= W
					X = 0
					Y = Y + 1
				EndIf
			Wend
		Wend
	EndIf
	
	Check = ReadString$(IFile)
	If Check <> "Level"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Level'  Found '" + Check + "'")
	EndIf
	
	G_LevelWidth = ReadInt(IFile)
	Dim height(G_LevelWidth)
	
	G_ShoreLeft = ReadInt(IFile)
	G_ShoreRight = ReadInt(IFile)
	
	For H = 0 To G_LevelWidth - 1
		Height(H) = ReadFloat(IFile)
	Next
	
	Check = ReadString$(IFile)
	If Check = "Water"
		G_WaterDepth = ReadInt(IFile)
		G_WaterLevel = ReadInt(IFile)
		Check = ReadString$(IFile)
	Else
		G_WaterDepth = 0
		G_WaterLevel = 0
	EndIf
	
	If Check = "Wind"
		G_WindSpeedStart	= ReadInt(IFile)
		G_WindSpeedLow		= ReadInt(IFile)
		G_WindSpeedHigh		= ReadInt(IFile)
		G_WindSpeedCycle	= ReadInt(IFile)
		Check = ReadString$(IFile)
	Else
		G_WindSpeedStart	= 0
		G_WindSpeedLow		= 0
		G_WindSpeedHigh		= 0
		G_WindSpeedCycle	= 0
	EndIf
	
	If Check = "Train"
		G_TrainStartX	= ReadInt(IFile)
		G_TrainStartY	= ReadInt(IFile)
		G_TrainLength	= ReadInt(IFile)
		G_TrainStopX	= ReadInt(IFile)
		Check = ReadString$(IFile)
	Else
		G_TrainStartX	= 0
		G_TrainStartY	= 0
		G_TrainLength	= 5
		G_TrainStopX	= 1600
	EndIf
	
	ControlsInitialise()
	
	If Check <> "Bolts"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Bolts'  Found '" + Check + "'")
	EndIf
	
	NBolts = ReadInt(IFile)

	For iBolt = 1 To nBolts
		B.T_Bolt	= New T_Bolt
		
		B\Id		= ReadInt(IFile)
		
		B\BoltType	= ReadInt(IFile)
		B\Locked	= ReadInt(IFile)

		B\GroupId	= ReadInt(IFile)
		If B\GroupId > G_GroupId
			G_GroupId = B\GroupId
		EndIf
		
		B\DynamicId = ReadInt(IFile)
		B\ActionId	= ReadInt(IFile)
		B\OriginalX	= ReadFloat(IFile)
		B\OriginalY	= ReadFloat(IFile)
		
	Next
	
	Check = ReadString$(IFile)
	If Check <> "Segments"
		GW_AbortErrorMessage("Invalid string in '" + IFileName + "'  Expected 'Segments'  Found '" + Check + "'")
	EndIf
.LV4
	
	nSegments = ReadInt(IFile)
	
	For iSegment = 1 To nSegments
		S.T_Segment	= New T_Segment
		
		S\Id = ReadInt(IFile)
		S\SegmentType = ReadInt(IFile)
		S\Locked = ReadInt(IFile)
		
		S\GroupId = ReadInt(IFile)
		If S\GroupId > G_GroupId
			G_GroupId = S\GroupId
		EndIf
		
		S\DynamicId = ReadInt(IFile)
		S\ActionId = ReadInt(IFile)
		
		iMaterial = ReadInt(IFile)
		For M.T_Material = Each T_Material
			If M\Id = iMaterial
				S\Material = M
				Exit
			EndIf
		Next
		
		iB1 = ReadInt(IFile)
		For B = Each T_Bolt
			If B\Id = iB1
				S\Bolt1 = B
				Exit
			EndIf
		Next
			
		iB2 = ReadInt(IFile)
		For B = Each T_Bolt
			If B\Id = iB2
				S\Bolt2 = B
				Exit
			EndIf
		Next
		
		S\Length = Sqr((S\Bolt1\OriginalX - S\Bolt2\OriginalX) ^ 2 + (S\Bolt1\OriginalY - S\Bolt2\OriginalY) ^ 2)
		
	Next
		
	CloseFile IFile
	
	G_FileChanged = False
	G_FileSlot = Id
	
	; Forced consistency check
	ConsistencyCheck()
	
	;GW_AbortErrorMessage("OK so far")
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SaveMenu()
	Local Done
	
	If C_FunctionTrace Then FunctionEntree("SaveMenu")
	
	If G_SaveBackImage = 0
		;G_SaveBackImage = GW_LoadImage("", "DBLUE019.JPG")
		G_SaveBackImage = GW_LoadImage("Images\", "Metal020.JPG")
	EndIf
	
	If G_SaveHeadImage = 0
		G_SaveHeadImage = GW_LoadImage("Images\", "Heading - Bridge Save.PNG")
	EndIf
	
	LoadScanFiles(G_FilesPageStart)
	
	Done = False
	While Not Done
		If KeyHit(C_Key_Escape)
			Goto SaveMenuFailed
		Else
			Done = SaveControls()
		EndIf
		
		Flip

		SaveDraw()
		
	Wend
	
	G_Mode = C_ModeEdit
	G_EditTool = C_EditToolSelect
	PanelIconSelect(G_ToolPanel, G_IconSelect)
	FlushKeys
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return True
	
.SaveMenuFailed
	
	DialogBox("Save file cancelled", C_DialogBoxOK)
	
	G_Mode = C_ModeEdit
	G_EditTool = C_EditToolSelect
	PanelIconSelect(G_ToolPanel, G_IconSelect)
	FlushKeys
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return False
	
End Function

Function SaveControls()
	Local Bridge
	
	If C_FunctionTrace Then FunctionEntree("SaveControls")
	
	G_MouseX = MouseX()
	G_MouseY = MouseY()
	
	If MouseHit(1) > 0
		G_Mouse1 = True
	Else
		G_Mouse1 = False
	EndIf
	G_Mouse1Down = MouseDown(1)
	
	G_Mouse2 = MouseDown(2)
	G_Mouse3 = MouseDown(3)
	
	If KeyHit(C_Key_SysReq) Or KeyHit(C_Key_F12)
		; There is no Print_Screen key and SysReq doesn't work ?
		If PrintScreen()
			DialogBox("Print Screen - Press any key to resume", C_DialogBoxAny)
		EndIf
	EndIf
	
	Bridge = -1
	
	If G_Mouse1
		Bridge = G_FilesPageStart + G_FilesMouseOver
	EndIf
	
	If KeyHit(C_Key_0)
		Bridge = 0
	ElseIf KeyHit(C_Key_1)
		Bridge = 1
	ElseIf KeyHit(C_Key_2)
		Bridge = 2
	ElseIf KeyHit(C_Key_3)
		Bridge = 3
	ElseIf KeyHit(C_Key_4)
		Bridge = 4
	ElseIf KeyHit(C_Key_5)
		Bridge = 5
	ElseIf KeyHit(C_Key_6)
		Bridge = 6
	ElseIf KeyHit(C_Key_7)
		Bridge = 7
	ElseIf KeyHit(C_Key_8)
		Bridge = 8
	ElseIf KeyHit(C_Key_9)
		Bridge = 9
	Else
		If KeyHit(C_Key_Home)
			G_FilesPageStart = 0
			LoadScanFiles(G_FilesPageStart)
		ElseIf KeyHit(C_Key_End)
			G_FilesPageStart = 100 - G_FilesPerPage
			LoadScanFiles(G_FilesPageStart)
		ElseIf KeyHit(C_Key_Page_Down)
			If G_FilesPageStart + G_FilesPerPage < 100
				G_FilesPageStart = G_FilesPageStart + G_FilesPerPage
				LoadScanFiles(G_FilesPageStart)
			EndIf
		ElseIf KeyHit(C_Key_Page_Up)
			If G_FilesPageStart - G_FilesPerPage >= 0
				G_FilesPageStart = G_FilesPageStart - G_FilesPerPage
			Else
				G_FilesPageStart = 0
			EndIf
			LoadScanFiles(G_FilesPageStart)
		EndIf			
	EndIf
	
	If Bridge = -1
		If C_FunctionTrace Then FunctionEgress()
	
		Return False
	Else
		If Bridge = 0
			If C_FunctionTrace Then FunctionEgress()
	
			Return False
		EndIf
		
		If SaveBridgeV4(Bridge) = True
			If C_FunctionTrace Then FunctionEgress()
	
			Return True
		Else
			If C_FunctionTrace Then FunctionEgress()
	
			Return False
		EndIf
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SaveDraw()
	Local i
	Local BaseX, BaseY
	
	If C_FunctionTrace Then FunctionEntree("SaveDraw")
	
	SetBuffer BackBuffer()
	TileBlock G_SaveBackImage
	
	DrawImage G_SaveHeadImage, G_ScreenCentreX - ImageWidth(G_SaveHeadImage) / 2, 10
	
	G_FilesMouseOver = -1
	For i = 0 To G_FilesPerPage - 1
		If G_FilesPageStart + i > 99
			Exit
		EndIf
		
		BaseX = 10 + (i / G_FilesPerColumn) * G_FilesWidth
		BaseY = 120 + (i Mod G_FilesPerColumn) * G_FilesHeight
		
		G_Files(i)\EdgeLeft = BaseX
		G_Files(i)\EdgeRight = BaseX + G_ThumbnailWidth - 1
		G_Files(i)\EdgeTop = BaseY
		G_Files(i)\EdgeBottom = BaseY + G_ThumbnailHeight - 1
		
		If G_MouseX >= G_Files(i)\EdgeLeft And G_MouseX <= G_Files(i)\EdgeRight And G_MouseY >= G_Files(i)\EdgeTop And G_MouseY <= G_Files(i)\EdgeBottom
			G_FilesMouseOver = i
			Color 150, 50, 50
			Rect BaseX - 5, BaseY - 5, G_FilesWidth, G_ThumbnailHeight + 10, True
			;G_ScreenWidth / (G_FilesPerPage / G_FilesPerColumn)
		EndIf
		
		If G_Files(i)\Status = C_FileStatusOK
			Color 200, 200, 200
		Else
			Color 255, 100, 100
		EndIf
		
		Rect BaseX - 2, BaseY - 2, G_ThumbnailWidth + 4, G_ThumbnailHeight + 4, True
		
		DrawBlock G_Files(i)\Image, BaseX, BaseY
		
		Text BaseX + 140, BaseY, G_Files(i)\FileName
		
		DrawImage G_MouseImage, G_MouseX, G_MouseY
		
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SaveBridgeV3(Id)
	Local OFileName$
	Local OFile
	Local I
	
	If C_FunctionTrace Then FunctionEntree("SaveBridge")
	
	; Forced consistency check
	ConsistencyCheck()
	
	OFileName = "Data\Bridge XY" + Right("0" + Id, 2) + ".DAT"
	
	Select FileType(OFileName)
		Case 0
		Case 1
			If Id <> G_FileSlot
				Answer = DialogBox("Replace existing file ?", C_DialogBoxYN)
				If Answer = C_DialogBoxYes
					DeleteFile OFileName
				Else
					If C_FunctionTrace Then FunctionEgress()
					Return False
				EndIf
			Else
				DeleteFile OFileName
			EndIf
		Case 2
			GW_AbortErrorMessage("File name '" + OFileName + "' conflicts with directory name")
		Default
			GW_AbortErrorMessage("Unknown status in SaveBridge(" + Id + ")")
	End Select
	
	OFile = WriteFile(OFileName)
	
	WriteString OFile, "Bridge Data File"
	WriteString OFile, "Version"
	WriteInt OFile, 3
	
	BridgeDrawMini()
	WriteString OFile, "Thumbnail"
	WriteInt OFile, G_ThumbnailHeight
	WriteInt OFile, G_ThumbnailWidth
	Buffer = ImageBuffer(G_ThumbnailImage)
	
	If 1 = 0
		; Straight write of the pixels
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				V = ReadPixel (X, Y, Buffer)
				WriteInt OFile, V
			Next
		Next
	Else
		; Run length encoding
		
		V = ReadPixel(0, 0, Buffer) And $FFFFFF
		N = 0
		
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				C = ReadPixel(X, Y, Buffer) And $FFFFFF
				If C = V
					If N = 255
						WriteInt OFile, N Shl 24 + V
						N = 1
					Else
						N = N + 1
					EndIf
				Else
					WriteInt OFile, N Shl 24 + V
					V = C
					N = 1
				EndIf
			Next
		Next
		
		WriteInt OFile, N Shl 24 + V
	EndIf
	
	WriteString OFile, "Level"
	
	WriteInt OFile, G_LevelWidth
	WriteInt OFile, G_WaterDepth
	WriteInt OFile, levelheight
	WriteInt OFile, G_WaterLevel
	
	For H = 0 To G_LevelWidth - 1
		WriteFloat OFile, height(H)
	Next
	
	WriteString OFile, "Bolts"
	
	P = FilePos(OFile)		;start of bolts
	SeekFile OFile, p + 4	;skip short
	
	I = 0
	For b.T_Bolt=Each T_Bolt	;write boltdata
		WriteInt OFile, B\GroupId
		WriteFloat OFile, B\OriginalX
		WriteFloat OFile, B\OriginalY
		WriteByte OFile, B\BoltType
		I = I + 1
		B\Id = I
	Next
	
	SeekFile OFile, p	;set to start
	WriteInt OFile, I	;write number
	
	p = p + 4 + i * 13
	SeekFile OFile, p	;set to end/start of segment
	
	WriteString OFile, "Segments"
	
	P = FilePos(OFile)
	SeekFile OFile, p + 4

	I = 0
	For S.T_Segment = Each T_Segment
		WriteInt OFile, S\GroupId
		WriteShort OFile, S\Bolt1\Id
		WriteShort OFile, S\Bolt2\Id
		WriteByte OFile, S\Material\Id
		WriteByte OFile, S\SegmentType
		I = I + 1
	Next
	
	SeekFile OFile, p
	WriteInt OFile, I

	CloseFile OFile
	
	G_FileSlot = Id
	G_FileChanged = False
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return True
	
End Function

Function SaveBridgeV4(Id)
	Local OFileName$
	Local OFile
	Local I
	
	Local NBolts
	Local NSegments
	Local B.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("SaveBridge")
	
	; Forced consistency check
	ConsistencyCheck()
	
	OFileName = "Data\Bridge XZ" + Right("0" + Id, 2) + ".DAT"
	
	Select FileType(OFileName)
		Case 0
		Case 1
			If Id <> G_FileSlot
				Answer = DialogBox("Replace existing file ?", C_DialogBoxYN)
				If Answer = C_DialogBoxYes
					DeleteFile OFileName
				Else
					If C_FunctionTrace Then FunctionEgress()
					Return False
				EndIf
			Else
				DeleteFile OFileName
			EndIf
		Case 2
			GW_AbortErrorMessage("File name '" + OFileName + "' conflicts with directory name")
		Default
			GW_AbortErrorMessage("Unknown status in SaveBridge(" + Id + ")")
	End Select
	
	OFile = WriteFile(OFileName)
	
	WriteString OFile, "Bridge Data File"
	WriteString OFile, "Version"
	WriteInt OFile, 4
	
	BridgeDrawMini()
	WriteString OFile, "Thumbnail"
	WriteInt OFile, G_ThumbnailHeight
	WriteInt OFile, G_ThumbnailWidth
	Buffer = ImageBuffer(G_ThumbnailImage)
	
	If 1 = 0
		; Straight write of the pixels
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				V = ReadPixel (X, Y, Buffer)
				WriteInt OFile, V
			Next
		Next
	Else
		; Run length encoding
		
		V = ReadPixel(0, 0, Buffer) And $FFFFFF
		N = 0
		
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				C = ReadPixel(X, Y, Buffer) And $FFFFFF
				If C = V
					If N = 255
						WriteInt OFile, N Shl 24 + V
						N = 1
					Else
						N = N + 1
					EndIf
				Else
					WriteInt OFile, N Shl 24 + V
					V = C
					N = 1
				EndIf
			Next
		Next
		
		WriteInt OFile, N Shl 24 + V
	EndIf
	
	WriteString OFile, "Level"
	
	WriteInt OFile, G_LevelWidth
	WriteInt OFile, G_ShoreLeft
	WriteInt OFile, G_ShoreRight
	
	For H = 0 To G_LevelWidth - 1
		WriteFloat OFile, height(H)
	Next
	
	WriteString OFile, "Water"
	WriteInt OFile, G_WaterDepth
	WriteInt OFile, G_WaterLevel
	
	WriteString OFile, "Wind"
	WriteInt OFile, G_WindSpeedStart
	WriteInt OFile, G_WindSpeedLow
	WriteInt OFile, G_WindSpeedHigh
	WriteInt OFile, G_WindSpeedCycle
	
	WriteString OFile, "Train"
	WriteInt OFile, G_TrainStartX
	WriteInt OFile, G_TrainStartY
	WriteInt OFile, G_TrainLength
	WriteInt OFile, G_TrainStopX
	
	WriteString OFile, "Bolts"
	
	NBolts = 0
	For B = Each T_Bolt
		NBolts = NBolts + 1
		B\Id = NBolts
	Next
	
	WriteInt OFile, NBolts
.SV4	
	For B = Each T_Bolt
		WriteInt OFile, B\Id
		WriteInt OFile, B\BoltType
		WriteInt OFile, B\Locked
		WriteInt OFile, B\GroupId
		WriteInt OFile, B\DynamicId
		WriteInt OFile, B\ActionId
		WriteFloat OFile, B\OriginalX
		WriteFloat OFile, B\OriginalY
	Next
	
	WriteString OFile, "Segments"
	
	NSegments = 0
	For S = Each T_Segment
		NSegments = NSegments + 1
		S\Id = NSegments
	Next
	
	WriteInt OFile, NSegments
	
	For S = Each T_Segment
		WriteInt OFile, S\Id
		WriteInt OFile, S\SegmentType
		WriteInt OFile, S\Locked
		WriteInt OFile, S\GroupId
		WriteInt OFile, S\DynamicId
		WriteInt OFile, S\ActionId
		WriteInt OFile, S\Material\Id
		WriteInt OFile, S\Bolt1\Id
		WriteInt OFile, S\Bolt2\Id
	Next

	CloseFile OFile
	
	G_FileSlot = Id
	G_FileChanged = False
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return True
	
End Function

Function BridgeClear(flag)
	
	If C_FunctionTrace Then FunctionEntree("BridgeClear")
	
	; Check if the current file has been changed and prompt to save it before loading
	If G_FileChanged
		Answer = DialogBox("Current file modified.  Save first ?", C_DialogBoxYNC)
		If Answer = C_DialogBoxYes
			SaveMenu()
		ElseIf Answer = C_DialogBoxCancel
			G_Mode = C_ModeEdit
			If C_FunctionTrace Then FunctionEgress()
			Return False
		EndIf
	EndIf
	
	If flag=True Then
		Delete Each T_Segment
		Delete Each T_Bolt
		Delete Each T_Wheel
	Else
		For S.T_Segment = Each T_Segment
			If S\SegmentType <> C_SegmentTypeFixed And S\SegmentType <> C_SegmentTypeFixedRail
				Delete S
			EndIf
		Next
		
		For B.T_Bolt=Each T_Bolt
			If B\BoltType <> C_BoltTypeFixed
				Delete B
			EndIf
		Next
		
		Delete Each T_Wheel
	End If
	
	G_FileSlot = -1
	G_FileChanged = False
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return True

End Function

Function BridgeNew()
	Local B1.T_Bolt, B2.T_Bolt
	Local S.T_Segment
	
	If C_FunctionTrace Then FunctionEntree("BridgeNew")
	
	RX = -10 * G_BlockSize
	While RX <= G_LevelWidth + 10 * G_BlockSize
		B1 = New T_Bolt
		B1\OriginalX = RX
		B1\OriginalY = 0
		If RX <= G_ShoreLeft Or RX >= G_ShoreRight
			B1\BoltType = C_BoltTypeFixed
		Else
			B1\BoltType = C_BoltTypeRegular
		EndIf
		B1\Locked = True
		RX = RX + C_SegmentLengthNormal
	Wend
	
	B1 = First T_Bolt
	B2 = After B1
	While B2 <> Null
		S = New T_Segment
		S\Bolt1 = B1
		S\Bolt2 = B2
		
		S\Length = blocksize
		S\SegmentType = rail
		S\Material = G_MaterialSteelBeam
		S\SegmentType = C_SegmentTypeRail
		S\Locked = True
		B1 = B2
		B2 = After B1
	Wend

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function PrintScreen()
	Local FileName$

	If C_FunctionTrace Then FunctionEntree("PrintScreen")
	
	N = 0
	While N < 100
		FileName = "Screen" + Right$("00" + N, 3) + ".BMP"
		If FileType(FileName) = 0
			SaveBuffer(FrontBuffer(), FileName)
			
			If C_FunctionTrace Then FunctionEgress()
	
			Return True
		EndIf
		
		N = N + 1
	Wend
	
	; Only drops through if there is no available slot
	
	DialogBox("No available file slot for PrintScreen", C_DialogBoxOK)
	
	If C_FunctionTrace Then FunctionEgress()
	
	Return False
	
End Function

Function ConvertFiles()
	Local FileN
	Local SrcFile, SrcExists, SrcFileName$, SrcImage
	Local DstFile, DstExists, DstFileName$, DstImage
	Local Check$
	Local Version
	Local ChangeCount = 0
	Local AutoOverwrite
	
	If C_FunctionTrace Then FunctionEntree("ConvertFiles")
	
	Answer = DialogBox("Automatically overwrite exsting files (Y/N/C) ?", C_DialogBoxYNC)
	If Answer = C_DialogBoxCancel
		Return
	ElseIf Answer = C_DialogBoxYes
		AutoOverwrite = True
	Else
		AutoOverwrite = False
	EndIf
	
	SrcImage = CreateImage(G_ThumbnailWidth, G_ThumbnailHeight)
	DstImage = CreateImage(G_ThumbnailWidth, G_ThumbnailHeight)
	
	For FileN = 0 To 99
		SrcFileName = "Data\Bridge XY" + Right("0" + (i + FileN), 2) + ".DAT"
		DstFileName = "Data\Bridge XZ" + Right("0" + (i + FileN), 2) + ".DAT"
		
		If FileType(SrcFileName) = 1
			SrcExists = True
			SrcFile = OpenFile(SrcFileName)
			
			Check = ReadString$(SrcFile)
			If Check <> "Bridge Data File"
				GW_AbortErrorMessage("Invalid string in '" + SrcFileName + "'  Expected 'Bridge Data File'  Found '" + Check + "'")
			EndIf
			
			Check = ReadString$(SrcFile)
			If Check <> "Version"
				GW_AbortErrorMessage("Invalid string in '" + SrcFileName + "'  Expected 'Version'  Found '" + Check + "'")
			EndIf
			
			Version = ReadInt(SrcFile)
			If Version = 1
			ElseIf Version = 2
			ElseIf Version = 3
			Else
				GW_AbortErrorMessage("Invalid file version in '" + SrcFileName + "'  Expected 1  Found " + Version)
			EndIf
			
			Check = ReadString$(SrcFile)
			If Check <> "Thumbnail"
				GW_AbortErrorMessage("Invalid string in '" + FileName + "'  Expected 'Thumbnail'  Found '" + Check + "'")
			EndIf
			
			SrcImage = LoadThumbnail(SrcImage, SrcFile)
			
			CloseFile SrcFile
			SrcFile = 0
		Else
			SrcExists = False
		EndIf
		
		If FileType(DstFileName) = 1
			DstExists = True
			
			DstFile = OpenFile(DstFileName)
			
			Check = ReadString$(DstFile)
			If Check <> "Bridge Data File"
				GW_AbortErrorMessage("Invalid string in '" + DstFileName + "'  Expected 'Bridge Data File'  Found '" + Check + "'")
			EndIf
			
			Check = ReadString$(DstFile)
			If Check <> "Version"
				GW_AbortErrorMessage("Invalid string in '" + DstFileName + "'  Expected 'Version'  Found '" + Check + "'")
			EndIf
			
			Version = ReadInt(DstFile)
			If Version = 1
				IntSize = 2
			ElseIf Version = 2
				IntSize = 4
			ElseIf Version = 3
				IntSize = 4
			Else
				GW_AbortErrorMessage("Invalid file version in '" + DstFileName + "'  Expected 1  Found " + Version)
			EndIf
			
			Check = ReadString$(DstFile)
			If Check <> "Thumbnail"
				GW_AbortErrorMessage("Invalid string in '" + DstFileName + "'  Expected 'Thumbnail'  Found '" + Check + "'")
			EndIf
			
			DstImage = LoadThumbnail(DstImage, DstFile)
			
			CloseFile DstFile
			DstFile = 0
		Else
			DstExists = False
		EndIf
		
		SetBuffer BackBuffer()
		Cls
		
		Color 200, 200, 200
		Rect 0, 0, G_ScreenWidth - 1, G_ScreenHeight - 1, False
		
		Color 100, 255, 100
		If SrcExists
			Text 10, 10, "File " + FileN
			DrawBlock SrcImage, 10, 30
		Else
			Color 100, 255, 100
			Text 10, 10, "No file " + FileN
		EndIf
		
		If DstExists
			DrawBlock DstImage, 10 + G_ThumbnailWidth + 10, 30
		EndIf
		
		Flip True
		
		Answer = C_DialogBoxYes
		If DstExists
			If AutoOverwrite
				DeleteFile DstFileName
			Else
				Answer = DialogBox("Overwrite existing file (Y/N/C", C_DialogBoxYNC)
				If Answer = C_DialogBoxCancel
					Exit
				ElseIf Answer = C_DialogBoxYes
					DeleteFile DstFileName
				EndIf
			EndIf
		EndIf
		
		If SrcExists And Answer = C_DialogBoxYes
			LoadBridgeV3(FileN)
			SaveBridgeV4(FileN)
			ChangeCount = ChangeCount + 1
			
			SetBuffer FrontBuffer()
			Color 100, 255, 100
			Text 10 + G_ThumbnailWidth + 10, 10, "Converted"
			
			Delay 100
		Else
			SetBuffer FrontBuffer()
			Color 100, 255, 100
			Text 10 + G_ThumbnailWidth + 10, 10, "Not converted"
			
			Delay 10
		EndIf
	Next
	
	If SrcImage <> 0
		FreeImage SrcImage
		SrcImage = 0
	EndIf
	
	If DstImage <> 0
		FreeImage DstImage
		DstImage = 0
	EndIf
	
	If ChangeCount = 0
		DialogBox("No files converted", C_DialogBoxOK)
	Else
		DialogBox(ChangeCount + " files converted", C_DialogBoxOK)
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

; Load a thumbnail
;   - File should be positioned at the W/H integers
;   - Img is allocated if not already given and resized if necessary
;   - Returns the Img handle which may change
;   - file will be left positioned after last integer of the thumbnail
Function LoadThumbnail(Img, IFile)
	
	Local W, H
	Local X, Y
	Local Buffer, V, C, N
	
	H = ReadInt(IFile)
	W = ReadInt(IFile)
	
	If Img = 0
		Img = CreateImage(W, H)
	Else
		If ImageWidth(Img) <> W Or ImageHeight(Img) <> H
			FreeImage Img
			Img = CreateImage(W, H)
		EndIf
	EndIf
	
	Buffer = ImageBuffer(Img)
	
	SetBuffer Buffer
	ClsColor 0, 0, 0
	Cls
	
	If 1 = 0
		; Straight pixels
		For Y = 0 To G_ThumbnailHeight - 1
			For X = 0 To G_ThumbnailWidth - 1
				V = ReadInt(IFile) And $FFFFFF
				WritePixel X, Y, V, Buffer
			Next
		Next
	Else
		; Run length encoded
		Y = 0
		X = 0
		
		While Y < H
			V = ReadInt(IFile)
			C = V And $FFFFFF
			N = V Shr 24
			
			While N > 0
				If N = 1
					WritePixel X, Y, C, Buffer
					X = X + 1
					N = 0
				ElseIf X + N <= W
					Color 0, 0, C
					Rect X, Y, N, 1
					X = X + N
					N = 0
				Else
					Color 0, 0, C
					Rect X, Y, W - X, 1
					N = N - W + X
					X = W
				EndIf
			
				If X >= W
					X = 0
					Y = Y + 1
				EndIf
			Wend
		Wend
	EndIf
			
	Return Img
End Function