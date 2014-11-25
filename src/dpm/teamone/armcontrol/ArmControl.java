package dpm.teamone.armcontrol;

/**
 * ArmControl is the Main control class and Constant storage class for the Truck
 * Brick
 * 
 * @author Michael Williams
 * 
 */
public class ArmControl {

	/**
	 * Main thread of the truck block. This block handles picking up and
	 * dropping blocks, and displaying stuff on the LCD screen.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		DriverComms communications = new DriverComms();
		while (true) {
			communications.waitForSignal();
			try {
				Thread.sleep(100); // Only poll 10 times a second
			} catch (Exception e) {

			}
		}

	}

}