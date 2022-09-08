;---------------------------------------------------------------------------------------------------------------------------------
;
; HyperText subsystem
;
; --------------------------------------------------------------------------------------------------------------------------------
Type HT_T_Item
	Field	ItemType
	Field	Name$
	
	; Links to the other Items
	Field	ParentItem.HT_T_Item
	Field	PrevItem.HT_T_Item
	Field	NextItem.HT_T_Item
	Field	HeadChildItem.HT_T_Item
	Field	TailChildItem.HT_T_Item
	
	; Size and position attributes
	Field	MaxWidth
	Field	MaxHeight
	Field	AdjWidth
	Field	AdjHeight
	Field	EdgeLeft
	Field	EdgeRight
	Field	EdgeTop
	Field	EdgeBottom
	
	; General attributes
	Field	FontFace
	Field	FontSize
	Field	FontBold
	Field	FontItalic
	Field	FontUnderlined
	Field	PointSize
	
	; Colour attributes
	Field	Colour
	Field	BackgroundColour
	Field	TextColour
	Field	LinkColour
	Field	VLinkColour
	
	Field	BackgroundImage
	
	; Miscellaneous attributes - Most of these relate to specific item types only
	Field	HeadingLevel
	Field	Alignment
	Field	ListType
	Field	TableColSpan
	Field	TableRowSpan
	
	; Links to Shards (for the composed text fragments)
	Field	HeadShard.HT_T_Shard
	Field	TailShard.HT_T_Shard
	
	; Flag to indicate a Push/Pull operation on the Attribute stack
	Field	AttributeStack
End Type

Const HT_C_ItemTypeRoot			= 0
Const HT_C_ItemTypeHead			= 100
Const HT_C_ItemTypeMeta			= 101
Const HT_C_ItemTypeBody			= 200
Const HT_C_ItemTypeHeading		= 201
Const HT_C_ItemTypeParagraph	= 202
Const HT_C_ItemTypeList			= 203
Const HT_C_ItemTypeListItem		= 204
Const HT_C_ItemTypeTable		= 205
Const HT_C_ItemTypeTableRow		= 206
Const HT_C_ItemTypeTableCell	= 207
Const HT_C_ItemTypeImage		= 208
Const HT_C_ItemTypeLink			= 209
Const HT_C_ItemTypeBreak		= 210
Const HT_C_ItemTypeTab			= 211

Type HT_T_Shard
	Field	NextShard.HT_T_Shard
	Field	TextOffset
	Field	TextLength
	
End Type

Global HT_G_Root.HT_T_Item

; Code starts

HT_Test()
End

Function HT_Test()
	Local I1.HT_T_Item
	Local I2.HT_T_Item
	Local I3.HT_T_Item
	Local I4.HT_T_Item
	Local I5.HT_T_Item
	Local I6.HT_T_Item
	
	Graphics 800, 600
	Cls
	
	HT_G_Root = HT_ItemAddRoot	(			"Root")
	I1 = HT_ItemAddChild		(HT_G_Root, "1000")
	I2 = HT_ItemAddPeer			(I1, 		"2000")
	I3 = HT_ItemAddChild		(I2, 		"2100")
	I4 = HT_ItemAddChild		(I3,		"2110")
	HT_ItemAddPeer				(I4,		"2120")
	I5 = HT_ItemAddPeer			(I3,		"2200")
	I6 = HT_ItemAddChild		(I5,		"2210")
	I6 = HT_ItemAddPeer			(I6,		"2220")
	I6 = HT_ItemAddPeer			(I6,		"2230")
	I6 = HT_ItemAddPeer			(I6,		"2240")
	I6 = HT_ItemAddPeer			(I6,		"2250")
	I6 = HT_ItemAddPeer			(I6,		"2260")
	I6 = HT_ItemAddPeer			(I6,		"2270")
	I6 = HT_ItemAddPeer			(I6,		"2280")
	
	I6 = HT_ItemAddChild		(I1, 		"1100")
	HT_ItemAddPeer				(I6,		"1200")
	HT_ItemAddChild				(I1,		"1300")
	HT_ItemAddPeer				(I6,		"1150")
	
	HT_ItemTreeWalk(HT_G_Root)
	
	WaitKey()
End Function

Function HT_ItemAddRoot.HT_T_Item(Name$)
	Local NewI.HT_T_Item
	
	NewI = New HT_T_Item
	
	NewI\Name 			= Name
	
	NewI\PrevItem		= Null
	NewI\ParentItem		= Null
	NewI\NextItem		= Null
	NewI\HeadChildItem = Null
	NewI\TailChildItem	= Null
	
	Return NewI

End Function

Function HT_ItemAddPeer.HT_T_Item(OldI.HT_T_Item, Name$)
	Local NewI.HT_T_Item
	
	NewI = New HT_T_Item
	
	NewI\Name 			= Name
	
	NewI\PrevItem		= OldI
	NewI\ParentItem		= OldI\ParentItem
	NewI\NextItem		= OldI\NextItem
	NewI\HeadChildItem = Null
	NewI\TailChildItem	= Null
	
	OldI\NextItem		= NewI
	
	If NewI\ParentItem\TailChildItem = OldI
		NewI\ParentItem\TailChildItem = NewI
	EndIf
	
	Return NewI

End Function

Function HT_ItemAddChild.HT_T_Item(OldI.HT_T_Item, Name$)
	Local NewI.HT_T_Item
	
	NewI = New HT_T_Item
	
	NewI\Name 			= Name
	
	NewI\PrevItem		= OldI\TailChildItem
	If NewI\PrevItem = Null
		OldI\HeadChildItem = NewI
	Else
		NewI\PrevItem\NextItem = NewI
	EndIf
	
	OldI\TailChildItem	= NewI
	
	NewI\ParentItem		= OldI
	NewI\NextItem		= Null
	NewI\HeadChildItem = Null
	NewI\TailChildItem	= Null
	
	Return NewI

End Function

Function HT_ItemTreeWalk(Root.HT_T_Item)
	Local I.HT_T_Item
	
	I = Root
	Level = 0
	Y = 0
	Color 255, 255, 255

	While I <> Null
		; Print this node
		Text Level * 20, Y, "Node - " + I\Name
		Y = Y + 10
		
		; Walk to the next node
		If I\HeadChildItem <> Null
			I = I\HeadChildItem
			Level = Level + 1
		ElseIf I\NextItem <> Null
			I = I\NextItem
		Else
			While True
				I = I\ParentItem
				Level = Level - 1
				
				If I = Null
					; Gotten back to the root
					Exit
				ElseIf I\NextItem <> Null
					; there is a NextItem
					I = I\NextItem
					Exit
				EndIf
			Wend
		EndIf
	Wend
	
End function

Function HT_ItemHeadBegin()
End Function

Function HT_ItemHeadEnd()
End Function

Function HT_ItemMeta()
End Function

Function HT_ItemBodyBegin()
End Function

Function HT_ItemBodyEnd()
End Function

Function HT_ItemHeadingBegin()
End Function

Function HT_ItemHeadingEnd()
End function

Function HT_ItemParagraphBegin()
End Function

Function HT_ItemParagraphEnd()
End function

Function HT_ItemTableBegin()
End Function

Function HT_ItemTableEnd()
End Function

Function HT_ItemTableRowBegin()
End Function

Function HT_ItemTableRowEnd()
End Function

Function HT_ItemTableCellBegin()
End Function

Function HT_ItemTableCellEnd()
End Function

Function HT_ItemListBegin()
End Function

Function HT_ItemListEnd()
End Function

Function HT_ItemListItemBegin()
End Function

Function HT_ItemListItemEnd()
End Function

Function HT_ItemImage()
End Function