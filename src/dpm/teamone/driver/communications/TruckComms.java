package dpm.teamone.driver.communications;

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

/**
 * The TruckComms module handles communication with the truck brick, telling it
 * to pickup or drop the brick, while receiving any move commands needed to
 * adjust the bot to pick up a brick. Also sets the displayed starting position
 * on the
 * 
 * @author Michael Williams
 *
 */
public class TruckComms {

	RS485Connection connection;
	Object lock;
	
	protected TruckComms(){
		lock = new Object();
	}
	
	/**
	 * Tells the truck brick to drop what it is holding
	 */
	public void drop() {
		byte buffer[] = "d".getBytes();
		connection.sendPacket(buffer, buffer.length);
		connection.readPacket(buffer, buffer.length);
		if(buffer.equals("k")){
			return;
		}
	}

	/**
	 * Tells the truck brick to pick up what is in front of it
	 */
	public void pickUp() {
		byte buffer[] = "p".getBytes();
		connection.sendPacket(buffer, buffer.length);
		connection.readPacket(buffer, buffer.length);
		if(buffer.equals("k")){
			return;
		}
	}

	/**
	 * Starts communication with the Truck Brick
	 */
	public void setup() {
		connection = RS485.connect("NXT", NXTConnection.PACKET);
	}

}