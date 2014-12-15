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
	public static final int GRID_SIZE = 17;
	public static final int GRID_SIZE_X = GRID_SIZE;
	public static final int GRID_SIZE_Y = GRID_SIZE;

	//8,8
	//1,1 //1,18 //14,1 //14,18
	public static int[][] grid = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
													 			{1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,3,1},
													 			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
													 			{1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1},
													 			{1,0,0,1,1,1,0,0,0,0,0,1,1,1,0,0,1},
													 			{1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1},
													 			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
																{1,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,1},
													 			{1,0,0,0,0,0,0,1,6,1,0,0,0,0,0,0,1},
													 			{1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1},
													 			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
													 			{1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1},
													 			{1,0,0,1,1,1,0,0,0,0,0,1,1,1,0,0,1},
													 			{1,0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,1},
													 			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
																{1,4,0,0,0,0,0,0,0,0,0,0,0,0,0,5,1},
													 			{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};

	public static final int WALL = 1;
	public static final int RED_GHOST = 2;
	public static final int YELLOW_GHOST = 3;
	public static final int BLUE_GHOST = 4;
	public static final int GREEN_GHOST = 5;
	public static final int PACMAN = 6;

	/* Grid boundaries */
	public static final int TOP = GRID_SIZE - GRID_SIZE;
	public static final int LEFT = GRID_SIZE - GRID_SIZE;
	public static final int RIGHT = GRID_SIZE - 1;
	public static final int BOTTOM = GRID_SIZE - 1;

	/**/
	private int posX = 0;
	private int posY = 0;

	/* Pacman position */
	protected Point pacman;

	/* Ghost positions */
	protected Point redGhost;
	protected Point blueGhost;
	protected Point yellowGhost;
	protected Point greenGhost;

	/* Number of steps to run */
	protected static final int MAX_STEPS = 50;

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
	private transient	InputStream wallStream = null;

	private transient	InputStream redGhostStream = null;
	private transient	InputStream blueGhostStream = null;
	private transient	InputStream yellowGhostStream = null;
	private transient	InputStream greenGhostStream = null;

	/* Set up buffers for images */
	private transient BufferedImage pacmanImg;
	private transient BufferedImage wallImg;

	private transient BufferedImage redGhostImg;
	private transient BufferedImage blueGhostImg;
	private transient BufferedImage yellowGhostImg;
	private transient BufferedImage greenGhostImg;

	/* The width and height of an image */
	int imgwidth;
	int imgheight;

	/**
	 * Set up buffer to hold images from disk .
	 * @return Nothing.
	 */
	private void initImages() {
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

	/* Random action methods*/

	/**
	 * This method generates a random point.
	 * @return Point This returns a new point inside the grid.
	 */
	protected Point randomPosition() {
		Random r = new Random();
		int x = 0;
		int y = 0;

		do {
			x = r.nextInt(GRID_SIZE);
			y = r.nextInt(GRID_SIZE);
		} while(x == TOP || x == BOTTOM || y == TOP || y == BOTTOM);

		return new Point(x, y);
	}

	/**
	 * Generate a random action.
	 * @return int This returns random action.
	 */
	private int randomAction() {
		Random r = new Random();

		return (r.nextInt(NO_ACTIONS));
	}

	/* Move ghost methods */

	/**
	 * Move the ghosts
	 * @return Nothing.
	 */
	private void moveGhosts() {
		redGhost = move(redGhost, randomAction());
		yellowGhost = move(yellowGhost, randomAction());
		blueGhost = move(blueGhost, randomAction());
		greenGhost = move(greenGhost, randomAction());
	}

	/**
	 * Checks whether a ghost is caught and repositions if true
	 * @return Nothing.
	 */
	private void checkCaught() {
		if(pacman.equals(redGhost)) {
			caught++;
			redGhost = randomPosition();
			grid[redGhost.x][redGhost.y] = RED_GHOST;
		} else if(pacman.equals(yellowGhost)) {
			caught++;
			yellowGhost = randomPosition();
			grid[yellowGhost.x][yellowGhost.y] = YELLOW_GHOST;
		} else if(pacman.equals(blueGhost)) {
			caught++;
			blueGhost = randomPosition();
			grid[blueGhost.x][blueGhost.y] = BLUE_GHOST;
		} else if(pacman.equals(greenGhost)) {
			caught++;
			greenGhost = randomPosition();
			grid[greenGhost.x][greenGhost.y] = GREEN_GHOST;
		}

		grid[pacman.x][pacman.y] = PACMAN;
	}

	/* General move methods */

	/**
	 * Check is any position going over the boudaries.
	 * @param pos This is the position of an object.
	 * @return boolean This returns whether an object has crossed a boundary.
	 */
	private boolean boundaryCheck(int x, int y) {
		return grid[x][y] > 0 && grid[x][y] < 6;
	}

	private boolean pacmanBoundary(int x, int y) {
		return grid[x][y] == 1 || grid[x][y] == -1;
	}

	/**
	 * Move from position.
	 * @param startPos First parameter position.
	 * @return Nothing.
	 */
	private Point move(Point startPos, int direction) {
		int x = startPos.x;
		int y = startPos.y;
		int temp = grid[x][y];

		if(temp == 6) {
			if(timeStep < 2) {
				grid[x][y] = -1;
				x--;
				grid[x][y] = temp;
			} else {
				if(posX == x && posY == y) {
					grid[x][y] = -1;
					direction = randomAction();
				} else {
					posX = x;
					posY = y;
				}

				if(direction == ACTION_LEFT) { //move left must be y--
					if(pacmanBoundary(x, y - 1)) {
						direction = ACTION_RIGHT;
					} else {
						grid[x][y] = 0;
						y--;
						grid[x][y] = temp;
					}
				} else if(direction == ACTION_RIGHT) { //move right must be y++
					if(pacmanBoundary(x, y + 1)) {
						direction = ACTION_DOWN;
					} else {
						grid[x][y] = 0;
						y++;
						grid[x][y] = temp;
					}
				} else if(direction == ACTION_DOWN) { //move down must be x++
					if(pacmanBoundary(x + 1, y)) {
						direction = ACTION_UP;
					} else {
						grid[x][y] = 0;
						x++;
						grid[x][y] = temp;
					}
				} else if(direction == ACTION_UP) { //move up is  x--
					if(pacmanBoundary(x - 1, y)) {
						return move(startPos, ACTION_LEFT);
					} else {
						grid[x][y] = 0;
						x--;
						grid[x][y] = temp;
					}
				}
			}
		} else {
			if(direction == ACTION_LEFT)	{ //must be y--
				if(boundaryCheck(x, y - 1)) {
					direction = ACTION_RIGHT;
				} else {
					grid[x][y] = 0;
					y--;
					grid[x][y] = temp;
				}
			} else if(direction == ACTION_RIGHT)	{ //must be y++
				if(boundaryCheck(x, y + 1)) {
					direction = ACTION_DOWN;
				} else {
					grid[x][y] = 0;
					y++;
					grid[x][y] = temp;
				}
			} else if(direction == ACTION_DOWN) { //move down must be x++
				if(boundaryCheck(x + 1, y)) {
					direction = ACTION_DOWN;
				} else {
					grid[x][y] = 0;
					x++;
					grid[x][y] = temp;
				}
			} else if(direction == ACTION_UP) { // move up is x--
				if(boundaryCheck(x - 1, y)) {
					return move(startPos, ACTION_LEFT);
				} else {
					grid[x][y] = 0;
					x--;
					grid[x][y] = temp;
				}
			}
		}

		return new Point(x,y);
	}

	/**
	 * World must respond to these methods:
	 * newrun(), endrun()
	 * getstate(), takeaction()
	 * getscore(), getimage()
	 */

	/* Start and finish methods */

	/**
	 * Initialise pacman and ghost positions on the grid.
	 * @return Nothing.
	 */
	protected void initPos() {
		/* Pacman in the middle */
		pacman = new Point(8,8);

		/* Ghosts in each corner */
		redGhost = new Point(TOP + 1, LEFT + 1);
		yellowGhost = new Point(TOP + 1, RIGHT - 1);
		blueGhost = new Point(BOTTOM - 1, LEFT + 1);
		greenGhost = new Point(BOTTOM - 1, RIGHT - 1);
	}

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

		addImage();

		/* Make the move */
		pacman = move(pacman, i);

		/* Check to see if the ghost is caught */
		checkCaught();
		addImage();

		/* Randomly move the ghost and check if caught */
		//redGhost = move(redGhost, 3);
		moveGhosts();
		checkCaught();
		addImage();

		//moveGhosts();
		//checkCaught();
		//addImage();

		//moveGhosts();
		//checkCaught();
		//addImage();

		/* Move onto the next step */
		timeStep++;

		/* Check if the run is finished */
		if(runFinished()) {
			addImage();
		}

		/* Return the state of the world */
		return getstate();
	}

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
	 * Add image to buffer.
	 * @return Nothing.
	 */
	private void addImage() {
		if(imagesDesired) {
			BufferedImage img = new BufferedImage((imgwidth*GRID_SIZE),(imgheight*GRID_SIZE),BufferedImage.TYPE_INT_RGB);

			for(int x = 0; x < GRID_SIZE_X; x++) {
				for(int y = 0; y < GRID_SIZE_Y; y++) {
					Point p = new Point(x,y);
					if(grid[y][x] == 1) {
						img.createGraphics().drawImage(wallImg,(imgwidth*p.x),(imgheight*p.y),null);
					} else if(grid[y][x] == 6) {
						img.createGraphics().drawImage(pacmanImg,(imgwidth*p.x),(imgheight*p.y),null);
					} else if(grid[y][x] == 2) {
						img.createGraphics().drawImage(redGhostImg,(imgwidth*p.x),(imgheight*p.y),null);
					} else if(grid[y][x] == 3) {
						img.createGraphics().drawImage(yellowGhostImg,(imgwidth*p.x),(imgheight*p.y),null);
					} else if(grid[y][x] == 4) {
						img.createGraphics().drawImage(blueGhostImg,(imgwidth*p.x),(imgheight*p.y),null);
					} else if(grid[y][x] == 5) {
						img.createGraphics().drawImage(greenGhostImg,(imgwidth*p.x),(imgheight*p.y),null);
					}
				}
			}

			/* Add image to buffer */
			buffer.add(img);
		}
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

	/**
	 * Check if run finished.
	 * @return boolean This returns true if time is up.
	 */
	private boolean runFinished() {
		return timeStep >= MAX_STEPS;
	}
}
