; --------------------------------------------------------------------------------------------------------------------------------
;
; PrepImages - Prepare image files for Bridge Builder project
;
; --------------------------------------------------------------------------------------------------------------------------------
;
; Revisions:
;
; 20040214 PGF Original
;

Include "\Paths\Path Blitz Basic.BB"
Include "Source\GameWork.BB"

Global G_DirSrc$ = "C:\Temp\"
Global G_DirDst$ = "C:\Data\Blitz Basic\Bridge Builder\Images\"

Global G_TextLine
Global G_TextColour

G_ScreenMode = 0
GW_GraphicsModeSet(C_Graphics1024x768x16)

MainInitialise()
Mainline()
MainFinalise()

End

Function MainInitialise()
	
End Function

Function Mainline()
	Local I
	Local Name$
	Local Y
	
	MainDraw()
	Flip True
	SetBuffer FrontBuffer()
	
	For I = 0 To 25
		Select I
			Case  0 : Name = "Empty"
			Case  1 : Name = "Wood Beam"
			Case  2 : Name = "Steel Beam"
			Case  3 : Name = "Concrete Beam"
			Case  4 : Name = "Steel Cable"
			Case  5 : Name = "Select"
			Case  6 : Name = "Build"
			Case  7 : Name = "Build Multiple"
			Case  8 : Name = "Line"
			Case  9 : Name = "Beam"
			Case 10 : Name = "Arch"
			Case 11 : Name = "Circle"
			Case 12 : Name = "Default"
			Case 13 : Name = "Replacer"
			Case 14 : Name = "Load Bridge"
			Case 15 : Name = "Save Bridge"
			Case 16 : Name = "New Bridge"
			Case 17 : Name = "Quit"
			Case 18 : Name = "Copy"
			Case 19 : Name = "Move"
			Case 20 : Name = "Stretch"
			Case 21 : Name = "FlipX"
			Case 22 : Name = "FlipY"
			Case 23 : Name = "Rotate"
			Case 24 : Name = "Group"
			Case 25 : Name = "Test"
			
			;Case 26 : Name = ""
			;Case 27 : Name = ""
			;Case 28 : Name = ""
			;Case 29 : Name = ""
			
			Default
				Name = "Unknown"
		End Select
		
		FileProcess(I, Name)
		
		Color 100, 100, 100
		Y = 18 + G_TextLine * 12
		Line 0, Y, G_ScreenWidth - 1, Y
		
		TextLineInc()
		Delay 100
	Next
	
	TextLineInc()
	
	Y = 18 + G_TextLine * 12
	Color 0, 0, C_Colour_Green
	Text G_ScreenWidth / 2, Y, "Completed Processing", True, False
	
	WaitKey()
	
End Function

Function MainFinalise()
End Function

Function MainDraw()
	SetBuffer BackBuffer()
	
	ClsColor 0, 0, 0
	Cls
	
	Color 100, 100, 100
	Rect 0, 0, G_ScreenWidth - 1, G_ScreenHeight - 1, False
	
	G_TextLine = 1
	
End Function

Function FileProcess(N, Name$)
	Local IconSrc$
	Local IconDst$
	
	Local I
	
	; Process the toolbar icons.  There are 4: Normal, Selected, Disabled and Unavailable
	For I = 0 To 3
		IconSrc = G_DirSrc + "Icon - " + Right$("000" + (N * 4 + I), 3) + ".PNG"
	    IconDst = G_DirDst + "Icon - " + Right$("000" + (N * 4 + I), 3) + ".PNG"

		FileDo(IconSrc, IconDst)
	Next
	
	; Process the help icon.  Only the Normal version is created and it is given a meaningful name
	IconSrc$ = G_DirSrc + "Tool - " + Right$("00" + n, 2) + ".PNG"
	IconDst$ = G_DirDst + "Tool - " + Name + ".PNG"
	
	FileDo(IconSrc, IconDst)
	
End Function

Function FileDo(Src$, Dst$)	
	Local CheckSrc, CheckDst

	TextLineInc()

	StatusMessage("Copy", Src, Dst, "Checking", C_Colour_White)
	
	CheckSrc = FileCheck(Src)
	If Not CheckSrc
		GW_AbortErrorMessage("Source file '" + Src + "' does not exist")
	EndIf
	
	CheckDst = FileCheck(Dst)
	If CheckDst
		StatusMessage("Replace", "", "", "", C_Colour_Orange)
	EndIf
	
	CopyFile Src, Dst
	
	If FileCheck(Dst)
		StatusMessage("", "", "", "Successful", C_Colour_Green)
	Else
		StatusMessage("", "", "", "Error", C_Colour_Red)
	EndIf
	
End Function

Function FileCheck(Name$)
	Return FileType(Name) = 1
End Function

Function TextMessage(Msg$)
	TextLineInc()
	
	Text 10, G_TextLine * 12, Msg
End Function

Function StatusMessage(Op$, Src$, Dst$, Status$, Clr)
	G_TextColour = Clr
	
	If Op <> ""
		StatusText(0, 8, Op)
	EndIf
	
	If Src <> ""
		StatusText(8, 20, Src)
	EndIf
	
	If Dst <> ""
		StatusText(30, 60, Dst)
	EndIf
	
	If Status <> ""
		StatusText(90, 10, Status)
	EndIf
	
End Function

Function StatusText(Pos, Wid, Txt$)
	Local X = 10 + Pos * 10
	Local Y = G_TextLine * 12
	Local W = Wid * 10
	Local H = 15
	
	Color 0, 0, 0
	Rect X, Y, W, H, True
	
	Color 0, 0, G_TextColour
	Text X, Y, Txt
End Function

Function TextLineInc()
	G_TextLine = G_TextLine + 1
	If G_TextLine * 12 > G_ScreenHeight - 12
		MainDraw()
		Flip True 
		SetBuffer FrontBuffer()
	EndIf
End Function