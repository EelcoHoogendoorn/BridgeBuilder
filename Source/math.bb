Type vector2d
	Field x#
	Field y#
End Type

Function get_vector2d.vector2d(x#=0,y#=0)
	v.vector2d=New vector2d
	do_set_vector2d(v,x,y)
	Return v
End Function

Function do_set_vector2d(v.vector2d,x#,y#)
	v\x=x
	v\y=y
End Function

Function do_copy_vector2d(r.vector2d,v.vector2d)
	do_set_vector2d(r,v\x,v\y)
End Function

Function do_add_vector2d(r.vector2d,v1.vector2d,v2.vector2d)
	r\x=v1\x+v2\x
	r\y=v1\y+v2\y
End Function

Function do_sub_vector2d(r.vector2d,v1.vector2d,v2.vector2d)
	r\x=v1\x-v2\x
	r\y=v1\y-v2\y
End Function

Function do_change_vector2d(v.vector2d,x#,y#)
	v\x=v\x+x
	v\y=v\y+y
End Function

Function do_normalize_vector2d(v.vector2d)
	scale#=1.0/get_magnitude_vector2d(v)
	do_scale_vector2d(v,scale)
End Function

Function do_scale_vector2d(v.vector2d,s#)
	do_set_vector2d(v,v\x*s,v\y*s)
End Function

Function do_negate_vector2d(v.vector2d)
	v\x=-v\x
	v\y=-v\y
End Function

Function get_magnitude_vector2d#(v.vector2d)
	Return Sqr(v\x^2+v\y^2)
End Function

Function get_dotproduct_vector2d#(v1.vector2d,v2.vector2d)
	Return v1\x*v2\x+v1\y*v2\y
End Function

Function get_innerproduct_vector2d#(v1.vector2d,v2.vector2d)
	Return v1\x*v2\y-v1\y*v2\x
End Function

Type vector3d
	Field x#
	Field y#
	Field z#
End Type

Type matrix3x3
	Field x.vector3d
	Field y.vector3d
	Field z.vector3d
End Type

Function get_vector3d.vector3d(x#=0,y#=0,z#=0)
	v.vector3d=New vector3d
	do_set_vector3d(v,x,y,z)
	Return v
End Function

Function do_set_vector3d(v.vector3d,x#,y#,z#)
	v\x=x
	v\y=y
	v\z=z
End Function

Function do_copy_vector3d(r.vector3d,v.vector3d)
	do_set_vector3d(r,v\x,v\y,v\z)
End Function

Function do_add_vector3d(r.vector3d,v1.vector3d,v2.vector3d)
	r\x=v1\x+v2\x
	r\y=v1\y+v2\y
	r\z=v1\z+v2\z
End Function

Function do_sub_vector3d(r.vector3d,v1.vector3d,v2.vector3d)
	r\x=v1\x-v2\x
	r\y=v1\y-v2\y
	r\z=v1\z-v2\z
End Function

Function do_change_vector3d(v.vector3d,x#,y#,z#)
	do_set_vector3d(v,v\x+x,v\y+y,v\z+z)
End Function

Function do_normalize_vector3d(v.vector3d)
	scale#=1.0/get_magnitude_vector3d(v)
	do_scale_vector3d(v,scale)
End Function

Function do_scale_vector3d(v.vector3d,s#)
	do_set_vector3d(v,v\x*s,v\y*s,v\z*s)
End Function

Function do_negate_vector3d(v.vector3d)
	do_set_vector3d(v,-v\x,-v\y,-v\z)
End Function

Function get_magnitude_vector3d#(v.vector3d)
	Return Sqr(v\x^2+v\y^2+v\z^2)
End Function

Function get_matrix3x3.matrix3x3()
	m.matrix3x3=New matrix3x3
	m\x=get_vector3d(1,0,0)
	m\y=get_vector3d(0,1,0)
	m\z=get_vector3d(0,0,1)
	Return m
End Function

Function do_multiply_matrix3x3(r.matrix3x3,m1.matrix3x3,m2.matrix3x3)
;	If r=m1 Or r=m2 Then End			;big headache stopper...
	do_transform_vector3d(r\x,m1\x,m2)
	do_transform_vector3d(r\y,m1\y,m2)
	do_transform_vector3d(r\z,m1\z,m2)
End Function

Function do_set_matrix3x3(m.matrix3x3,v.vector3d)
	cx#=Cos(v\x) : sx#=Sin(v\x)
	cy#=Cos(v\y) : sy#=Sin(v\y)
	cz#=Cos(v\z) : sz#=Sin(v\z)
;	m\x\x=cy*cz			: m\x\y=cy*sz			: m\x\z=-sy
;	m\y\x=sx*sy*cz-cx*sz: m\y\y=sx*sy*sz+cx*cz	: m\y\z=sx*cy
;	m\z\x=cx*sy*cz+sx*sz: m\z\y=cx*sy*sz-sx*cz	: m\z\z=cx*cy
	
	;inverted? works much better anyway...
	m\x\x=cy*cz	: m\x\y=sx*sy*cz-cx*sz	: m\x\z=cx*sy*cz+sx*sz
	m\y\x=cy*sz	: m\y\y=sx*sy*sz+cx*cz	: m\y\z=cx*sy*sz-sx*cz
	m\z\x=-sy	: m\z\y=sx*cy			: m\z\z=cx*cy
End Function

Function do_copy_matrix3x3(r.matrix3x3,v.matrix3x3)
	do_copy_vector3d(r\x,v\x)
	do_copy_vector3d(r\y,v\y)
	do_copy_vector3d(r\z,v\z)
End Function

Function do_transform_vector3d(r.vector3d,v.vector3d,m.matrix3x3)
	r\x=get_dotproduct_vector3d(m\x,v)
	r\y=get_dotproduct_vector3d(m\y,v)
	r\z=get_dotproduct_vector3d(m\z,v)
End Function

Function get_dotproduct_vector3d#(v1.vector3d,v2.vector3d)
	Return v1\x*v2\x+v1\y*v2\y+v1\z*v2\z
End Function

Function get_innerproduct_vector3d#(v1.vector3d,v2.vector3d)
	Return (v1\y*v2\z-v1\z*v2\y)+(v2\x*v1\z-v2\z*v1\x)+(v1\x*v2\y-v1\y*v2\x)
End Function

Function get_cosangle_vector3d#(v1.vector3d,v2.vector3d)
	Return (get_dotproduct_vector3d(v1,v2)/get_magnitude_vector3d(v1)/get_magnitude_vector3d(v2))
End Function

Function get_sinangle_vector3d#(v1.vector3d,v2.vector3d)
	Return (get_innerproduct_vector3d(v1,v2)/get_magnitude_vector3d(v1)/get_magnitude_vector3d(v2))
End Function

Function do_crossproduct_vector3d(r.vector3d,v1.vector3d,v2.vector3d)
	r\x=v1\y*v2\z-v1\z*v2\y
	r\y=v2\x*v1\z-v2\z*v1\x
	r\z=v1\x*v2\y-v1\y*v2\x
End Function