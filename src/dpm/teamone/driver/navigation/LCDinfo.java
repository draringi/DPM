package dpm.teamone.driver.navigation;

import lejos.nxt.LCD;
import dpm.teamone.driver.DriverRobot;

/**
 * Simple Access to the LCD, to show localization data.
 * @author Michael Williams
 *
 */
public class LCDinfo {

	/**
	 * Creates the object,
	 * clearing the screen and setting the string "Localizing..." to the 1st line.
	 */
	protected LCDinfo() {
		LCD.clear();
		LCD.drawString("Localizing...", 0, 0);
	}

	/**
	 * Sets the starting position as determined by localization.
	 * @param pos Starting Position as Location Array.
	 */
	protected void setStartPos(int pos[]) {
		Direction dir = Direction.toDirection(pos[DriverRobot.POS_THETA]);
		this.setStartPos(pos[DriverRobot.POS_X], pos[DriverRobot.POS_Y], dir);
	}

	/**
	 * Sets the starting position as determined by localization.
	 * @param x Starting Location in the x-axis. 
	 * @param y Starting Location in the y-axis.
	 * @param dir Starting Direction.
	 */
	protected void setStartPos(int x, int y, Direction dir) {
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt(x, 3, 0);
		LCD.drawInt(y, 3, 1);
		LCD.drawString(dir.toString(), 3, 2);
	}

}
