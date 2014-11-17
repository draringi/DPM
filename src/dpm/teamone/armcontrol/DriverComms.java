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

	public DriverComms(){
		connection = RS485.waitForConnection(0, NXTConnection.PACKET);
	}
	
	public void waitForSignal() {
		byte buffer[] = new byte[4];
		int res = this.connection.readPacket(buffer, buffer.length);
		if (res == 0){
			return;
		}
		if(buffer.equals("p")){
			Arm.lower();
			Arm.grab();
			Arm.raise();
		} else if (buffer.equals("d")){
			Arm.lower();
			Arm.release();
			Arm.raise();
		} else if(buffer.equals("a")){
			Arm.release();
			Arm.lower();
		} else if(buffer.equals("t")){
			Arm.raise();
			Arm.grab();
		}
		buffer = "k".getBytes();
		this.connection.sendPacket(buffer, buffer.length);
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

}