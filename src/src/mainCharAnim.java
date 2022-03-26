package src;

import src.game2D.Animation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class mainCharAnim extends JFrame
{

    private Animation anim;

    public Animation loadImages()
    {

        // Create animation
        anim = new Animation();
        anim.loadAnimationFromSheet("src/images/spritesheet (2).png" , 1 , 18 , 75);

        return anim;

    }

    private Image loadImage(String fileName)
    {
        return new ImageIcon(fileName).getImage();
    }


    public void go()
    {
        setSize(800,600);
        setVisible(true);
        loadImages();
        animationLoop();
    }


    public void animationLoop()
    {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        // We are going to use an image buffer to make the draw process more efficient
        // This buffer will be the same size as the screen.
        BufferedImage buffer;
        buffer = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_RGB);
        // We can get a virtual graphics object from our buffer which we can draw to
        Graphics2D bg = (Graphics2D)buffer.createGraphics();

        while (true)
        {
            long elapsedTime = System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

            // update animation
            anim.update(elapsedTime);

            // To avoid flickering, draw to an image buffer first,
            draw(bg);

            // Now draw the contents of this image buffer on the screen.
            Graphics g = getGraphics();
            g.drawImage(buffer,0,0,null);
            g.dispose();

            // take a nap
            try { Thread.sleep(20); } catch (InterruptedException ex) { }
        }

    }


    public void draw(Graphics g)
    {
        // draw image
        g.drawImage(anim.getImage(), 50, 50, null);
    }

}
