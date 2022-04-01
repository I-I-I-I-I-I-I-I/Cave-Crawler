package src;

import java.awt.*;
import java.awt.event.KeyEvent;


import src.game2D.*;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc. By default GameCore
// will handle the 'Escape' key to quit the game but you should
// override this with your own event handler.

/**
 * @author David Cairns
 *
 */

public class Game extends GameCore
{
	// Useful game constants
	static int screenWidth = 1920;
	static int screenHeight = 1080;

    float 	lift = 0.005f;
    float	gravity = 0.0001f;
    float globalint = 0;
    
    // Game state flags
    boolean flipped = false;

    //Jump shit

    boolean jumping = false;
    boolean canJump = true;

    //Background image importing stuff


    //Loading bar animations
    Animation backgroundAnim;

    // Player Animations
    Animation playerIdleAnim;
    Animation playerWalkingAnim;
    Animation playerJumpingAnim;

    //Sprites
    Sprite player;
    Sprite loadingBar;
    Sprite background;

    TileMap tmap = new TileMap();	// Our tile map, note that we load it in init()
    
    int total = 0;         			// The score will be the total time elapsed since a crash


    /**
	 * The obligatory main method that creates
     * an instance of our class and starts it running
     * 
     * @param args	The list of parameters this program might use (ignored)
     */
    public static void main(String[] args) {

        Game gct = new Game();
        gct.init();
        // Start in windowed mode with the given screen height and width
        gct.run(false,screenWidth,screenHeight);
    }

    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers
     */
    public void init()
    {

        // Load the tile map and print it out so we can check it is valid
        tmap.loadMap("src/maps", "map.txt");
        
        setSize(1920, 1080);
        setVisible(true);

        //Loading bar
        backgroundAnim = new Animation();
        backgroundAnim.addFrame(loadImage("src/maps/background.png") , 1000);

        //Player Animation setup
        playerIdleAnim = new Animation();
        playerWalkingAnim = new Animation();
        playerJumpingAnim = new Animation();

        for(int x = 3 ; x < 20 ; x++){

            playerIdleAnim.addFrame(loadImage("mainChar/Idle/BlueIdle" + x + ".png") , 60);

        }

        for(int x = 3 ; x < 20 ; x++){

            playerWalkingAnim.addFrame(loadImage("mainChar/Walk/BlueWalking" + x + ".png") , 60);

        }

        for(int x = 0 ; x < 8 ; x++){

            playerJumpingAnim.addFrame(loadImage("mainChar/Jump/blueJump" + x + ".png") , 60);

        }



        // Initialise the player with an animation
        player = new Sprite(playerIdleAnim);



        initialiseGame();
      		
        System.out.println(tmap);
    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {
    	      
        player.setX(100);
        player.setY(900);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();
    }
    
    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g)
    {
    	// First work out how much we need to shift the view 
    	// in order to see where the player is.
        int xo = 0;
        int yo = 0;
        g.drawImage(loadImage("src/maps/background.png") , 0 , 0 , null);
//        g.setColor(Color.black);
//        g.fillRect(0, 0 , getWidth() , getHeight());
//        g.drawRect(getWidth() / 3 , (getHeight() - getHeight() / 3) , 500 , 32);

        //Check to see what orientation to draw the player in
        if(flipped == true)
            {
                player.drawTransformed(g);
            }
        else {
            player.draw(g);
        }

             g.setColor(Color.blue);
             g.drawString("Player Y : " + (int) player.getY() , 100 , 100);
             g.drawString("Player Y Velocity : " + player.getVelocityY() , 100 , 120);
             g.drawString("Canjump : " + canJump, 100 , 140);

        g.setColor(Color.red);
        player.drawBoundingBox(g);
        tmap.draw(g , xo, yo);
    }

    /**
     * Update any sprites and check for collisions
     * 
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */    
    public void update(long elapsed)
    {

        while(jumping == true && player.getVelocityY() > -1)
        {
            player.setAnimationSpeed(0.1f);
            player.setVelocityY(player.getVelocityY()- 0.1f);

            jumping = false;
        }

        if(canJump == false)
            {
                player.setAnimation(playerJumpingAnim);
            }
        else if(player.getAnimation() == playerJumpingAnim)
            {
                player.setAnimation(playerIdleAnim);
            }

        // Make adjustments to the speed of the sprite due to gravity
        player.setVelocityY(player.getVelocityY()+(gravity*elapsed));



      	player.setAnimationSpeed(1.0f);

       	
        // Now update the sprites animation and position
        player.update(elapsed);
       
        // Then check for any collisions that may have occurred
        handleScreenEdge(player, tmap, elapsed);
        checkTileCollision(player, tmap);
    }
    
    
    /**
     * Checks and handles collisions with the edge of the screen
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     * @param elapsed	How much time has gone by since the last call
     */
    public void handleScreenEdge(Sprite s, TileMap tmap, long elapsed)
    {
    	// This method just checks if the sprite has gone off the bottom screen.
    	// Ideally you should use tile collision instead of this approach

        if (s.getY() + s.getHeight() > tmap.getPixelHeight())
        {
        	// Put the player back on the map 1 pixel above the bottom
        	s.setY(tmap.getPixelHeight() - s.getHeight() - 1);

        	// and make them bounce
        	s.setVelocityY(0);
        }

        if(s.getX() < 0)
            {
                s.setX(0);

                s.setVelocityY(0);

            }
    }
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {
        long initTime = System.currentTimeMillis();
        long keyPressTime = 0;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) stop();

        if (key == KeyEvent.VK_S) {

            player.setAnimation(playerWalkingAnim);
            player.setScale(-1, 1);

            // Example of playing a sound as a thread
            Sound s = new Sound("sounds/caw.wav");
            s.start();
        }

        if (key == KeyEvent.VK_D) {
            flipped = true;

            player.setScale(1, 1);


            if(player.getVelocityX() < 0.5f)
                {
                    player.setVelocityX(0.5f);
                }
            else if(player.getVelocityX() <= 1){
                player.setVelocityX((player.getVelocityX() + 0.1f));
            }

            player.setAnimation(playerWalkingAnim);
        }

        if (key == KeyEvent.VK_R) {
            player.show();
        }

        if (key == KeyEvent.VK_A) {
            flipped = true;
            player.setScale(-1, 1);

            if(player.getVelocityX() > -0.5f)
            {
                player.setVelocityX(-0.5f);
            }
            else if(player.getVelocityX() >= -1){
                player.setVelocityX((player.getVelocityX() + -0.1f));
            }

            player.setAnimation(playerWalkingAnim);

        }

        if (key == KeyEvent.VK_SPACE) {

                if(canJump == true)
                {
                    canJump = false;
                    jumping = true;
                }
        }
    }

    public boolean boundingBoxCollision(Sprite s1, Sprite s2)
    {
    	return false;   	
    }
    
    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'. Initial functionality is limited...
     * 
     * @param s			The Sprite to check collisions for
     * @param tmap		The tile map to check 
     */

    public void checkTileCollision(Sprite s, TileMap tmap)
    {
    	// Take a note of a sprite's current position
    	float sx = s.getX();
    	float sy = s.getY();
    	
    	// Find out how wide and how tall a tile is
    	float tileWidth = tmap.getTileWidth();
    	float tileHeight = tmap.getTileHeight();
    	
    	// Divide the sprite’s x coordinate by the width of a tile, to get
    	// the number of tiles across the x axis that the sprite is positioned at 
    	int	xtile = (int)(sx / tileWidth);
    	// The same applies to the y coordinate
    	int ytile = (int)(sy / tileHeight);
    	
    	// What tile character is at the top left of the sprite s?
    	char ch = tmap.getTileChar(xtile, ytile);
    	
    	
    	if (ch != '.') // If it's not a dot (empty space), handle it
    	{
    		// Here we just stop the sprite. 
    		s.stop();
            System.out.println("TOP LEFT COLLISION");
    		// You should move the sprite to a position that is not colliding
    	}
        else{

        }
    	
    	// We need to consider the other corners of the sprite
    	// The above looked at the top left position, let's look at the bottom left.
    	xtile = (int)(sx / tileWidth);
    	ytile = (int)((sy + s.getHeight())/ tileHeight);
    	ch = tmap.getTileChar(xtile, ytile);

        if(ch != '.')
            {
                    s.setVelocityY(0);
                    s.setY(tmap.getPixelHeight() - (tmap.getTileHeight() + s.getHeight()));
                    System.out.println("BOTTOM LEFT COLLISION");

                    canJump = true;

                }

        else{
        }
    }

	public void keyReleased(KeyEvent e) { 

		int key = e.getKeyCode();

		// Switch statement instead of lots of ifs...
		// Need to use break to prevent fall through.
		switch (key)
		{
			case KeyEvent.VK_ESCAPE : stop(); break;
            case KeyEvent.VK_D      : player.setVelocityX(0); player.setAnimation(playerIdleAnim);
            case KeyEvent.VK_A      : player.setVelocityX(0); player.setAnimation(playerIdleAnim);
            case KeyEvent.VK_SPACE  : System.out.println("Space released");
			default :  break;
		}
	}
}
