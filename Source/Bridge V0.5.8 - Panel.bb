; --------------------------------------------------------------------------------------------------------------------------------
;
; Panel Module
;
; --------------------------------------------------------------------------------------------------------------------------------

Function ToolPanelInitialise()
	Local W, H
	
	Local I.T_Icon
	
	Local TempIcon
	
	TempIcon = GW_LoadImage("Images\", "Icon - 000.PNG")
	G_ToolPanelIconWidth = ImageWidth(TempIcon)
	G_ToolPanelIconHeight = ImageHeight(TempIcon)

	W = 1 + (1 + G_ToolPanelIconWidth + 1) * G_ToolPanelToolsWide + 1
	H = 1 + 20 + 1 + (1 + G_ToolPanelIconHeight + 1) * G_ToolPanelToolsHigh + 1
	
	FreeImage TempIcon
	TempIcon = 0
	
	G_ToolPanel = PanelCreate(C_PanelTypeToolBox, "Tool Box", "Tools", "TB", W, H, 10, 80)
	
	For M.T_Material = Each T_Material
		M\Icon\Status = C_IconStatusNormal
		M\Icon\HoverText = M\Name
		PanelIconAdd(G_ToolPanel, M\Icon)
	Next

	G_MaterialPrimary\Icon\Status = C_IconStatusSelected
	
	G_IconBuild			= ToolIconInitialise(C_IconStatusNormal, 	1, 6,	"Build (A)", True)
	G_IconBuildMultiple = ToolIconInitialise(C_IconStatusNormal,	1, 7,	"Build Multiple", False)
	G_IconGroup			= ToolIconInitialise(C_IconStatusNormal,	1, 24,	"Group (G)", False)
	G_IconReplace		= ToolIconInitialise(C_IconStatusNormal,	1, 13,	"Replace (R)", False)

	G_IconSelect		= ToolIconInitialise(C_IconStatusSelected,	1, 5,	"Select (Z)", True)
	G_IconCopy			= ToolIconInitialise(C_IconStatusNormal,	1, 18,	"Copy (C)", False)
	G_IconMove			= ToolIconInitialise(C_IconStatusNormal,	1, 19,	"Move (V)", False)
	G_IconStretch		= ToolIconInitialise(C_IconStatusNormal,	1, 20,	"Stretch (B)", False)
	
	G_IconFlipX			= ToolIconInitialise(C_IconStatusNormal,	1, 21,	"Flip X (X)", True)
	G_IconFlipY			= ToolIconInitialise(C_IconStatusNormal,	1, 22,	"Flip Y (Y)", False)
	G_IconRotate		= ToolIconInitialise(C_IconStatusNormal,	1, 23,	"Rotate", False)
	G_IconTest			= ToolIconInitialise(C_IconStatusNormal,    1, 25,  "Test (Space)", False)
	
	G_IconMacroLine		= ToolIconInitialise(C_IconStatusNormal,	1, 8,	"Line", True)
	G_IconMacroBeam		= ToolIconInitialise(C_IconStatusNormal,	1, 9,	"Beam", False)
	G_IconMacroArch		= ToolIconinitialise(C_IconStatusNormal,	1, 10,	"Arch", False)
	G_IconMacroCircle	= ToolIconinitialise(C_IconStatusNormal,	1, 11,	"Circle", False)
	
	G_IconMacroTower	= ToolIconinitialise(C_IconStatusNormal,	1, 12,	"Tower", True)
	ToolIconinitialise(C_IconStatusNormal,	1, 12,	"Stub", False)
	ToolIconinitialise(C_IconStatusNormal,	1, 12,	"Stub", False)
	ToolIconinitialise(C_IconStatusNormal,	1, 12,	"Stub", False)

	G_IconLoad			= ToolIconInitialise(C_IconStatusNormal,	1, 14,	"Load Bridge (L)", True)
	G_IconSave			= ToolIconInitialise(C_IconStatusNormal,	1, 15,	"Save Bridge (S)", False)
	G_IconNew			= ToolIconInitialise(C_IconStatusNormal,	1, 12,	"New Bridge (N)", False)
	G_IconExit			= ToolIconInitialise(C_IconStatusNormal,	1, 12,	"Exit (Q)", False)

	PanelIconArrange(G_ToolPanel)
	PanelRender(G_ToolPanel)
	
	G_ToolPanel\Status = C_PanelStatusShow
	
End Function

Function ToolIconInitialise.T_Icon(Status, Group, Image, Hover$, NewLine)
	Local I.T_Icon
	
	I = New T_Icon
	I\Status = Status
	I\Group = Group
	IconImageLoad(I, Image)
	I\NewLine = NewLine
	I\HoverText = Hover
	
	PanelIconAdd(G_ToolPanel, I)
	
	Return I
End Function

Function ToolPanelDraw()

	DrawBlock G_ToolPanel\Image, G_ToolPanel\EdgeLeft, G_ToolPanel\EdgeTop
	
	If G_HoverDisplay And G_MousePanelOver <> Null And G_MouseIconOver <> Null
		X = G_MousePanelOver\EdgeLeft + G_MouseIconOver\EdgeLeft + G_MouseIconOver\Width / 2
		Y = G_MousePanelOver\EdgeTop + G_MouseIconOver\EdgeBottom + 2
		W = StringWidth(G_MouseIconOver\HoverText) + 10
		H = StringHeight(G_MouseIconOver\HoverText) + 4
		
		Color 100, 100, 100
		Rect X - W / 2, Y, W, H, True
		
		Color 255, 255, 50
		Rect X - W / 2, Y, W, H, False
		
		Color 255, 255, 255
		Text X, Y + 2, G_MouseIconOver\HoverText, True, False
	EndIf

End Function

Function SuperPanelInitialise()
	Local W, H
	
	Local I.T_Icon
	
	Local TempIcon
	
	TempIcon = GW_LoadImage("Images\", "Icon - 000.PNG")
	G_SuperPanelIconWidth = ImageWidth(TempIcon)
	G_SuperPanelIconHeight = ImageHeight(TempIcon)

	W = 1 + (1 + G_SuperPanelIconWidth + 1) * G_SuperPanelToolsWide + 1
	H = 1 + 20 + 1 + (1 + G_SuperPanelIconHeight + 1) * G_SuperPanelToolsHigh + 1
	
	FreeImage TempIcon
	TempIcon = 0
	
	G_SuperPanel = PanelCreate(C_PanelTypeToolBox, "Super Box", "Super", "SB", W, H, G_ToolPanel\EdgeLeft, G_ToolPanel\EdgeBottom + 10)
	
	G_SuperIconUnlock		= SuperIconInitialise(C_IconStatusNormal,	1, 12,	"Unlock (U)", True)
	G_SuperIconLock			= SuperIconInitialise(C_IconStatusNormal,	1, 12,	"Lock (I)", False)
	
	G_SuperIconNormal		= SuperIconInitialise(C_IconStatusNormal,	2, 12,	"Normal (-)", True)
	G_SuperIconRail			= SuperIconInitialise(C_IconStatusNormal,	2, 12,	"Rail (=)", False)
	
	G_SuperIconFreeBolt		= SuperIconInitialise(C_IconStatusNormal,	2, 12,	"Free (;)", True)
	G_SuperIconFixedBolt	= SuperIconInitialise(C_IconStatusNormal,	2, 12,	"Fixed (')", False)
	
	G_SuperIconLoad			= SuperIconInitialise(C_IconStatusNormal,	3, 14,	"Load Scenario (L)", True)
	G_SuperIconSave			= SuperIconInitialise(C_IconStatusNormal,	3, 15,	"Save Scenario (S)", False)
	
	PanelIconArrange(G_SuperPanel)
	PanelRender(G_SuperPanel)
	
	G_SuperPanel\Status = C_PanelStatusShow
	
End Function

Function SuperIconInitialise.T_Icon(Status, Group, Image, Hover$, NewLine)
	Local I.T_Icon
	
	I = New T_Icon
	I\Status = Status
	I\Group = Group
	IconImageLoad(I, Image)
	I\NewLine = NewLine
	I\HoverText = Hover
	
	PanelIconAdd(G_SuperPanel, I)
	
	Return I
End Function

Function SuperPanelDraw()

	DrawBlock G_SuperPanel\Image, G_SuperPanel\EdgeLeft, G_SuperPanel\EdgeTop
	
	If G_HoverDisplay And G_MousePanelOver <> Null And G_MouseIconOver <> Null
		X = G_MousePanelOver\EdgeLeft + G_MouseIconOver\EdgeLeft + G_MouseIconOver\Width / 2
		Y = G_MousePanelOver\EdgeTop + G_MouseIconOver\EdgeBottom + 2
		W = StringWidth(G_MouseIconOver\HoverText) + 10
		H = StringHeight(G_MouseIconOver\HoverText) + 4
		
		Color 100, 100, 100
		Rect X - W / 2, Y, W, H, True
		
		Color 255, 255, 50
		Rect X - W / 2, Y, W, H, False
		
		Color 255, 255, 255
		Text X, Y + 2, G_MouseIconOver\HoverText, True, False
	EndIf

End Function

Function IconImageLoad(I.T_Icon, N)
	Local FileName$
	
	FileName = "Icon - " + Right$("000" + (N * 4 + 0), 3) + ".PNG"
	I\ImageNormal 		= GW_LoadImage("Images\", FileName)
	
	FileName = "Icon - " + Right$("000" + (N * 4 + 1), 3) + ".PNG"
	I\ImageSelected 	= GW_LoadImage("Images\", FileName)
	
	FileName = "Icon - " + Right$("000" + (N * 4 + 2), 3) + ".PNG"
	I\ImageDisabled 	= GW_LoadImage("Images\", FileName)
	
	FileName = "Icon - " + Right$("000" + (N * 4 + 3), 3) + ".PNG"
	I\ImageUnavailable 	= GW_LoadImage("Images\", FileName)
	
End Function

Function PanelCreate.T_Panel(PanelType, Name1$, Name2$, Name3$, W, H, X, Y)
	Local P.T_Panel
	
	P = New T_Panel
	
	P\PanelType = PanelType
	
	P\Name1 = Name1
	P\Name2 = Name2
	P\Name3 = Name3
	
	P\MaxWidth = W 
	P\MaxHeight = H
	
	P\EdgeLeft = X
	P\EdgeRight = X + W - 1
	P\EdgeTop = Y
	P\EdgeBottom = Y + H - 1
	
	P\CentreX = W / 2
	P\CentreY = H / 2
	
	P\IconFirst = Null
	P\IconLast = Null
	
	Return P
	
End Function

Function PanelDelete.T_Panel(P.T_Panel)

	If P\Image <> 0
		FreeImage P\Image
		P\Image = 0
	EndIf
	
	Delete P
	
	Return Null
End Function

Function PanelMove(P.T_Panel, X, Y)
	P\EdgeLeft = X
	P\EdgeRight = X + P\AdjWidth - 1
	P\EdgeTop = Y
	P\EdgeBottom = Y + P\AdjHeight - 1
End Function

Function PanelSize(P.T_Panel, W, H)
	P\MaxWidth = W 
	P\MaxHeight = H
	
	P\EdgeLeft = X
	P\EdgeRight = P\EdgeLeft + W - 1
	P\EdgeTop = Y
	P\EdgeBottom = P\EdgeTop + H - 1
	
	P\CentreX = W / 2
	P\CentreY = H / 2
End Function	

Function PanelIconAdd.T_Icon(P.T_Panel, I.T_Icon)
	
	I\Width  = ImageWidth(I\ImageNormal)
	I\Height = ImageHeight(I\ImageNormal)
	
	; Link it to the end of the icon list of the panel
	If P\IconLast = Null
		P\IconFirst = I
		P\IconLast = I
	Else
		P\IconLast\IconNext = I
		P\IconLast = I
	EndIf
	
	I\IconNext = Null
	
	Return I

End Function

Function PanelIconArrange(P.T_Panel)
	Local I.T_Icon
	Local RowIconHeight = 0
	Local MaxWidth, MaxHeight
	
	I = P\IconFirst
	
	X = 1
	Y = 1 + 20 + 1
	While I <> Null
		If I\NewLine Or (X <> 1 And X + I\Width > P\MaxWidth)
			X = 1
			Y = Y + RowIconHeight + 1
			RowIconHeight = 0
		EndIf
			
		I\EdgeLeft = X
		I\EdgeRight = I\EdgeLeft + I\Width
		I\EdgeTop = Y
		I\EdgeBottom = I\EdgeTop + I\Height
		
		X = X + I\Width + 1
		If X > MaxWidth
			MaxWidth = X
		EndIf
		
		If I\Height > RowIconHeight
			RowIconHeight = I\Height
		EndIf
		
		I = I\IconNext
	Wend
	
	MaxHeight = Y + RowIconHeight + 1
	
	If MaxWidth <> P\AdjWidth Or MaxHeight <> P\AdjHeight
		; Resize the image
		P\AdjWidth = MaxWidth
		P\AdjHeight = MaxHeight
		
		If P\Image <> 0
			FreeImage P\Image
		EndIf
		
		P\Image = CreateImage(P\AdjWidth, P\AdjHeight)
	EndIf
	
	P\EdgeRight = P\EdgeLeft + P\AdjWidth - 1
	P\EdgeBottom = P\EdgeTop + P\AdjHeight - 1
	
End Function

Function PanelIconScan(P.T_Panel)
	Local I.T_Icon
	
	OffsetX = G_MouseX - P\EdgeLeft
	OffsetY = G_MouseY - P\EdgeTop
	
	I = P\IconFirst
	G_MouseIconOver = Null
	G_HoverDisplay = False
	
	; Could optimise this by checking the icon that the mouse was over last time since almost always
	;   it will still be the same one that the mouse is over now
	
	While I <> Null
		
		If I\Status <> C_IconStatusDisabled And I\Status <> C_IconStatusUnavailable
			If OffsetX >= I\EdgeLeft And OffsetX <= I\EdgeRight And OffsetY >= I\EdgeTop And OffsetY <= I\ EdgeBottom
				;Stop
				G_MouseIconOver = I
				If I\HoverTime <> 0
					If G_MainTime > I\HoverTime + G_HoverDelay
						G_HoverDisplay = True
					End If
				Else
					I\HoverTime = G_MainTime
				EndIf
			Else
				I\HoverTime = 0
			EndIf
		EndIf
		
		I = I\IconNext
	Wend
	
End Function

Function PanelIconSelect(P.T_Panel, I.T_Icon)
	Local TempIcon.T_Icon
	
	If I\Group <> 0
		; Turn off other icons in the same group
		TempIcon = P\IconFirst
	
		While TempIcon <> Null
		
			If TempIcon <> I And TempIcon\Group <> 0 And TempIcon\Group = I\Group And TempIcon\Status = C_IconStatusSelected
				TempIcon\Status = C_IconStatusNormal
			EndIf
			TempIcon = TempIcon\IconNext
		Wend
	EndIf
	
	I\Status = C_IconStatusSelected
	
	PanelRender(P)
	
	;G_Channel1 = PlaySound(G_Sound1)
	
End Function

Function PanelRender(P.T_Panel)
	Local I.T_Icon
	Local Title$
	Local ColourBodyBack
	Local ColourBodyBorder
	Local ColourTitleBack
	Local ColourtitleBorder

	SetBuffer ImageBuffer(P\Image)
	
	If P\PanelType = C_PanelTypeDialogBox
		ColourBodyBack = GW_ColorMake(75, 125, 75)
		ColourBodyBorder = GW_ColorMake(120, 200, 120)
		ColourTitleBack = GW_ColorMake(40, 80, 40)
		ColourTitleBorder = ColourBodyBorder
	Else
		; C_PanelTypeToolBox and anything else by default
		ColourBodyBack = GW_ColorMake(75, 75, 75)
		ColourBodyBorder = GW_ColorMake(120, 120, 200)
		ColourTitleBack = GW_ColorMake(40, 40, 80)
		ColourTitleBorder = ColourBodyBorder
	EndIf
	
	Color 0, 0, ColourBodyBack
	Rect 0, 0, P\AdjWidth, P\AdjHeight, True

	Color 0, 0, ColourBodyBorder
	Rect 0, 0, P\AdjWidth, P\AdjHeight, False
	
	Color 0, 0, ColourTitleBack
	Rect 1, 1, P\AdjWidth - 2, 20, True
	
	Color 0, 0, ColourTitleBorder
	Rect 0, 0, P\AdjWidth, 22, False

	Title = P\Name1
	If StringWidth(Title) > P\AdjWidth - 2
		Title = P\Name2
		If StringWidth(Title) > P\AdjWidth - 2
			Title = P\Name3
			If StringWidth(Title) > P\AdjWidth - 2
				Title = ""
			EndIf
		EndIf
	EndIf
	
	If Title <> ""
		Color 255, 255, 255
		Text P\AdjWidth / 2, 10, Title, True, True
	EndIf
	
	If P\Logo <> 0
		DrawImage P\Logo, P\LogoX, P\LogoY
	EndIf
	
	If P\Message <> ""
		Color 255, 255, 255
		Text P\MessageX, P\MessageY, P\Message, False, False
	EndIf
	
	I = P\IconFirst
	
	While I <> Null
		If I\Status = C_IconStatusDisabled
			Img = I\ImageDisabled
		ElseIf I\Status = C_IconStatusNormal
			Img = I\ImageNormal
		ElseIf I\Status = C_IconStatusSelected
			Img = I\ImageSelected
		ElseIf I\Status = C_IconStatusUnavailable
			Img = I\ImageUnavailable
		Else
			Img = 0
		EndIf

		If Img <> 0
			DrawBlock Img, I\EdgeLeft, I\EdgeTop
		EndIf

		I = I\IconNext
	Wend
	
End Function

; Adjust panel position if any part goes off screen
Function PanelVisibility(P.T_Panel)

	If P\EdgeRight >= G_ScreenWidth
		P\EdgeLeft = P\EdgeLeft - (P\EdgeRight - G_ScreenWidth + 1)
	EndIf
	
	If P\EdgeBottom >= G_ScreenHeight
		P\EdgeTop = P\EdgeTop - (P\EdgeBottom - G_ScreenHeight + 1)
	EndIf
	
	If P\EdgeLeft < 0
		P\EdgeLeft = 0
	EndIf
	
	If P\EdgeTop < 0 
		P\EdgeTop = 0
	EndIf

	P\EdgeRight = P\EdgeLeft + P\AdjWidth - 1
	P\EdgeBottom = P\EdgeTop + P\AdjHeight - 1
	
	P\CentreX = P\AdjWidth / 2
	P\CentreY = P\AdjHeight / 2
	
End Function

Function DialogBox(Msg$, Flags)
	Local DialogPanel.T_Panel
	
	;If G_Mode = C_ModeEdit Or G_Mode = C_ModeTest
	;	DrawMain()
	;ElseIf G_Mode = C_ModeLoad
	;	LoadDraw()
	;ElseIf G_Mode = C_ModeSave
	;Else
	;	GW_AbortErrorMessage("Unknown mode " + G_Mode + " in DialogBox(). Message: " + Msg)
	;EndIf
	
	; Save FrontBuffer to SaveBuffer so that it can be restored
	If G_SaveBuffer = 0
		G_SaveBuffer = CreateImage(G_ScreenWidth, G_ScreenHeight)
	EndIf
	CopyRect 0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, FrontBuffer(), ImageBuffer(G_SaveBuffer)
	
	If G_GreyBuffer = 0
		G_GreyBuffer = CreateImage(G_ScreenWidth, G_ScreenHeight)
	EndIf
	CopyRect 0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, FrontBuffer(), ImageBuffer(G_GreyBuffer)
	;GreyCopy(0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, ImageBuffer(G_SaveBuffer), ImageBuffer(G_GreyBuffer))
	
	W = StringWidth(Msg) + 100
	H = 170
	X = G_ScreenCentreX - W / 2
	Y = G_ScreenCentreY - H / 2
	
	; Prepare the panel for the dialog box
	DialogPanel = PanelCreate(C_PanelTypeDialogBox, "Dialog Box", "Dialog", "DB", W, H, X, Y)
	DialogPanel\Message = Msg
	
	; Temp ####
	DialogPanel\Logo = G_PanelLogoWarning
	
	; Add standard buttons
	If (Flags And C_DialogBoxYes) <> 0
		PanelIconAdd(DialogPanel, G_ButtonYes\Icon)
		G_ButtonYes\Icon\Status = C_IconStatusSelected
	EndIf
	
	If (Flags And C_DialogBoxNo) <> 0
		PanelIconAdd(DialogPanel, G_ButtonNo\Icon)
		G_ButtonNo\Icon\Status = C_IconStatusNormal
	EndIf
	
	If (Flags And C_DialogBoxCancel) <> 0
		PanelIconAdd(DialogPanel, G_ButtonCancel\Icon)
		G_ButtonCancel\Icon\Status = C_IconStatusNormal
	EndIf
	
	If (Flags And C_DialogBoxOK) <> 0
		PanelIconAdd(DialogPanel, G_ButtonOK\Icon)
		G_ButtonCancel\Icon\Status = C_IconStatusNormal
	EndIf
	
	DialogIconArrange(DialogPanel)
	
	PanelRender(DialogPanel)
	
	GreyY = 0
	
	FlushKeys()
	Response = 0
	Repeat
		; Fade the background progressively
		If GreyY <= G_ScreenHeight - 1
			SrcB = ImageBuffer(G_SaveBuffer)
			DstB = ImageBuffer(G_GreyBuffer)
			
			LockBuffer SrcB
			LockBuffer DstB

			t = MilliSecs()
			While (GreyY <= G_ScreenHeight - 1) And (MilliSecs() - t < 20)
				For GreyX = 0 To G_ScreenWidth - 1
					V = ReadPixelFast(GreyX, GreyY, SrcB)
					WritePixelFast GreyX, GreyY, (V Shr 1 And $7F7F7F), DstB	; Reduce to 1/2
				Next
				
				GreyY = GreyY + 1
			Wend
			
			UnlockBuffer DstB
			UnlockBuffer SrcB
			
		EndIf
		
		CopyRect 0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, ImageBuffer(G_GreyBuffer), BackBuffer()
		;GreyCopy(0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, ImageBuffer(G_SaveBuffer), BackBuffer())

		; Draw dialog box
		SetBuffer BackBuffer()
		
		;Color 75, 50, 100
		;Rect X, Y, W, H, True
		;Color 40, 30, 60 ;150, 110, 200
		;Rect X + 1, Y + 1, W - 2, H - 2, False
		;Color 255, 255, 255
		;Rect X, Y, W, H, False
		
		;Text G_ScreenCentreX, Y + 20, Msg, True, False
		
		DrawBlock DialogPanel\Image, DialogPanel\EdgeLeft, DialogPanel\EdgeTop
		
		Color 250, 150, 150
		G_MouseX = MouseX()
		G_MouseY = MouseY()
		
		If MouseHit(1) > 0
			G_Mouse1 = True
		Else
			G_Mouse1 = False
		EndIf
		G_Mouse1Down = MouseDown(1)
		
		Oval G_MouseX - 5, G_MouseY - 5, 10, 10, True
		
		Flip True
		
		; Process any input
		
		If (Flags And C_DialogBoxAny) <> 0 And GetKey() <> 0
			Response = C_DialogBoxAny
		ElseIf (Flags And C_DialogBoxYes) <> 0 And KeyHit(C_Key_Y)
			Response = C_DialogBoxYes
		ElseIf (Flags And C_DialogBoxNo) <> 0 And KeyHit(C_Key_N)
			Response = C_DialogBoxNo
		ElseIf (Flags And C_DialogBoxCancel) <> 0 And KeyHit(C_Key_Escape)
			Response = C_DialogBoxCancel
		ElseIf (Flags And C_DialogBoxOK) <> 0 And KeyHit(C_Key_Enter)
			Response = C_DialogBoxOK
		EndIf
		
		If G_MouseX >= DialogPanel\EdgeLeft And G_MouseX <= DialogPanel\EdgeRight And G_MouseY >= DialogPanel\EdgeTop And G_MouseY <= DialogPanel\EdgeBottom
			G_MousePanelOver = DialogPanel
			
			PanelIconScan(DialogPanel)
			If G_MouseIconOver <> Null
				PanelIconSelect(DialogPanel, G_MouseIconOver)
				
				If G_Mouse1
					If (Flags And C_DialogBoxYes) <> 0 And G_MouseIconOver = G_ButtonYes\Icon
						Response = C_DialogBoxYes
					ElseIf (Flags And C_DialogBoxNo) <> 0 And G_MouseIconOver = G_ButtonNo\Icon
						Response = C_DialogBoxNo
					ElseIf (Flags And C_DialogBoxCancel) <> 0 And G_MouseIconOver = G_ButtonCancel\Icon
						Response = C_DialogBoxCancel
					ElseIf (Flags And C_DialogBoxOK) <> 0 And G_MouseIconOver = G_ButtonOK\Icon
						Response = C_DialogBoxOK
					EndIf
				EndIf
			EndIf
		EndIf

	Until Response <> 0
	
	DialogPanel = PanelDelete(DialogPanel)
	
	FlushKeys()
	
	; Restore FrontBuffer()
	CopyRect 0, 0, G_ScreenWidth, G_ScreenHeight, 0, 0, ImageBuffer(G_SaveBuffer), BackBuffer()
	Flip
	
	Return Response
		
End Function

Const C_DialogBorder = 10
Const C_DialogSpacing = 20

Function DialogIconArrange(P.T_Panel)
	Local I.T_Icon
	Local Width, Height, W1, H1, W2, H2
	Local nButtons
	
	; Determine Image + Message + space
	;   (a bit ugly - need to generalise the H1, H2 stuff)
	
	W1 = C_DialogBorder
	H1 = C_DialogBorder + 1 + 20 + 1
	
	If P\Logo <> 0
		P\LogoX = W1
		P\LogoY = H1
		W1 = W1 + ImageWidth(P\Logo)
		H1 = H1 + ImageHeight(P\Logo)
	EndIf
	
	If P\Message <> ""
		If P\Logo = 0
			W1 = W1 + C_DialogSpacing
		EndIf
		
		P\MessageX = W1
		W1 = W1 + StringWidth(P\Message)
		
		H2 = C_DialogBorder + 1 + 20 + 1 + StringHeight(P\Message)
		If H2 > H1
			H1 = H2
			P\MessageY = C_DialogBorder + 1 + 20 + 1
		Else
			P\MessageY = P\LogoY + (H1 - H2) / 2
		EndIf
	EndIf
		
	W1 = W1 + C_DialogBorder
	
	; Determine the Icons + space
	nButtons = 0
	
	I = P\IconFirst
	While I <> Null
		nButtons = nButtons + 1
		I = I\IconNext
	Wend
	
	If nButtons = 0
		W2 = 0
	Else
		W2 = 2 * C_DialogBorder + nButtons * G_ButtonWidth + (nButtons - 1) * C_DialogSpacing
	EndIf
	
	H2 = G_ButtonHeight + C_DialogSpacing
	
	If W1 >= W2
		Width = W1
	Else
		Width = W2
	EndIf
	
	Height = H1 + H2 + C_DialogSpacing + 2 * C_DialogBorder
	
	; Space out the buttons
	
	X = C_DialogBorder + (Width - W2) / 2
	Y = H1 + 2 * C_DialogSpacing
	
	I = P\IconFirst
	While I <> Null
			
		I\EdgeLeft = X
		I\EdgeRight = I\EdgeLeft + I\Width
		I\EdgeTop = Y
		I\EdgeBottom = I\EdgeTop + I\Height
		
		X = X + G_ButtonWidth + C_DialogSpacing

		I = I\IconNext
	Wend
	
	; Safety check to prevent zero width or height
	If Width = 0
		Width = 100
	EndIf
	
	If Height = 0
		Height = 100
	EndIf
	
	If Width <> P\AdjWidth Or Height <> P\AdjHeight
		; Resize the image
		P\AdjWidth = Width
		P\AdjHeight = Height
		
		If P\Image <> 0
			FreeImage P\Image
		EndIf
		
		P\Image = CreateImage(P\AdjWidth, P\AdjHeight)
	EndIf
	
	P\EdgeRight = P\EdgeLeft + P\AdjWidth - 1
	P\EdgeBottom = P\EdgeTop + P\AdjHeight - 1
	
End Function

Function ButtonInitialise()
	Local W, H
	Local B.T_Button
	Local I.T_Icon
	
	G_ButtonYes		= ButtonCreate("Yes")
	G_ButtonNo		= ButtonCreate("No")
	G_ButtonCancel	= ButtonCreate("Cancel")
	G_ButtonOK		= ButtonCreate("OK")
	
	; Determine widest and tallest button
	
	G_ButtonWidth	= 0
	G_ButtonHeight	= 0
	For B = Each T_Button
		
		W = StringWidth(B\Icon\Label) + C_ButtonMarginH * 2
		If W > G_ButtonWidth
			G_ButtonWidth = W
		EndIf
		
		H = StringHeight(B\Icon\Label) + C_ButtonMarginV * 2
		If H > G_ButtonHeight
			G_ButtonHeight = H
		EndIf
	Next
	
	If G_ButtonWidth > C_ButtonWidthMax
		G_ButtonWidth = C_ButtonWidthMax
	EndIf
	
	If G_ButtonHeight > C_ButtonHeightMax
		G_ButtonHeight = C_ButtonHeightMax
	EndIf
	
	; Adjust buttons and create images
	For B = Each T_Button
		I = B\Icon
		
		I\Width = G_ButtonWidth
		I\Height = G_ButtonHeight
		I\ImageNormal = CreateImage(I\Width, I\Height)
		
		; Draw the image
		SetBuffer ImageBuffer(I\ImageNormal)
		Color 60, 60, 60
		Rect 0, 0, I\Width - 1, I\Height - 1, True
		
		Color 150, 150, 100
		Rect 0, 0, I\Width - 1, I\Height - 1, False
		
		Color 200, 200, 250
		Text I\Width / 2, I\Height / 2, I\Label, True, True
		
		I\ImageDisabled = CopyImage(I\ImageNormal)
		SetBuffer ImageBuffer(I\ImageDisabled)
		Color 50, 50, 50
		For X = 1 To I\Width -2 Step 3
			Rect X, 1, 1, I\Height - 2
		Next
		
		I\ImageSelected = CopyImage(I\ImageNormal)
		SetBuffer ImageBuffer(I\ImageSelected)
		Color 50, 250, 50
		Rect 0, 0, I\Width - 1, I\Height - 1, False
		
		I\ImageUnavailable = CopyImage(I\ImageNormal)
		SetBuffer ImageBuffer(I\ImageUnavailable)
		Color 250, 150, 150
		For X = 1 To I\Width -2 Step 3
			Rect X, 1, 1, I\Height - 2
		Next
		
		;SetBuffer FrontBuffer()
		;DrawBlock I\ImageNormal, 0, Y
		;DrawBlock I\ImageDisabled, I\Width * 1.1, Y
		;DrawBlock I\ImageSelected, I\Width * 2.2, Y
		;DrawBlock I\ImageUnavailable, I\Width * 3.3, Y
				
		Y = Y + I\Height + 10
	Next
	
	;WaitKey()	; Display for testing
	
End Function

Function ButtonCreate.T_Button(Label$)
	Local B.T_Button
	
	B = New T_Button
	B\Icon = New T_Icon
	
	B\Icon\Label = Label
	B\Icon\Status = C_IconStatusNormal
	B\Icon\Group = 1
	
	; The attributes will probably be added to ...
	
	Return B
End Function

Function GreyCopy(SrcX, SrcY, Width, Height, DstX, DstY, SrcB, DstB)

	t = MilliSecs()
	
	LockBuffer SrcB
	LockBuffer DstB
	
	For Y = 0 To Height - 1
		For X = 0 To Width - 1
			WritePixelFast DstX + X, DstY + Y, (ReadPixelFast(SrcX + X, SrcY + Y, SrcB) Shr 1) And $7F7F7F, DstB
		Next
	Next
	
	UnlockBuffer DstB
	UnlockBuffer SrcB
	
	;SetBuffer DstB
	;Text 400, 10, MilliSecs() - t
	
End Function