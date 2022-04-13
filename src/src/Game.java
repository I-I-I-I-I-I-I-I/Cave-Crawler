package src;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


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

public class Game extends GameCore implements MouseListener
{
	// Useful game constants
	static int screenWidth = 1920;
	static int screenHeight = 1200;

    float 	lift = 0.005f;
    float	gravity = 0.0003f;

    int xo = 0;
    int yo;
    
    // Game state flags
    boolean flipped = false;
    boolean inMenu = true;
    boolean callOnce = true;

    //Dashing checks
    boolean dash = false;
    float currentXVel = 0;

    //Collision checks
    CollisionType cT;
    char[] coords;
    boolean debugFreeze;

    //Jump checks
    boolean jumping = false;
    boolean canJump = false;

    // Player Animations
    Animation playerIdleAnim;
    Animation playerWalkingAnim;
    Animation playerJumpingAnim;
    Animation playerDashAnim;

    //Player Sprites
    mainChar player;

    //Menu sprites
    Sprite playButton;
    Animation playButtonAnim;
    Sprite quitButton;
    Animation quitButtonAnim;

    ArrayList<Sprite> Clickable = new ArrayList<Sprite>();

    int nOc = 0;


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

            setSize(1920, 980);
            setVisible(true);


            //Player Animation setup
            playerIdleAnim = new Animation();
            playerWalkingAnim = new Animation();
            playerJumpingAnim = new Animation();
            playerDashAnim = new Animation();
            playerJumpingAnim.setLoop(false);

            //Idle load
            for (int x = 3; x < 20; x++) {
                playerIdleAnim.addFrame(loadImage("mainChar/Idle/BlueIdle" + x + ".png"), 60);
            }
            //Walk load
            for (int x = 3; x < 20; x++) {
                playerWalkingAnim.addFrame(loadImage("mainChar/Walk/BlueWalking" + x + ".png"), 60);
            }
            //Jump load
            for (int x = 0; x < 8; x++) {
                playerJumpingAnim.addFrame(loadImage("mainChar/Jump/BlueJumping" + x + ".png"), 60);
            }
            //Dash load
            for (int x = 0; x < 16; x++) {
                playerDashAnim.addFrame(loadImage("mainChar/Jump/Dash2/teseter/DashAgain" + x + ".png"), 19);
            }

            //Menu Initialise
            playButtonAnim = new Animation();
            playButtonAnim.addFrame(loadImage("src/menu/LargeButtons/LargeButtons/PlayButton.png") , 1000);
            playButtonAnim.addFrame(loadImage("src/menu/LargeButtons/LargeButtons/PlayButtonHighlight.png") , 1000);
            playButton = new Sprite(playButtonAnim);

            quitButtonAnim = new Animation();
            quitButtonAnim.addFrame(loadImage("src/menu/LargeButtons/LargeButtons/QuitButton.png") , 1000);
            quitButtonAnim.addFrame(loadImage("src/menu/LargeButtons/LargeButtons/QuitButtonHighlight.png") , 1000);
            quitButton = new Sprite(quitButtonAnim);


            // Initialise the player with an animation
            player = new mainChar(playerIdleAnim);

            initialiseMenu();

    }

    public void initialiseMenu(){

        //Menu Init

        //Adds all Sprites which are clickable, mouse listener will only pay attention to these sprites
        Clickable.add(playButton);
        Clickable.add(quitButton);

        //Set inital positions of menu buttons, all buttons are relative to the topmost button (playButton), it makes moving them easier.
        playButton.setY(298);
        playButton.setX(298);
        quitButton.setY(playButton.getY() + 150);
        quitButton.setX(playButton.getX());

        addMouseListener(this);

    }

    /**
     * You will probably want to put code to restart a game in
     * a separate method so that you can call it to restart
     * the game.
     */
    public void initialiseGame()
    {
        //Player Init
        player.setX(100);
        player.setY(770);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.show();

    }
    
    /**
     * Draw the current state of the game
     */
    public void draw(Graphics2D g)
    {

        /*Not an efficient way of displaying a menu as everything else is still being rendered
        currently mouse listener is only active within the menu active state as that's the only
        time it's really useful in the game, could easily be changed though.
        */
        if(inMenu == true)
        {
            setSize(896 , 896);
            tmap.loadMap("src/menu" , "menu.txt");
            tmap.draw(g, 0, 0);
            try {
                checkMouseEvent(getMousePosition().x, getMousePosition().y, 504);
            }
            catch(NullPointerException exc)
                {
                    System.out.println(System.currentTimeMillis() + "Mouse not on screen");
                }
            playButton.draw(g);
            quitButton.draw(g);
        }
    else {

            if(callOnce == true)
                {
                    initialiseGame();
                    callOnce = false;
                }

            // First work out how much we need to shift the view
            // in order to see where the player is.



            if(player.getX() >= (screenWidth / 2))
                {
                    xo = - ((int)(player.getX() - (screenWidth / 2)));
                }
            else{

                xo = 0;

            }
            int yo = 0;

            setSize(1920, 980);
            tmap.loadMap("src/maps" , "map.txt");
            //Draw The background
            g.drawImage(loadImage("src/maps/background.png"), 0, 0, null);


            //Check to see what orientation to draw the player in
            if (flipped == true) {
                player.drawTransformed(g);
            } else {
                player.draw(g);
            }


            tmap.draw(g, xo, yo);

            //DEBUGGING, Draw debug stats
            g.setColor(Color.blue);
            g.drawString("Player Y : " + (int) player.getY(), 100, 100);
            g.drawString("Player X : " + (int) player.getX() , 100 , 80);
            g.drawString("Player Y Velocity : " + player.getVelocityY(), 100, 120);
            g.drawString("Canjump : " + canJump, 100, 140);
            g.drawString("Dash : " + dash, 100, 160);
            g.drawString("Main char collision direction : " + cT.toString() , 100 , 180);
            for(int x = 0 ; x < coords.length ; x++)
                {
                    g.drawString("Collision coords : " + coords[x] , 100 , 200 + (x * 20));
                }

            g.setColor(Color.red);
            player.drawBoundingBox(g);
        }
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
            player.setVelocityY(player.getVelocityY() - 0.3f);
            jumping = false;
        }

        if(dash == true)
        {

             if(player.dash(elapsed , currentXVel) == false)
                 {
                       player.setAnimation(playerIdleAnim);
                       dash = false;
                 }

             else{
                 player.setScale(0.9f);
                 player.setAnimation(playerDashAnim);
             }

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
            System.out.println("SCREEN EDGE COLLISION DETECTED BOTTOM");
        	// Put the player back on the map 1 pixel above the bottom
        	s.setY(tmap.getPixelHeight() - s.getHeight() - 1);

        	// and make them bounce
        	s.setVelocityY(0);
        }

        if(s.getX() < 0)
            {
                System.out.println("SCREEN EDGE COLLISION DETECTED LEFT SIDE");
                s.setX(0);

                s.setVelocityY(0);

            }

        if(s.getX() > getWidth() - s.getWidth())
            {
                System.out.println("SCREEN EDGE COLLISION DETECTED RIGHT SIDE");
                s.setX(getWidth() - s.getWidth());

                s.setVelocityX(0);

            }
    }
    
    
     
    /**
     * Override of the keyPressed event defined in GameCore to catch our
     * own events
     * 
     *  @param e The event that has been generated
     */
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) stop();

        if (key == KeyEvent.VK_S) {

            player.setAnimation(playerWalkingAnim);
            player.setScale(-1, 1);

            // Example of playing a sound as a thread
            Sound s = new Sound("sounds/caw.wav");
            s.start();
        }

        //This can be activated at any time, the only conditional section is if the
        //character is jumping, the walking animation will not be played in place of the jumping animation
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

            if(jumping == false) {
                player.setAnimation(playerWalkingAnim);
            }
            }

        if (key == KeyEvent.VK_R) {

            debugFreeze = true;

        }

        //Dash
        if(key == KeyEvent.VK_SHIFT)
            {
                currentXVel = player.getVelocityX();
                dash = true;
            }

        //This can be activated at any time, the only conditional section is if the
        //character is jumping, the walking animation will not be played in place of the jumping animation
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

            if(jumping == false) {
                player.setAnimation(playerWalkingAnim);
            }
        }

        if (key == KeyEvent.VK_SPACE) {

                if(canJump == true)
                {
                    player.setAnimation(playerJumpingAnim);
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
    	int	xtile = (int)((sx / tileWidth));
    	// The same applies to the y coordinate
    	int ytile = (int)(sy / tileHeight);

        //SPRITE CORNER COORDS
        int[] topLeft = {(int) s.getX() , (int) s.getY()};
        System.out.println("topLeft : " + topLeft[0] + " " + topLeft[1]);
        int[] topRight = {((int) s.getX() + s.getWidth()) , (int) s.getY()};
        System.out.println("topRight : " + topRight[0] + " " + topRight[1]);
        int[] bottomLeft = {(int) s.getX() , ((int) s.getY() + s.getHeight())};
        System.out.println("bottomLeft : " + bottomLeft[0] + " " + bottomLeft[1]);
        int[] bottomRight = {((int) s.getX() + s.getWidth()) , ((int)s.getY() + s.getHeight())};
        System.out.println("bottomRight : " + bottomRight[0] + " " + bottomRight[1]);
//DEBUG
        if(debugFreeze == true)
            {
                System.out.println("D");
            }

    	// What tile character is at the top left of the sprite s?
    	char ch_topLeft = tmap.getTileChar(topLeft);
    	char ch_topRight = tmap.getTileChar(topRight);
        char ch_bottomLeft = tmap.getTileChar(bottomLeft);
        char ch_bottomRight = tmap.getTileChar(bottomRight);

        coords = new char[]{ch_topLeft, ch_topRight, ch_bottomLeft, ch_bottomRight};

        cT = cT.NoCollision;

        //Check collisions about Top left
    	if (coords[0] != '.') // If it's not a dot (empty space), handle it
    	{
            cT = CollisionType.TopLeft;
                s.setY((tileHeight * (tmap.getMapHeight() - (ytile - 1))));

                canJump = true;

                    if(coords[2] != '.')
                        {
                            cT = CollisionType.Left;
                            System.out.println("LEFT SIDE COLLISION");
                        }

                    if(coords[1] != '.')
                        {
                            cT = CollisionType.Top;
                            System.out.println("TOP COLLISION");
                        }

            s.setVelocityY(-s.getVelocityY());
    	}
        else if(coords[1] != '.')
            {
                cT = CollisionType.TopRight;

                if (coords[3] != '.')
                    {
                    cT = CollisionType.Right;
                        System.out.println("RIGHT SIDE COLLISION");

                        s.setVelocityX(0);
                        s.setX((tmap.getTileXC((topLeft[0] / (int) tileWidth) , (topLeft[1] / (int) tileWidth))) + (tileWidth - s.getWidth()));

                    }
        }
        //Check collision about bottom left
        else if(coords[2] != '.')
            {
                cT = CollisionType.BottomLeft;

                if(coords[3] != '.')
                    {
                        cT = CollisionType.Bottom;
                        System.out.println("BOTTOM COLLISION");
                    }

            }




    	// We need to consider the other corners of the sprite
    	// The above looked at the top left position, let's look at the bottom left.
    	xtile = (int)(sx / tileWidth);
    	ytile = (int)((sy + s.getHeight())/ tileHeight);
    	ch_topLeft = tmap.getTileChar(xtile, ytile);

        if(ch_topLeft != '.') {
                    player.setVelocityY(0);
                    s.setY((tileHeight * tmap.getMapHeight()) - (tileHeight * (tmap.getMapHeight() - ytile)) - player.getHeight());
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

            case KeyEvent.VK_A      : {
                                        player.setVelocityX(0);
                                            if(jumping == false)
                                            {
                                                player.setAnimation(playerIdleAnim);
                                            }
                                        }

            case KeyEvent.VK_SPACE  : System.out.println("Space released");
			default :  break;
		}
	}


    @Override
    public void mouseClicked(MouseEvent e) {

        int mouseClickX = getMousePosition().x;
        int mouseClickY = getMousePosition().y;
        nOc = nOc + 1;
        Sprite current = null;

        try {
            checkMouseEvent(mouseClickX, mouseClickY , 500);

        } catch (NullPointerException exc) {
            System.out.println("No sprite clicked!");
        }

        if (current != null) {

            System.out.println(current.toString() + " clicked");

        }


        System.out.println("Click at X : " + mouseClickX + " Y : " + mouseClickY + " Number of clicks: " + nOc);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    private void checkMouseEvent(int clickX , int clickY , int type){

        for(int x = 0 ; x < Clickable.size() ; x++) {

            Sprite current = Clickable.get(x);
            float currentX = current.getX();
            float currentY = current.getY();

            //Check within X and Y bounds of current sprite in clickable list
            if (clickX >= currentX && clickX <= (currentX + current.getWidth()) && clickY >= currentY && clickY <= (currentY + current.getHeight())) {

                mouseEvents(current, type);

            }

                else{

                    mouseEvents(current , 505);

                }

        }

    }


    /**
     * Tells a specific sprite what to do when it is clicked.
     * NEVER CALL DIRECTLY --> ONLY CALL THROUGH "checkClickEvents()" method
     * @param selected The reference of the sprite to be checked
     * @param type The type of mouse event that occurred
     *
     * MOUSE EVENTS -> Click = 500
     *                 Enter = 504
     *                 Exit = 505
     */
    private void mouseEvents (Sprite selected , int type)
        {
            //Play button selected
            if(selected == playButton)
                {
                    switch(type) {
                        case 500 : {
                                inMenu = false;
                                break;
                                   }
                        case 504 : {
                                    playButton.setAnimationFrame(1);
                                    break;
                                    }
                        case 505 : {
                                    playButton.setAnimationFrame(0);
                                    break;
                                   }
                    }
                }

            //Quit button selected
            if(selected == quitButton)
            {
                switch(type) {
                    case 500 : {
                        stop();
                        break;
                    }
                    case 504 : {
                        quitButton.setAnimationFrame(1);
                        break;
                    }
                    case 505 : {
                        quitButton.setAnimationFrame(0);
                        break;
                    }
                }
            }



        }

        public enum CollisionType {

            TopLeft,
            TopRight,
            BottomLeft,
            BottomRight,
            Top,
            Right,
            Bottom,
            Left,
            NoCollision

        }

}
