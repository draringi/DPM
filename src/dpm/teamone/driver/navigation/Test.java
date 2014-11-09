// Test file for the navigation using a map without obstacles
// Since localisation has not yet been completed the robot's
// initial location is considered to be (0,0) facing north.

package dpm.teamone.driver.navigation;

import dpm.teamone.driver.maps.GridMap;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.navigation.Pose;

public class Test {

	public static void main(String[] args) {

		int buttonChoice;

		do {
			// clear the display
			LCD.clear();

			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Test  | Test  ", 0, 2);
			LCD.drawString("navig  | localisation   ", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			GridMap grid = new GridMap(4, 4);
			NavigationController nav = new NavigationController(grid);
			Pose initialLocation = new Pose(0, 0, 90); // Replace by
														// localisation after
			nav.setPose(initialLocation);
			nav.driveToGrid(50, 50);
		} else {
			// Implement testing for localisation
			// Requires a test lineMap
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}
}
