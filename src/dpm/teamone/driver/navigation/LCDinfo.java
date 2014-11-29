package dpm.teamone.driver.navigation;

import lejos.nxt.LCD;
import dpm.teamone.driver.DriverRobot;

public class LCDinfo {

	protected LCDinfo() {
		LCD.clear();
		LCD.drawString("Localizing...", 0, 0);
	}

	protected void setStartPos(int pos[]) {
		Direction dir = Direction.toDirection(pos[DriverRobot.POS_THETA]);
		this.setStartPos(pos[DriverRobot.POS_X], pos[DriverRobot.POS_Y], dir);
	}

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
