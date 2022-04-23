package src.game2D;

public class mainChar extends Sprite{

    boolean stillDashing = true;
    long total = 0;
    float dashVelocity;

    /**
     * Creates a new Sprite object with the specified Animation.
     *
     * @param anim The animation to use for the sprite.
     */
    public mainChar(Animation anim) {
        super(anim);
    }

    public boolean dash(long elapsed , float dashVelocity)
        {

            total += elapsed;

            if(total < 300)
                {
                    setVelocityX(dashVelocity * 3);
                    stillDashing = true;
                }
            else
                {
                    setVelocityX(dashVelocity);
                    stillDashing = false;
                    total = 0;
                }

            return stillDashing;

        }

        public void collision(){



        }

}
