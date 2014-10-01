import lejos.nxt.*;
import lejos.util.*;

/**
 * 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public class Lab3 {
	private static final int FREQ = 15;

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		

		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Part  | Part  ", 0, 2);
			LCD.drawString(" One   | Two ", 0, 3);
			LCD.drawString("       |  ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);


		if (buttonChoice == Button.ID_LEFT) {
			// Run part 1
			Navigator nav = new Navigator();
			Timer t = new Timer(FREQ, nav);
			Sound.beep();
			t.start();
			nav.travelTo(30, 60);
			while(nav.isNavigating());
			Sound.beep();
			nav.travelTo(30, 30);
			while(nav.isNavigating());
			Sound.beep();
			nav.travelTo(60, 30);
			while(nav.isNavigating());
			Sound.beep();
			nav.travelTo(0, 60);
			while(nav.isNavigating());
			Sound.beep();
			Sound.beep();
			Sound.beep();
			t.stop();
		} else {
			// Run part 2
			Navigator nav = new Navigator();
			Timer t = new Timer(FREQ, nav);
			Sound.beep();
			t.start();
			nav.travelTo(60, 0);
			while(nav.isNavigating());
			Sound.beep();
			nav.travelTo(0, 60);
			while(nav.isNavigating());
			Sound.beep();
			Sound.beep();
			Sound.beep();
		}
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
