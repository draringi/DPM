package dpm.teamone.driver.navigation;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * Averaging filtering Ultrasonic reader
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class UltraSonic {

	private static final int MAX = 90, LOOPS = 10;

	private final UltrasonicSensor us;

	public UltraSonic() {
		// Change this to which ever port contains the US
		this.us = new UltrasonicSensor(SensorPort.S1);
	}

	private int getFiltered() {
		this.us.ping();
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {
		}
		int dist = this.us.getDistance();
		if (dist > MAX) {
			return MAX;
		}
		return dist;
	}

	public int poll() {
		int i, sum = 0;
		for (i = 0; i < LOOPS; i++) {
			sum += this.getFiltered();
		}
		return sum / LOOPS;
	}

}
