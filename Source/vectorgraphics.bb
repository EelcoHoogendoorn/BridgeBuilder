Include "math.bb"

Type space
	Field parent.space
	Field children
	Field firstchild.space
	Field locked
	
	Field localposition.vector2d
	Field worldposition.vector2d
	Field cameraposition.vector2d

	Field localangle#
	Field worldangle#
	Field transform.matrix2x2
	Field property.matrix2x2
End Type
Global worldspace.space=space(Null)

Type camera
	Field localspace.space
	Field viewportsize.vector2d
	Field viewportposition.vector2d
	Field centre.vector2d
	Field project.matrix2x2
End Type

Type mesh
	Field localspace.space
	Field boundradius#
	
	Field vertices
	Field firstvertex.vertex
	
	Field edges
	Field firstedge.edge
End Type

Type vertex
	Field localposition.vector2d
	Field worldposition.vector2d
	Field cameraposition.vector2d
	
	Field sector
End Type

Type edge
	Field v0.vertex,v1.vertex
	Field col
	Field collide
End Type


Function linkspace(parent.space)

	child.space=parent\firstchild
	For i=1 To parent\children
		If child\locked Then child\worldangle=child\localangle+parent\worldangle Else child\worldangle=child\localangle

		do_eulertomatrix2x2(child\transform,child\worldangle)
		do_buffered_multiply_matrix2x2(child\transform,child\property)
		
		do_transform_vector2d(child\worldposition,child\localposition,parent\transform)
		do_add_vector2d(child\worldposition,child\worldposition,parent\worldposition)
		
		linkspace(child)
		
		child=After child
	Next

End Function

Function transformgeometry()
	For m.mesh=Each mesh
		mb#=0
		v.vertex=m\firstvertex
		For i=1 To m\vertices
			do_transform_vector2d(v\worldposition,v\localposition,m\localspace\transform)
			
			bd#=v\worldposition\x^2+v\worldposition\y^2
			If bd>mb Then mb=bd
			
			do_add_vector2d(v\worldposition,v\worldposition,m\localspace\worldposition)
			v=After v
		Next
		m\boundradius=Sqr(mb)*1.1
	Next
End Function

Function projectcamera(c.camera)
	do_invert_matrix2x2(c\project,c\localspace\transform)
	
	For m.mesh=Each mesh
		do_sub_vector2d(tempvector2d,m\localspace\worldposition,c\localspace\worldposition)
		do_transform_vector2d(m\localspace\cameraposition,tempvector2d,c\project)
	
		v.vertex=m\firstvertex
		For i=1 To m\vertices
		
			do_sub_vector2d(tempvector2d,v\worldposition,c\localspace\worldposition)
			do_transform_vector2d(v\cameraposition,tempvector2d,c\project)
			
			v=After v
		Next
	Next
End Function

Function drawcamera(c.camera)
	LockBuffer

	Viewport c\viewportposition\x,c\viewportposition\y,c\viewportsize\x,c\viewportsize\y
;	Origin c\viewportposition\x,c\viewportposition\y
	
	;dereference
	vsx=c\viewportsize\x
	vsy=c\viewportsize\y
	
	vx#=Float(c\viewportsize\x)/2
	vy#=Float(c\viewportsize\y)/2

	cs#=maxscale(c\project)

	For m.mesh=Each mesh
		ms#=cs*m\boundradius
;Oval m\localspace\cameraposition\x+vx-ms,m\localspace\cameraposition\y+vy-ms,ms*2,ms*2,False
		If m\localspace\cameraposition\x+ms>-vx Then
		If m\localspace\cameraposition\x-ms<vx Then 
		If m\localspace\cameraposition\y+ms>-vy Then
		If m\localspace\cameraposition\y-ms<vy Then
	
		v.vertex=m\firstvertex
		For i=1 To m\vertices
		
			v\cameraposition\x=v\cameraposition\x+vx
			v\cameraposition\y=v\cameraposition\y+vy
			
			v\sector=0
			If (v\cameraposition\x)<0 Then
				v\sector=(v\sector Or %000100)
			Else If (v\cameraposition\x)=>c\viewportsize\x
				v\sector=(v\sector Or %000001)
			Else 
				v\sector=(v\sector Or %000010)
			End If

			If (v\cameraposition\y)<0 Then
				v\sector=(v\sector Or %100000)
			Else If (v\cameraposition\y)=>c\viewportsize\y
				v\sector=(v\sector Or %001000)
			Else 
				v\sector=(v\sector Or %010000)
			End If
			
			v\cameraposition\x=v\cameraposition\x+c\viewportposition\x
			v\cameraposition\y=v\cameraposition\y+c\viewportposition\y
			
			v=After v
		Next
		
		e.edge=m\firstedge
		For i=1 To m\edges
			x0#=e\v0\cameraposition\x
			y0#=e\v0\cameraposition\y
			x1#=e\v1\cameraposition\x
			y1#=e\v1\cameraposition\y
						
			dx#=x1-x0
			dy#=y1-y0

			If (e\v0\sector=%010010) And (e\v1\sector=%010010) Then 
				drawedge2 x0,y0,dx,dy,1,e\col
			Else If (e\v0\sector=e\v1\sector) Then
			Else If (((e\v0\sector Xor e\v1\sector) And %000111)=False) And ((e\v0\sector And %000010)=False)
			Else If (((e\v0\sector Xor e\v1\sector) And %111000)=False) And ((e\v0\sector And %010000)=False)
			Else If (e\v0\sector=%010010) And (e\v1\sector<>%010010) Then 
				dpx#=-c\viewportsize\x*dy
				dpy#=-c\viewportsize\y*dx
				
				u#=y0-c\viewportposition\y
				d#=y0-c\viewportposition\y-c\viewportsize\y
				l#=x0-c\viewportposition\x
				r#=x0-c\viewportposition\x-c\viewportsize\x

				pm#=1
				
				pu#=(u*vsx)/dpx
				If pu=>0 And pu<pm Then pm=pu
				
				pd#=(d*vsx)/dpx
				If pd=>0 And pd<pm Then pm=pd

				pl#=(l*vsy)/dpy
				If pl=>0 And pl<pm Then pm=pl

				pr#=(r*vsy)/dpy
				If pr=>0 And pr<pm Then pm=pr
				
				drawedge2 x0,y0,dx,dy,pm,e\col
			Else If (e\v1\sector=%010010) And (e\v0\sector<>%010010) Then
				dpx#=vsx*dy
				dpy#=vsy*dx
				
				u#=y1-c\viewportposition\y
				d#=y1-c\viewportposition\y-vsy
				l#=x1-c\viewportposition\x
				r#=x1-c\viewportposition\x-vsx

				pm#=1
				
				pu#=(u*vsx)/dpx
				If pu=>0 And pu<pm Then pm=pu
				
				pd#=(d*vsx)/dpx
				If pd=>0 And pd<pm Then pm=pd

				pl#=(l*vsy)/dpy
				If pl=>0 And pl<pm Then pm=pl

				pr#=(r*vsy)/dpy
				If pr=>0 And pr<pm Then pm=pr
				drawedge2 x1,y1,-dx,-dy,pm,e\col
			Else
				dpx#=-vsx*dy
				dpy#=-vsy*dx
				
				u#=y0-c\viewportposition\y
				d#=y0-c\viewportposition\y-vsy
				l#=x0-c\viewportposition\x
				r#=x0-c\viewportposition\x-vsx
				
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

				If p=2 Then drawedge x0+dx*p0,y0+dy*p0,x0+dx*p1,y0+dy*p1,e\col
			End If
			
			e=After e
		Next

		End If
		End If
		End If
		End If

	Next
	
	UnlockBuffer
End Function

Function drawedge2(sx#,sy#,dx#,dy#,d#,col)

	ax#=Abs(dx)
	ay#=Abs(dy)
	
	If ax>ay Then
		t=(ax*d)
		ty#=dy/ax
		tx#=Sgn(dx)
	Else
		t=(ay*d)
		tx#=(dx/ay)
		ty#=Sgn(dy)
	End If
	
	While i<t
		WritePixel sx,sy,col
		sx=sx+tx
		sy=sy+ty
		i=i+1
	Wend

End Function

Function drawedge(sx#,sy#,ex#,ey#,col)
	dx#=ex-sx
	dy#=ey-sy
	
	ax#=Abs(dx)
	ay#=Abs(dy)
	
	If ax>ay Then
		t=ax
		ty#=dy/ax
		tx#=Sgn(dx)
	Else
		t=ay
		tx#=(dx/ay)
		ty#=Sgn(dy)
	End If
	
	ix#=sx
	iy#=sy
	
	While i<t
		WritePixel ix,iy,col
		ix=ix+tx
		iy=iy+ty
		i=i+1
	Wend
	WritePixel ex,ey,col
End Function

Function space.space(parent.space)
	s.space=New space
	s\localposition	=get_vector2d()
	s\worldposition	=get_vector2d()
	s\cameraposition=get_vector2d()
	s\property		=get_matrix2x2()
	s\transform		=get_matrix2x2()
	s\locked		=True
	
	s\parent=parent
	If parent<>Null Then
		parent\children=parent\children+1
		If parent\firstchild<>Null Then Insert s Before parent\firstchild
		parent\firstchild=s
	End If
	Return s
End Function

Function translatespace(s.space,v.vector2d)
	do_transform_vector2d(buffervector2d,v,s\transform)
	do_add_vector2d(s\localposition,s\localposition,buffervector2d)
End Function

Function positionspace(s.space,x#,y#)
	do_set_vector2d(s\localposition,x,y)
End Function

Function movespace(s.space,x#,y#)
	do_change_vector2d(s\localposition,x,y)
End Function

Function rotatespace(s.space,angle#)
	s\localangle=angle
End Function

Function turnspace(s.space,angle#)
	s\localangle=s\localangle+angle
End Function

Function mesh.mesh(localspace.space,parent.space)
	If localspace=Null Then localspace=space(parent)
	m.mesh=New mesh
	m\localspace=localspace
	Return m
End Function

Function getboundradius(m.mesh)
	rm#=0
	v.vertex=m\firstvertex
	For i=1 To m\vertices
		r2# = v\localposition\x^2+v\localposition\y^2
		If r2>rm Then rm=r2
		v=After v
	Next
	m\boundradius=Sqr(rm)*1.1
End Function

Function maxscale#(m.matrix2x2)
	l1#=m\x\x^2+m\x\y^2
	l2#=m\y\x^2+m\y\y^2
	If l1>l2 Then Return Sqr(l1) Else Return Sqr(l2)
End Function

Function camera.camera(parent.space)
	c.camera=New camera
	c\localspace		=space(parent)
	c\viewportposition	=get_vector2d()
	c\viewportsize		=get_vector2d()
	c\centre			=get_vector2d()
	setviewport(c,0,0,GraphicsWidth(),GraphicsHeight())
	c\project			=get_matrix2x2()
	
	Return c
End Function

Function setviewport(c.camera,x,y,w,h)
	do_set_vector2d(c\viewportposition,x,y)
	do_set_vector2d(c\viewportsize,w,h)
	do_set_vector2d(c\centre,c\viewportposition\x+c\viewportsize\x/2,c\viewportposition\y+c\viewportsize\y/2)
End Function

Function vertex.vertex(m.mesh,x#,y#)
	v.vertex=New vertex
	v\localposition=get_vector2d(x,y)
	v\worldposition=get_vector2d()
	v\cameraposition=get_vector2d()
	
	m\vertices=m\vertices+1
	If m\firstvertex<>Null Then Insert v Before m\firstvertex
	m\firstvertex=v
	Return v
End Function

Function edge.edge(m.mesh,v0.vertex,v1.vertex,col)
	e.edge=New edge
	e\v0=v0
	e\v1=v1
	e\col=col
	
	m\edges=m\edges+1
	If m\firstedge<>Null Then Insert e Before m\firstedge
	m\firstedge=e
	Return e
End Function