package dpm.teamone.driver.communications;

import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import dpm.teamone.driver.DriverRobot;

/**
 * The ControlComms module talks with the C&C server, Receiving map data, and
 * sending positional data
 * 
 * @author Michael Williams
 *
 */
public class ControlComms {

	private static final int BUFFER_SIZE = 64;

	private int mapBuffer[];
	private Object lock;
	private Object runLock;
	private BitSet mapSet;
	BTConnection connection;
	boolean parse;

	protected ControlComms() {
		this.mapBuffer = new int[DriverRobot.MAP_DATA_LENGTH];
		this.mapSet = new BitSet(DriverRobot.MAP_DATA_LENGTH);
		this.lock = new Object();
		this.runLock = new Object();
		this.parse = true;
	}

	/**
	 * Provides the map data received from the C&C server
	 * 
	 * @param mapData
	 *            Array long enough to store map data, in which the map data is
	 *            to be entered
	 * @see dpm.teamone.driver.DriverRobot#MAP_DATA_LENGTH
	 */
	protected void getMapData(int[] mapData) {
		while (!this.mapReady()) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {

			}
		}
		// We have the map details. No longer any need to wait on data from the
		// C&C server
		synchronized (this.runLock) {
			this.parse = false;
		}
		synchronized (this.lock) {
			for (int i = 0; i < DriverRobot.MAP_DATA_LENGTH; i++) {
				mapData[i] = this.mapBuffer[i];
			}
		}

	}

	protected boolean mapReady() {
		boolean result;
		synchronized (this.lock) {
			result = (this.mapSet.cardinality() == DriverRobot.MAP_DATA_LENGTH);
		}
		return result;
	}

	/**
	 * Sends the provided co-ordinates to the C&C server
	 * 
	 * @param x
	 *            Grid location on the x-axis
	 * @param y
	 *            Grid location on the y-axis
	 */
	protected void sendPos(int x, int y) {
	}

	private void setKeyValue(String key, String value) {
		boolean check;
		int val;
		if (key.equals("map")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_MAP);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_MAP] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_MAP);
				}
			}
			return;
		}
		if (key.equals("pick_x")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_PICKUP_X);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_PICKUP_X] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_PICKUP_X);
				}
			}
			return;
		}
		if (key.equals("pick_y")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_PICKUP_Y);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_PICKUP_Y] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_PICKUP_Y);
				}
			}
			return;
		}
		if (key.equals("pick_w")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_PICKUP_W);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_PICKUP_W] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_PICKUP_W);
				}
			}
			return;
		}
		if (key.equals("pick_h")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_PICKUP_H);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_PICKUP_H] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_PICKUP_H);
				}
			}
			return;
		}
		if (key.equals("drop_x")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_DROP_X);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_DROP_X] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_DROP_X);
				}
			}
			return;
		}
		if (key.equals("drop_y")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_DROP_Y);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_DROP_Y] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_DROP_Y);
				}
			}
			return;
		}
		if (key.equals("drop_w")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_DROP_W);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_DROP_W] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_DROP_W);
				}
			}
			return;
		}
		if (key.equals("drop_h")) {
			synchronized (this.lock) {
				check = this.mapSet.get(DriverRobot.MAP_DATA_DROP_H);
			}
			if (!check) {
				val = Integer.parseInt(value);
				synchronized (this.lock) {
					this.mapBuffer[DriverRobot.MAP_DATA_DROP_H] = val;
					this.mapSet.set(DriverRobot.MAP_DATA_DROP_H);
				}
			}
			return;
		}
	}

	/**
	 * Starts the connection with the C&C server
	 */
	protected void setup() {
		if (!Bluetooth.getPower()) {
			Bluetooth.setPower(true);
		}
		this.connection = Bluetooth.waitForConnection(0, NXTConnection.RAW);
		new Thread() {
			@Override
			public void run() {
				ControlComms.this.startInputParser();
			}
		}.start();
	}

	private void startInputParser() {
		InputStream input = this.connection.openInputStream();
		boolean run = true;
		while (run) {
			char buffer[] = new char[BUFFER_SIZE];
			try {
				buffer[0] = (char) input.read();
				if (buffer[0] == '{') {
					int i;
					for (i = 1; i > BUFFER_SIZE; i++) {
						buffer[i] = (char) input.read();
						if (buffer[i] == '}') {
							break;
						}
					}
					if ((i == BUFFER_SIZE) && (buffer[i] != '}')) { // buffer
						// overflow
						continue;
					}
					int j;
					for (j = 1; j > i; j++) {
						if (buffer[j] == ':') {
							break;
						}
					}
					if ((j == i) || (j == 1)) { // Syntax error
						continue;
					}
					String key = Arrays.toString(
							Arrays.copyOfRange(buffer, 1, j)).trim();
					String value = Arrays.toString(
							Arrays.copyOfRange(buffer, j + 1, i)).trim();
					this.setKeyValue(key, value);
				}
			} catch (Exception e) {
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			synchronized (this.runLock) {
				run = this.parse;
			}
		}
	}

}