package dpm.teamone.driver.navigation;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

/**
 * Averaging filtering Ultrasonic reader
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class UltraSonic {

	private static final int MAX = 90, LOOPS = 35;

	private final UltrasonicSensor us;

	public UltraSonic() {
		// Change this to which ever port contains the US
		this.us = new UltrasonicSensor(SensorPort.S1);
	}

	private int getFiltered() {
		this.us.ping();
		Delay.msDelay(15); // 34cm/ms, 34*15 = 255*2
		int dist = this.us.getDistance();
		if (dist > MAX) {
			return MAX;
		}
		return dist;
	}
	
	public int poll(int count) {
		int i, sum = 0;
		for (i = 0; i < count; i++) {
			sum += this.getFiltered();
		}
		return sum / count;
	}

	public int poll() {
		return poll(LOOPS);
	}

}
