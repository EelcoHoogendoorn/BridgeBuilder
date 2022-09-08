	;graphics
	Const screenx=1024,screeny=768
	Graphics screenx,screeny,16
	SeedRnd MilliSecs()

	;level
	Global levelwidth=80*15
	Global leveldepth=400
	Global levelheight=1000
	Global levelimage
	Dim height#(levelwidth-1)
	Global leftshore=240
	Global rightshore=levelwidth-240
	Global waterlevel=37
	setheight()
	prerender()
	
	;mouse
	Global xmouse,ymouse
	Global pointer=LoadImage("red_pointer.bmp")
	MaskImage pointer,255,255,255
	Const scrollspeed=5
	Const clicktime=300
	Global leftbutton,rightbutton
	Global leftclick,rightclick
	Global leftdouble,rightdouble
	Global lefttime,righttime
	Global lefthold,righthold
	Global leftrelease,rightrelease
	Global pull.bolt
	MoveMouse screenx/2,screeny/2

	;camera
	Global camerax
	Global cameray=levelheight-screeny/2

	;grid
	Const gridsize=10
	Const blocksize=8*gridsize
	Global drawgrid=True
	Global snaptogrid=True
	
	;bolts
	Type bolt
		Field px#,py#
		Field lpx#,lpy#
		Field sx#,sy#
		Field lsx#,lsy#
		Field fx#,fy#
		Field lfx#,lfy#
		Field mass#
		Field bolttype
		Field id
	End Type
	Const regular=0,fixed=1,rail=2,fixedrail=3,train=4
	
	;segment
	Type segment
		Field length#
		Field stress#
		Field lstress#
		Field bolt1.bolt
		Field bolt2.bolt
		Field segmenttype
		Field material.material
	End Type
	
	;materials
	Type material
		Field strength#
		Field elasticity#
		Field mass#
		Field name$
		Field r,g,b
		Field id
	End Type
	Const watermass=1
	Const airmass#=.001
	Global steel.material=New material
	steel\name="Steel"
	steel\id=0
	steel\strength=1200000
	steel\elasticity=.01
	steel\mass=8
	steel\r=90
	steel\g=100
	steel\b=255
	Global wood.material=New material
	wood\name="Wood"
	wood\id=1
	wood\strength=320000
	wood\elasticity=.02
	wood\mass=.8
	wood\r=200
	wood\g=150
	wood\b=30
	Global concrete.material=New material	;WIP
	concrete\name="Concrete"
	concrete\id=2
	concrete\strength=20000000
	concrete\elasticity=.004
	concrete\mass=100
	concrete\r=200
	concrete\g=200
	concrete\b=200
	Global selectedmaterial.material=steel
		
	;programflow
	Const edit=0,test=1,menu=-1
	Global mode=edit
	Global viewstress=True
	Global traintest=False
	Global construction.bolt

	;physics
	Const gravity#=9.81
	Const boltmass#=.01
	Global windspeed#=3
	
	;train
	Type wheel
		Field wheel.segment
		Field rail.segment
		Field contact#
		Field lcontact#
	End Type
	Global trainengine.bolt
	Global trainend.bolt
	Global trainlength=5
	Global trainmass=3600
	Global trainpower=trainlength*trainmass/10
	
Global G_PhysicsCycle

Global G_DebugTraceBoltId = 10

	setupstandard()


Global test1,test2#
SetBuffer BackBuffer()
Repeat

	drawgrid()
	drawlevel()
	drawbridge()
	t=MilliSecs()
	physics(20)
	t=MilliSecs()-t
	construct()
	mouseinput()
	keyboardinput()
	
	Color 255,255,255
	If mode=edit Then
		Text 10,10,"Material="+selectedmaterial\name

		Text 10,30 ,"Left mousebutton to constuct"
		Text 10,40 ,"Right mousebutton to remove"
		Text 10,50 ,"'M' to change Material"
		Text 10,60 ,"Space to test bridge"
		Text 10,70 ,"'L'+[number] to Load a bridge"
		Text 10,80 ,"'S'+[number] to Save this bridge"
		Text 10,90 ,"'N' to start New bridge"
		Text 10,100,"Backspace to clear bridge"
		Text 10,110,"'G' to toggle Grid"
	Else
		Text 10,10,"Fps:"+fps
		Text 10,20,"Physicstime "+t
		Text 10,30,"Wind "+windspeed

		Text 10,50 ,"Left mousebutton to select bolt"
		Text 10,60 ,"Right mousebutton to pull selected bolt"
		Text 10,70 ,"'V' to toggle stress View"
		Text 10,80 ,"'T' to start/stop Train"
		Text 10,90 ,"Space to return to editor"
	End If
	Flip 
	fps=1000/(MilliSecs()-start)
	start=MilliSecs()
	Cls

Until KeyHit(1)
Stop
End


Function keyboardinput()

	If KeyHit(57) Then 
		If mode=edit Then 
			setuptest()
		Else If mode=test Then 
			mode=edit
			traintest=False
			loadbridge(tempbackup)
		End If
	End If
	
	
	If mode=edit Then
		If KeyDown(31) Then
			For k=1 To 10
				If KeyHit(k+1) Then savebridge(k)
			Next
		End If
	
		If KeyDown(38) Then
			For k=1 To 10
				If KeyHit(k+1) Then 
					loadbridge(k)
					prerender()
				End If
			Next
		End If

		If KeyHit(14) Then clearbridge(False)

		If KeyHit(49) Then 	;New
			clearbridge(True)
			setheight()
			prerender()
			setupstandard()
		End If

		If KeyHit(19) Then	;Restart
			clearbridge(True)
			setupstandard()
		End If
		
		If KeyHit(50) Then
			If selectedmaterial=Last material Then
				selectedmaterial=First material
			Else
				selectedmaterial=After selectedmaterial
			End If
		End If
	Else
		If KeyHit(20) Then
			If traintest=False Then
				setuptrain()
			Else
				cleartrain()
			End If
		End If
	
		If KeyHit(47) Then viewstress=Not viewstress
	End If
	
	If KeyHit(34) Then drawgrid=Not drawgrid	
	
End Function

Function mouseinput()
	
	xmouse=MouseX()
	ymouse=MouseY()

	leftclick=False
	leftdouble=False	
	If MouseDown(1) Then
		If leftbutton=True Then 
			lefthold=True 
		Else 
			leftclick=True
			If lefttime+clicktime>MilliSecs() Then leftdouble=True
			If leftdouble=False Then lefttime=MilliSecs()
		End If
		leftbutton=True
	Else
		leftrelease=False
		If leftbutton=True Then leftrelease=True
		leftbutton=False
		lefthold=False
	End If

	rightclick=False
	rightdouble=False
	If MouseDown(2) Then
		If rightbutton=True Then 
			righthold=True 
		Else 
			rightclick=True
			If righttime+clicktime>MilliSecs() Then rightdouble=True
			If rightdouble=False Then righttime=MilliSecs()
		End If
		rightbutton=True
	Else
		rightrelease=False
		If rightbutton=True Then rightrelease=True
		rightbutton=False
		righthold=False
	End If
	
	DrawImage pointer,xmouse,ymouse
	
	If xmouse>screenx-10 Then camerax=camerax+scrollspeed
	If camerax>levelwidth-screenx-1 Then camerax=levelwidth-screenx-1
	If xmouse<10 Then camerax=camerax-scrollspeed
	If camerax<0 Then camerax=0
	
	If ymouse>screeny-10 Then cameray=cameray+scrollspeed
	If cameray>leveldepth-screeny-1 Then cameray=leveldepth-screeny-1
	If ymouse<10 Then cameray=cameray-scrollspeed
	If cameray<-levelheight Then cameray=-levelheight
	
	If mode=edit Then
		If leftclick Then
			If construction=Null Then 
				startconstruction() 
			Else 
				finishconstruction()
			End If
		End If
		
		If rightclick Then
			If construction<>Null Then
				construction=Null
			Else If Not deletebolt(findbolt(xmouse+camerax,ymouse+cameray)) Then
				deletesegment(findsegment())
			End If
		End If
	End If
	
	If mode=test Then
		If leftclick Then
			pull=findbolt(xmouse+camerax,ymouse+cameray)
		End If
	End If
	
	
End Function


Function startconstruction()

	construction=findbolt(xmouse+camerax,ymouse+cameray)

End Function

Function findbolt.bolt(x,y)

	For f.bolt=Each bolt
		If Abs(f\px-x)+Abs(f\py-y)<10 Then Return f
	Next

End Function

Function construct()

	If construction=Null Then Return

	fx=Int(Float(xmouse+camerax)/gridsize)*gridsize
	fy=Int(Float(ymouse+cameray)/gridsize)*gridsize
	
	d#=Sqr((fx-construction\px)^2+(fy-construction\py)^2)

	If Not d<90 Then Return
	
	Color 150,200,255
	Line fx-camerax,fy-cameray,construction\px-camerax,construction\py-cameray
	Color 0,255,0
	Oval fx-2-camerax,fy-2-cameray,5,5,False

End Function

Function finishconstruction()

	fx=Int(Float(xmouse+camerax)/gridsize)*gridsize
	fy=Int(Float(ymouse+cameray)/gridsize)*gridsize
	
	d#=Sqr((fx-construction\px)^2+(fy-construction\py)^2)
	If Not d<90 Then Return
	If d=0 Then Return
	
	b.bolt=findbolt(fx,fy)
	If b=Null Then
		b=New bolt
		b\px=fx
		b\py=fy
	End If
	
	s.segment=New segment
	s\bolt1=construction
	s\bolt2=b
	s\material=selectedmaterial
	s\length=d
	If s\bolt1\py=0 And s\bolt2\py=0 Then s\segmenttype=rail

	construction=Null

End Function

Function deletebolt(b.bolt)

	If b=Null Then Return
	If b\bolttype=fixed Then Return
	For s.segment=Each segment
		If s\bolt1=b Or s\bolt2=b Then deletesegment(s)
	Next
	Return True

End Function

Function deletesegment(s.segment)

	If s=Null Then Return
	If s\segmenttype=fixed Or s\segmenttype=fixedrail Then Return
	For f.segment=Each segment
		If f<>s Then
			If f\bolt1=s\bolt1 Or f\bolt2=s\bolt1 Then b1=True
			If f\bolt1=s\bolt2 Or f\bolt2=s\bolt2 Then b2=True
		End If
	Next
	If b1=False And s\bolt1\bolttype<>fixed Then Delete s\bolt1
	If b2=False And s\bolt2\bolttype<>fixed Then Delete s\bolt2
	Delete s

End Function

Function findsegment.segment()

	x#=xmouse+camerax:y#=ymouse+cameray
	
	For s.segment=Each segment
		If s\segmenttype<>fixed And s\segmenttype<>fixedrail Then
			b1#=ATan2(s\bolt1\py-s\bolt2\py,s\bolt1\px-s\bolt2\px)-ATan2(y-s\bolt1\py,x-s\bolt1\px)

			lb1#=Sqr((s\bolt1\px-x)^2+(s\bolt1\py-y)^2)
			lb2#=Sqr((s\bolt2\px-x)^2+(s\bolt2\py-y)^2)
			p#=lb1*-Cos(b1)
			If p>0 And p<s\length Then
				h#=lb1*-Sin(b1)
				If Abs(h)<4 Then Return s
			Else If p<0 Then
				If lb1<4 Then Return s
			Else If p>s\length
				If lb2<4 Then Return s
			End If
		End If
	Next

End Function

Function findrail.segment(px,py)

	For s.segment=Each segment
		If s\segmenttype=rail Then
			b1#=ATan2(s\bolt1\py-s\bolt2\py,s\bolt1\px-s\bolt2\px)-ATan2(py-s\bolt1\py,px-s\bolt1\px)

			lb1#=Sqr((s\bolt1\px-px)^2+(s\bolt1\py-py)^2)
			lb2#=Sqr((s\bolt2\px-px)^2+(s\bolt2\py-py)^2)
			p#=lb1*-Cos(b1)
			If p>0 And p<s\length Then
				h#=lb1*-Sin(b1)
				If Abs(h)<1 Then Return s
			Else If p<0 Then
				If lb1<1 Then Return s
			Else If p>s\length
				If lb2<1 Then Return s
			End If
		End If
	Next

End Function

Function drawgrid()

	If drawgrid=False Or mode<>edit	Then Return

	x=-(camerax Mod gridsize)
	While x<screenx
		If Float(x+camerax)/blocksize=(x+camerax)/blocksize Then Color 192,192,192 Else Color 128,128,128
		Line x,0,x,screeny
		x=x+gridsize
	Wend
	y=-(cameray Mod gridsize)
	While y<screeny
		If Float(y+cameray)/blocksize=(y+cameray)/blocksize Then Color 192,192,192 Else Color 128,128,128
		Line 0,y,screenx,y
		y=y+gridsize
	Wend

End Function

Function drawlevel()

	DrawImageRect levelimage,0,-cameray,camerax,0,screenx,leveldepth
	
End Function

Function drawbridge()

	;draw segments
	For ds.segment=Each segment	
		;get the right color
		If mode=test And viewstress=True Then
			c#=ds\stress/(ds\material\elasticity*ds\length)*128
			Color 128-c,128+c,128-Abs(c)
		Else If ds\segmenttype=train Then
			Color 0,255,0
		Else
			If ds\segmenttype=rail Or ds\segmenttype=fixedrail Then
				Color ds\material\r/2,ds\material\g/2,ds\material\b/2
			Else
				Color ds\material\r,ds\material\g,ds\material\b
			End If
		End If

		Line ds\bolt1\px-camerax,ds\bolt1\py-cameray,ds\bolt2\px-camerax,ds\bolt2\py-cameray
	Next
	
	;draw bolts
	If mode=edit Then
		For db.bolt=Each bolt
			If db\Id = G_DebugTraceBoltId
				Color 255, 255, 0
			Else
				Color 0, 255, 0
			EndIf
			Oval db\px-2-camerax,db\py-2-cameray,5,5,False
		Next
	Else
		If pull<>Null Then
			Color 255,255,255
			Oval pull\px-2-camerax,pull\py-2-cameray,5,5,False
		End If
	End If

End Function


Function setheight()
	
	;set the height to a parabol
	For s=0 To levelwidth-1
		x=Float((s-leftshore))*levelwidth/(levelwidth-(levelwidth-rightshore)-leftshore)
		y#=(-(x^2)+levelwidth*x)/(-(levelwidth/2)^2+levelwidth^2/2)*leveldepth*2
		If y<0 Then y=0
		height(s)=y
	Next

	;add some random height
	For r=0 To levelwidth/2
		x=Rnd(leftshore+80,rightshore-80)
		height(x)=height(x)+Rnd(-leveldepth,+leveldepth)
	Next

	;smooth it out to get a random but smooth skyline
	For s=1 To 500
		For h=1 To levelwidth-2
			height(h)=(height(h-1)+height(h+1)+height(h))/3
		Next
	Next
	
	;chop off mountains
	For m=0 To levelwidth-1
		If m<leftshore Or m>rightshore And height(m)<0 Then height(m)=0
	Next
	
End Function

Function prerender()

	If levelimage=True Then FreeImage levelimage
	levelimage=CreateImage(levelwidth-1,leveldepth-1)
	MaskImage levelimage,0,0,0
	SetBuffer ImageBuffer(levelimage)
	Color 0,30,200
	Rect 0,waterlevel,levelwidth,leveldepth,True
	Color 160,128,64
	For d=0 To levelwidth-1
		Line d,leveldepth,d,height(d)
	Next
	SetBuffer BackBuffer()

End Function


Function setupstandard()
	
	For rx=0 To levelwidth Step blocksize
		b.bolt=New bolt
		b\px=rx
		b\py=0
		If rx=<leftshore Or rx=>rightshore Then b\bolttype=fixed
	Next
	
	For b.bolt=Each bolt
		If b.bolt=Last bolt Then Return
		s.segment=New segment
		s\bolt1=b
		s\bolt2=After b
		s\length=blocksize
		s\segmenttype=rail
		s\material=selectedmaterial
		If s\bolt1\bolttype=fixed And s\bolt2\bolttype=fixed Then s\segmenttype=fixedrail Else s\segmenttype=rail
	Next

End Function


Function savebridge(id)
	
	If FileType("bridge"+Str(id)+".dat")=True Then DeleteFile ("bridge"+Str(id)+".dat")
	file=WriteFile("bridge"+Str(id)+".dat")
	
		WriteShort file,levelwidth
		WriteShort file,leveldepth
		WriteShort file,levelheight
		WriteShort file,waterlevel
		
		For h=0 To levelwidth-1
			WriteFloat file,height(h)
		Next
		
		p=FilePos(file)		;start of bolts
		SeekFile file,p+2	;skip short
		For b.bolt=Each bolt	;write boltdata
			WriteFloat file,b\px
			WriteFloat file,b\py
			WriteByte file,b\bolttype
			i=i+1
			b\id=i
		Next
		SeekFile file,p		;set to start
		WriteShort file,i	;write number
		p=p+2+i*9
		i=0
		SeekFile file,p+2	;set to end/start of segment

		For s.segment=Each segment
			WriteShort file,s\bolt1\id
			WriteShort file,s\bolt2\id
			WriteByte file,s\material\id
			WriteByte file,s\segmenttype
			i=i+1
		Next
		
		SeekFile file,p
		WriteShort file,i

	CloseFile file

End Function


Function loadbridge(id)

	If Not FileType("bridge"+Str(id)+".dat")=True Then Return False
	clearbridge(True)

	file=OpenFile("bridge"+Str(id)+".dat")

		levelwidth=ReadShort(file)
		Dim height(levelwidth)
		leveldepth=ReadShort(file)
		levelheight=ReadShort(file)
		waterlevel=ReadShort(file)
		
		For h=0 To levelwidth-1
			height(h)=ReadFloat(file)
		Next

		i=ReadShort(file)
		For lb=1 To i
			b.bolt=New bolt
			b\px=ReadFloat(file)
			b\py=ReadFloat(file)
			b\bolttype=ReadByte(file)
			b\id=lb
		Next
		
		i=ReadShort(file)
		For ls=1 To i
			s.segment=New segment
			b1=ReadShort(file)
			For b=Each bolt
				If b\id=b1 Then s\bolt1=b
			Next
			b2=ReadShort(file)
			For b=Each bolt
				If b\id=b2 Then s\bolt2=b
			Next
			mi=ReadByte(file)
			For m.material=Each material
				If m\id=mi Then s\material=m
			Next
			s\segmenttype=ReadByte(file)
			s\length=Sqr((s\bolt1\px-s\bolt2\px)^2+(s\bolt1\py-s\bolt2\py)^2)
		Next
		
	CloseFile file

End Function

Function clearbridge(flag)

	If flag=True Then
		Delete Each segment
		Delete Each bolt
		Delete Each wheel
	Else
		For s.segment=Each segment
			If Not s\segmenttype=fixed Or s\segmenttype=fixedrail Then Delete s
		Next
		For b.bolt=Each bolt
			If Not b\bolttype=fixed Then Delete b
		Next
		Delete Each wheel
	End If

End Function


Function setuptest()

	;align rails
	For s.segment=Each segment
		If s\segmenttype=rail Then
			If s\bolt1\px>s\bolt2\px Then
				t.bolt=s\bolt1
				s\bolt1=s\bolt2
				s\bolt2=t
			End If
		End If
	Next
	
	savebridge(tempbackup)

	For b.bolt=Each bolt
		;reset speed
		b\sx=0
		b\lsx=0
		b\sy=0
		b\lsy=0
		;reset mass
		b\mass=boltmass
	Next
	
	For s.segment=Each segment
		mass#=s\length*s\material\mass	; Change mass from int to float to accuracy
		s\bolt1\mass=s\bolt1\mass+mass
		s\bolt2\mass=s\bolt2\mass+mass
	Next

	mode=test

End Function


Function setuptrain()

	For c=1 To trainlength
		b1.bolt=New bolt
		b1\px=leftshore-35*c
		b1\py=0
		b1\bolttype=train
		b1\mass=trainmass
		
		b2.bolt=New bolt
		b2\px=leftshore-35*c
		b2\py=-15
		b2\bolttype=train
		b2\mass=trainmass
		
		b3.bolt=New bolt
		b3\px=leftshore-35*c-30
		b3\py=0
		b3\bolttype=train
		b3\mass=trainmass
		
		b4.bolt=New bolt
		b4\px=leftshore-35*c-30
		b4\py=-15
		b4\bolttype=train
		b4\mass=trainmass


		s1.segment=New segment
		s1\bolt1=b1
		s1\bolt2=b3
		s1\segmenttype=train
		s1\material=steel
		s1\length=Sqr((s1\bolt1\px-s1\bolt2\px)^2+(s1\bolt1\py-s1\bolt2\py)^2)
		
		s2.segment=New segment
		s2\bolt1=b2
		s2\bolt2=b4
		s2\segmenttype=train
		s2\material=steel
		s2\length=Sqr((s2\bolt1\px-s2\bolt2\px)^2+(s2\bolt1\py-s2\bolt2\py)^2)	
		
		s3.segment=New segment
		s3\bolt1=b1
		s3\bolt2=b4
		s3\segmenttype=train
		s3\material=steel
		s3\length=Sqr((s3\bolt1\px-s3\bolt2\px)^2+(s3\bolt1\py-s3\bolt2\py)^2)
		
		s4.segment=New segment
		s4\bolt1=b3
		s4\bolt2=b2
		s4\segmenttype=train
		s4\material=steel
		s4\length=Sqr((s4\bolt1\px-s4\bolt2\px)^2+(s4\bolt1\py-s4\bolt2\py)^2)

		s5.segment=New segment
		s5\bolt1=b1
		s5\bolt2=b2
		s5\segmenttype=train
		s5\material=steel
		s5\length=Sqr((s5\bolt1\px-s5\bolt2\px)^2+(s5\bolt1\py-s5\bolt2\py)^2)
		w.wheel=New wheel
		w\wheel=s5
		
		s6.segment=New segment
		s6\bolt1=b3
		s6\bolt2=b4
		s6\segmenttype=train
		s6\material=steel
		s6\length=Sqr((s6\bolt1\px-s6\bolt2\px)^2+(s6\bolt1\py-s6\bolt2\py)^2)
		w.wheel=New wheel
		w\wheel=s6
		
		
		If c>1 Then 
			s.segment=New segment
			s\bolt1=b1
			s\bolt2=l.bolt
			s\length=Sqr((s\bolt1\px-s\bolt2\px)^2+(s\bolt1\py-s\bolt2\py)^2)
			s\material=steel;cable
			s\segmenttype=train
		Else
			trainengine=b1
		End If
		If c=trainlength Then trainend=b4
		l=b3
	Next
	
	traintest=True

End Function

Function cleartrain()

	For b.bolt=Each bolt
		If b\bolttype=train Then Delete b
	Next
	For s.segment=Each segment
		If s\segmenttype=train Then Delete s
	Next
	Delete Each wheel
	traintest=False

End Function

Function physics(n)

	If mode=edit Then Return

	G_PhysicsCycle = G_PhysicsCycle + 1

	;dynamic-precision physics update.:P 
	For iStep=1 To n
;		DebugLog "Cycle" + RSet$(G_PhysicsCycle, 4) +" Step" + RSet$(iStep, 3)
		;all forces applied by the segments
		For s.segment=Each segment

;			If S\Bolt1\Id = G_DebugTraceBoltId
;				DebugLog "  Segment Loop - A - Bolt1"
;				DebugLog "    PX" + RSet$(S\Bolt1\PX, 14) + " PY" + RSet$(S\Bolt1\PY, 14)
;				DebugLog "    SX" + RSet$(S\Bolt1\SX, 14) + " SY" + RSet$(S\Bolt1\SY, 14)
;				DebugLog "    FX" + RSet$(S\Bolt1\FX, 14) + " FY" + RSet$(S\Bolt1\FY, 14)
;			EndIf
;			If S\Bolt2\Id = G_DebugTraceBoltId
;				DebugLog "  Segment Loop - A - Bolt2"
;				DebugLog "    PX" + RSet$(S\Bolt2\PX, 14) + " PY" + RSet$(S\Bolt2\PY, 14)
;				DebugLog "    SX" + RSet$(S\Bolt2\SX, 14) + " SY" + RSet$(S\Bolt2\SY, 14)
;				DebugLog "    FX" + RSet$(S\Bolt2\FX, 14) + " FY" + RSet$(S\Bolt2\FY, 14)
;				Stop
;			EndIf
		
			;precalc some frequently needed things to speed up
			angle#=ATan2(s\bolt1\py-s\bolt2\py,s\bolt1\px-s\bolt2\px)
			cosa#=Cos(angle):sina#=Sin(angle)

			;air/water resistance
			If s\bolt1\py>waterlevel Then 
				c1#=watermass 
				w1#=0
			Else 
				c1=airmass
				w1#=windspeed
			End If
			If s\bolt2\py>waterlevel Then 
				c2#=watermass 
				w2#=0
			Else 
				c2=airmass
				w2#=windspeed
			End If

			sd1#=ATan2(s\bolt1\sy,s\bolt1\sx+w1)
			sd2#=ATan2(s\bolt2\sy,s\bolt2\sx+w2)
			If (angle+180) Mod 180-(sd1+180) Mod 180<0 Then fd1#=((angle+180) Mod 180) +90 Else fd1#=(angle+180) Mod 180-90
			If (angle+180) Mod 180-(sd2+180) Mod 180<0 Then fd2#=((angle+180) Mod 180) +90 Else fd2#=(angle+180) Mod 180-90
			sp1#=(s\bolt1\sx+w1)^2+s\bolt1\sy^2
			sp2#=(s\bolt2\sx+w2)^2+s\bolt2\sy^2

;				If S\Bolt2\Id = G_DebugTraceBoltId
;					DebugLog "    Angle   " + Angle
;					DebugLog "    sd2     " + sd2
;					DebugLog "    length  " + s\length
;					DebugLog "    sp2     " + sp2
;					DebugLog "    c2      " + c2
;					DebugLog "    fd2     " + fd2
;					DebugLog "    sgn     " + Sgn(s\bolt2\sy)
;					DebugLog "    gravity " + gravity
;				EndIf
			
								;oppervlakte	 *lengte  *snelheid^2  *dichtheidsconstante *richtingscomponent
			s\bolt1\fx=s\bolt1\fx-Abs(Sin(angle-sd1))*s\length*sp1*c1*Cos(fd1)*Sgn(s\bolt1\sy);floating
			s\bolt1\fy=s\bolt1\fy-Abs(Sin(angle-sd1))*s\length*sp1*c1*Sin(fd1)*Sgn(s\bolt1\sy)-s\length*c1*gravity/100
			s\bolt2\fx=s\bolt2\fx-Abs(Sin(angle-sd2))*s\length*sp2*c2*Cos(fd2)*Sgn(s\bolt2\sy)
			s\bolt2\fy=s\bolt2\fy-Abs(Sin(angle-sd2))*s\length*sp2*c2*Sin(fd2)*Sgn(s\bolt2\sy)-s\length*c2*gravity/100
			
;				If S\Bolt1\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - B - Bolt1"
;					DebugLog "    PX" + RSet$(S\Bolt1\PX, 14) + " PY" + RSet$(S\Bolt1\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt1\SX, 14) + " SY" + RSet$(S\Bolt1\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt1\FX, 14) + " FY" + RSet$(S\Bolt1\FY, 14)
;				EndIf
;				If S\Bolt2\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - B - Bolt2"
;					DebugLog "    PX" + RSet$(S\Bolt2\PX, 14) + " PY" + RSet$(S\Bolt2\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt2\SX, 14) + " SY" + RSet$(S\Bolt2\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt2\FX, 14) + " FY" + RSet$(S\Bolt2\FY, 14)
;				EndIf
			
			;deformation resistance (ds=deformationspeed)
			ds#=(s\stress-s\lstress)
			force#=Sqr(Abs(ds))*Sgn(ds)*s\material\strength/100000 ;square it to especially take on those low-amplitudic wobbles

			s\bolt1\fx=s\bolt1\fx-force*cosa*s\bolt1\mass
			s\bolt1\fy=s\bolt1\fy-force*sina*s\bolt1\mass
			s\bolt2\fx=s\bolt2\fx+force*cosa*s\bolt2\mass
			s\bolt2\fy=s\bolt2\fy+force*sina*s\bolt2\mass

;				If S\Bolt1\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - C - Bolt1"
;					DebugLog "    PX" + RSet$(S\Bolt1\PX, 14) + " PY" + RSet$(S\Bolt1\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt1\SX, 14) + " SY" + RSet$(S\Bolt1\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt1\FX, 14) + " FY" + RSet$(S\Bolt1\FY, 14)
;				EndIf
;				If S\Bolt2\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - C - Bolt2"
;					DebugLog "    PX" + RSet$(S\Bolt2\PX, 14) + " PY" + RSet$(S\Bolt2\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt2\SX, 14) + " SY" + RSet$(S\Bolt2\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt2\FX, 14) + " FY" + RSet$(S\Bolt2\FY, 14)
;				EndIf
				
			;stress
			s\lstress=s\stress
			s\stress=Sqr((s\bolt1\px-s\bolt2\px)^2+(s\bolt1\py-s\bolt2\py)^2)-s\length
			force#=s\stress*s\material\strength/s\length

			s\bolt1\fx=s\bolt1\fx-force*cosa
			s\bolt1\fy=s\bolt1\fy-force*sina
			s\bolt2\fx=s\bolt2\fx+force*cosa
			s\bolt2\fy=s\bolt2\fy+force*sina
						
;				If S\Bolt1\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - D - Bolt1"
;					DebugLog "    PX" + RSet$(S\Bolt1\PX, 14) + " PY" + RSet$(S\Bolt1\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt1\SX, 14) + " SY" + RSet$(S\Bolt1\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt1\FX, 14) + " FY" + RSet$(S\Bolt1\FY, 14)
;				EndIf
;				If S\Bolt2\Id = G_DebugTraceBoltId
;					DebugLog "  Segment Loop - D - Bolt2"
;					DebugLog "    PX" + RSet$(S\Bolt2\PX, 14) + " PY" + RSet$(S\Bolt2\PY, 14)
;					DebugLog "    SX" + RSet$(S\Bolt2\SX, 14) + " SY" + RSet$(S\Bolt2\SY, 14)
;					DebugLog "    FX" + RSet$(S\Bolt2\FX, 14) + " FY" + RSet$(S\Bolt2\FY, 14)
;				EndIf
				
			;segment breaks if it is streched too far
			If Abs(s\stress)>s\material\elasticity*s\length And s\segmenttype<>train Then Deletesegment(s)
		Next
		
		;wheel-rail collision detection
		For w.wheel=Each wheel
			s=w\wheel
			;find rail
			w\rail=Null	;supporting rail
			For r.segment=Each segment
				If r\segmenttype=rail Or r\segmenttype=fixedrail Then
					b1#=ATan2(r\bolt1\py-r\bolt2\py,r\bolt1\px-r\bolt2\px)-ATan2(s\bolt1\py-r\bolt1\py,s\bolt1\px-r\bolt1\px)
					lb1#=Sqr((r\bolt1\px-s\bolt1\px)^2+(r\bolt1\py-s\bolt1\py)^2)
					lb2#=Sqr((r\bolt2\px-s\bolt1\px)^2+(r\bolt2\py-s\bolt1\py)^2)
					p#=lb1*-Cos(b1)				;position along rail
					If p>0 And p<r\length Then
						h#=lb1*-Sin(b1)			;distance from rail
						If Abs(h)<2 Then 
							h=-(h-2)*400
							w\lcontact=w\contact
							w\contact=h
							w\rail=r
							Goto found
						End If
					End If
				End If
			Next	
			.found
			If w\rail<>Null Then
				r=w\rail
				angle#=ATan2(r\bolt1\py-r\bolt2\py,r\bolt1\px-r\bolt2\px)
				normal#=angle+90
				;counter bobbing
				m#=(w\lcontact-w\contact)*100
				;apply force on wheel
				s\bolt1\fx=s\bolt1\fx+Cos(normal)*(h-m)
				s\bolt1\fy=s\bolt1\fy+Sin(normal)*(h-m)
				;apply force on rail
				r\bolt1\fx=r\bolt1\fx-Cos(normal)*h*p/r\length
				r\bolt1\fy=r\bolt1\fy-Sin(normal)*h*p/r\length
				r\bolt2\fx=r\bolt2\fx-Cos(normal)*h*(r\length-p)/r\length
				r\bolt2\fy=r\bolt2\fy-Sin(normal)*h*(r\length-p)/r\length
				;engine
				If s\bolt1=trainengine Then
					s\bolt1\fx=s\bolt1\fx-Cos(angle)*trainpower
					s\bolt1\fy=s\bolt1\fy-Sin(angle)*trainpower
				End If
			End If
		Next
		
		If traintest=True Then
			If trainend\px>rightshore Then 
				Color 255,255,255
				Text screenx/2,screeny/2,"Victory!"
				If trainengine\px>levelwidth Then cleartrain()
			End If
		End If
		
		If pull<>Null And rightbutton Then
			pull\fx=pull\fx-Float(pull\px-xmouse-camerax)/n*1000
			pull\fy=pull\fy-Float(pull\py-ymouse-cameray)/n*1000
		End If

		;apply all the forces to the bolts
		For b.bolt=Each bolt
;			If B\Id = G_DebugTraceBoltId
;				DebugLog "  Bolt Loop - A"
;				DebugLog "    PX" + RSet$(B\PX, 14) + " PY" + RSet$(B\PY, 14)
;				DebugLog "    SX" + RSet$(B\SX, 14) + " SY" + RSet$(B\SY, 14)
;				DebugLog "    FX" + RSet$(B\FX, 14) + " FY" + RSet$(B\FY, 14)
;			EndIf

			If b\bolttype<>fixed Then
				If b\px>0 And b\px<levelwidth-1 Then
					;depth
					d#=b\py-height(b\px)
					;calc the slope of this part of the river/lake floor
					slope#=ATan2(height(Ceil(b\px+.0001))-height(Floor(b\px)),1)
				Else
					slope=0
					d=b\py
				End If
				If d>0 Then
					;normal force
					b\fx=b\fx+Sin(slope)*b\mass*d
					b\fy=b\fy-Cos(slope)*b\mass*d
					;very heavy fricton
					b\fx=b\fx-b\sx*d*1000
					b\fy=b\fy-b\sy*d*1000
				End If
				
				;add gravity
				b\fy=b\fy+b\mass*gravity/100

				;accelerate
				b\sx=b\sx+b\fx/b\mass/4/n
				b\sy=b\sy+b\fy/b\mass/4/n

				;and move
				b\px=b\px+b\sx/4/n
				b\py=b\py+b\sy/4/n
			End If

			b\lpx=b\px
			b\lpy=b\py
			b\lsx=b\sx
			b\lsy=b\sy
			b\lfx=b\fx
			b\lfy=b\fy

;			If B\Id = G_DebugTraceBoltId
;				DebugLog "  Bolt Loop - B"
;				DebugLog "    PX" + RSet$(B\PX, 14) + " PY" + RSet$(B\PY, 14)
;				DebugLog "    SX" + RSet$(B\SX, 14) + " SY" + RSet$(B\SY, 14)
;				DebugLog "    FX" + RSet$(B\FX, 14) + " FY" + RSet$(B\FY, 14)
;			EndIf

			;reset forces
			b\fx=0
			b\fy=0

		Next	
	Next

End Function