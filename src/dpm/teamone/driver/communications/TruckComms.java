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

	// Object lock;

	protected TruckComms() {
		// this.lock = new Object();
	}

	/**
	 * Tells the truck brick to arm the claw (Lower and open)
	 */
	public void armClaw() {
		this.ensureConnection();
		byte buffer[] = "a".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		int result = this.connection.read(buffer, buffer.length);
		if (result == 0) {
			this.armClaw();
		}
		String parser = new String(buffer);
		if (parser.equals("k")) {
			return;
		}
	}

	/**
	 * Tells the truck brick to drop what it is holding
	 */
	public void drop() {
		this.ensureConnection();
		byte buffer[] = "d".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		int result = this.connection.read(buffer, buffer.length);
		if (result == 0) {
			this.drop();
		}
		String parser = new String(buffer);
		if (parser.equals("k")) {
			return;
		}
	}

	private void ensureConnection() {
		while (this.connection == null) {
			this.connection = RS485.connect("NXT", NXTConnection.PACKET);
		}
	}

	/**
	 * Tells the truck brick to pick up what is in front of it
	 */
	public void pickUp() {
		this.ensureConnection();
		byte buffer[] = "p".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		int result = this.connection.read(buffer, buffer.length);
		if (result == 0) {
			this.pickUp();
		}
		String parser = new String(buffer);
		if (parser.equals("k")) {
			return;
		}
	}
	
	/**
	 * Tells the truck brick to grap up what is in front of it
	 */
	public void grab() {
		this.ensureConnection();
		byte buffer[] = "g".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		int result = this.connection.read(buffer, buffer.length);
		if (result == 0) {
			this.grab();
		}
		String parser = new String(buffer);
		if (parser.equals("k")) {
			return;
		}
	}

	/**
	 * Starts communication with the Truck Brick
	 */
	public void setup() {
		this.connection = RS485.connect("NXT", NXTConnection.PACKET);
	}

	/**
	 * Tells the truck brick to place the claw in travel position (raised and
	 * closed)
	 */
	public void travel() {
		this.ensureConnection();
		byte buffer[] = "t".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
		int result = this.connection.read(buffer, buffer.length);
		if (result == 0) {
			this.travel();
		}
		String parser = new String(buffer);
		if (parser.equals("k")) {
			return;
		}
	}

}