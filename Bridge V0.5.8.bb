; --------------------------------------------------------------------------------------------------------------------------------
;
; Bridge Builder - Extending Eelco's effort with GUI and many other enhancements
;
; --------------------------------------------------------------------------------------------------------------------------------
;
; Revisions:
;
; 20021230 PGF Basic framework without simulation code
;              Did Grid, Zoom In/Out simple rectangular selection and panning
;              Bugs in the panning
; 20030101 PGF Debugged the panning and zooming
; 20030104 PGF Integrated data structures and 'loadbridge()' (as LoadBridgeOld()) from original
;              Simple BridgeDrawFull()
; 20030105 PGF Enhanced BridgeDrawFull() with colouring
;              Added SelectUpdate() and other stuff to support selection of Bolts/Segments
; 20030108 PGF Added (empty) ToolPanel
;              Added simple GroundDraw()
;              Added stubs in Mainline() for Edit/Test/Load/Save/Demo modes
; 20030111 PGF A few changes to the control routines
;              Started integrating Physics() - Bugs with PX,PY going to +/- Infinity
; 20030116 PGF TestPhysics() now working (without Trains).  Had problems with getting the physics
;                right again after reversing the Y direction to suit the graphics code.  Had to step
;                through the old and new versions tracing a single node in detail to resolve it !
;                Added DebugTraceSegment() ad DebugTraceBolt() to assist.
;              Found a minor bug in original code - In the old version of function loadbridge(), the
;                segment length was being treated as an Int and the proper value was being truncated.
;              Did a basic Load function still driven by keys 1-9 instead of the GUI menu that is
;                envisaged for the final product.
;              Enhanced GroundDraw() with the water level and under water contour.
;              Tool Panel can now toggle on/off using Tab key
; 20030120 PGF Created images of Wood_Beam, Steel_Beam, Concrete_Beam and Steel_Cable in POV-Ray
;              Testing images in faked up ToolPanel
; 20030124 PGF Generalised ToolPanel by introducing T_Panel and T_Icon and associated functions
;              HoverText added.  Slight bug where HoverText stays on if the mouse is moved off
;                the panel and then back on to the same icon.  Also want to make it 'sticky'
;              Added material cycling by keyboard and selection by mouse
; 20030126 PGF Fixed HoverText problem when moving off and then back on to the panel
;              Splitting controls into logical areas and introducing a better method of doing the 
;                tools.  Pretty close to a state machine.
;              Changed the Select method from two click to one click / drag
;              Added the MacroArch tool.
; 20030127 PGF Added the MacroPlace function to turn macro constructs into real Bolts/Segments (with attaching
;                to close Bolts that already exist)
;              Changed CursorDraw() to only show select/macro stuff when in edit mode
; 20030210 PGF Added Bolt / Segment point selection
;              Fixed SelectDelete() to reset EditStep so that it doesn't then select next
;                nearest point
;              Moved the icon images out of T_Material
;              Created icons and added to ToolPanel for Select, Build, BuildMultiple
; 20030314 PGF Made icons in the same group mutually exclusive
;              Added ViewFull()
; 20030322 PGF Changed ViewFull() to use OriginalX/Y in Edit mode but PX/Y in Test mode
;              Changed macro colour to green to distinguish from the yellow selection colour
;              Added Build and BuildMultiple functions (trouble with BuildMultiple)
;              Limiting of segment length
;              Fixed up the transitions between various macro and select modes
;              Improved GroundDraw() by changing to Rect instead of Line for the middle section.  Now appreciably
;                faster but I'll still cache it later on
;              Started on LoadMenu() etc
;              Added dragging of ToolPanel (not quite right as the mouse can move too far in one step and gets
;                outside the panel so the dragging stops)
;              Added StressGraphs to allow the stress within selected nodes to be graphed.  Basic but good.
; 20030325 PGF Minor changes to StressGraph
;              Changed SelectCopy() to use the macro method instead of the old fixed array.  So now MacroPlace() acts
;                as the paste function.
;              Linked up BridgeOldSave() but it is saving under a dummy name.
; 20030328 PGF Fixed SelectFlipX/Y
; 20030330 PGF Added thumbnail images to Load/Save
;              Changed ViewFull() to work by segments to solve a problem where fixed bolts were being ignored
;              The test train is now working
; 20030331 PGF V0.4.2
;              Tweaked the grid colour To brighten it up
;              Made <Esc> to cancel clear the macro and selection (if any)
;              Played around with 'extending selection' but it isn't quite right.  Taken out for now.
; 20030401 PGF Implemented run-length encoding for thumbnail images in Load/Save dialogs.  Reduces from ~45K to ~11K.
;                Should be faster too.
;              Added page up/down in load dialog and started on mouse selection
;              Added extended selection (Left or Right Control key)
; 20030403 PGF Completed mouse selectin for load dialog
;              Added pause (0) and single step (1) into TestControls() etc
; 20030405 PGF Added icons and macros for Line, Beam and Circle - All work well
;              Fixed GroundDraw() by interpolating the height.  Results in a smooth curve now
; 20030410 PGF V0.4.3
;              Added Move And Stretch functions
;              Changed Copy/Move/Stretch to be relative to G_CursorX/Y instead of G_Point2X/Y.  Much better
;              Linked up icons for Copy/Move/Stretch
;              Working on Load/Save functions
;              Added Replace function
; 20030414 PGF Changes to MacroUpdateArch to work from both sides inwards for better symmetry
;              Introduced C_SegmentLengthMinimum
; 20030418 PGF New control '\' to view selected segments.  Changed ViewFull() to cater for this
;              Added 'Z' key as a shortcut to Select tool
; 20030419 PGF Changed file format to use Ints rather than Shorts in most cases
;              Added icons for Load/Save Bridge and connected to the functions
;              Completed Save function
;              Made Load/Save autosize depending upon the screen size
;              Various fixes and additions to ToolPanel
;                - Fixed moving problem
;                - Polished HoverText
;                - Made it autosize to the adjusted width/height after arranging icons
;                - Switched to 40x40 icons (was 50x50)
;                - Added NewLine option to force icon to next row
;                - Prevent ViewPanControls() while in the panel
;              Started on DialogBox()
;              Changed colour of Steel Cable to purple to better differentate from Steel Beam
;              Added display of P1/P2/Distance/Angle
;              Added 'A' key as shortcut to Build tool
;              Removed <Escape> key as the Exit command.  Now must use 'Q'
;              Added some basic sound - Prototype only
;              Various renaming of functions
;              Played around heaps - Mainly with building a catapult !
;              Very primitive Help system - Prototype only
; 20030423 PGF Added Physics timer
;              Added confirm on exit using DialogBox()
; 20030426 PGF V0.5.0
;              Integrating new physics from Eelco
;              Modularisation
; 20030428 PGF V0.5.1
;              Use Path Include To make more easily portable between machines
;              Removed some legacy variables and testing code sections that are no longer required
;              Added image caching in GroundDraw() and generally tidied this up
;                - Caching works well
;                - Maybe slower when FreeImage/CreateImage is performed but acceptable and overall there is a saving
;                - Timing for just GroundDraw()
;                  - Laptop - Win2K P3-700 / S3 (?) - Fallen to 0-1ms when cached image is re-used
;                  - Home Desktop (Win98 Athlon XP 1800+ / GF2) - Reports 13ms but I think this is a quirk of Flip True
;                      on this machine (OS or drivers ?)
;              Connected Replace tool icon
;              Connected Copy/Move/Stretch icons.  They move the cursor to G_Point2X/Y if possible otherwise G_ScreenCentreX/Y
;              Added <Enter> as the Copy/Move/Stretch/Macro completion key (currently as well as 'O')
;              Bug fixes/workarounds in Physics to be reported to Eelco
;              Re-implemented BridgeNew() with icon (placeholder) and 'N' as a shortcut.  More fixed
;                bolts To the left and right to act as anchor points
;              Added Exit icon (placeholder)
; 20030429 PGF Added icons for Copy/Move/Stretch
;              Prototype of panel resizing
;              Prototype of screen print to disk to gather images for release
; 200305?? PGF Mostly exploring doing HyperText for Help system - Ambitious !
; 20030523 PGF Added ConsistencyCheck()
; 20030530 PGF Added ButtonInitialise() with Yes/No/Cancel
; 20030601 PGF Changing DialogBox() to loop the drawing and check for input rather than halting for WaitKey().  Getting
;                ready for mouse button checking
; 20030610 PGF Integrated changes to different code lines for ConsistencyCheck(), ButtonInitialise() etc
;              Converted DialogBox() to use generic T_Panel and T_Icon types and functions (with some exceptions)
;              Added mouse control to DialogBox()
;              Added progressive background screen fade to DialogBox().  Started with simple GreyCopy() but it was too long
;                to spend in one chunk before displaying the dialog:
;                - Slow pixel method / debug off = 4s @ 1024x768
;                - Fast pixel method / debug off = 0.5s @ 1024x768
; 20030612 PGF File safety - Prompting before New/Load/Quit if current file hasn't been changed
;              Added OK button option to DialogBox()
; 20030617 PGF Check & confirm on replacement of a different (non-empty) file slot in SaveBridge()
;              Extended PrintScreen() to check for available file slot to save to.
; 20030629 PGF Changed SelectUpdate() to use distance to line and tweaked the behaviour to work as desired
;              Added Primary/Secondary material selection, display and setting in some macros (prototype only)
;              Added Right-Mouse button for delete nearest segment/bolt in Edit mode
;              GroupId selection <Alt> including Load/Save (required new version 3 of file format with backwards compatibility)
;              Changed background images for file Load/save
; 20030707 PGF Rendered headings for Load/Save
;              Cranked up the number of files shown per page in Load/Save
; 20030725 PGF Minor improvement to Copy/Move/Steretch icons and layout of ToolPanel
;              Completed overlap prevention in MacroPlace().  There should now be no way to
;                create a situation whare a bolt or segment overlap occurs.
;              Added Group tool (placeholder icon for now)
;              Changed Grid toggle from 'G' to '.' in both Edit and Test
;              Changed ToolPanel Narrow / Widen to '[' / ']' and add PanelVisibility()
;              Reorganised the directory structure
;              Changed colours used in DialogBox to differentiate from ToolBox.
; 20031011 PGF Integrating changes by Eelco - Problem with changes in main file clashing
; 200310?? EH  Completed new Physics module
;              New variable thickness line draw
; 20031021 PGF Received V0.5.3 from Eelco with custom line draw and his other changes integrated into
;                my previous V0.5.2
;              Excellent work by Eelco - the line draw is very good
;              Bumped version up to V0.5.4
;              Added icon for Test and altered logic for switching in and out of Test mode a bit to suit
;              Adjusted bridge drawing to include bolts in Test mode and fiddled to improve the bolt
;                appearance and positioning
; 20031124 PGF V0.5.5
;              New modules Draw and Tool created in order to split out functions a bit better
;              Changed Grid display toggle command in Test mode from 'G' to '.' for consistency
;              Added 'Locked' attribute to T_Bolt and T_Segment.  To be used to implement scenario objects
;              Build Multiple completed
;              Left-Click to complete Copy/Move/Stretch
;              Fixed a bug with Right-Click to delete.  It was taking the previous selection box rather
;                than using only the specified point.  Still something wrong after box selection then right-clicking
;                near a node ?
; 20031224 PGF Hacked in 'collapsible' behaviour for SteelCable material - Looks good
; 20031227 PGF Fixed a minor bug in Left-Click to complete action.  The previous selection box had to
;                be cleared and also the Left-Click should not then select any near bolt or segment.
; 20040103 PGF V0.5.6
;              Start of Super edit mode - Super mode allows control over the environment and will
;                in future be where scenarios are created
;                - Dragable controls for:
;                  - Water level and depth
;                  - Left and right shore
;                  - Wind velocity
;                - Super panel (with a few dummy icons and basic panel dragging but nothing else)
;              Change Grid scaling factor from 8 to 10 (trial)
; 20040116 PGF Moved stuff added in last session to the correct modules
;              Started on Locked mode for scenario objects
;                - The attribute can be set on/off via icon controls in Super mode
;                - Selection behaves properly - ie it ignores locked objects unless in Super mode
;                - Even MacroPlace won't allow a new segment to overlay a locked segment
;                - The file format will have to be changed to make it persist (incomplete)
;              Tweaks to display
;                - Darker colours for locked objects
;                - Grid mid-points (incomplete)
; 20040120 PGF Fixes to some issues in selection of Locked Rail and FixedRail segments
;                - Segment type is now preserved in the T_MSegment during Copy/Move/Stretch
;                - Allowing box selection of Locked segments in Super mode (there was a small loophole)
;              Changed Rail spacing in BridgeNew to C_SegmentLengthNormal (was G_Blocksize)
;              Trying out blue sky
; 20040121 PGF Fixed a bug in GroundDraw with blue sky.  Had to set ClsColor to 0,0,0 for cached image draw
;              Added optional function call tracing
;                - Set C_FunctionTrace = True To invoke
;                - If False Then I believe Blitz will eliminate the dependent code so there is no
;                    performance loss or increase in EXE size
;                - Added FunctionEntree() and FunctionEgress() calls to most of Main, Edit, Draw, File
;                    and Physics
;              V0.5.7
;              Major change to separate attributes such as Fixed and Rail from each other
;                - So SegmentTypeFixedRail becomes a normal segment with attributes Fixed and Rail both True
; 20040124 PGF Fixed sporadic crash due to Wheels being left around after a Test
;              Added mouse selection in Test mode (a bit messy as OriginalX/Y or PX/Y have to be chosen
;                depending upon the mode
;              Added continuous tracking in ViewZoomControls
; 20040208 PGF Fixed bug in selection
;              Generalised Cable property in Material.  Only SteelCable has this property at present but
;                in future there may be Rope, Kevlar etc
;              Improved colouring of cable material under compression.  It now shows as purple rather than red and
;                flashing yellow
;              Tidying up icon graphics
; 20040214 PGF More tidying up of graphics resources
;              Wrote PrepImages to automatically move and rename the icons generated by POV-Ray
;              A bit of work on the help file
; 20040216 PGF Added icons for FlipX, FlipY
;              Created Rotate tool
;                - Works well but needs prompting Or something similar
;                - Possibly strange behaviour for bolts that are linked to both rotating and stationary segments ?
; 20040229 PGF Changed naming of LeftShore/RightShore to G_ShoreLeft/G_ShoreRight (whoopee !)
;              Fixed minor bug with reverting to Select tool after macros etc which should be sticky
;              Added Super controls to set TrainStart, TrainLength, TrainStop
;              New check for completion of train objective and plays sound effect
;              Started on In/Ex regions.  Defined type and did drawing but no checking yet
; 20040302 PGF Made pan controls work properly when dragging Super controls
; 20040303 PGF Testing performance and options for AA on DrawSegment (TestDrawSegment.BB)
;              Played around with pre-stressing cables
;                - RLength * 1.005 seems to work
;                - Needs refinement
; 20040307 PGF Adjusted pre-stressing of cables and interaction of pre-stressing and collapsible nature
;              Special draw routine for rails to show top surface in a different colour (White for now)
;              Corrected drawing during BuildMultiple so that the last step shows Green/Red depending
;                upon whether the length is acceptable or not
;              Re-wrote MacroUpdateBeam to make it handle Primary/Secondary materials properly.  Also
;                the code is a lot clearer now
;              Added sound effect for breaking segments
;              Found an error in many old saved files.  There was a segment with the same Bolt1 and
;                Bolt2 (at 0,0).  Added a check for this in ConsistencyCheck() and also added information
;                messages to show any problems found and corrected
;              Changed StressGraph to show cables in compression as purple and to fit them within the graph
;                instead of shooting down off it
;              Added control (W=Which?) to show broken segments in Edit mode
;              Added Super controls and functions to set Normal(-)/Rail(=), Free(;)/Fixed(') properties
;              Hacked a different train shape.  Will generalise this later
; 20040312 PGF V0.5.8
;              Cleaned up attributes in Bolt and Segment types and throughout code
;              Deleted BridgeLoadOld() and BridgeSaveOld() as they're no longer compatible with types
; 20040313 PGF Added ConvertFiles() to allow file V3 to V4 conversion (and so on in the future)
;              Updated file format to V4 which now includes:
;                - Unlocked/Locked attribute
;                - Normal/Rail attribute
;                - Free/Fixed attribute
;                - Environment variables: Level, Shore, Water, Wind, Train
; 20040314 PGF Changed BuildMultiple to complete if same point is clicked
; 20040611 PGF (After a long hiatus while working on QiD and other C# stuff)
;              Added Tower macro
;              Fixed bug with macro stuff being added as rail rather than normal SegmentType
; 20040626 PGF Adding material limits.
;                - Additional fields in T_Segment and T_MSegment, defaults in MaterialInitialise()
;                - Addition of MaterialChecks(), MaterialInfoDraw()
;                - Changes to MacroPlace(), MacroDraw(), CursorDraw()
;              Not yet done:
;                - Super controls to set the material limits and budget
;                - Saving/Loading
; 20040627 PGF Fine tuning of material limits:
;                - Proper handling in cases of replacing materials, move/stretch etc
;              Added G_SoundStatus system for confirmation/warning/error audible indication
;              Added selected material qty/mass/cost display
;              Moved budget and material info display to right side and tweaked initial panel positions
; 20040704 PGF Built a huge suspension bridge - beautiful !
;              Increased Wheel\Drive from 10,000 to 20,000 to speed up the train
;              
; --------------------------------------------------------------------------------------------------------------------------------
;
; Plan:
;
;              Add a damping component to TestPhysics() to gradually iron out the tension/compression
;                flip/flop in doubly braced units.  I'm sure that this is not realistic.
;              Possibly allow variable cross section for given materials.  All beams in a real bridge
;                are not necessarily the same strength/weight.  Possibly have a 'tune' function that
;                progressively trims the cross section of each segment to withstand the maximum
;                strain over a period (* a safety factor).  Do this with the wind and train dynamic
;                loads operating.  Maybe only allow Light/Medium/Heavy 'weights' of materials rather than
;                a continuously variable amount.  Would it produce workable And better designs ?
;              Could have an optional display mode that shows the current and maximum tension and
;                compression loads on each segment - eg. +0.57 / -0.03 displayed as text on the segment
;              Scenarios that have to be solved
;                - Limited materials and weights such as:
;                    -  350 AD - Only Light To Medium Wood and lengths up to 3m
;                    - 1850 AD - Light to Medium Steel Beam up to lengths of 10m
;                    - 1910 AD - Heavy Steel Beam and Light to Medium Steel Cable
;                    - 1960 AD - All grades of Wood Beam, Steel Beam, Concrete Beam, Steel Cable
;                    - 2010 AD - Boron Nanotube Beam/Cable
;                  (Check historical material and design availability)
;                - Varying spans
;                - Varying left, right and middle levels with the road/rail >= the highest of these
;                - Material costs and you have to complete the objective within a budget.  Could also include
;                    site and building costs (so much per span/joint)
;                - Different static (Wind) and dynamic (Train/Road traffic) loads
;                  - Could do an existing bridge that has to be strengthened (within a budget) to withstand
;                      increased loads
;                    (Check historical bridge disasters)
;              Output design and maybe animations to POV for rendering ?
;
; --------------------------------------------------------------------------------------------------------------------------------
;
; Completed Plan Items
;
;              Zoom In/Out
;              Change to HoverText - Make it sticky
;              Probably have to do major rework of MainControls() for different modes - Underway
;              Enhanced Save/Load
;              GUI elements for:
;                - Materials: Wood, Steel, Concrete etc - Done
;                - Mass move/copy/delete/change material - Underway
;                - Templates for towers, beams, arches etc - Underway
;              Graph tension/compression of segments
;              More efficient GroundDraw() - Cache an image for re-use if there is no Pan or Zoom
;
; --------------------------------------------------------------------------------------------------------------------------------

Include "\Paths\Path Blitz Basic.BB"
Include "Source\GameWork.BB"

G_ScreenMode = 0

Global G_ScreenFlipping = True
Global G_Cancel			= False

Global G_MainTime

; Mode variables
Global G_Mode
Const C_ModeEdit = 0
Const C_ModeTest = 1
Const C_ModeSave = 2
Const C_ModeLoad = 3
Const C_ModeHelp = 4
Const C_ModeExit = 5
Const C_ModeDemo = 6

Global G_PromptText$ = ""

; Mouse variables
Global G_MouseX
Global G_MouseY
Global G_MouseZ
Global G_MouseZOld

Global G_Mouse1			; Hit ?
Global G_Mouse1Down		; Down ?

Global G_Mouse2
Global G_Mouse2Down

Global G_Mouse3
Global G_Mouse3Down

Global G_MouseImage

Global G_MousePanelOver.T_Panel
Global G_MouseIconOver.T_Icon

; Zoom variables 
Global G_ZoomLevel#	= 0
Global G_ZoomLevelMin = -10
Global G_ZoomLevelMax = 50
Global G_ZoomFactor# = 0.9 ^ G_ZoomLevel

Global G_ZoomMinX#, G_ZoomMinY#
Global G_ZoomMaxX#, G_ZoomMaxY#

Const C_ViewModeAll = 1
Const C_ViewModeSelected = 2

; Grid variables
Global G_GridDisplay = True
Global G_GridSize = 10
Global G_GridDivisions = 10
Global G_GridSnap = 1

Global G_BlockSize = 10 * G_GridSize

; Cursor variables
Global G_CursorFreeX# = 0	; Coordinates before snap
Global G_CursorFreeY# = 0
Global G_CursorX = 0		; Coordinates snapped to grid
Global G_CursorY = 0
Global G_CursorOldX = 0		; Coordinates saved from last cycle for comparison
Global G_CursorOldY = 0

; Tool variables
Global G_EditTool
Const C_EditToolSelect			= 1
Const C_EditToolCopy			= 2
Const C_EditToolMove			= 3
Const C_EditToolStretch			= 4
Const C_EditToolRotate			= 5

Const C_EditToolBuild			= 100
Const C_EditToolBuildMultiple	= 101
Const C_EditToolGroup			= 102
Const C_EditToolReplace			= 103

Const C_EditToolMacroLine		= 200
Const C_EditToolMacroBeam		= 201
Const C_EditToolMacroArch		= 202
Const C_EditToolMacroCircle		= 203
Const C_EditToolMacroTower		= 204

; Catenary ?
; Tower ?

Global G_EditStep		; Each tool uses the step as necessary
Const C_EditStepZero	= 0
Const C_EditStepP1		= 1
Const C_EditStepP2		= 2
Const C_EditStepP3		= 3
Const C_EditStepP4		= 4
Const C_EditStepDone	= 99

Global G_Point1X
Global G_Point1Y

Global G_Point2X
Global G_Point2Y

Global G_Point3X
Global G_Point3Y

Global G_Point4X
Global G_Point4Y

Global G_RelativeX
Global G_RelativeY

; Selection variables
Global G_SelectModeType = 0
Const  C_SelectModeTypeCrossing = 0
Const  C_SelectModeTypeContains = 1

; Camera variables
Global G_CameraX# = 640
Global G_CameraY# = 240

Global G_CameraStepLarge# = 2.
Global G_CameraStepSmall# = 0.5

Global G_CameraEdgeLarge# = 10
Global G_CameraEdgeSmall# = 50

; Debug variables
Global G_DebugLine

Global G_DebugTrace 		= False

Global G_DebugTraceBolt		= False
Global G_DebugTraceBoltId	= 10

Global G_DebugTraceSegment	= False
Global G_DebugTraceSegmentId = -1

; Flag to cause function calls to be written to Debug.TXT
;   - As this is a Const, I understand that Blitz will eliminate the dependent code if the
;     value is False
;
Const  C_FunctionTrace 		= False
Global G_FunctionTraceLevel = 0
Global G_DebugTraceFile				; File handle
Global G_DebugTraceFilename$		; File name

; Stress graph variables
Global G_StressColourDisplay = True
Global G_StressGraphDisplay = False
Global G_StressGraph = 0
Dim G_StressGraphs(1)
Global G_StressGraphBlank
Global G_StressGraphFrameColour = GW_ColorMake(150, 50, 150)
Global G_StressGraphMarkColour  = GW_ColorMake(75, 25, 75)

Const C_StressGraphHeight	= 200
Const C_StressGraphAxis		= 100

Global G_BreakDisplay		; Flag set in Edit mode to cause broken segments to show

Global TempX#
Global TempY#

Global G_PhysicsCycle
Global G_PhysicsTime
Global G_PhysicsClock#

; Group Id - Groups of elements placed together get a common GroupId
Global G_GroupId

; GUI elements
Type T_Panel
	Field	Id
	Field	PanelType
	Field	Status
	
	Field	Name1$				; Longest name
	Field	Name2$				; Medium length name
	Field	Name3$				; Shortest name
	
	Field	Message$			; Used in DialogBox form of panels
	Field	MessageX
	Field	MessageY
	
	Field	Logo				; DialogBox optionally can have an image
	Field	LogoX
	Field	LogoY
	
	Field	MaxWidth
	Field	MaxHeight
	Field	AdjWidth
	Field	AdjHeight
	Field	EdgeLeft
	Field	EdgeRight
	Field	EdgeTop
	Field	EdgeBottom
	Field	CentreX
	Field	CentreY
	Field	Image				; Image of the panel that is rendered
	
	Field	IconFirst.T_Icon	; Chain of icons attached to the panel
	Field	IconLast.T_Icon
End Type

Const C_PanelTypeToolBox	= 0
Const C_PanelTypeDialogBox	= 1

Const C_PanelStatusHide		= 0
Const C_PanelStatusShow		= 1

Type T_Icon
	Field	Id
	Field	Label$
	Field	Status
	Field	Group				; Used to turn off mutually exlusive icons.  0 = No group
	Field	Width
	Field	Height
	Field	EdgeLeft
	Field	EdgeRight
	Field	EdgeTop
	Field	EdgeBottom
	Field	NewLine				; Set to True if this icon should start on a new line
	Field	ImageDisabled
	Field	ImageNormal
	Field	ImageSelected
	Field	ImageUnavailable
	Field	IconNext.T_Icon
	Field	HoverTime
	Field	HoverText$
End Type

Const C_IconStatusNormal		= 0
Const C_IconStatusSelected		= 1
Const C_IconStatusDisabled		= 2
Const C_IconStatusUnavailable	= 3

; Icons for Tool Panel
Global G_IconSelect.T_Icon
Global G_IconCopy.T_Icon
Global G_IconMove.T_Icon
Global G_IconStretch.T_Icon

Global G_IconFlipX.T_Icon
Global G_IconFlipY.T_Icon
Global G_IconRotate.T_Icon

Global G_IconBuild.T_Icon
Global G_IconBuildMultiple.T_Icon
Global G_IconGroup.T_Icon
Global G_IconReplace.T_Icon

Global G_IconMacroLine.T_Icon
Global G_IconMacroBeam.T_Icon
Global G_IconMacroArch.T_Icon
Global G_IconMacroCircle.T_Icon
Global G_IconMacroTower.T_Icon

Global G_IconTest.T_Icon

Global G_IconLoad.T_Icon
Global G_IconSave.T_Icon
Global G_IconNew.T_Icon
Global G_IconExit.T_Icon

; Tool Panel variables
Global G_ToolPanel.T_Panel

Global G_ToolPanelToolsWide = 5	; For resizing the ToolPanel
Global G_ToolPanelToolsHigh = 6
Global G_ToolPanelIconWidth
Global G_ToolPanelIconHeight

Global G_ToolPanelMoving		; Flag for when the tool panel is in the process of being moved
Global G_ToolPanelMoveX = 0
Global G_ToolPanelMoveY = 0
	
; Hover text variables
Global G_HoverDisplay = False
Global G_HoverDelay = 750

; Logo images for the DialogBox form of Panels
Global G_PanelLogoWarning
Global G_PanelLogoInformation
Global G_PanelLogoQuestion

; Buttons
;
; NB: T_Button is mainly just a pointer to the T_Icon that represents it.
;
Type T_Button
	Field	Icon.T_Icon
End Type

Const C_ButtonStatusDisabled	= 0
Const C_ButtonStatusNormal		= 1
Const C_ButtonStatusSelected	= 2
Const C_ButtonStatusUnavailable	= 3

Global G_ButtonYes.T_Button
Global G_ButtonNo.T_Button
Global G_ButtonCancel.T_Button
Global G_ButtonOK.T_Button

Const C_ButtonMarginH = 8
Const C_ButtonMarginV = 4

Global G_ButtonWidth
Global G_ButtonHeight
Const C_ButtonWidthMax	= 60
Const C_ButtonHeightMax	= 25

; Super mode variables
Type T_Control
	Field	ControlType
	Field	PosX
	Field	PosY
End Type

Const C_ControlTypeWaterLevel	= 1
Const C_ControlTypeWaterDepth	= 2
Const C_ControlTypeShoreLeft	= 3
Const C_ControlTypeShoreRight	= 4
Const C_ControlTypeWind			= 5
Const C_ControlTypeTrainStart	= 6
Const C_ControlTypeTrainLength	= 7
Const C_ControlTypeTrainStop	= 8


Global G_ControlWaterLevel.T_Control
Global G_ControlWaterDepth.T_Control
Global G_ControlShoreLeft.T_Control
Global G_ControlShoreRight.T_Control
Global G_ControlWind.T_Control
Global G_ControlTrainStart.T_Control
Global G_ControlTrainLength.T_Control
Global G_ControlTrainStop.T_Control

Global G_EditModeSuper = True
Global G_ControlSelected.T_Control

; Super Panel variables
Global G_SuperPanel.T_Panel

Global G_SuperPanelToolsWide = 2	; For resizing
Global G_SuperPanelToolsHigh = 6
Global G_SuperPanelIconWidth
Global G_SuperPanelIconHeight

Global G_SuperPanelMoving		; Flag for when the tool panel is in the process of being moved
Global G_SuperPanelMoveX = 0
Global G_SuperPanelMoveY = 0

Global G_SuperIconUnlock.T_Icon
Global G_SuperIconLock.T_Icon
Global G_SuperIconNormal.T_Icon
Global G_SuperIconRail.T_Icon
Global G_SuperIconFreeBolt.T_Icon
Global G_SuperIconFixedBolt.T_Icon
Global G_SuperIconLoad.T_Icon
Global G_SuperIconSave.T_Icon

; Load/Save menu variables
Global G_LoadBackImage = 0
Global G_LoadHeadImage = 0
Global G_SaveBackImage = 0
Global G_SaveHeadImage = 0

Type T_FileInfo
	Field	Id
	Field	Status
	Field	FileName$
	Field	Image
	Field	EdgeLeft
	Field	EdgeRight
	Field	EdgeTop
	Field	EdgeBottom
	Field	HotKey$
End Type

Const C_FileStatusNone = 0
Const C_FileStatusOK = 1

Dim G_Files.T_FileInfo(0)	; Will be redimensioned according to screen size

Global G_FileSlot		= -1
Global G_FileChanged	= False

Global G_FilesWidth = 320
Global G_FilesHeight = 90

Global G_FilesColumns = 2
Global G_FilesPerColumn = 8
Global G_FilesPerPage = 16

Global G_FilesMouseOver
Global G_FilesPageStart = 0

Global G_ThumbNailWidth	= 120
Global G_ThumbnailHeight = 80
Global G_ThumbNailImage

; Ground drawing variables
Global G_GroundYMin#
Global G_GroundYMax#
Global G_GroundImage
Global G_GroundImageWidth
Global G_GroundImageHeight
Global G_GroundImageZoomFactor#
Global G_GroundImageCameraX#
Global G_GroundImageCameraY#
Global G_GroundCacheUsed = 0

; DialogBox and Button variables
Const C_DialogBoxAny    = %00000001
Const C_DialogBoxYes	= %00000010
Const C_DialogBoxNo		= %00000100
Const C_DialogBoxCancel = %00001000
Const C_DialogBoxOK     = %00010000

Const C_DialogBoxYN		= C_DialogBoxYes Or C_DialogBoxNo
Const C_DialogBoxYNC	= C_DialogBoxYes Or C_DialogBoxNo Or C_DialogBoxCancel

Global G_SaveBuffer			; Full screen sized buffer for temporarily saving the screen contents
Global G_GreyBuffer			; Full screen sized buffer for temporarily saving the faded screen contents

; Sound variables
Global G_Sound1				; Handles to WAV files that are loaded
Global G_Sound2
Global G_Sound3

; Channels in which the sounds are played.  NB:  Sound1 does not have to play in Channel1 
Global G_Channel1			; Success alert
Global G_Channel2			; Break alert
Global G_Channel3			; Testing for now

; 
Global G_SoundStatus		; General audio status indication
Const C_SoundStatusNone		= 0	; Nothing to unusual
Const C_SoundStatusDone		= 1	; Did something without error (eg tool worked)
Const C_SoundStatusWarn		= 2	; Doing something but there is a minor problem (eg tool has a warning while it is still in use)
Const C_SoundStatusError	= 3	; Tried something but there is a major problem (eg tool had an error when completing)
;
Global G_SoundBreak = False		; Set True/False in TestPhysics() to tell SoundUpdate() what to do

; Help variables
Global G_HelpFile
Const C_HelpLineMax = 500
Dim G_HelpLines$(C_HelpLineMax)
Global G_HelpLineCount
Global G_HelpLineStart

; Environment variables

; Level
Global G_LevelWidth = 1200
Dim Height#(G_LevelWidth-1)

Global G_ShoreLeft	= 240
Global G_ShoreRight	= G_LevelWidth - 240

Global G_WindSpeed#	= 0		; Windspeed at the given time

Global G_WindSpeedStart#	= 3					; Initial windspeed
Global G_WindSpeedLow#		= G_WindSpeedStart	; Low value for cycling
Global G_WindSpeedHigh#		= G_WindSpeedStart	; High value for cycling
Global G_WindSpeedCycle#	= 0					; Cycle time. 0 = No cycling


; New global environment variables
Global G_GroundLevel = 0

Global G_WaterLevel  = -40		; Water surface
Global G_WaterDepth = -400		; Water bottom

;setheight()
;prerender()

	;mouse
;	Global xmouse,ymouse
;	Global pointer=LoadImage("red_pointer.bmp")
;	MaskImage pointer,255,255,255
;	Const scrollspeed=5
;	Const clicktime=300
;	Global leftbutton,rightbutton
;	Global leftclick,rightclick
;	Global leftdouble,rightdouble
;	Global lefttime,righttime
;	Global lefthold,righthold
;	Global leftrelease,rightrelease
;	Global pull.T_Bolt
;	MoveMouse screenx/2,screeny/2

	;camera
;	Global camerax
;	Global cameray=levelheight-screeny/2

	;grid
;	Const gridsize=10
;	Const blocksize=8*gridsize
;	Global drawgrid=True
;	Global snaptogrid=True
	
; Bolt type
Type T_Bolt
	Field Id
	Field BoltType		; Regular or Fixed bolt.  See constants below
	Field Locked		; Flag - True = A 'scenario' object that can only be affected in super mode
	Field GroupId
	Field DynamicId		; Id to associate dynamically created objects. 0 = Not dynamic
	Field ActionId		; Id to attach actions. 0 = None
;
	Field Mass#
	Field Volume#
	Field Medium
	Field OriginalX		; Main X,Y
	Field OriginalY
	Field PX#,PY#		; Physics X,Y
	Field LPX#,LPY#
	Field SX#,SY#,S#
	Field LSX#,LSY#
	Field FX#,FY#
	Field LFX#,LFY#
	Field TX#,TY#
	Field Sector
	Field Selected		; Flag - True = Currently selected
	Field SelectedMaybe	; Flag - True = Tentatively selected during cursor box dragging
	Field Trace			; Flag - Used in algorithms to detect unconnected bolts
	Field TraceBolt.T_Bolt	; Pointer to another Bolt - e.g. during ConsistencyCheck()
	Field MacroBolt.T_MBolt	; Used to point to macro copy during some operations
End Type

Const C_BoltTypeRegular		= 1		; Normal bolt that can move during simulation
Const C_BoltTypeFixed		= 2		; Not able to move during simulation

; Segment type
Type T_Segment
	Field Id
	Field SegmentType	; Regular or Rail segment. See constants below
	Field Locked		; Flag - Used to indicate fixed 'scenario' objects (True) or normal (False)
	Field GroupId
	Field DynamicId		; Id to associate dynamically created objects. 0 = Not dynamic
	Field ActionId		; Id to attach actions. 0 = None
	Field Material.T_Material
	Field Bolt1.T_Bolt
	Field Bolt2.T_Bolt
	Field Status		; Normally C_SegmentStatusOK but can change to C_SegmentStatusBroken during simulation
	Field Length#
	Field Stress#
	Field LStress#
	Field Selected
	Field SelectedMaybe
	Field Trace			; Flag - Used in algorithms to detect coincident segments
	Field Shadow		; Indicate if the segment should be drawn in 'shadow' colour during Copy/Move/Stretch
End Type

Const C_SegmentTypeRegular		= 1
Const C_SegmentTypeRail			= 2

Const C_SegmentStatusOK 		= 0
Const C_SegmentStatusBroken		= 1

Const C_SegmentLengthMinimum#	= 5.0		; Below this the behaviour is unstable
Const C_SegmentLengthMaximum#	= 86.163	; Sqr(80^2 + 32^2) = 86.1626369...
Const C_SegmentLengthNormal#	= 80

; Bolt and Segment types for temporary operations such as the macro commands and Copy/Move
Type T_MBolt
	Field Id
	Field BoltType		; Regular or Fixed bolt.  See constants below
	Field Locked		; Flag - True = A 'scenario' object that can only be affected in super mode
	Field OriginalX
	Field OriginalY
	Field Trace			; Used in algorithms to detect unconnected bolts
	Field Movable		; Indicates if the bolt is subject to movement in Copy/Move/Stretch
	Field RealBolt.T_Bolt
End Type

Type T_MSegment
	Field Id
	Field SegmentType	; Regular or Rail segment. See constants below
	Field Length#
	Field Locked		; Flag - Used to indicate fixed 'scenario' objects (True) or normal (False)
	Field Material.T_Material	; Original material for Copy/Move or Null for default to selected material for new objects
	Field MaterialChoice		; Indicate Primary (0 or 1) or Secondary (2) material for new objects
	Field Bolt1.T_MBolt
	Field Bolt2.T_MBolt
	Field LengthError			; Flag if there is an error with the length (too long or too short)
	Field BudgetError			; Flag if there is an error with the budget (of the material available or the total cost)
End Type

Type T_Region
	Field	RegionType
	Field	EdgeLeft
	Field	EdgeRight
	Field	EdgeTop
	Field	EdgeBottom
End Type

Const C_RegionTypeNone	= 0
Const C_RegionTypeIn	= 1
Const C_RegionTypeEx	= 2

Global G_RegionDefault = C_RegionTypeIn

; Consistency check variables
Global G_ConsistencyMergeBolts
Global G_ConsistencyMergeSegments

; Program flow
Global traintest=False		; This will be moved to Physics module and renamed G_TrainTest or whatever
;
Global G_TrainStartX	= 0
Global G_TrainStartY	= 0
Global G_TrainLength	= 5
Global G_TrainStopX		= 1200

; --------------------------------------------------------------------------------------------------------------------------------
;
; Include other modules
;

Include "Source\Bridge V0.5.8 - Material.BB"
Include "Source\Bridge V0.5.8 - Physics.BB"
Include "Source\Bridge V0.5.8 - Edit.BB"
Include "Source\Bridge V0.5.8 - File.BB"
Include "Source\Bridge V0.5.8 - Help.BB"
Include "Source\Bridge V0.5.8 - Draw.BB"
Include "Source\Bridge V0.5.8 - Panel.BB"

; --------------------------------------------------------------------------------------------------------------------------------
;
; Code starts
;

;GW_GraphicsModeSet(C_Graphics800x600x16)
GW_GraphicsModeSet(C_Graphics1024x768x16)
;GW_GraphicsModeSet(C_Graphics1600x1200x16)

MainInitialise()

MainLine()

MainFinalise()

; If tracing then stop to allow for the DebugLog to be viewed
If G_DebugTrace
	Stop
EndIf

End

Function MainInitialise()
	;
	;	Initialisation
	;

	If C_FunctionTrace	
		G_DebugTraceFilename = "Debug.TXT"
		
		Select FileType(G_DebugTraceFilename)
			Case 0
			Case 1
				DeleteFile G_DebugTraceFileName
			Case 2
				GW_AbortErrorMessage("File name '" + G_DebugTraceFileName + "' conflicts with directory name")
			Default
				GW_AbortErrorMessage("Unknown debug file status in MainInitialise(" + G_DebugTraceFilename + ")")
		End Select
		
		G_DebugTraceFile = WriteFile(G_DebugTraceFilename)
	
		FunctionEntree("MainInitialise")
	EndIf

	GW_TrigInitialise()
	
	MouseInitialise()
	
	MaterialInitialise()
	
	ButtonInitialise()
	
	G_PanelLogoWarning		= GW_LoadImage("Images\", "LogoWarning.PNG")
	G_PanelLogoInformation	= GW_LoadImage("Images\", "LogoQuestion.PNG")
	G_PanelLogoQuestion		= GW_LoadImage("Images\", "LogoQuestion.PNG")
	
	ToolPanelInitialise()
	SuperPanelInitialise()
	
	LoadBridgeV4(4)
	; Testing of GroundDraw()
	;BridgeOldClear(False)
	;G_GridDisplay = False
	;G_ToolPanel\Status = C_PanelStatusHide
	
	ControlsInitialise()
	
	ViewFull(C_ViewModeAll)
	;ViewPanReset()
	
	EditInitialise()
	
	StressGraphInitialise()
	
	G_Sound1 = GW_LoadSound("Source\", "Sound1.WAV")
	G_Sound2 = GW_LoadSound("Source\", "Sound2.WAV")
	G_Sound3 = GW_LoadSound("Source\", "Sound3.WAV")
	
	G_FilesColumns		= G_ScreenWidth / G_FilesWidth
	G_FilesPerColumn	= (G_ScreenHeight - 120) / G_FilesHeight
	G_FilesPerPage		= G_FilesColumns * G_FilesPerColumn
	
	Dim G_Files.T_FileInfo(G_FilesPerPage - 1)
	
	HelpInitialise()
	
	If G_SaveBuffer = 0
		G_SaveBuffer = CreateImage(G_ScreenWidth, G_ScreenHeight)
	EndIf
	
	G_RegionDefault = C_RegionTypeEx
	
	R.T_Region = New T_Region
	R\RegionType = 3 - G_RegionDefault	; Trick dependent on In = 1, Ex = 2
	R\EdgeLeft = 800
	R\EdgeRight = 1200
	R\EdgeTop = 400
	R\EdgeBottom = 100
	
	R.T_Region = New T_Region
	R\RegionType = G_RegionDefault
	R\EdgeLeft = 900
	R\EdgeRight = 1000
	R\EdgeTop = 200
	R\EdgeBottom = 150
	
	R.T_Region = New T_Region
	R\RegionType = G_RegionDefault
	R\EdgeLeft = 900
	R\EdgeRight = 1000
	R\EdgeTop = 350
	R\EdgeBottom = 300
	
	R.T_Region = New T_Region
	R\RegionType = G_RegionDefault
	R\EdgeLeft = 1000
	R\EdgeRight = 1100
	R\EdgeTop = 350
	R\EdgeBottom = 150
	
	If C_FunctionTrace Then FunctionEgress()
End Function

Function MainFinalise()

	If C_FunctionTrace
		FunctionEntree("MainFinalise")
		CloseFile G_DebugTraceFile
		G_DebugTraceFile = 0
	EndIf
	
	If G_ThumbnailImage <> 0
		FreeImage G_ThumbnailImage
		G_ThumbNailImage = 0
	EndIf
	
End Function

Function Mainline()
	Local TimeEnd
	Local DialogAnswer
	
	If C_FunctionTrace Then FunctionEntree("Mainline")

	G_Cancel = False
	
	While Not G_Cancel
		
		G_MainTime = MilliSecs()
		
		If C_FunctionTrace
			FunctionMessage("Mainline time = " + G_MainTime)
		EndIf
		
		G_SoundStatus = C_SoundStatusNone
		
		MainControls()
		
		G_PromptText = ""
		
		If G_Mode = C_ModeEdit
			EditControls()
		EndIf
		
		If G_Mode = C_ModeTest
			TestControls()
			If G_TestPhysicsSteps <> 0
				TestPhysics(G_TestPhysicsSteps)
			EndIf
			G_PhysicsClock = G_PhysicsClock + G_TestPhysicsSteps / 20.0
		ElseIf G_Mode = C_ModeLoad
			LoadMenu()
		ElseIf G_Mode = C_ModeSave
			SaveMenu()
		ElseIf G_Mode = C_ModeHelp
			HelpMenu()
			G_Mode = C_ModeEdit
		ElseIf G_Mode = C_ModeDemo
			G_PromptText = "Demo mode not complete"
		EndIf
		
		If G_Mode = C_ModeExit
			If G_FileChanged
				Answer = DialogBox("Current file modified.  Save first ?", C_DialogBoxYNC)
				If Answer = C_DialogBoxYes
					If SaveMenu()
						G_Cancel = True
					EndIf
				ElseIf Answer = C_DialogBoxNo
					G_Cancel = True
				EndIf
			Else
				G_Cancel = True
			EndIf
			
			;Answer = DialogBox("Exit Bridge Builder ?", C_DialogBoxYN)
			;If Answer = C_DialogBoxYes
			;	G_Cancel = True
			;Else
			;	G_Cancel = False
			;EndIf
			
			If Not G_Cancel
				G_Mode = C_ModeEdit
			EndIf
		EndIf
		
		If KeyHit(C_Key_F2)
			DialogBox("Test of DialogBox()", C_DialogBoxAny)
		EndIf
		
		MaterialChecks()
		MainDraw()
		
		If KeyDown(C_Key_F4)
			ConsistencyCheck()
			If G_ConsistencyMergeBolts <> 0 Or G_ConsistencyMergeSegments <> 0
				DebugText("Merge Bolts    = " + G_ConsistencyMergeBolts) 
				DebugText("Merge Segments = " + G_ConsistencyMergeSegments)
			Else
				DebugText("No inconsistencies in structure")
			EndIf
		EndIf
		
		TimeMins = Floor(G_PhysicsClock / 60)
		TimeSecs = G_PhysicsClock - TimeMins * 60
		
		DebugText("Clock   = " + TimeMins + ":" + Right("0" + TimeSecs, 2))
		DebugText("")
		
		TimeEnd = MilliSecs()
		DebugText("Cycle   = " + (TimeEnd - G_MainTime))
		If G_Mode = C_ModeTest
			DebugText("Physics = " + (G_PhysicsTime))
		EndIf
		
		If G_Mode = C_ModeTest
			Flip False
			If TimeEnd - G_MainTime = 0
				Delay 1
			EndIf
		Else
			Flip True
		EndIf
		
		SoundUpdate()
		
	Wend
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MainControls()

	If C_FunctionTrace Then FunctionEntree("MainControls")

	; Interactive debug switch
	If KeyHit(C_Key_F1)
		Stop
	EndIf
		
	; Mouse controls
	G_MouseX = MouseX()
	G_MouseY = MouseY()
	G_MouseZOld = G_MouseZ
	G_MouseZ = MouseZ()

	If MouseHit(1) > 0
		G_Mouse1 = True
	Else
		G_Mouse1 = False
	EndIf
	G_Mouse1Down = MouseDown(1)
	
	If MouseHit(2) > 0
		G_Mouse2 = True
	Else
		G_Mouse2 = False
	EndIf
	
	G_Mouse2Down = MouseDown(2)
	
	If MouseHit(3) > 0
		G_Mouse3 = True
	Else
		G_Mouse3 = False
	EndIf
	G_Mouse3Down = MouseDown(3)
	
	If KeyHit(C_Key_Q)
		G_Mode = C_ModeExit
		Goto MainControlsReturn
	EndIf
	
	;If KeyHit(C_Key_Escape)
	;	If G_Mode = C_ModeEdit
	;		G_EditStep = C_EditStepZero
	;		MacroClear()
	;		SelectClear()
	;	EndIf
	;EndIf
	
	If KeyHit(C_Key_Space)
		If G_Mode = C_ModeEdit
			TestInitialise()
		ElseIf G_Mode = C_ModeTest
			TestFinalise()
			EditInitialise()
		Else
			EditInitialise()
		EndIf
	Else
		If KeyHit(C_Key_H)
			G_Mode = C_ModeHelp
		EndIf
	EndIf
	
	If G_Mode = C_ModeEdit
		If KeyHit(C_Key_L)
			G_Mode = C_ModeLoad
		ElseIf KeyHit(C_Key_S)
			G_Mode = C_ModeSave
		EndIf
	EndIf
	
	If KeyHit(C_Key_P)
		DialogBox("Paused - Press any key to resume", C_DialogBoxAny)
	EndIf
	
	If KeyHit(C_Key_SysReq) Or KeyHit(C_Key_F12)
		; There is no Print_Screen key and SysReq doesn't work ?
		If PrintScreen()
			DialogBox("Print Screen - Press any key to resume", C_DialogBoxAny)
		EndIf
	EndIf
	
	If KeyHit(C_Key_F7)
		ConvertFiles()
	EndIf
	
	; Throw away any other keystrokes
	;FlushKeys()
	
.MainControlsReturn
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MainDraw()
	Local DistX#, DistY#

	If C_FunctionTrace Then FunctionEntree("MainDraw")

	G_DebugLine = 0
	
	MainBuffer()
	MainViewport()
	
	
	If G_RegionDefault <> C_RegionTypeNone
		RegionDraw()
	Else
		If G_Mode = C_ModeTest
			; Blue background for testing
			ClsColor 0, 0, 50
		Else
			; Black background for everything else
			ClsColor 0, 0, 0
		EndIf
	EndIf
	
	If KeyDown(C_Key_Left_Shift)
		Goto MainDrawReturn
	EndIf
	
	If G_Mode = C_ModeEdit And G_EditModeSuper
		; In Super mode draw the grid over the ground
		GroundDraw()
		If G_GridDisplay
			GridDraw()
		EndIf
	Else
		; Normally the ground is drawn over the grid
		If G_GridDisplay
			GridDraw()
		EndIf
		GroundDraw()
	EndIf
	
	If G_Mode = C_ModeEdit And G_RegionDefault <> C_RegionTypeNone
		RegionDrawOutline()
	EndIf
	
	BridgeDrawFull2()
	
	If G_Mode = C_ModeEdit And G_EditModeSuper
		ControlsDraw()
	EndIf
	
	CursorDraw()

	If G_StressGraphDisplay
		DrawImage G_StressGraphs(G_StressGraph), 0, 0
	EndIf
	
	If G_Mode = C_ModeEdit
		MacroDraw()
		MaterialInfoDraw()
		
		If G_ToolPanel\Status = C_PanelStatusShow
			ToolPanelDraw()
		EndIf

		If G_EditModeSuper And G_SuperPanel\Status = C_PanelStatusShow
			SuperPanelDraw()
		EndIf
	EndIf
	
	MouseDraw()
	
	Color 100, 200, 100
	
	If G_PromptText <> ""
		DebugText(G_PromptText)
	EndIf
	
	;DebugText("G_ZoomLevel  = " + G_ZoomLevel)
	;DebugText("G_ZoomFactor = " + G_ZoomFactor)
	;DebugText("G_GridSnap   = " + G_GridSnap)
	;DebugText("G_CameraX = " + G_CameraX)
	;DebugText("G_CameraY = " + G_CameraY)
	;DebugText("G_MouseX  = " + G_MouseX)
	;DebugText("G_MouseY  = " + G_MouseY)
	;DebugText("G_CursorX = " + G_CursorX)
	;DebugText("G_CursorY = " + G_CursorY)
	;DebugText("TempX = " + TempX)
	;DebugText("TempY = " + TempY)
	
	;DebugText("G_Mouse1Down = " + G_Mouse1Down)
	;DebugText("G_EditTool = " + G_EditTool)
	;DebugText("G_EditStep = " + G_EditStep)
	;DebugText("G_Point1X = " + G_Point1X)
	;DebugText("G_Point1Y = " + G_Point1Y)
	;DebugText("G_Point2X = " + G_Point2X)
	;DebugText("G_Point2Y = " + G_Point2Y)
	;DebugText("G_Point3X = " + G_Point3X)
	;DebugText("G_Point3Y = " + G_Point3Y)
	;DebugText("G_Point4X = " + G_Point4X)
	;DebugText("G_Point4Y = " + G_Point4Y)
	
	;DebugText("Caching = " + G_GroundCacheUsed)
	
	If G_Mode = C_ModeEdit
		If G_EditStep >= C_EditStepP1
			DebugText("P1: " + G_Point1X + "," + G_Point1Y)
		EndIf
		
		If G_EditStep >= C_EditStepP2
			DebugText("P2: " + G_Point2X + "," + G_Point2Y)
			DistX = G_Point2X - G_Point1X
			DistY = G_Point2Y - G_Point1Y
			DebugText("Dist: " + Sqr(DistX * DistX + DistY * DistY))
			If DistX <> 0 Or DistY <> 0
				DebugText("Angle: " + ATan2(DistY, DistX))
			EndIf
		EndIf
	EndIf
	
.MainDrawReturn
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function MainBuffer()

	If C_FunctionTrace Then FunctionEntree("MainBuffer")

	If G_ScreenFlipping
		SetBuffer BackBuffer()
	Else
		SetBuffer FrontBuffer()
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function
	
Function MainViewport()

	If C_FunctionTrace Then FunctionEntree("MainViewport")
	
	Viewport G_FrameWidth, G_FrameWidth, G_ScreenWidth - G_FrameWidth * 2, G_ScreenHeight - G_FrameWidth * 2
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SoundUpdate()
		If G_Channel1 = 0
			If G_Mode = C_ModeTest And G_TestComplete And G_TestCompleteCount < 5
				G_TestCompleteCount = G_TestCompleteCount + 1
				G_Channel1 = PlaySound(G_Sound1)
			EndIf
		EndIf
		
		If G_SoundBreak
			If G_Channel2 <> 0
				StopChannel G_Channel2
				G_Channel2 = 0
			EndIf
			
			G_Channel2 = PlaySound(G_Sound2)
			G_SoundBreak = False
		EndIf
		
		If G_Channel3 = 0
			If G_SoundStatus <> C_SoundStatusNone
				If G_Channel3 <> 0
					StopChannel G_Channel3
					G_Channel3 = 0
				EndIf
				
				If G_SoundStatus = C_SoundStatusDone
					G_Channel3 = PlaySound(G_Sound1)
				ElseIf G_SoundStatus = C_SoundStatusWarn
					G_Channel3 = PlaySound(G_Sound2)
				ElseIf G_SoundStatus = C_SoundStatusError
					G_Channel3 = PlaySound(G_Sound1)
				EndIf
			EndIf
		EndIf
			
		If G_Channel3 = 0
			If KeyDown(C_Key_F3)
				G_Channel3 = PlaySound(G_Sound1)
			EndIf
		EndIf
		
		; Terminate any finished sounds
		If G_Channel1 <> 0
			If Not ChannelPlaying(G_Channel1)
				StopChannel G_Channel1
				G_Channel1 = 0
			EndIf
		EndIf
			
		If G_Channel2 <> 0
			If Not ChannelPlaying(G_Channel2)
				StopChannel G_Channel2
				G_Channel2 = 0
			EndIf
		EndIf

		If G_Channel3 <> 0
			If Not ChannelPlaying(G_Channel3)
				StopChannel G_Channel3
				G_Channel3 = 0
			EndIf
		EndIf
		
End Function

Function ViewZoomControls()

	If C_FunctionTrace Then FunctionEntree("ViewZoomControls")
	
	If KeyDown(C_Key_Page_Up) Or G_MouseZOld < G_MouseZ
		ViewZoom(0.5)
	Else If KeyDown(C_Key_Page_Down) Or G_MouseZOld > G_MouseZ
		ViewZoom(-0.5)
	End If
	
	If KeyHit(C_Key_Home)
		ViewFull(C_ViewModeAll)
		;ViewPanReset()
	ElseIf KeyHit(C_Key_End) Or KeyDown(C_Key_End)
		ViewFull(C_ViewModeSelected)
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewZoomReset()

	If C_FunctionTrace Then FunctionEntree("ViewZoomReset")
	
	G_ZoomLevel = 25
	ViewZoom(0)
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewZoom(n#)

	If C_FunctionTrace Then FunctionEntree("ViewZoom")
	
	G_ZoomLevel = G_ZoomLevel + n
	If G_ZoomLevel > G_ZoomLevelMax Then G_ZoomLevel = G_ZoomLevelMax
	If G_ZoomLevel < G_ZoomLevelMin Then G_ZoomLevel = G_ZoomLevelMin
	G_ZoomFactor = 0.9 ^ G_ZoomLevel
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewPanControls()

	If C_FunctionTrace Then FunctionEntree("ViewPanControls")
	
	If KeyDown(C_Key_Up_Arrow)
		ViewPan(0, G_CameraStepLarge)
	ElseIf KeyDown(C_Key_Down_Arrow)
		ViewPan(0, -G_CameraStepLarge)
	EndIf
	
	If KeyDown(C_Key_Left_Arrow)
		ViewPan(-G_CameraStepLarge, 0)
	ElseIf KeyDown(C_Key_Right_Arrow)
		ViewPan(G_CameraStepLarge, 0)
	EndIf
	
	If G_MouseX < G_CameraEdgeLarge
		ViewPan(-G_CameraStepLarge, 0)
	ElseIf G_MouseX < G_CameraEdgeSmall
		ViewPan(-G_CameraStepSmall, 0)
	ElseIf G_MouseX > G_ScreenWidth - G_CameraEdgeLarge - 1
		ViewPan(G_CameraStepLarge, 0)
	ElseIf G_MouseX > G_ScreenWidth - G_CameraEdgeSmall - 1 
		ViewPan(G_CameraStepSmall, 0)
	EndIf

	If G_MouseY < G_CameraEdgeLarge
		ViewPan(0, G_CameraStepLarge)
	ElseIf G_MouseY < G_CameraEdgeSmall
		ViewPan(0, G_CameraStepSmall)
	ElseIf G_MouseY > G_ScreenHeight - G_CameraEdgeLarge - 1
		ViewPan(0, -G_CameraStepLarge)
	ElseIf G_MouseY > G_ScreenHeight - G_CameraEdgeSmall - 1
		ViewPan(0, -G_CameraStepSmall)
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewPanReset()

	If C_FunctionTrace Then FunctionEntree("ViewPanReset")
	
	G_CameraX = 600
	G_CameraY = 240

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewPan(x#, y#)

	If C_FunctionTrace Then FunctionEntree("ViewPan")
	
	G_CameraX = G_CameraX + x / G_ZoomFactor
	G_CameraY = G_CameraY + y / G_ZoomFactor

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function ViewFull(Mode)
	Local Initial
	Local S.T_Segment
	Local X1#, Y1#, X2#, Y2#
	Local CentreX#, CentreY#
	Local W, H
	Local Margin#, Aspect#
	
	If C_FunctionTrace Then FunctionEntree("ViewFull")
	
	Initial = True
	For S = Each T_Segment
		If (Mode = C_ViewModeAll Or (Mode = C_ViewModeSelected And S\Selected)) And S\SegmentType <> C_SegmentTypeFixed And S\SegmentType <> C_SegmentTypeFixedRail
			If G_Mode = C_ModeEdit
				X1 = S\Bolt1\OriginalX
				Y1 = S\Bolt1\OriginalY
				X2 = S\Bolt2\OriginalX
				Y2 = S\Bolt2\OriginalY
			Else
				X1 = S\Bolt1\PX
				Y1 = S\Bolt1\PY
				X2 = S\Bolt2\PX
				Y2 = S\Bolt2\PY
			EndIf
			
			If Initial
				Initial = False
				G_ZoomMinX = X1
				G_ZoomMinY = Y1
				G_ZoomMaxX = X1
				G_ZoomMaxY = Y1
			Else
				If X1 < G_ZoomMinX Then G_ZoomMinX = X1
				If Y1 < G_ZoomMinY Then G_ZoomMinY = Y1
				If X1 > G_ZoomMaxX Then G_ZoomMaxX = X1
				If Y1 > G_ZoomMaxY Then G_ZoomMaxY = Y1
			EndIf
			
			If X2 < G_ZoomMinX Then G_ZoomMinX = X2
			If Y2 < G_ZoomMinY Then G_ZoomMinY = Y2
			If X2 > G_ZoomMaxX Then G_ZoomMaxX = X2
			If Y2 > G_ZoomMaxY Then G_ZoomMaxY = Y2
			
		EndIf
	Next
	
	If G_ZoomMinX = 0 And G_ZoomMinY = 0 And G_ZoomMaxX = 0 And G_ZoomMaxY = 0
		G_ZoomMaxX = 600 * 2
		G_ZoomMaxY = 240 * 2
	Else
		If Mode = C_ViewModeAll
			If G_ZoomMinY > -10 Then G_ZoomMinY = -10
			If G_ZoomMaxY < 10 Then G_ZoomMaxY = 10
		EndIf
	EndIf
	
	CentreX = (G_ZoomMinX + G_ZoomMaxX) / 2
	CentreY = (G_ZoomMinY + G_ZoomMaxY) / 2
	
	If Mode = C_ViewModeAll
		Margin = 0.1	; Allow 10% wider / taller
	Else
		Margin = 0.5	; Increase to 50% for zoom on selected items
	EndIf
	
	W = (G_ZoomMaxX - G_ZoomMinX) * (1 + Margin)
	H = (G_ZoomMaxY - G_ZoomMinY) * (1 + Margin)
	
	If W = 0
		If H = 0
			W = 10
			H = 10
		Else
			W = H
		EndIf
	Else
		If H = 0
			H = W / 2
		EndIf
	EndIf
	
	Aspect = Float(H) / W
	
	If Aspect > G_ScreenAspect
		; Height controls the setting
		G_ZoomFactor = Float(G_ScreenHeight) / (H * G_GridSize)
	Else
		; Width controls the setting
		G_ZoomFactor = Float(G_ScreenWidth) / (W * G_GridSize)
	EndIf

	G_CameraX = CentreX
	G_CameraY = CentreY
		
	G_ZoomLevel = Log(G_ZoomFactor) / Log(0.9) 
	ViewZoom(0)
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function DebugText(Msg$)
	Text G_ScreenWidth - 150, 4 + G_DebugLine * 12, Msg
	G_DebugLine = G_DebugLine + 1
End Function

Function DebugTraceSegment(Point$, S.T_Segment)

	If C_FunctionTrace Then FunctionEntree("DebugTraceSegment")
	
	If S\Id = G_DebugTraceSegmentId
	EndIf
	
	If G_DebugTraceBolt
		If S\Bolt1\Id = G_DebugTraceBoltId
			DebugLog "  Segment " + Point + " Bolt1"
			DebugLog "    PX" + RSet$(S\Bolt1\PX, 14) + " PY" + RSet$(S\Bolt1\PY, 14)
			DebugLog "    SX" + RSet$(S\Bolt1\SX, 14) + " SY" + RSet$(S\Bolt1\SY, 14)
			DebugLog "    FX" + RSet$(S\Bolt1\FX, 14) + " FY" + RSet$(S\Bolt1\FY, 14)
			Stop
		EndIf
		If S\Bolt2\Id = G_DebugTraceBoltId
			DebugLog "  Segment " + Point + " Bolt2"
			DebugLog "    PX" + RSet$(S\Bolt2\PX, 14) + " PY" + RSet$(S\Bolt2\PY, 14)
			DebugLog "    SX" + RSet$(S\Bolt2\SX, 14) + " SY" + RSet$(S\Bolt2\SY, 14)
			DebugLog "    FX" + RSet$(S\Bolt2\FX, 14) + " FY" + RSet$(S\Bolt2\FY, 14)
			Stop
		EndIf
	EndIf
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function DebugTraceBolt(Point$, B.T_Bolt)

	If B\Id = G_DebugTraceBoltId
		DebugLog "  Bolt Loop - " + Point
		DebugLog "    PX" + RSet$(B\PX, 14) + " PY" + RSet$(B\PY, 14)
		DebugLog "    SX" + RSet$(B\SX, 14) + " SY" + RSet$(B\SY, 14)
		DebugLog "    FX" + RSet$(B\FX, 14) + " FY" + RSet$(B\FY, 14)
	EndIf

End Function

Function StressGraphInitialise()
	Local W1 = G_ScreenWidth
	Local W2 = C_TestPhysicsSteps
	Local H = C_StressGraphHeight
	
	If C_FunctionTrace Then FunctionEntree("StressGraphInitialise")
	
	G_StressGraphs(0) = CreateImage(W1, H)
	G_StressGraphs(1) = CreateImage(W1, H)
	
	G_StressGraphBlank = CreateImage(W2, H)
	SetBuffer ImageBuffer(G_StressGraphBlank)
	Color 0, 0, G_StressGraphFrameColour
	Line 0, 0, W2 - 1, 0
	Line 0, H - 1, W2 - 1, H - 1
	Line 0, H / 2, W2 - 1, H / 2
;	Color 0, 0, G_StressGraphMarkColour
;	Line W2 - 1, 1, W2 - 1, H - 2

	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SetHeight()
	Local I, S
	Local X#, Y#, MidX#, ScaleY#

	If C_FunctionTrace Then FunctionEntree("SetHeight")
	
	; Create standard parabolic trench
	MidX = (G_ShoreRight - G_ShoreLeft) / 2
	ScaleY = (G_GroundLevel - G_WaterDepth) / MidX^2
	For S = 0 To G_LevelWidth - 1
		If S < G_ShoreLeft+40 Or S > G_ShoreRight-40
			Y = G_GroundLevel
		Else
			X = S - (G_ShoreLeft + G_ShoreRight) / 2.0
			Y = G_WaterDepth + X^2 * ScaleY
			If Y > G_GroundLevel
				Y = G_GroundLevel
			EndIf
		EndIf
		Height(S) = Y
	Next
	
	; Determine LoX, HiX for further operations
	If G_ShoreLeft < 0
		LoX = 1
	Else
		LoX = G_ShoreLeft + 1
	EndIf
	
	If G_ShoreRight > G_LevelWidth - 1
		HiX = G_LevelWidth - 2
	Else
		HiX = G_ShoreRight - 1
	EndIf
	
	; Add random bumps
	For I = 1 To G_LevelWidth
		X = Rnd(LoX + 80, HiX - 80)
		Height(X) = Height(X) + Rnd(G_WaterDepth, -G_WaterDepth)
	Next
	
	; Smooth it out
	For I = 1 To 250
		For S = LoX To HiX
			Height(S) = (Height(S-1) + Height(S) + Height(S+1)) / 3
		Next
	Next
	
	; Chop off anything above ground level or below water depth
	For S = 0 To G_LevelWidth - 1
		If Height(S) > G_GroundLevel
			Height(S) = G_GroundLevel
		ElseIf Height(S) < G_WaterDepth
			Height(S) = G_WaterDepth
		EndIf
	Next
	
	; Smooth it out again
	For I = 1 To 250
		For S = LoX To HiX
			Height(S) = (Height(S-1) + Height(S) + Height(S+1)) / 3
		Next
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function SetHeightOld()
	
	If C_FunctionTrace Then FunctionEntree("SetHeightOld")
	
	;set the height to a parabol
	For s=0 To G_LevelWidth-1
		x=Float((s-G_ShoreLeft))*G_LevelWidth/(G_LevelWidth-(G_LevelWidth-G_ShoreRight)-G_ShoreLeft)
		y#=-(-(x^2)+G_LevelWidth*x)/(-(G_LevelWidth/2)^2+G_LevelWidth^2/2)*leveldepth*2
		If y>0 Then y=0
		height(s)=y
	Next

	;add some random height
	For r=0 To G_LevelWidth/2
		x=Rnd(G_ShoreLeft+80,G_ShoreRight-80)
		height(x)=height(x)-Rnd(-leveldepth,+leveldepth)
	Next

	;smooth it out to get a random but smooth skyline
	For s=1 To 200 ;500
		For h=1 To G_LevelWidth-2
			height(h)=(height(h-1)+height(h+1)+height(h))/3
		Next
	Next
	
	;chop off mountains
	For m=0 To G_LevelWidth-1
		If m<G_ShoreLeft Or m>G_ShoreRight And height(m)<0 Then height(m)=0
	Next
	
	If C_FunctionTrace Then FunctionEgress()
	
End Function

Function FunctionEntree(FunctionName$)
	
	G_FunctionTraceLevel = G_FunctionTraceLevel + 1
	WriteLine G_DebugTraceFile, CurrentTime() + String( " ", G_FunctionTraceLevel * 2 + 1) + FunctionName
	
End Function

Function FunctionEgress()
	G_FunctionTraceLevel = G_FunctionTraceLevel - 1
End Function

Function FunctionMessage(Msg$)
	
	WriteLine G_DebugTraceFile, CurrentTime() + String( " ", G_FunctionTraceLevel * 2 + 1) + Msg
	
End Function