#include "colors.inc"
#include "metals.inc"
#include "stones.inc"
#include "textures.inc"

// Re-inforced concreate beam

#declare RodOffset = 0.6;
#declare RodTexture = texture { T_Chrome_2A }
#declare Concrete_Beam =
  object
    { union
        { intersection
            { box { <-4, -1, -1> <4, 1, 1> }
              box { <-4, -1.2, -1.2> <4, 1.2, 1.2> rotate x * 45 }
              texture { T_Stone8 finish { specular 0.9 roughness 0.01 } }
            }
          cylinder { <-5, RodOffset, RodOffset> <5, RodOffset, RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-5, RodOffset, -RodOffset> <5, RodOffset, -RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-5, -RodOffset, RodOffset> <5, -RodOffset, RodOffset>, 0.05 texture { RodTexture } }
          cylinder { <-5, -RodOffset, -RodOffset> <5, -RodOffset, -RodOffset>, 0.05 texture { RodTexture } }
        }
      rotate x * -15
      rotate z * 30
      rotate x * -15
      rotate y * 30
    }

// Steel girder

#declare W1 = 0.35;
#declare W2 = 0.25;

#declare Steel_Beam =
  object
    { union
        { box { <-4, 1, -1> <4, 1 - W1, 1> }
          box { <-4, -1, -1> <4, -1 + W1, 1> }
          box { <-4, 1-W1, -W2 / 2> <4, -1 + W1, W2 / 2> }
          //texture { T_Chrome_2A }
          //texture { T_Silver_1A }
          //texture { pigment { image_map { jpeg "C:\E22018\Downloads\Images\Textures\Metal\Texture - Metal15.jpg" } } scale 5 finish { ambient 0.25 } }
          texture { pigment { image_map { jpeg "C:\Downloads\Images\Textures\Metal\Metal019.jpg" } } scale 5 rotate x * 30 finish { ambient 0.25 } }
        }
    rotate x * -15
    rotate z * 30
    rotate x * -15
    rotate y * 30
  }  

// Wood beam

#declare Wood_Beam =
object
  { union
      { box { <-4, 1, -0.75> <4, -1, 0.75> }
        texture
         { pigment
             { wood
               warp
                 { turbulence <0,0.75,1>
                   octaves 3
                   lambda 1.5
                   omega 0.5
                 }
               translate <5,2,0>
               rotate y * 90
               scale 0.1
             }
         }
      }
    rotate x * -15
    rotate z * 30
    rotate x * -15
    rotate y * 30
  }  

/*  Test scene
 */

camera
 { orthographic
   location < 0, 0, -10 >
   up y * 12
   right x * 12
   look_at  < 0, 0, 0 >
 }

light_source { <500, 500, -1000> White * 0.25 }
light_source { <0, 500, -1000> White * 1 }


object { Wood_Beam translate <2, -2.5, -3> }
object { Steel_Beam }
object { Concrete_Beam translate <-2, 2.5, 3> }
