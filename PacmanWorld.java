//This is a 2-dimensional version of Mark Humphrys'
//1-dimensional "Cops and Robbers" world with images.

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

//The aim of this code is to give you a basis on which to build
//your own 2-dimensional worlds.

public class PacmanWorld extends AbstractWorld {
	public static final int GRID_SIZE = 20;		//The dimensions of the grid in 2 dimensions

	/* Pacman position */
	protected Point pacmanPosition;

	/* Ghost positions */
	protected Point redGhost;
	protected Point blueGhost;
	protected Point yellowGhost;
	protected Point greenGhost;

	protected static final int MAX_STEPS = 20;	//The maximum number of steps in a run.

	protected int caught;		//The number of times the mouse was caught
	/* Don't think I need this */
	//protected int numTimesMouseCaughtByCat;		//The number of times the mouse was caught by a cat move

	List<String> scoreColumnNames;			//Headers for the score fields

	protected int timeStep;				//The current time step of the simulation

	//List of possible actions performed
	public static final int ACTION_LEFT	= 0;
	public static final int ACTION_RIGHT = 1;
	public static final int ACTION_UP	= 2;
	public static final int ACTION_DOWN	= 3;
	public static final int NO_ACTIONS = 4;
	/* Don't think I need this */
	//public static final int STAY_STILL = 4;
	//public static final int NO_ACTIONS = 5;


	/* Set up image support */
	String SUPPORT_DIR = "images";
	String IMG_PACMAN = SUPPORT_DIR + "pacman.jpg";
	String IMG_RED_GHOST = SUPPORT_DIR + "redghost.png";
	String IMG_BLUE_GHOST = SUPPORT_DIR + "blueghost.gif";
	String IMG_YELLOW_GHOST = SUPPORT_DIR + "yellieghost.png";
	String IMG_GREEN_GHOST = SUPPORT_DIR + "greenghost.png";


	//transient - don't serialise these:
	//The data contained in these classes is generated at run time and should not be persisted.
	private transient ArrayList<BufferedImage> buffer;
	
	/* Set up input streams */
	private transient InputStream pacmanStream = null;
	private transient InputStream caughtStream = null;

	private transient	InputStream redGhostStream = null;
	private transient	InputStream blueGhostStream = null;
	private transient	InputStream yellowGhostStream = null;
	private transient	InputStream greenGhostStream = null;

	/* Set up buffers for images */
  private transient BufferedImage pacmanImg;
	private transient BufferedImage caughtImg;

	private transient BufferedImage redGhostImg;
	private transient BufferedImage blueGhostImg;
	private transient BufferedImage yellowGhostImg;
	private transient BufferedImage greenGhostImg;

	/* The width and height of an image */
	int imgwidth;
	int imgheight;

	/* Return a random position on the grid */
	protected Point randomPosition() {
		Random r = new Random();

		return new Point(r.nextInt(GRID_SIZE),r.nextInt(GRID_SIZE));
	}

	/* Initialise pacman and ghost positions on the grid */
	protected void initPos() {
		redGhost = new Point(1,1);
		blueGhost = new Point(19,1);
		yellowGhost = new Point(1,19);
		greenGhost = new Point(19,19);
	}

	/* Generate a random action */
	private int randomAction() {
		Random r = new Random();

		return (r.nextInt(NO_ACTIONS));
	}

	/* Move from position */
	private void move(Point startPos, int direction) {
		if(direction == ACTION_LEFT)	{
			startPos.x = ((startPos.x - 1 + GRID_SIZE) % GRID_SIZE);
		} if(direction == ACTION_RIGHT)	{
			startPos.x = ((startPos.x + 1 + GRID_SIZE) % GRID_SIZE);
		} if(direction == ACTION_UP) {
			startPos.y = ((startPos.y - 1 + GRID_SIZE) % GRID_SIZE);
		} if(direction == ACTION_DOWN) {
			startPos.y = ((startPos.y + 1 + GRID_SIZE) % GRID_SIZE);
		}
	}

	/* Check if run finished */
	private boolean runFinished() {
		return timeStep >= MAX_STEPS;
	}

	/* Set up buffer to hold images from disk */
	private void initImages()	{
		if(imagesDesired) {
			/* reinitialise the buffer */
			buffer = new ArrayList<>();

			/* This block is only executed once (only read from disk once) */
			if(pacmanStream == null) {
				try {
					ImageIO.setUseCache(false);	//Use memory, not disk, for temporary images

					pacmanStream	= getClass().getResourceAsStream(IMG_PACMAN);
					//caughtStream = getClass().getResourceAsStream(IMG_CAUGHT);

					redGhostStream	= getClass().getResourceAsStream(IMG_RED_GHOST);
					blueGhostStream	= getClass().getResourceAsStream(IMG_BLUE_GHOST);
					yellowGhostStream	= getClass().getResourceAsStream(IMG_YELLOW_GHOST);
					greenGhostStream	= getClass().getResourceAsStream(IMG_GREEN_GHOST);


					pacmanImg = javax.imageio.ImageIO.read(pacmanStream);
					//caughtImg	= javax.imageio.ImageIO.read(caughtStream);

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

	/* add image to buffer */
	private void addImage() {
		if(imagesDesired) {
			BufferedImage img = new BufferedImage((imgwidth*GRID_SIZE),(imgheight*GRID_SIZE),BufferedImage.TYPE_INT_RGB);

			//if(catPosition.equals(mousePosition)) {
				//The cat has caught the ,ouse so display the explosion
				//img.createGraphics().drawImage(caughtImg,(imgwidth*catPosition.x),(imgheight*catPosition.y),null);
			//} else {
				//Otherwise just display the cat and mouse images
				img.createGraphics().drawImage(pacmanImg,(imgwidth*pacmanPosition.x),(imgheight*pacmanPosition.y),null);

				img.createGraphics().drawImage(redGhostImg,(imgwidth*redGhost.x),(imgheight*redGhost.y),null);
				img.createGraphics().drawImage(blueGhostImg,(imgwidth*blueGhost.x),(imgheight*blueGhost.y),null);
				img.createGraphics().drawImage(yellowGhostImg,(imgwidth*yellowGhost.x),(imgheight*yellowGhost.y),null);
				img.createGraphics().drawImage(greenGhostImg,(imgwidth*greenGhost.x),(imgheight*greenGhost.y),null);
			//}

			//Add this image to the buffer for this timestep.
			buffer.add(img);
		}
	}

	/**
	* World must respond to these methods: 
	* newrun(), endrun()
	* getstate(), takeaction()
	* getscore(), getimage()
	*/


	public void newrun() throws RunError {
		/* Create points to hold positions */
		redGhost = new Point();
		blueGhost = new Point();
		yellowGhost = new Point();
		greenGhost = new Point();

		//Reset everything
		timeStep = 0;
		caught = 0;

		initPos();

		/* Set up headers for score fields */
		scoreColumnNames = new LinkedList<>();

		scoreColumnNames.add("Caught");
	}

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

	private String ghostsAsString() {
		String x = String.format("%d,%d, %d,%d, %d,%d, %d,%d",
			redGhost.x,redGhost.y, blueGhost.x,blueGhost.y,
			yellowGhost.x, yellowGhost.y, greenGhost.x, greenGhost.y);

		return x;
	}


	//Return the current state of the world
	/* Returns the state of the world */
	public State getstate() throws RunError {
		String x = ghostsAsString();

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

	//Take the action specified
	public State takeaction(Action action) throws RunError {
		//Add any number of images to a list of images for this step.
		//The first image on the list for this step should be the image before we take the action.

		initImages();	//If run with images off, imagesDesired = false and this does nothing.

		addImage();				// image before my move
		// If run with images off, imagesDesired = false and this does nothing.

		String s = action.toString();		// parse the action
		String[] a = s.split(",");		// parsed into a[0], a[1], ...
		int i = Integer.parseInt(a[0]);		// ignore any other fields

		//The action is a message from the mind to tell the cat what to do
		//move(catPosition, i);

		//We want to show the individual movements of the cat and the mouse
		//Add an image to the buffer to show the cat's movement
		//This is the intermediate image, before mouse moves
		addImage();

		//if(catPosition.equals(mousePosition)) {
		//	numTimesMouseCaught++;		//Have already shown the catch action
		//	numTimesMouseCaughtByCat++;	//caught due to cat's action, not mouse's action
		//	initPos();			//Loop around, new image will be shown in next step
		//} else {
			//move the mouse randomly
			move(redGhost, randomAction());

			// addImage(); 			// new image will be shown in next step

		//	if(catPosition.equals(mousePosition)) {
		//		addImage();          		//show the "capture" image
		//		numTimesMouseCaught++;		//caught due to mouse's action
		//		initPos();
		//	}
		//}

		timeStep++;

		if(runFinished()) {
			//There will be no loop round
			addImage();
		}

		//The last timestep of the run shows the final state, and no action can be taken in this state.
		//Whatever is the last image built on the run will be treated as the image for this final state.

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


	//Return the score
	public Score getscore() throws RunError {
		String s = String.format ("%d", caught);

		List<Comparable> values = new LinkedList<>();
		values.add(caught);
		//values.add(numTimesMouseCaughtByCat);

		return new Score(s, runFinished(), scoreColumnNames, values);
	}


	// Return image(s) of World.
	// Image may show more information than State (what the Mind sees).
	// This method actually returns a list of images, i.e. we allow multiple images per timestep.
	// e.g. You move, get one image, the mouse moves, next image, your turn again (this is 2 images per timestep).
	// This list of images should normally be built in takeaction method.
	// The first image on the list for this step should be the image before we take the action on this step.
	public ArrayList<BufferedImage> getimage() throws RunError {
		return buffer;
	}
}
