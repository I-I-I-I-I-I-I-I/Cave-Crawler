package src.game2D;

import java.util.ArrayList;

public class backgroundElement{

    ArrayList<Sprite> layer1 = new ArrayList<>();
    ArrayList<Sprite> layer2 = new ArrayList<>();
    ArrayList<Sprite> layer3 = new ArrayList<>();


    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite.
     */
            public backgroundElement(int layer , Animation...anim )
                {
                    for(int x = 0 ; x < anim.length ; x++)
                        {
                        switch (layer) {
                            case 1:
                                layer1.add(new Sprite(anim[x]));
                                break;
                            case 2:
                                layer2.add(new Sprite(anim[x]));
                            case 3:
                                layer3.add(new Sprite(anim[x]));
                        }
                    }
                }

                //This layer is the furthest back of the scene
                public void layerOne(Sprite player , long elapsed)
                    {

                        for(int x = 0 ; x < layer1.size() ; x++)
                            {
                                layer1.get(x).setVelocityX(player.getVelocityX() / 2);
                                layer1.get(x).update(elapsed);
                            }

                    }
}
