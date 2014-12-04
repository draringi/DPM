package dpm.teamone.driver.communications;

import lejos.nxt.Button;
import lejos.nxt.LCD;
//
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.BitSet;
//
//import lejos.nxt.comm.BTConnection;
//import lejos.nxt.comm.Bluetooth;
//import lejos.nxt.comm.NXTConnection;
import dpm.teamone.driver.DriverRobot;

//
/**
 * The ControlComms handles communication with the user, receiving the map
 * number and drop-off point sending positional data
 *
 * @author Michael Williams
 *
 */
public class ControlComms {
	private int mapBuffer[];

	protected ControlComms() {
		this.mapBuffer = new int[DriverRobot.MAP_DATA_LENGTH];
	}

	/**
	 * Waits for user input to set the map, and then the x and y location of the
	 * drop off zone.
	 * 
	 * @return Map data array
	 */
	protected int[] getMapData() {
		byte option = 1;
		boolean wait = true;
		int buttonPressed;
		LCD.clear();
		LCD.drawString("Enter Map Number:", 0, 0);
		while (wait) {
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch (buttonPressed) {
			case Button.ID_RIGHT:
				option++;
				if (option > 6) {
					option = 1;
				}
				break;
			case Button.ID_LEFT:
				option--;
				if (option < 1) {
					option = 6;
				}
				break;
			case Button.ID_ENTER:
				this.mapBuffer[DriverRobot.MAP_DATA_MAP] = option;
				wait = false;
				break;
			}
		}
		wait = true;
		option = -1;
		LCD.clear();
		LCD.drawString("Enter X drop off:", 0, 0);
		while (wait) {
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch (buttonPressed) {
			case Button.ID_RIGHT:
				option++;
				break;
			case Button.ID_LEFT:
				option--;
				break;
			case Button.ID_ENTER:
				this.mapBuffer[DriverRobot.MAP_DATA_DROP_X] = option + 1;
				wait = false;
				break;
			}
		}
		wait = true;
		option = -1;
		LCD.clear();
		LCD.drawString("Enter Y drop off:", 0, 0);
		while (wait) {
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch (buttonPressed) {
			case Button.ID_RIGHT:
				option++;
				break;
			case Button.ID_LEFT:
				option--;
				break;
			case Button.ID_ENTER:
				this.mapBuffer[DriverRobot.MAP_DATA_DROP_Y] = option + 1;
				wait = false;
				break;
			}
		}
		LCD.clear();
		return this.mapBuffer;
	}
}