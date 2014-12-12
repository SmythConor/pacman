/** @author Conor Smyth 12452382
 *  @since 01/12/2014
 */

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.awt.Point;
import org.w2mind.net.*;

//This demo world is simply a 2-dimensional version of Mark
//Humphrys' 1-dimensional "Cops and Robbers" game.
//This time a cat chases a mouse aroun an 8x8 grid.
//Both the cat and the mouse can move up,down,left, or right.
//The movement of the mouse is controlled in logic contained
//within the world. The movement of the cat is controlled via
//the selected brain. The score is the number of times the cat
//catches the mouse. If the cat catches the mouse then everything
//resets to the start position.

/** Grid Size
 * Grid size is dynamic can be anything
 */

/** Pacman
 * Movements of pacman is controlled by the mind
 * Depending on where the ghosts are
 */


/** Ghosts
 * Movement of ghosts is controlled by world
 * Movement is random
 */

public class PacmanWorld extends AbstractWorld {
	/* Dimensions of the grid */
	public static final int GRID_SIZE = 30;

	/* Grid boundaries */
	public static final int TOP = GRID_SIZE - GRID_SIZE;
	public static final int LEFT = GRID_SIZE - GRID_SIZE;
	public static final int RIGHT = GRID_SIZE - 1;
	public static final int BOTTOM = GRID_SIZE - 1;

	/* Wall boundaries */
	public static final int X_LEFT = TOP + 4;
	public static final int Y_LEFT = TOP + 2;

	/* Pacman position */
	protected Point pacman;

	/* Ghost positions */
	protected Point redGhost;
	protected Point blueGhost;
	protected Point yellowGhost;
	protected Point greenGhost;

	/* Wall positions */
	protected Point[] wall = new Point[400];
	int size; //Size of wall array

	/* Number of steps to run */
	protected static final int MAX_STEPS = 100;

	/* Number of lives */
	protected static final int NO_LIVES = 3;

	
	/* Number of times caught */
	protected int caught;

	/* Headers for the score fields */
	List<String> scoreColumnNames;

	/* Keep track of time step */
	protected int timeStep;

	//List of possible actions performed
	public static final int ACTION_LEFT	= 0;
	public static final int ACTION_RIGHT = 1;
	public static final int ACTION_UP	= 2;
	public static final int ACTION_DOWN	= 3;
	public static final int NO_ACTIONS = 4;

	/* Set up image support */
	String SUPPORT_DIR = "images";
	String IMG_PACMAN = SUPPORT_DIR + "/pacman.jpg";
	String IMG_RED_GHOST = SUPPORT_DIR + "/redghost.png";
	String IMG_BLUE_GHOST = SUPPORT_DIR + "/blueghost.gif";
	String IMG_YELLOW_GHOST = SUPPORT_DIR + "/yellieghost.png";
	String IMG_GREEN_GHOST = SUPPORT_DIR + "/greenghost.png";
	String IMG_WALL = SUPPORT_DIR + "/wall.png";

	//transient - don't serialise these:
	//The data contained in these classes is generated at run time and should not be persisted.
	private transient ArrayList<BufferedImage> buffer;

	/* Set up input streams */
	private transient InputStream pacmanStream = null;
	private transient InputStream caughtStream = null;
	private transient	InputStream wallStream = null;

	private transient	InputStream redGhostStream = null;
	private transient	InputStream blueGhostStream = null;
	private transient	InputStream yellowGhostStream = null;
	private transient	InputStream greenGhostStream = null;

	/* Set up buffers for images */
	private transient BufferedImage pacmanImg;
	private transient BufferedImage caughtImg;
	private transient BufferedImage wallImg;

	private transient BufferedImage redGhostImg;
	private transient BufferedImage blueGhostImg;
	private transient BufferedImage yellowGhostImg;
	private transient BufferedImage greenGhostImg;

	/* The width and height of an image */
	int imgwidth;
	int imgheight;

	/**
	* This method generates a random point.
	* @return Point This returns a new point inside the grid.
	*/
	protected Point randomPosition() {
		Random r = new Random();

		return new Point(r.nextInt(GRID_SIZE),r.nextInt(GRID_SIZE));
	}

	/** 
	* Initialise pacman and ghost positions on the grid.
	* @return Nothing.
	*/
	protected void initPos() {
		/* Pacman in the middle */
		pacman = new Point(11,11);

		/* Ghosts in each corner */
		redGhost = new Point(TOP + 1, TOP + 1);
		yellowGhost = new Point(RIGHT - 1, TOP + 1);
		blueGhost = new Point(LEFT + 1, BOTTOM - 1);
		greenGhost = new Point(RIGHT - 1, BOTTOM - 1);

		int x = 0;
		int y = 0;
		size = 0;

		/* Outside wall positions */
		for(x = 0; x < GRID_SIZE; x++) {
			wall[size] = new Point(x,y);
			size++;
		}

		for(y = 1; y < GRID_SIZE; y++) {
			x = LEFT;
			wall[size] = new Point(x,y);
			size++;
			x = RIGHT;
			wall[size] = new Point(x,y);
			size++;
		}

		y = RIGHT;

		for(x = 0; x < GRID_SIZE; x++) {
			wall[size] = new Point(x,y);
			size++;
		}

		x = X_LEFT;
		y = Y_LEFT;

		for(int i = 0; i < 3; i++) {
			wall[size] = new Point(x,y);
			y++;
			size++;
		}

		x = Y_LEFT;
		y = Y_LEFT;

		for(int i = 0; i < 5; i++) {
			wall[size] = new Point(x,y);
			x++;
			size++;
		}

	}

	/**
	* Generate a random action.
	* @return int This returns random action.
	*/
	private int randomAction() {
		Random r = new Random();

		return (r.nextInt(NO_ACTIONS));
	}

	/** 
	* Check is any position going over the boudaries.
	* @param pos This is the position of an object.
	* @return boolean This returns whether an object has crossed a boundary.
	*/
	boolean boundaryCheck(double pos) {
		return (pos == TOP || pos == BOTTOM || pos == LEFT || pos == RIGHT);
	}

	/** 
	* Move from position.
	* @param startPos First parameter position.
	* @return Nothing.
	*/
	private void move(Point startPos, int direction) {
		if(direction == ACTION_LEFT)	{
			if(boundaryCheck((startPos.x - 1))) {
				direction = ACTION_RIGHT;
			} else {
				startPos.x--;
			}
		} else if(direction == ACTION_RIGHT)	{
			if(boundaryCheck(startPos.x + 1)) {
				direction = ACTION_UP;
			} else {
				startPos.x++;
			}
		} else if(direction == ACTION_UP) {
			if(boundaryCheck(startPos.y - 1)) {
				direction = ACTION_DOWN;
			} else {
				startPos.y--;
			}
		} else if(direction == ACTION_DOWN) {
			if(boundaryCheck(startPos.y + 1)) {
				move(startPos, ACTION_LEFT);
			} else {
				startPos.y++;
			}
		}
	}

	/**
	 * Check if run finished.
	 * @return boolean This returns true if time is up.
	 */
	private boolean runFinished() {
		return timeStep >= MAX_STEPS;
	}

	/**
	 * Set up buffer to hold images from disk .
	 * @return Nothing.
	 */
	private void initImages()	{
		if(imagesDesired) {
			/* reinitialise the buffer */
			buffer = new ArrayList<>();

			/* This block is only executed once (only read from disk once) */
			if(pacmanStream == null) {
				try {
					ImageIO.setUseCache(false);	//Use memory, not disk, for temporary images

					pacmanStream	= getClass().getResourceAsStream(IMG_PACMAN);
					wallStream = getClass().getResourceAsStream(IMG_WALL);

					redGhostStream	= getClass().getResourceAsStream(IMG_RED_GHOST);
					blueGhostStream	= getClass().getResourceAsStream(IMG_BLUE_GHOST);
					yellowGhostStream	= getClass().getResourceAsStream(IMG_YELLOW_GHOST);
					greenGhostStream	= getClass().getResourceAsStream(IMG_GREEN_GHOST);

					pacmanImg = javax.imageio.ImageIO.read(pacmanStream);
					wallImg = javax.imageio.ImageIO.read(wallStream);

					redGhostImg = javax.imageio.ImageIO.read(redGhostStream);
					blueGhostImg = javax.imageio.ImageIO.read(blueGhostStream);
					yellowGhostImg = javax.imageio.ImageIO.read(yellowGhostStream);
					greenGhostImg = javax.imageio.ImageIO.read(greenGhostStream);

					//Store the dimensions of jpg covering one square of the grid
					imgwidth = pacmanImg.getWidth();
					imgheight = pacmanImg.getHeight();
				}
				catch(IOException e) {}
			}
		}
	}

	/**
	* Add image to buffer.
	* @return Nothing.
	*/
	private void addImage() {
		if(imagesDesired) {
			BufferedImage img = new BufferedImage((imgwidth*GRID_SIZE),(imgheight*GRID_SIZE),BufferedImage.TYPE_INT_RGB);

			img.createGraphics().drawImage(pacmanImg,(imgwidth*pacman.x),(imgheight*pacman.y),null);

			for(int i = 0; i < size; i++) {
				img.createGraphics().drawImage(wallImg,(imgwidth*wall[i].x),(imgheight*wall[i].y),null);
			}

			img.createGraphics().drawImage(redGhostImg,(imgwidth*redGhost.x),(imgheight*redGhost.y),null);
			img.createGraphics().drawImage(blueGhostImg,(imgwidth*blueGhost.x),(imgheight*blueGhost.y),null);
			img.createGraphics().drawImage(yellowGhostImg,(imgwidth*yellowGhost.x),(imgheight*yellowGhost.y),null);
			img.createGraphics().drawImage(greenGhostImg,(imgwidth*greenGhost.x),(imgheight*greenGhost.y),null);

			/* Add image to buffer */
			buffer.add(img);
		}
	}

	/**
	 * World must respond to these methods:
	 * newrun(), endrun()
	 * getstate(), takeaction()
	 * getscore(), getimage()
	 */

	/**
	* Start a new run.
	* @return Nothing.
	* @exception RunError On run error.
	* @see RunError
	*/
	public void newrun() throws RunError {
		/* Create points to hold positions */
		pacman = new Point();
		redGhost = new Point();
		blueGhost = new Point();
		yellowGhost = new Point();
		greenGhost = new Point();

		//Reset everything
		timeStep = 0;
		caught = 0;

		/* Initialise the postitions of everything */
		initPos();

		/* Set up headers for score fields */
		scoreColumnNames = new LinkedList<>();
		scoreColumnNames.add("Caught");
	}

	/**
	* World must respond to this method.
	* @return Nothing.
	* @exception RunError On run error.
	* @see RunError
	*/
	public void endrun() throws RunError {}

	//====== Definition of state: ===========================================================================
	// State in general:
	//  World.getstate() constructs a string to describe World State.
	//  Pass string to State() constructor.
	//  The format of the string is up to the World author.
	//  Explain it on your World description page so people can write Minds.
	//  Typically, state might be a string of fields separated by commas:
	//   state x = "x1,x2,..,xn"
	//  State may be partial state - this is what the Mind can see, maybe not the whole World.
	//
	// State in this world:
	//  Here, state will be the string:
	//   state s = "cx,cy,mx,my" (position of cat and mouse)
	//======================================================================================================

	/**
	* Formats the positions as a string
	* @return String This returns the positions as a string
	*/
	private String positionsAsString() {
		String x = String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
				pacman.x, pacman.y,
				redGhost.x,redGhost.y, blueGhost.x,blueGhost.y,
				yellowGhost.x, yellowGhost.y, greenGhost.x, greenGhost.y);

		return x;
	}

	/**
	* Returns the state of the world.
	* @return State This returns the state
	* @exception RunError On run error.
	* @see RunError
	*/
	public State getstate() throws RunError {
		String x = positionsAsString();

		return new State(x);
	}

	//====== Definition of action: ============================================================================
	// Action in general:
	//  Mind.getaction() constructs a string to describe the action.
	//  Passes string to Action() constructor.
	//  The format of the string is up to the World author.
	//  Explain it on your World description page so people can write Minds.
	//  Typically, action might be a string of fields separated by commas:
	//   action a = "a1,a2,..,an"
	//
	// Action in cop world:
	//  Here, action will be the string:
	//   action a = "i" (an integer describing how to move)
	//=========================================================================================================


	//====== Extra information in action: =====================================================================
	// Each World should TOLERATE extra information in the action fields.
	//  This extra information can be read by other Minds, but is ignored by World.
	//  This will allow Minds call other Minds and receive additional information (e.g. W-values)
	//  to help them decide what action to send to the World.
	//
	// Here, allow Minds send other information (which the World will ignore):
	//  action a = "i,w1,w2,...,wn"
	//=========================================================================================================

	/**
	* Move the ghosts
	* @return Nothing.
	*/
	private void moveGhosts() {
		move(redGhost, randomAction());
		move(yellowGhost, randomAction());
		move(blueGhost, randomAction());
		move(greenGhost, randomAction());
	}

	/**
	* Checks whether a ghost is caught and repositions if true
	* @return Nothing.
	*/
	private void checkCaught() {
		if(pacman.equals(redGhost)) {
			caught++;
			redGhost = randomPosition();
		} else if(pacman.equals(yellowGhost)) {
			caught++;
			yellowGhost = randomPosition();
		} else if(pacman.equals(blueGhost)) {
			caught++;
			blueGhost = randomPosition();
		} else if(pacman.equals(greenGhost)) {
			caught++;
			greenGhost = randomPosition();
		}
	}

	/**
	* Take the action given by Mind.
	* @return State This returns the state of the world
	* @exception RunError On run error.
	* @see RunError
	*/
	public State takeaction(Action action) throws RunError {
		/* Initialise the images */
		initImages();

		/* Parse the action */
		String s = action.toString();
		String[] a = s.split(",");
		int i = Integer.parseInt(a[0]);

		/* Make the move */
		move(pacman, i);

		/* Check to see if the ghost is caught */
		checkCaught();
		addImage();

		/* Randomly move the ghost and check if caught */
		moveGhosts();
		checkCaught();
		addImage();

		moveGhosts();
		checkCaught();
		addImage();

		moveGhosts();
		checkCaught();
		addImage();

		/* Move onto the next step */
		timeStep++;

		/* Check if the run is finished */
		if(runFinished()) {
			addImage();
		}

		/* Return the state of the world */
		return getstate();
	}

	//====== Definition of score: ==============================================================================================
	// Score in general:
	//  World.getscore() returns the score achieved by the Mind in this World.
	//  The score should consist of separated fields that the scoreboard can sort by.
	//  Explain the score fields on your World description page.
	//  The score as a string would just be the fields separated by commas:
	//   score s = "s1,s2,..,sn"
	//
	// Score in this world:
	//  Here, score will be:
	//   score s = "s1,s2"
	//  s1 = number of times mouse was caught (primary score, larger is better)
	//  s2 = number of times mouse was caught due to cat's action (secondary score, larger is better)
	//==========================================================================================================================

	/** 
	* Return the score.
	* @return Score This is the score of the game.
	* @exception RunError On run error.
	* @see RunError
	*/
	public Score getscore() throws RunError {
		String s = String.format ("%d", caught);

		List<Comparable> values = new LinkedList<>();
		values.add(caught);

		return new Score(s, runFinished(), scoreColumnNames, values);
	}

	/** 
	* Return images of World.
	* @return Nothing.
	* @exception RunError On run error.
	* @see RunError
	*/
	public ArrayList<BufferedImage> getimage() throws RunError {
		return buffer;
	}
}
