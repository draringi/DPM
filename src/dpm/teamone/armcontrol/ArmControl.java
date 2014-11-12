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
	public static void main() {
		DriverComms communications = new DriverComms();
		while(true){
			communications.waitForSignal();
			try{
				Thread.sleep(1000); //Only poll once a second
			} catch (Exception e){
				
			}
		}
		
	}


}