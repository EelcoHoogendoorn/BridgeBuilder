; 20030428 PGF Changed colour of SteelCable
; 20030629 PGF Change from G_MaterialSelected to G_MaterialPrimary and added G_MaterialSecondary etc
; --------------------------------------------------------------------------------------------------------------------------------
;
; Material Module
;
; --------------------------------------------------------------------------------------------------------------------------------

Type T_Material
	Field Id
	Field Name$
	Field Mass#
	Field Strength#			;in force/stress%
	Field MaxPressure#		; %
	Field MaxTension#		; %
	Field Damping#
	Field Cable				; True = Cable like behaviour under compression
	Field QtyAvailable#		; Amount of this material that is available (-1 = No limit)
	Field QtyUsed#			; How much of this material has been used (actually placed)
	Field QtyMacro#         ; How much of this material is being requested through current macro build (if any)
	Field CostPerUnit#      ; Cost of the material in $/unit
	Field CostUsed#			; Cost of this material (actually placed)
	Field CostMacro#		; Cost of this material being requested through current macro build (if any)
	Field R,G,B
	;Field PopupImage
	Field Icon.T_Icon
End Type

; Material variables
Global G_MaterialCostUsed#
Global G_MaterialCostMacro#
Global G_MaterialBudget#

Global G_MaterialSelectedQty#
Global G_MaterialSelectedCost#
Global G_MaterialSelectedMass#

Global G_MaterialWoodBeam.T_Material = New T_Material
Global G_MaterialSteelBeam.T_Material = New T_Material
Global G_MaterialConcreteBeam.T_Material = New T_Material
Global G_MaterialSteelCable.T_Material = New T_Material

Global G_MaterialPrimary.T_Material
Global G_MaterialSecondary.T_Material

Function MaterialInitialise()
	Local M.T_Material
	
	G_MaterialBudget = 5000000
	
	M.T_Material = G_MaterialWoodBeam
	M\Id					= 0
	M\Name					= "Wood Beam"
	M\Mass					= 0.8
	M\Strength				= 300000
	M\MaxPressure			= .01
	M\MaxTension			= .01
	M\Damping				= 10000
	M\Cable					= False
	M\QtyAvailable			= -1
	M\CostPerUnit			= 1.0
	M\R						= 200
	M\G						= 150
	M\B						= 30
	M\Icon					= New T_Icon
	M\Icon\Group			= 2 
	IconImageLoad(M\Icon, 1)
	
	M.T_Material = G_MaterialSteelBeam
	M\Id					= 1
	M\Name					= "Steel Beam"
	M\Mass					= 8
	M\Strength				= 1000000
	M\MaxPressure			= .01
	M\MaxTension			= .01
	M\Damping				= 20000
	M\Cable					= False
	M\QtyAvailable			= -1
	M\CostPerUnit			= 5.0
	M\R						= 90
	M\G						= 100
	M\B						= 255
	M\Icon					= New T_Icon
	M\Icon\Group			= 2 
	IconImageLoad(M\Icon, 2)
	
	M.T_Material = G_MaterialConcreteBeam
	M\Id					= 2
	M\Name					= "Concrete Beam"
	M\Mass					= 20
	M\Strength				= 10000000
	M\MaxPressure			= .01
	M\MaxTension			= .002
	M\Damping				= 50000
	M\Cable					= False
	M\QtyAvailable			= -1
	M\CostPerUnit			= 10.0
	M\R						= 200
	M\G						= 200
	M\B						= 200
	M\Icon					= New T_Icon
	M\Icon\Group			= 2 
	IconImageLoad(M\Icon, 3)
	
	M.T_Material = G_MaterialSteelCable
	M\Id					= 3
	M\Name					= "Steel Cable"
	M\Mass					= 2
	M\Strength				= 1000000
	M\MaxPressure			= .001
	M\MaxTension			= .05
	M\Damping				= 10000
	M\Cable					= True
	M\QtyAvailable			= -1
	M\CostPerUnit			= 2.0
	M\R						= 200
	M\G						= 100
	M\B						= 250
	M\Icon					= New T_Icon
	M\Icon\Group			= 2 
	IconImageLoad(M\Icon, 4)
	
	G_MaterialPrimary = G_MaterialWoodBeam
	G_Materialsecondary = G_MaterialPrimary

End Function

Function MaterialSelectNext()
	Local M.T_Material
	
	M = G_MaterialPrimary
	
	M\Icon\Status = C_IconStatusNormal
	
	Repeat
		If M = Last T_Material
			M = First T_Material
		Else
			M = After M
		EndIf
	Until M\Icon\Status = C_IconStatusNormal
	
	G_MaterialPrimary = M
	G_Materialsecondary = M
	
	M\Icon\Status = C_IconStatusSelected
	
	PanelRender(G_ToolPanel)
	
End Function

; Check the amount of each material used against that available and the overall budget
Function MaterialChecks()
	Local M.T_Material
	Local S.T_Segment
	Local MS.T_MSegment
	
	; Prepare the counters
	For M = Each T_Material
		M\QtyUsed				= 0
		M\QtyMacro				= 0
		M\CostUsed				= 0
		M\CostMacro				= 0
	Next

	G_MaterialSelectedQty	= 0
	G_MaterialSelectedCost	= 0
	G_MaterialSelectedMass	= 0
	
	; Add up material in all of the existing segments
	For S = Each T_Segment
		; Don't count segment undergoing move or stretch
		If Not (S\Shadow And (G_EditTool = C_EditToolMove Or G_EditTool = C_EditToolStretch))
			M = S\Material
			M\QtyUsed = M\QtyUsed + S\Length
			
			If M\QtyAvailable <> -1
				If M\QtyUsed > M\QtyAvailable
					GW_AbortErrorMessage("Material limit exceeded for " + M\Name + " - " + M\QtyUsed + " versus " + M\QtyAvailable)
				EndIf
			EndIf
		EndIf
		
		If S\Selected Or S\SelectedMaybe
			G_MaterialSelectedQty = G_MaterialSelectedQty + S\Length
			G_MaterialSelectedCost = G_MaterialSelectedCost + S\Length * S\Material\CostPerUnit
			G_MaterialSelectedMass = G_MaterialSelectedMass + S\Length * S\Material\Mass
		EndIf
	Next

	; Total cost of all existing material
	G_MaterialCostUsed = 0

	For M = Each T_Material
		M\CostUsed = M\QtyUsed * M\CostPerUnit
		G_MaterialCostUsed = G_MaterialCostUsed + M\CostUsed
	Next

	; Add up material in all macro segments and flag any exceeding the budget of either material or money
	Local MCost# = G_MaterialCostUsed
	Local UCost#
	For MS = Each T_MSegment
		If MS\Material <> Null
			M = MS\Material
		Else
			If MS\MaterialChoice = 2
				M = G_MaterialSecondary
			Else
				M = G_MaterialPrimary
			EndIf
		EndIf
		
		UCost = MS\Length * M\CostPerUnit
		If MCost + UCost <= G_MaterialBudget
			If M\QtyAvailable = -1 Or (M\QtyUsed + M\QtyMacro + MS\Length <= M\QtyAvailable)
				M\QtyMacro = M\QtyMacro + MS\Length
				MCost = MCost + UCost
				MS\BudgetError = False
			Else
				MS\BudgetError = True
			EndIf
		Else
			MS\BudgetError = True
		EndIf
		
		If MS\BudgetError And G_SoundStatus = C_SoundStatusNone
			G_SoundStatus = C_SoundStatusWarn
		EndIf

	Next
	
	; Total cost of all macro segments
	;   NB: The total may be slightly different to that accumulated above due to rounding errors
	G_MaterialCostMacro = 0
	For M = Each T_Material
		M\CostMacro = M\QtyMacro * M\CostPerUnit
		G_MaterialCostMacro = G_MaterialCostMacro + M\CostMacro
	Next
	
End Function