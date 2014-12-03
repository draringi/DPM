package dpm.teamone.armcontrol;

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.RS485Connection;

/**
 * The DriverComms handles communications with the Drive Brick.
 * 
 * @author Michael Williams
 * @see dpm.teamone.driver.communications.TruckComms
 */
public class DriverComms {

	RS485Connection connection;

	public DriverComms() {
		this.connection = RS485.waitForConnection(0, NXTConnection.PACKET);
	}

	/**
	 * Waits for signal over the wire, and follows requested instruction, before sending the confirmation signal.
	 * @see dpm.teamone.driver.communications.TruckComms
	 */
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
		} else if (parser.equals("g")) {
			Arm.lower();
			Arm.grab();
		} else if (parser.equals("d")) {
			Arm.lower();
			Arm.release();
			Arm.raise();
			Arm.grab();
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
