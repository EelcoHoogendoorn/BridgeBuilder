; --------------------------------------------------------------------------------------------------------------------------------
;
; Help Module
;
; --------------------------------------------------------------------------------------------------------------------------------
Type T_HelpPage
End Type

Type T_HelpItem
End Type

Function HelpInitialise()
	Local FileName$ = "Bridge Builder Help.HTML"
	
	G_HelpFile = OpenFile(FileName)
	
	i = 0
	While Not Eof(G_HelpFile)
		If i > 100
			Exit
		EndIf
		
		G_HelpLines(i) = ReadLine(G_HelpFile)
		
		i = i + 1
	Wend
	
	G_HelpLineCount = i
	
End Function

; Load specified Help file into internal data structures
Function HelpLoad()
End Function

; Control the overall Help system when invoked by the user
Function HelpMenu()
	HelpDraw()
	
	FlushKeys()
	WaitKey()
	FlushKeys()
	
End Function

Function HelpControls()
End Function

; Draw the Help screen including the framework and contents
Function HelpDraw()
	SetBuffer BackBuffer()
	
	Cls
	
	Flip
	
	Color 100, 255, 100
	
	For i = 0 To G_HelpLineCount
		Print G_HelpLines(i)
	Next
	
	SetBuffer FrontBuffer()
	
	Color 255, 150, 255
	
	Rect 0, 0, G_ScreenWidth, G_ScreenHeight, False
	
End Function

; Construct the text and image componets that make up a Help page
;
;   This includes determining the dimensions and offsets given the framework in which it will
;   be displayed
Function HelpPageConstruct()
End Function

; Destruct the text and image components
Function HelpPageDesctruct()
End Function

; 
Function HelpPageDraw()
End Function
