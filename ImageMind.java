//A Mind for ImageWorld
//This mind implements the logic for the cat in ImageWorld
import java.util.*;
import java.awt.Point;
import org.w2mind.net.*;


public class ImageMind implements Mind {
	//====== Mind must respond to these methods: ==========================================================
	//  newrun(), endrun()
	//  getaction()
	//======================================================================================================

	public void newrun()  throws RunError{}

	public void endrun()  throws RunError{}

	public Action getaction(State state) {
		//Parse the state sent to this mind from the world

		String s = state.toString();
		String[] x = s.split(",");			// parsed into x[0], x[1], ...

		Point pacmanPosition = new Point();

		Point redGhostPosition = new Point();
		Point blueGhostPosition = new Point();
		Point yellowGhostPosition = new Point();
		Point greenGhostPosition = new Point();

		redGhostPosition.x = Integer.parseInt(x[0]);
		redGhostPosition.y = Integer.parseInt(x[1]);

		//blueGhostPosition.x = Integer.parseInt(x[2]);
		//blueGhostPosition.y = Integer.parseInt(x[3]);

		//yellowGhostPosition.x = Integer.parseInt(x[4]);
		//yellowGhostPosition.y = Integer.parseInt(x[5]);

		//greenGhostPosition.x = Integer.parseInt(x[6]);
		//greenGhostPosition.y = Integer.parseInt(x[7]);


		// Generate non-random action.
		// This ignores wraparound.
		// You could easily make a better Mind that uses wraparound.

		int action;

		//Get the distance squared in the x and y directions
		int distanceX = 19 - redGhostPosition.x;
		int distanceY = 19 - redGhostPosition.y;

		//Do we want to move in the x or y direction
		//we move in which ever distance is greatest
		if(Math.abs(distanceX) > Math.abs(distanceY)) {
			//We move in the x direction

			//Now decide which way to move
			if(distanceX < 0) {
				//mouse is to the right of cat
				action = ImageWorld.ACTION_RIGHT;
			} else {
				//mouse is to the left or in line of cat
				action = ImageWorld.ACTION_LEFT;
			}
		} else {
			//We move in the y direction

			//Now decide which way to move
			if(distanceY < 0) {
				//mouse is below cat
				action = ImageWorld.ACTION_DOWN;
			} else {
				//mouse is to the left or in line of cat
				action = ImageWorld.ACTION_UP;
			}
		}

		//Return Action as simply one field, but you could potentially provide extra fields
		String a = String.format("%d", action);

		return new Action(a);
	}
}
