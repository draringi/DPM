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
 * The ControlComms handles communication with the user,
 * receiving the map number and drop-off point 
 * sending positional data
 *
 * @author Michael Williams
 *
 */
public class ControlComms {
	//
	// private static final int BUFFER_SIZE = 64;
	//
	 private int mapBuffer[];
	// private Object lock;
	// private Object runLock;
	// private BitSet mapSet;
	// BTConnection connection;
	//boolean parse;

	//
	protected ControlComms() {
		 this.mapBuffer = new int[DriverRobot.MAP_DATA_LENGTH];
	}

	//
	// /**
	// * Provides the map data received from the C&C server
	// *
	// * @param mapData
	// * Array long enough to store map data, in which the map data is
	// * to be entered
	// * @see dpm.teamone.driver.DriverRobot#MAP_DATA_LENGTH
	// */
	protected int[] getMapData() {
		byte option = 1;
		boolean wait = true;
		int buttonPressed;
		LCD.clear();
		LCD.drawString("Enter Map Number:", 0, 0);
		while(wait){
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch(buttonPressed){
			case Button.ID_RIGHT:
				option++;
				if(option > 6){
					option = 1;
				}
				break;
			case Button.ID_LEFT:
				option--;
				if(option < 1){
					option = 6;
				}
				break;
			case Button.ID_ENTER:
				mapBuffer[DriverRobot.MAP_DATA_MAP] = option;
				wait = false;
				break;
			}
		}
		wait = true;
		option = -1;
		LCD.clear();
		LCD.drawString("Enter X drop off:", 0, 0);
		while(wait){
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch(buttonPressed){
			case Button.ID_RIGHT:
				option++;
				break;
			case Button.ID_LEFT:
				option--;
				break;
			case Button.ID_ENTER:
				mapBuffer[DriverRobot.MAP_DATA_DROP_X] = option + 1;
				wait = false;
				break;
			}
		}
		wait = true;
		option = -1;
		LCD.clear();
		LCD.drawString("Enter Y drop off:", 0, 0);
		while(wait){
			LCD.clear(1);
			LCD.drawInt(option, 0, 1);
			buttonPressed = Button.waitForAnyPress();
			switch(buttonPressed){
			case Button.ID_RIGHT:
				option++;
				break;
			case Button.ID_LEFT:
				option--;
				break;
			case Button.ID_ENTER:
				mapBuffer[DriverRobot.MAP_DATA_DROP_Y] = option + 1;
				wait = false;
				break;
			}
		}
		LCD.clear();
		return mapBuffer;
	}
}