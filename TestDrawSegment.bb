Include "\Paths\Path Blitz Basic.BB"
Include "Source\GameWork.BB"

Global Method = 1

G_ScreenMode = 0
GW_GraphicsModeSet(C_Graphics1024x768x16)

Mainline()

End


Function Mainline()
	Local StartTime, EndTime

	While Not KeyHit(C_Key_Escape)
		StartTime = MilliSecs()
		
		SetBuffer BackBuffer()
		ClsColor 0, 0, 0
		Cls
		
		If KeyHit(C_Key_0)
			Method = 0
		EndIf
		
		If KeyHit(C_Key_1)
			Method = 1
		EndIf
		
		If KeyHit(C_Key_2)
			Method = 2
		EndIf
		
		If KeyHit(C_Key_3)
			Method = 3
		EndIf
		
		If KeyHit(C_Key_4)
			Method = 4
		EndIf
		
		Draw()
		
		Color 255, 255, 255
		Oval MouseX() - 5, MouseY() - 5, 11, 11, True
		
		Flip False
		
		EndTime = MilliSecs()
		
		SetBuffer FrontBuffer()
		Color 255, 255, 255
		Text 10, 10, "Time: " + (EndTime - StartTime)
	Wend
	
End Function

Function Draw()
	Local XM#, YM#
	Local X#, Y#
	Local R, G, B
	Local Angle#, Theta#
	
	Local D#
	Local W# = 10
	
	XM = G_ScreenWidth / 2
	YM = G_ScreenHeight / 2
	
	XD = MouseX() - XM
	YD = YM - MouseY()
	
	D = Sqr(XD * XD + YD * YD)
	
	If D > 300
		D = 300
	EndIf
	
	Theta = ATan2(YD, XD)
	
	; Draw background
	Color 100, 255, 100
	For X = 0 To G_ScreenWidth Step 20
		Line X, 0, X, G_ScreenHeight
	Next
	
	For Y = 0 To G_ScreenHeight Step 20
		Line 0, Y, G_ScreenWidth, Y
	Next
			
	For Angle = 0 To 359 Step 5
		X = XM + Cos(Theta + Angle) * D
		Y = YM - Sin(Theta + Angle) * D
		
		R = 128 + Sin(Angle * 2) * 127
		G = 128 + Cos(Angle * 3) * 127
		B = 128 + Sin(Angle * 5) * 127
		
		Select Method
			Case 1
				DrawSegment1(XM, YM, X, Y, R, G, B, W)
			Case 2
				DrawSegment2(XM, YM, XM-X, YM-Y, 0.5, R, G, B, W)
			Case 3
				DrawSegment3(XM, YM, X, Y, R, G, B, W)
			Case 4
				DrawSegment4(XM, YM, X, Y, R, G, B, W)
			Default
				Color R, G, B
				Line XM, YM, X, Y
		End Select
	Next
	
End Function

Function DrawSegment1(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	
	LockBuffer
	
	col=(r Shl 16) Or (g Shl 8) Or b
	thickness=thickness-1
	thickness2#=thickness/2+.001
	ex#=x1-x0
	ey#=y1-y0
	
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
 
			WritePixelFast x,iy-2,0
			WritePixelFast x,iy-1, (Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For iy=iy To Int(fy+thickness2)
				WritePixelFast x,iy,col
			Next
			WritePixelFast x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			WritePixelFast x,iy+1,0
			
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

			WritePixelFast ix-2,y,0
			WritePixelFast ix-1,y,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For ix=ix To Int(fx+thickness2)
				WritePixelFast ix,y,col
			Next
			WritePixelFast ix,y,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			WritePixelFast ix+1,y,0
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
	UnlockBuffer
	
End Function

Function DrawSegment2(x0#,y0#,ex#,ey#,l#,r,g,b,thickness#)
	
	LockBuffer
	
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

			WritePixelFast x,iy-1,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For iy=iy To Int(fy+thickness2)
				WritePixelFast x,iy,col
			Next
			WritePixelFast x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			
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

			WritePixelFast ix-1,y,(Int(o1*r) Shl 16) Or (Int(o1*g) Shl 8) Or Int(o1*b)
			For ix=ix To Int(fx+thickness2)
				WritePixelFast ix,y,col
			Next
			WritePixelFast ix,y,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If

	UnlockBuffer
	
End Function

Function DrawSegment3(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	
	LockBuffer
	
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
 
			;WritePixelFast x,iy-2,0
			
			V = ReadPixelFast(x, iy-1)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixelFast x,iy-1, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)
			
			For iy=iy To Int(fy+thickness2)
				WritePixelFast x,iy,col
			Next

			V = ReadPixelFast(x, iy)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixelFast x,iy, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			;WritePixelFast x,iy,(Int(r*o2) Shl 16) Or (Int(g*o2) Shl 8) Or Int(b*o2)
			;WritePixelFast x,iy+1,0
			
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

			;WritePixelFast ix-2,y,0
			
			V = ReadPixelFast(ix-1, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixelFast ix-1,y, (Int(o1*r + (1-o1) * VR) Shl 16) Or (Int(o1*g + (1-o1) * VG) Shl 8) Or Int(o1*b + (1-o1) * VB)

			For ix=ix To Int(fx+thickness2)
				WritePixelFast ix,y,col
			Next

			V = ReadPixelFast(ix, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixelFast ix,y, (Int(o2*r + (1-o2) * VR) Shl 16) Or (Int(o2*g + (1-o2) * VG) Shl 8) Or Int(o2*b + (1-o2) * VB)
			
			;WritePixelFast ix+1,y,0
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
	UnlockBuffer
	
End Function

; Version 4
;
; Like V3 but with Rect used instead of middle For...Next
;
Function DrawSegment4(x0#,y0#,x1#,y1#,r,g,b,thickness#)
	col=(r Shl 16) Or (g Shl 8) Or b
	Color R, G, B
	
	ex#=x1-x0
	ey#=y1-y0
	
	ax#=Abs(ex)
	ay#=Abs(ey)
	Theta# = ATan2(ay, ax)
	
	If ax>ay Then
		; Correct thickness
		thickness = Int(thickness / Cos(Theta))
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
 
			V = ReadPixel(x, iy-1)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel x, iy-1, Int(o1*r + (1-o1) * VR) Shl 16 + Int(o1*g + (1-o1) * VG) Shl 8 + Int(o1*b + (1-o1) * VB)
			;WritePixel x, iy-1, 255 Shl 16 + 255 Shl 8 + 255
						
			Rect x, iy, 1, thickness, True
			;For iy=iy To Int(fy+thickness2)
			;	WritePixelFast x,iy,col
			;Next

			V = ReadPixel(x, iy + thickness)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel x, iy + thickness, Int(o2*r + (1-o2) * VR) Shl 16 + Int(o2*g + (1-o2) * VG) Shl 8 + Int(o2*b + (1-o2) * VB)
			;WritePixel x, iy + thickness, 255 Shl 16 + 255 Shl 8
			
			;increase float y
			fy=fy+dy

			;increase AA
			o1=o1-dy
			o2=o2+dy

			x=x+1
		Wend
		
	Else
		; Correct thickness
		thickness = Int(thickness / Sin(Theta))
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

			V = ReadPixel(ix-1, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel ix-1, y, Int(o1*r + (1-o1) * VR) Shl 16 + Int(o1*g + (1-o1) * VG) Shl 8 + Int(o1*b + (1-o1) * VB)
			;WritePixel ix-1, y, 255 Shl 16 + 255 Shl 8 + 255

			Rect ix, y, thickness, 1, True
			;For ix=ix To Int(fx+thickness2)
			;	WritePixelFast ix,y,col
			;Next

			V = ReadPixel(ix + thickness, y)
			VR = V Shr 16 And 255
			VG = V Shr 8 And 255
			VB = V And 255
			WritePixel ix + thickness, y, Int(o2*r + (1-o2) * VR) Shl 16 + Int(o2*g + (1-o2) * VG) Shl 8 + Int(o2*b + (1-o2) * VB)
			;WritePixel ix + thickness, y, 255 Shl 16 + 255 Shl 8
			
			;increase float y
			fx=fx+dx

			;increase AA
			o1=o1-dx
			o2=o2+dx

			y=y+1
		Wend	
	End If
		
End Function

