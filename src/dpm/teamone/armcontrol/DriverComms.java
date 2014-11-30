package dpm.teamone.armcontrol;

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

/**
 * The DriverComms handles communications with the Drive Brick.
 * 
 * @author Michael Williams
 * 
 */
public class DriverComms {

	RS485Connection connection;

	public DriverComms() {
		this.connection = RS485.waitForConnection(0, NXTConnection.PACKET);
	}

	/**
	 * Tells the Drive Brick that is has successfully dropped the block
	 */
	public void signalDropped() {
	}

	/**
	 * Tells the Drive Brick that is has successfully picked-up the block
	 */
	public void signalPickedUp() {
	}

	/**
	 * Tells the Drive Brick to correct its orientation to allow better ability
	 * to pick up a block
	 * 
	 * @param deg
	 *            Degrees to turn (In a clockwise direction)
	 */
	public void signalRotate(int deg) {
	}

	public void waitForSignal() {
		byte buffer[] = new byte[1];
		int res = this.connection.read(buffer, buffer.length, true);
		if (res == 0) {
			return;
		}
		String parser = new String(buffer);
		if (parser.equals("p")) {
			Arm.lower();
			Arm.grab();
			Arm.raise();
		} else if (parser.equals("d")) {
			Arm.lower();
			Arm.release();
			Arm.raise();
		} else if (parser.equals("a")) {
			Arm.release();
			Arm.lower();
		} else if (parser.equals("t")) {
			Arm.raise();
			Arm.grab();
		} else {
			return;
		}
		buffer = "k".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
	}

}