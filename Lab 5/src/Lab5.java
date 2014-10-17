import lejos.nxt.*;
import lejos.util.*;

/**
 * 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public class Lab5 {
	private static final int FREQ = 15;

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		Orientation orienteer;
		Map map = Map.DefaultMap();
		LCDInfo lcd;
		Navigation nav = new Navigation(odo);
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left  ", 0, 0);
			LCD.drawString("Deterministic", 0, 1);
			LCD.drawString("~~~~~~~~~~~~~~~~", 0, 2);
			LCD.drawString("Right >", 0, 3);
			LCD.drawString("Stochastic", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		
		if (buttonChoice == Button.ID_LEFT) {
			orienteer = new DeterministicOrientation(map, odo);
		} else {
			orienteer = new DeterministicOrientation(map, odo);
		}
		lcd = new LCDInfo(odo, orienteer);
		orienteer.orienteer(nav);
		nav.travelToTile(new int [] {3, 3}, map);
		nav.turnTo(0);
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
