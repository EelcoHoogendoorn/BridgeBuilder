#include "colors.inc"
#include "metals.inc"
#include "stones.inc"
#include "textures.inc"

#include "C:\Paths\Path POV-Ray.inc"

// Control stuff

#declare nMaterials = 5;
#declare nModes = 4;

#declare iClock = frame_number;

#declare iIcon = div(iClock, nModes);
#declare iMode = mod(iClock, nModes);

#if (clock_on = 0)
  #declare iIcon = 20;
  #declare iMode = 1;
#end

#macro PositionMaterial()
  rotate x * -15
  rotate z * 30
  rotate x * -15
  rotate y * 30
#end

// Wood beam
#declare Wood_Beam =
  object
    { union
        { box { <-4, 1, -0.75> <4, -1, 0.75> }
          texture
            { pigment
                //{ image_map { jpeg concat(PFImagePath, "Textures\\Wood\\Wood_Laminate.jpg") }
                { image_map { jpeg concat(PFImagePath, "\\Textures\\Wood\\Texture - Wood12.jpg") }
                }
              scale 5
              rotate x * 30
              rotate y * 15
              rotate z * 90
              finish { ambient 0.25 }
            }
        }
      PositionMaterial()
    }  

#declare W1 = 0.15;
#declare W2 = 0.25;
#declare W3 = 0.1;

// Steel girder
#declare Steel_Beam =
  object
    { union
        { box { <-4, 1, -1> <4, 1 - W1, 1> }		// Top 
          box { <-4, -1, -1> <4, -1 + W1, 1> }  	// Bottom
          box { <-4, 1, -W2 / 2> <4, -1, W2 / 2> }	// Middle
          
          // Add fillip curves
          difference
            { box      { <-4,   1 - W1 - W3, -W2 / 2 - W3> < 4,   1 - W1,      -W2 / 2> }
              cylinder { <-4.5, 1 - W1 - W3, -W2 / 2 - W3> < 4.5, 1 - W1 - W3, -W2 / 2 - W3>, W3 }
            }
          difference
            { box      { <-4,   1 - W1 - W3, W2 / 2 + W3> < 4,   1 - W1,      W2 / 2> }
              cylinder { <-4.5, 1 - W1 - W3, W2 / 2 + W3> < 4.5, 1 - W1 - W3, W2 / 2 + W3>, W3 }
            }
          difference
            { box      { <-4,   -1 + W1 + W3, -W2 / 2 - W3> < 4,   -1 + W1,      -W2 / 2> }
              cylinder { <-4.5, -1 + W1 + W3, -W2 / 2 - W3> < 4.5, -1 + W1 + W3, -W2 / 2 - W3>, W3 }
            }
          difference
            { box      { <-4,   -1 + W1 + W3, W2 / 2 + W3> < 4,   -1 + W1,      W2 / 2> }
              cylinder { <-4.5, -1 + W1 + W3, W2 / 2 + W3> < 4.5, -1 + W1 + W3, W2 / 2 + W3>, W3 }
            }
          
          //texture { T_Chrome_2A }
          //texture { T_Silver_1A }
          //texture { pigment { image_map { jpeg concat(PFImagePath, "Textures\\Metal\\Texture - Metal15.jpg") } } scale 5 finish { ambient 0.25 } }
          texture
            { pigment { image_map { jpeg concat(PFImagePath, "Textures\\Metal\\Metal019.jpg") } }
              scale 5
              rotate x * 30
              rotate y * 15
              finish { ambient 0.25 }
            }
          }
      PositionMaterial()
    }  

// Re-inforced concreate beam
#declare RodOffset = 0.6;
#declare RodTexture = texture { T_Chrome_2A }
#declare Concrete_Beam =
  object
    { union
        { intersection
            { box { <-4, -1, -1> <4, 1, 1> }
              box { <-4, -1.2, -1.2> <4, 1.2, 1.2> rotate x * 45 }
              //texture { T_Stone8 finish { specular 0.9 roughness 0.01 } }
              texture
                { normal { bumps 1.05 }
                  //pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Stone\\Concrete.jpg" } }
                  pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Stone\\Texture - Stone25.jpg") } }
                  scale 5
                  rotate x * 30
                  rotate y * 15
                  finish { ambient 0.25 }
                }
            }
          cylinder { <-4.5, RodOffset, RodOffset> <4.5, RodOffset, RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-4.5, RodOffset, -RodOffset> <4.5, RodOffset, -RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-4.5, -RodOffset, RodOffset> <4.5, -RodOffset, RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-4.5, -RodOffset, -RodOffset> <4.5, -RodOffset, -RodOffset>, 0.05 texture { RodTexture } }
        }
      PositionMaterial()
    }

// Multi-strand Steel cable
#declare StrandRadius = 0.2;
#declare CableRadius = 1.0;
#declare CableTexture = texture { T_Chrome_2A }

#declare Seed1 = seed(1234);

#declare Steel_Cable =
  object
    { union
        { #declare iY = -6;
          #while (iY <= 6)
            #declare rY = iY * StrandRadius * sqrt(3);
          
            #declare iZ = -6;
            #while (iZ <= 6)
              #declare rZ = iZ * StrandRadius * 2 + mod(iY, 2) * StrandRadius;
              
              #declare rDist = sqrt(rY * rY + rZ * rZ);
              #if (rDist <= CableRadius)
                cylinder
                  { <-4, 0, 0> <4, 0, 0>, StrandRadius * 0.8
                    texture { pigment { spiral1 5 } rotate y * 90 translate < rand(Seed1), 0, 0> }
                    //texture { RodTexture pigment { colour White * (0.5 + rand(Seed1) / 2) } }
                    translate <0, rY, rZ>
                  }
              #end
              
              
              #declare iZ = iZ + 1;
            #end
            
            #declare iY = iY + 1;
          #end
          
          union
            { cylinder { <-3.5, 0, 0> <-3.3, 0, 0> CableRadius * 1.2 }
              cylinder { <3.5, 0, 0> <3.3, 0, 0> CableRadius * 1.2 }
              texture
                { pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Metal\\Metal019.jpg") } }
                  scale 5
                  rotate x * 30
                  rotate y * 15
                  finish { ambient 0.25 }
                }
            }
          //texture { RodTexture }
        }
      PositionMaterial()
    }

// Boron-Boron Multi-strand Steel cable
#declare StrandRadius = 0.2;
#declare CableRadius = 1.0;
#declare CableTexture = texture { T_Chrome_2A }

#declare Seed1 = seed(1234);

#declare Steel_Cable =
  object
    { union
        { #declare iY = -6;
          #while (iY <= 6)
            #declare rY = iY * StrandRadius * sqrt(3);
          
            #declare iZ = -6;
            #while (iZ <= 6)
              #declare rZ = iZ * StrandRadius * 2 + mod(iY, 2) * StrandRadius;
              
              #declare rDist = sqrt(rY * rY + rZ * rZ);
              #if (rDist <= CableRadius)
                cylinder
                  { <-4, 0, 0> <4, 0, 0>, StrandRadius * 0.8
                    texture { pigment { spiral1 5 } rotate y * 90 translate < rand(Seed1), 0, 0> }
                    //texture { RodTexture pigment { colour White * (0.5 + rand(Seed1) / 2) } }
                    translate <0, rY, rZ>
                  }
              #end
              
              
              #declare iZ = iZ + 1;
            #end
            
            #declare iY = iY + 1;
          #end
          
          union
            { cylinder { <-3.5, 0, 0> <-3.3, 0, 0> CableRadius * 1.2 }
              cylinder { <3.5, 0, 0> <3.3, 0, 0> CableRadius * 1.2 }
              texture
                { pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Metal\\Metal019.jpg") } }
                  scale 5
                  rotate x * 30
                  rotate y * 15
                  finish { ambient 0.25 }
                }
            }
          //texture { RodTexture }
        }
      PositionMaterial()
    }

// Icon for Select function
#declare Icon_Select =
  object
    { union
        { union
            { torus { 2.0, 0.15 }
              torus { 1.0, 0.15 }
              texture { T_Silver_1A }
            }

          union
            { cylinder { < -3,  0,  0 >,    < -1,  0,  0 >, 0.25 }
              cylinder { <  3,  0,  0 >,    <  1,  0,  0 >, 0.25 }
              cylinder { <  0,  0, -3 >,    <  0,  0, -1 >, 0.25 }
              cylinder { <  0,  0,  3 >,    <  0,  0,  1 >, 0.25 }
              cone     { <  0,  0,  0 >, 0, < -1,  0,  0 >, 0.25 }
              cone     { <  0,  0,  0 >, 0, <  1,  0,  0 >, 0.25 }
              cone     { <  0,  0,  0 >, 0, <  0,  0, -1 >, 0.25 }
              cone     { <  0,  0,  0 >, 0, <  0,  0,  1 >, 0.25 }
              texture { T_Silver_1C }
            }
          
          rotate x * 90
          translate < 2, 1.5, 0>
          
          /*
          difference
            { cylinder { < 0.5, 0, -0.25 >, < 0.5, 0, 0.25 >, 0.5 }
              cylinder { < 0.5, 0, -0.30 >, < 0.5, 0, 0.30 >, 0.25 }
            }
          cylinder { < 0.75, 0, 0 >, < 4, 0, 0 >, 0.25 }
          cone { < 4.0, 0, 0 >, 0.5, < 5.0, 0, 0 >, 0 }
          */
        }
      //PositionMaterial()
    }

// Icon for Build function
#declare Icon_Build =
  object
    { union
        { difference
            { cylinder { < 0.5, 0, -0.25 >, < 0.5, 0, 0.25 >, 0.5 }
              cylinder { < 0.5, 0, -0.30 >, < 0.5, 0, 0.30 >, 0.25 }
            }
          cylinder { < 0.75, 0, 0 >, < 4, 0, 0 >, 0.25 }
          cone { < 4.0, 0, 0 >, 0.5, < 5.0, 0, 0 >, 0 }
          texture { T_Silver_1A } 
        }
      PositionMaterial()
    }

// Icon for Build_Muiltiple function
#declare Icon_Build_Multiple =
  object
    { union
        { difference
            { cylinder { < 0.5, 0, -0.25 >, < 0.5, 0, 0.25 >, 0.5 }
              cylinder { < 0.5, 0, -0.30 >, < 0.5, 0, 0.30 >, 0.25 }
            }
          cylinder { < 0.75, 0, 0 >, < 4, 0, 0 >, 0.25 }
          cone { < 4.0, 0, 0 >, 0.5, < 5.0, 0, 0 >, 0 }
          cone { < 4.5, 0, 0 >, 0.5, < 5.5, 0, 0 >, 0 }
          cone { < 5.0, 0, 0 >, 0.5, < 6.0, 0, 0 >, 0 }
          texture { T_Silver_1A } 
        }
      PositionMaterial()
    }

// Icon for Macro_Line function
#declare BoltRadius = 0.1;
#declare LeftX = -5;
#declare RightX = -1;

#declare Icon_Macro_Line =
  object
    { #declare X = LeftX;
      
      union
        { #while (X <= RightX)
            #declare Y = (X - LeftX) * 0.6;
            
            #declare P1 = < X, Y, 0 >;
            
            cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
            
            sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
            #if (X > LeftX)
              cylinder { P1, P2 BoltRadius }
            #end
            
            #declare P2 = P1;
          
            #declare X = X + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
    }

// Icon for Macro_Beam function
#declare BoltRadius = 0.1;
#declare LeftX = -5;
#declare RightX = -1;
#declare Depth = 1;

#declare Icon_Macro_Beam =
  object
    { #declare Y1 = 1;
      #declare Y2 = Y1 + Depth;
      
      #declare X1 = LeftX;
      union
        { #while (X1 <= RightX)
            #declare X2 = X1 + 0.5;
            
            #declare P1 = < X1, Y1, 0 >;
            #declare P2 = < X2, Y2, 0 >;
            
            cylinder { < X1, Y1, -BoltRadius >, < X1, Y1, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
            sphere { < X1, Y1, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
            #if (X2 < RightX)
              cylinder { < X2, Y2, -BoltRadius >, < X2, Y2, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
              sphere { < X2, Y2, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
              
              cylinder { P1, P2, BoltRadius }
            #end
            
            #if (X1 > LeftX)
              cylinder { P1, P3, BoltRadius }
              cylinder { P1, P4, BoltRadius }
              
              #if (X2 < RightX)
                cylinder { P2, P4, BoltRadius }
              #end
            #end
            
            #declare P3 = P1;
            #declare P4 = P2;
          
            #declare X1 = X1 + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
    }

// Icon for Macro_Parabola function
#declare BoltRadius = 0.1;
#declare LeftX = -5;
#declare RightX = 5;

#declare Icon_Macro_Parabola =
  object
    { #declare X = LeftX;
      
      union
        { #while (X <= RightX)
            #declare Y1 = 2.5 - pow(X, 2) / 8;
            #declare Y2 = 3.0 - pow(X, 2) / 12;
            
            #declare P1 = < X, Y1, 0 >;
            #declare P2 = < X, Y2, 0 >;
            
            union
              { cylinder { < X, Y1, -BoltRadius >, < X, Y1, BoltRadius >, BoltRadius }
                cylinder { < X, Y2, -BoltRadius >, < X, Y2, BoltRadius >, BoltRadius }
                texture { T_Chrome_1A }
              }
            
            union
              { sphere { < X, Y1, -BoltRadius >, BoltRadius / 2 }
                sphere { < X, Y2, -BoltRadius >, BoltRadius / 2 }
                texture { T_Silver_1A }
              }
            sphere { P2, BoltRadius }
            
            cylinder { P1, P2, BoltRadius }
            
            #if (X > LeftX)
              cylinder { P1, P3, BoltRadius }
              cylinder { P2, P4, BoltRadius }
              #if (X <= 0)
                cylinder { P1, P4, BoltRadius }
              #else
                cylinder { P2, P3, BoltRadius }
              #end
            #end
            
            #declare P3 = P1;
            #declare P4 = P2; 
          
            #declare X = X + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
      //rotate y * 60
      //PositionMaterial()
    }

// Icon for Macro_Circle function
#declare BoltRadius = 0.1;

#declare R1 = 2;
#declare R2 = 1.5;

#declare AngleStep = 30;

#declare Icon_Macro_Circle =
  object
    { #declare Y1 = 1;
      #declare Y2 = Y1 + Depth;
      
      #declare Angle = 0;
      union
        { #while (Angle < 360)
            #declare X1 = R1 * cos(Angle * pi / 180);
            #declare Y1 = R1 * sin(Angle * pi / 180);
            
            #declare X2 = R2 * cos(Angle * pi / 180);
            #declare Y2 = R2 * sin(Angle * pi / 180);
            
            #declare P1 = < X1, Y1, 0 >;
            #declare P2 = < X2, Y2, 0 >;
            
            union
              { cylinder { < X1, Y1, -BoltRadius >, < X1, Y1, BoltRadius >, BoltRadius }
                cylinder { < X2, Y2, -BoltRadius >, < X2, Y2, BoltRadius >, BoltRadius }
                texture { T_Chrome_1A }
              }
            
            union
              { sphere { < X1, Y1, -BoltRadius >, BoltRadius / 2 }
                sphere { < X2, Y2, -BoltRadius >, BoltRadius / 2 }
                texture { T_Silver_1A }
              }
            
            cylinder { P1, P2, BoltRadius }
            
            #if (Angle = 0)
                #declare PA = P1;
                #declare PB = P2;
            #else
              cylinder { P1, P3, BoltRadius }
              cylinder { P1, P4, BoltRadius / 2 }
              cylinder { P2, P3, BoltRadius / 2 }
              cylinder { P2, P4, BoltRadius }
              
            #end
            
            #declare P3 = P1;
            #declare P4 = P2;
          
            #declare Angle = Angle + AngleStep;
          #end
          
          cylinder { P1, PA, BoltRadius }
          cylinder { P1, PB, BoltRadius / 2 }
          cylinder { P2, PA, BoltRadius / 2 }
          cylinder { P2, PB, BoltRadius }

          texture { T_Silver_1B } 
        }
      scale 1
      translate < 2, 1.5, 0 >
    }

#declare Icon_Replacer =
    
  #declare P0 = <-1, -1, -1>;
  #declare P1 = <1, -1, -1>;
  #declare P2 = <1, -1, 1>;
  #declare P3 = <-1, -1, 1>;
  
  #declare P4 = <-1, 1, -1>;
  #declare P5 = <1, 1, -1>;
  #declare P6 = <1, 1, 1>;
  #declare P7 = <-1, 1, 1>;

  object
    { union
        { union
            { triangle { P0, P1, P5 }
	          triangle { P0, P5, P4 }
              texture
                { pigment { image_map { jpeg concat(PFImagePath, "Textures\\Metal\\Metal019.jpg") } }
	              scale 5
	              rotate x * 30
	              rotate y * 15
	              finish { ambient 0.25 }
	            }
  	        }
          union
  	        { triangle { P4, P5, P6 }
  	          triangle { P4, P6, P7 }
	          texture
	            { pigment
	                { image_map { jpeg concat(PFImagePath, "Textures\\Wood\\Wood_Laminate.jpg") }
	                  //{ image_map { jpeg concat(PFImagePath, "\\Textures\\Wood\\Texture - Wood12.jpg" }
                    }
	              scale 5
	              rotate x * 30
	              rotate y * 15
	              rotate z * 90
	              finish { ambient 0.25 }
	            }
  	        }
  	      union
  	        { triangle { P1, P5, P6 }
  	          triangle { P1, P6, P2 }
              texture
                { normal { bumps 1.05 }
                  //pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Stone\\Concrete.jpg" } }
                  pigment { image_map { jpeg concat(PFImagePath, "\\Textures\\Stone\\Texture - Stone25.jpg") } }
                  scale 5
                  rotate x * 30
                  rotate y * 15
                  finish { ambient 0.25 }
                }
  	        }
  	      union
  	        { cylinder { P0, P1, 0.05 }
  	          cylinder { P1, P2, 0.05 }
  	          cylinder { P2, P3, 0.05 }
  	          cylinder { P3, P0, 0.05 }
  	           
  	          cylinder { P4, P5, 0.05 }
  	          cylinder { P5, P6, 0.05 }
  	          cylinder { P6, P7, 0.05 }
  	          cylinder { P7, P4, 0.05 }
  	           
  	          cylinder { P0, P4, 0.05 }
  	          cylinder { P1, P5, 0.05 }
  	          cylinder { P2, P6, 0.05 }
  	          cylinder { P3, P7, 0.05 }
  	           
  	          texture { T_Silver_1A }
  	        }
  	        
          union
  	        { sphere { P0, 0.15 }
  	          sphere { P1, 0.15 }
  	          sphere { P2, 0.15 }
  	          sphere { P3, 0.15 }
  	          sphere { P4, 0.15 }
  	          sphere { P5, 0.15 }
  	          sphere { P6, 0.15 }
  	          sphere { P7, 0.15 }
  	           
  	          texture { New_Brass }
  	        }
  	      
  	    rotate x * -30
  	    rotate y * 30
  	    scale 1.25
  	    translate <2, 1.5, 0>
      }
   }
  
#declare Icon_Save =

  #declare N = 5;
  #declare R1 = 1.25;
  #declare R2 = 0.25;
  #declare T = 0.05;
  #declare D = 0.25;
  
  #declare H = (N + 2) * D;
    
  object
    { union
        { union
            {
            #declare i = 0;
            #while (i < N)
              cylinder { <0, (i + 1) * D, 0>, <0, (i + 1) * D + T, 0>, R1 }
              #declare i = i + 1;
            #end
            texture { T_Silver_1A }
            }
          
          union
            { cylinder { <0, -D+T, 0>, <0, H+T, 0>, R2}
              texture { T_Chrome_1A } 
            }
            
          difference
            { box { <-1.5,     -D / 2, -1.5>, <1.5, H, 1.5> texture { T_Gold_1A } }
              box { <-1.5 + D / 2,  0, -1.6>, <1.5 - D / 2, H - D / 2, 2.5 - D / 2> texture { T_Chrome_1A } }
              //box { <-1.5, -D, -1.5>, <-1.5+T, 1.5, 1.5> }
              //box { <1.5, -D, -1.5>, <1.5-T, 1.5, 1.5> }
            }
          
          object
          {
          prism
            { linear_spline -0.25, 0.25 8,
              <0, 0>, <1, 0.5>, <0.5, 0.5>, <0.5, 1.25>, <-0.5, 1.25>, <-0.5, 0.5>, <-1, 0.5>, <0, 0>
            }
          texture { finish { F_MetalA } pigment { White + Blue / 2 } }
          scale 1.5
          rotate x * -90
          translate <0, H + 0.25, 0>
          }
        }
      rotate y * 30
      rotate x * -5
      translate <2, -0.25, 0>
    }
  
#declare Icon_Load =

  #declare N = 5;
  #declare R1 = 1.25;
  #declare R2 = 0.25;
  #declare T = 0.05;
  #declare D = 0.25;
  
  #declare H = (N + 2) * D;
    
  object
    { union
        { union
            {
            #declare i = 0;
            #while (i < N)
              cylinder { <0, (i + 1) * D, 0>, <0, (i + 1) * D + T, 0>, R1 }
              #declare i = i + 1;
            #end
            texture { T_Silver_1A }
            }
          
          union
            { cylinder { <0, -D+T, 0>, <0, H+T, 0>, R2}
              texture { T_Chrome_1A } 
            }
            
          difference
            { box { <-1.5,     -D / 2, -1.5>, <1.5, H, 1.5> texture { T_Gold_1A } }
              box { <-1.5 + D / 2,  0, -1.6>, <1.5 - D / 2, H - D / 2, 2.5 - D / 2> texture { T_Chrome_1A } }
              //box { <-1.5, -D, -1.5>, <-1.5+T, 1.5, 1.5> }
              //box { <1.5, -D, -1.5>, <1.5-T, 1.5, 1.5> }
            }
          
          object
          {
          prism
            { linear_spline -0.25, 0.25 8,
              <0, 0>, <1, 0.5>, <0.5, 0.5>, <0.5, 1.25>, <-0.5, 1.25>, <-0.5, 0.5>, <-1, 0.5>, <0, 0>
            }
          texture { finish { F_MetalA } pigment { White + Blue / 2 } }
          scale 1.5
          rotate x * 90
          translate <0, 2 + H + 0.25, 0>
          }
        }
      rotate y * 30
      rotate x * -5
      translate <2, -0.25, 0>
    }
  
// Icon for Macro_Line function
#declare BoltRadius = 0.2;
#declare LeftX = -5;
#declare RightX = -1;
#declare Offset = <-0.5, 1.5, 0>;

#declare Icon_Copy =
  object
    { #declare X = LeftX;
      
      union
        { #while (X <= RightX)
            #declare Y = 1.5 - pow(X + 2, 2) / 5;
            
            #declare P1 = < X, Y, 0 >;
            
            #if (X >= -4 & X <= -2)
              cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius pigment { colour Aquamarine } }
              sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
              cylinder { < X, Y, -BoltRadius > + Offset, < X, Y, BoltRadius > + Offset, BoltRadius pigment { colour Aquamarine } }
              sphere { < X, Y, -BoltRadius > + Offset, BoltRadius / 2 texture { T_Silver_1A } }

              /*  Draw arrow
               */
              object
                { union
                    { cylinder { P1 + Offset * 0.2, P1 + Offset * 0.6, BoltRadius / 2 }
                      cone { P1 + Offset * 0.85, 0, P1 + Offset * 0.6, BoltRadius }
                    }
                  pigment { colour Green }
                  no_shadow
                  translate <0, 0, -1>
                }
              
              #if (X > LeftX)
                cylinder { P1, P2 BoltRadius }
              #end
              
              #if (X >= -3 & X <= -2)
                cylinder { P1 + Offset, P2 + Offset BoltRadius pigment { colour Yellow } }
              #end
            #else
              cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
              sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
              #if (X > LeftX)
                cylinder { P1, P2 BoltRadius }
              #end
            #end

            #declare P2 = P1;
          
            #declare X = X + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
    }

#declare Icon_Move =
  object
    { #declare X = LeftX;
      
      union
        { #while (X <= RightX)
            #declare Y = 1.5 - pow(X + 2, 2) / 5;
            
            #declare P1 = < X, Y, 0 >;
            
            #if (X != -3)
              cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius pigment { colour Aquamarine } }
              sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            #end
            
            #if ( X >= -4 & X <= -2)
            
              cylinder { < X, Y, -BoltRadius > + Offset, < X, Y, BoltRadius > + Offset, BoltRadius pigment { colour Aquamarine } }
              sphere { < X, Y, -BoltRadius > + Offset, BoltRadius / 2 texture { T_Silver_1A } }
              
              /*  Draw arrow
               */
              object
                { union
                    { cylinder { P1 + Offset * 0.2, P1 + Offset * 0.6, BoltRadius / 2 }
                      cone { P1 + Offset * 0.85, 0, P1 + Offset * 0.6, BoltRadius }
                    }
                  pigment { colour Green }
                  no_shadow
                  translate <0, 0, -1>
                }
              
              #if (X > LeftX & X != -3 & X != -2)
                cylinder { P1, P2 BoltRadius }
              #end
              
              #if (X >= -3 & X <= -2)
                cylinder { P1 + Offset, P2 + Offset BoltRadius pigment { colour Yellow } }
              #end
            #else
              cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
              sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
              #if (X > LeftX)
                cylinder { P1, P2 BoltRadius }
              #end
            #end

            #declare P2 = P1;
          
            #declare X = X + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
    }

#declare Icon_Stretch =
  object
    { #declare X = LeftX;
      
      union
        { #while (X <= RightX)
            #declare Y = 1.5 - pow(X + 2, 2) / 5;
            
            #declare P1 = < X, Y, 0 >;
            
            #if (X = -3)
              cylinder { < X, Y, -BoltRadius > + Offset, < X, Y, BoltRadius > + Offset, BoltRadius pigment { colour Aquamarine } }
              sphere { < X, Y, -BoltRadius > + Offset, BoltRadius / 2 texture { T_Silver_1A } }

              cylinder { P1 + Offset, P2 BoltRadius pigment { colour Yellow } }
              
              /*  Draw arrow
               */
              object
                { union
                    { cylinder { P1 + Offset * 0.2, P1 + Offset * 0.6, BoltRadius / 2 }
                      cone { P1 + Offset * 0.85, 0, P1 + Offset * 0.6, BoltRadius }
                    }
                  pigment { colour Green }
                  no_shadow
                  translate <0, 0, -1>
                }
            #else
              cylinder { < X, Y, -BoltRadius >, < X, Y, BoltRadius >, BoltRadius texture { T_Chrome_1A } }
              sphere { < X, Y, -BoltRadius >, BoltRadius / 2 texture { T_Silver_1A } }
            
              #if (X > LeftX)
                cylinder { P1, P2 BoltRadius }
              #end
            #end

            #if (X = -2)
              cylinder { P1, P2 BoltRadius pigment { colour Yellow } }
            #end
            
            #if (X = -3)
              #declare P2 = P1 + Offset;
            #else
              #declare P2 = P1;
            #end
          
            #declare X = X + 1;
          #end
          texture { T_Silver_1B } 
        }
      scale 1
      translate < 5, 0, 0 >
    }


/*  Test scene
 */

#declare ViewX = 2;
#declare ViewY = 1.5;
#declare ViewZ = -20;
#declare ViewZoom = 5;

camera
 { orthographic
   location < ViewX, ViewY, ViewZ >
   up y * ViewZoom
   right x * ViewZoom
   look_at  < ViewX, ViewY, 0 >
 }

light_source { <500, 500, -1000> White * 0.25 }
light_source { <0, 500, -1000> White * 1 }

#switch (iIcon)
  #case (0)
    // Empty
  #break
  
  #case (1)
    object { Wood_Beam }
  #break
  
  #case (2)
    object { Steel_Beam }
  #break
  
  #case (3)
    object { Concrete_Beam }
  #break
  
  #case (4)
    object { Steel_Cable }
  #break
  
  #case (5)
    object { Icon_Select }
  #break
  
  #case (6)
    object { Icon_Build }
  #break
  
  #case (7)
    object { Icon_Build_Multiple }
  #break
  
  #case (8)
    object { Icon_Macro_Line }
  #break
  
  #case (9)
    object { Icon_Macro_Beam }
  #break
  
  #case (10)
    object { Icon_Macro_Parabola }
  #break
  
  #case (11)
    object { Icon_Macro_Circle }
  #break
  
  #case (12)
    // This is a sphere - Just a placeholder until other graphics are completed
  	object { sphere { <2, 1.5, 0>, 2 texture {T_Silver_1A} } }
  #break
  
  #case (13)
    // Material replacer
    object { Icon_Replacer }
  #break
  
  #case (14)
    // Load Bridge
    object { Icon_Load }
  #break
  
  #case (15)
    // Save Bridge
    object { Icon_Save }
  #break
  
  #case (16)
  	// New Bridge
  #break
  
  #case (17)
    // Quit
  #break
  
  #case (18)
    // Copy
    object { Icon_Copy }
  #break

  #case (19)
    // Move
    object { Icon_Move }
  #break

  #case (20)
    // Stretch
    object { Icon_Stretch }
  #break

#end                                        

#declare dX = ViewZoom / 2.0;
#declare dY = ViewZoom / 2.0;
#declare dW = 0.1;

#declare P1 = <-dX + ViewX, -dY + ViewY, ViewZ + ViewZoom>;
#declare P2 = < dX + ViewX, -dY + ViewY, ViewZ + ViewZoom>;
#declare P3 = < dX + ViewX,  dY + ViewY, ViewZ + ViewZoom>;
#declare P4 = <-dX + ViewX,  dY + ViewY, ViewZ + ViewZoom>;

// Icon frame
object
 { union
     { cylinder { P1, P2, dW}
       cylinder { P2, P3, dW}
       cylinder { P3, P4, dW}
       cylinder { P4, P1, dW}
       sphere { P1, dW }
       sphere { P2, dW }
       sphere { P3, dW }
       sphere { P4, dW }
     }
   texture {T_Silver_1A }
  }

#if (iMode = 0)
  object
   { union
       { cylinder { P1, P3, dW}
         cylinder { P2, P4, dW}
       }
     texture {pigment { Red }  }
    }
   
#end

#switch (iMode)
  #case (0)
    plane { -z, -10 texture { pigment { Red * 0.5} } }
  #break
  
  #case (1)
  #break
  
  #case (2)
    plane { -z, -10 texture { pigment { Green * 0.5} } }
  #break

  #case (3)
    plane { -z, -10 texture { checker texture { T_Silver_1A } texture { T_Chrome_1A } } }
  #break  
#end

//plane { -z, -10 texture { pigment { checker pigment{Red}, pigment{White} } } }
/*
object { Wood_Beam translate <2, -2.5, -3> }
object { Steel_Beam }
object { Concrete_Beam translate <-2, 2.5, 3> }
*/
