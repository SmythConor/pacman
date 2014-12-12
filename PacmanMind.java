//A Mind for PacmanWorld
import java.util.*;
import java.awt.Point;
import org.w2mind.net.*;


public class PacmanMind implements Mind {
	/** Mind must respond to these methods
	 *	newrun(), endrun(), getactions()
	 */

	/**
	* Mind must respond to this method
	* @return Nothing.
	*/
	public void newrun()  throws RunError {}

	/**
	* Mind must respond to this method
	* @return Nothing.
	*/
	public void endrun()  throws RunError {}

	/**
	* Get an action from the world and respond to it
	* @return Action This returns an action to the World
	*/
	public Action getaction(State state) {
		/* Parse the state sent to this mind from the world */
		String s = state.toString();
		String[] ss = s.split(",");

		Point pacmanPosition;

		Point[] ghosts = new Point[4];

		int x = Integer.parseInt(ss[0]);
		int y = Integer.parseInt(ss[1]);

		pacmanPosition = new Point(x,y);

		int k = 2;
		for(int i = 0; i < 4; i++) {
			x = Integer.parseInt(ss[k]);
			k++;
			y = Integer.parseInt(ss[k]);
			k++;
			ghosts[i] = new Point(x,y);
		}

		int action = 5;

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

		/* Format the string to send back to world */
		String a = String.format("%d,%d", action, nearest);

		return new Action(a);
	}

	/**
	 * This method gets the index of the smallest value in an int array
	 * @return int This returns the index of the smallest value in x
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
