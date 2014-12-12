//A Mind for PacmanWorld
import java.util.*;
import java.awt.Point;
import org.w2mind.net.*;


public class PacmanMind implements Mind {
	/** Mind must respond to these methods
	 *	newrun(), endrun(), getactions()
	 */

	public void newrun()  throws RunError {}

	public void endrun()  throws RunError {}

	public Action getaction(State state) {
		/* Parse the state sent to this mind from the world */
		String s = state.toString();
		String[] ss = s.split(",");

		Point pacmanPosition;

		Point[] ghosts = new Point[4];

		//Point redGhostPosition = new Point();
		//Point blueGhostPosition = new Point();
		//Point yellowGhostPosition = new Point();
		//Point greenGhostPosition = new Point();

		int x = Integer.parseInt(ss[0]);
		int y = Integer.parseInt(ss[1]);

		pacmanPosition = new Point(x,y);

		//pacmanPosition.x = Integer.parseInt(x[0]);
		//pacmanPosition.y = Integer.parseInt(x[1]);

		int k = 2;
		for(int i = 0; i < 4; i++) {
			x = Integer.parseInt(ss[k]);
			k++;
			y = Integer.parseInt(ss[k]);
			k++;
			ghosts[i] = new Point(x,y);
		}

		//redGhostPosition.x = Integer.parseInt(x[2]);
		//redGhostPosition.y = Integer.parseInt(x[3]);

		//blueGhostPosition.x = Integer.parseInt(x[4]);
		//blueGhostPosition.y = Integer.parseInt(x[5]);

		//yellowGhostPosition.x = Integer.parseInt(x[6]);
		//yellowGhostPosition.y = Integer.parseInt(x[7]);

		//greenGhostPosition.x = Integer.parseInt(x[8]);
		//greenGhostPosition.y = Integer.parseInt(x[9]);


		// Generate non-random action.
		// This ignores wraparound.
		// You could easily make a better Mind that uses wraparound.

		int action = 5;

		//Get the distance squared in the x and y directions
		int[] distances = new int[4];
		int iter = 0;
		for(int i = 0; i < 4; i++) {
			distances[i] = (int) (pacmanPosition.distance(ghosts[i]));
		}

		int nearest = indexOfSmallest(distances);

		int distanceX = pacmanPosition.x - ghosts[nearest].x;
		int distanceY = pacmanPosition.y - ghosts[nearest].y;

		if(Math.abs(distanceX) > Math.abs(distanceY)) {
			//We move in the x direction

			//Now decide which way to move			
			if(distanceX < 0) {
				//mouse is to the right of cat
				action = PacmanWorld.ACTION_RIGHT;
			} else {
				//mouse is to the left or in line of cat
				action = PacmanWorld.ACTION_LEFT;
			}
		} else {
			//We move in the y direction

			//Now decide which way to move			
			if(distanceY < 0) {
				//mouse is below cat
				action = PacmanWorld.ACTION_DOWN;
			} else {
				//mouse is to the left or in line of cat
				action = PacmanWorld.ACTION_UP;
			}
		}

		//Return Action as simply one field, but you could potentially provide extra fields
		String a = String.format("%d,%d", action, nearest);

		return new Action(a);
	}

	/**
	 * 0  -1     -2   -3 
	 * red-yellow-blue-green
	 */
	private int indexOfSmallest(int[] x) {
		int small = 100;
		int temp = 5;

		for(int i = 0; i < x.length; i++) {
			if(x[i] < small) {
				small = x[i];
				temp = i;
			}
		}

		return temp;
	}
}
