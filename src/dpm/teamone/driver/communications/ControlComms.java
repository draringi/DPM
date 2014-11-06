package dpm.teamone.driver.communications;

import java.util.BitSet;

import dpm.teamone.driver.DriverRobot;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.NXTConnection;

/**
 * The ControlComms module talks with the C&C server,
 * Receiving map data, and sending positional data
 * @author Michael Williams
 *
 */
public class ControlComms {

	private int mapBuffer[];
	private Object lock;
	private BitSet mapSet;
	BTConnection connection;
	
	protected ControlComms() {
		this.mapBuffer = new int[DriverRobot.MAP_DATA_LENGTH];
		this.mapSet = new BitSet(DriverRobot.MAP_DATA_LENGTH);
		this.lock = new Object();
	}

	/**
	 * Provides the map data received from the C&C server
	 * @param mapData Array long enough to store map data, in which the map data is to be entered  
	 * @see dpm.teamone.driver.DriverRobot#MAP_DATA_LENGTH
	 */
	protected void getMapData(int[] mapData) {
		while(!mapReady()){
			try{
				Thread.sleep(50);
			} catch (Exception e) {
				
			}
		}
		synchronized(lock){
			for(int i = 0; i < DriverRobot.MAP_DATA_LENGTH; i++){
				mapData[i] = mapBuffer[i];
			}
		}
	}
	
	protected boolean mapReady(){
		boolean result;
		synchronized(lock){
			result = (mapSet.cardinality() == DriverRobot.MAP_DATA_LENGTH);
		}
		return result;
	}

	/**
	 * Sends the provided co-ordinates to the C&C server
	 * @param x Grid location on the x-axis 
	 * @param y Grid location on the y-axis
	 */
	protected void sendPos(int x, int y) {
	}

	/**
	 * Starts the connection with the C&C server 
	 */
	protected void setup() {
		if(!Bluetooth.getPower()){
			Bluetooth.setPower(true);
		}
		connection = Bluetooth.waitForConnection(0, NXTConnection.RAW);
	}

}