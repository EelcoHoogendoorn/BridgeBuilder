/*  Generate images for headings etc in Bridge Builder
 *
 *  - Change the text in the declaration of 'Heading'
 *  - Generate the image.  Suggest using 640x480 AA 0.3 setting1
 *  - The output image will be saved in C:\Temp\Heading.PNG or wherever the default directory points
 *  - Use PSP or other tool to crop to the boundary of the surrounding border
 *  - Save as the appropriate heading file in 'Bridge Builder\Images\Heading - Whatever.PNG'
 *
 *  A bit manual but it shouldn't be a common occurrence.
 *
 */

#include "colors.inc"
#include "metals.inc"
#include "stones.inc"
#include "textures.inc"

#include "C:\Paths\Path POV-Ray.inc"

/* Test Scene
 */

#declare ViewX = 0;
#declare ViewY = 0;
#declare ViewZ = -20;
#declare ViewZoom = 10;

camera
 { orthographic
   location < ViewX, ViewY, ViewZ >
   up y * ViewZoom
   right x * ViewZoom
   look_at  < ViewX, ViewY, 0 >
 }

light_source { <500, 500, -1000> White * 0.25 }
light_source { <0, 500, -1000> White * 1 }

#declare Heading =
  text
	{ ttf "timrom.ttf" "Bridge File Load" 1, 0
	  pigment { BrightGold }
   	  finish { reflection .25 specular 1 }
	}

#declare HMin = min_extent(Heading);
#declare HMax = max_extent(Heading);

#declare BWidth = 0.25;
#declare BMin = HMin - < BWidth, BWidth, 0>;
#declare BMax = HMax + < BWidth, BWidth, 0>;

#declare BRadius = 0.03;

#declare P1 = BMin;
#declare P2 = < BMin.x, BMax.y, 0 >;
#declare P3 = < BMax.x, BMax.y, BMin.z >;
#declare P4 = < BMax.x, BMin.y, 0 >;

object
  { union
      { object { Heading translate < 0, 0, -0.1 > }
	    box
	 	  { BMin BMax
	 	    texture
	 	      { pigment { image_map { jpeg concat(PFImagePath, "Textures\\Metal\\Metal019.jpg") } }
	 	        scale 5
	 	        rotate z * 15
	 	      }
	 	    
	 	    /*texture { T_Stone18 }*/
	 	  }
	 	union
	 	  { sphere { P1 BRadius }
	 	    sphere { P2 BRadius }
	 	    sphere { P3 BRadius }
	 	    sphere { P4 BRadius }
	 	    cylinder { P1 P2, BRadius }
	 	    cylinder { P2 P3, BRadius }
	 	    cylinder { P3 P4, BRadius }
	 	    cylinder { P4 P1, BRadius }
	 	    texture { T_Silver_1A }
	 	  }
      }
    translate < -(HMax.x - HMin.x) / 2, 0, 0 >
    rotate x * 0
  }

