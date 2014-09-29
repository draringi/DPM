import lejos.nxt.*;
import lejos.util.*;

public class Lab3 {
	private static final int FREQ = 10;

	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		Navigator nav = new Navigator();
		Timer t = new Timer(FREQ, nav);

		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Do    | Do  ", 0, 2);
			LCD.drawString("Stuff  | Noth   ", 0, 3);
			LCD.drawString("       | ing ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			LCD.clear();
			LCD.drawString("Starting Timer", 0, 0);
			Sound.beep();
			//try{
				t.start();
				Sound.beep();
				nav.travelTo(60, 30);
				Sound.beep();
				while(nav.isNavigating());
				nav.travelTo(30, 30);
				while(nav.isNavigating());
				nav.travelTo(30, 60);
				while(nav.isNavigating());
				nav.travelTo(60, 30);
				while(nav.isNavigating());
				t.stop();
			//} catch (Exception e) {
			//	LCD.drawString(e.getMessage(), 0, 1);
			//	while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			//	System.exit(0);
			//}
		}
		
		
	}

}
