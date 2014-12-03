package dpm.teamone.driver.navigation;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

/**
 * Averaging filtering Ultrasonic reader.
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class UltraSonic {

	/**
	 * Maximum accepted reading from the sensor.
	 * Value: {@value}
	 */
	private static final int MAX = 90;
	
	/**
	 * Default number of readings to take.
	 * Value: {@value} 
	 */
	private static final int LOOPS = 35;

	private final UltrasonicSensor us;

	public UltraSonic() {
		// Change this to which ever port contains the US
		this.us = new UltrasonicSensor(SensorPort.S1);
	}

	/**
	 * Filtering for individual pings.
	 * @return Filtered data.
	 */
	private int getFiltered() {
		this.us.ping();
		Delay.msDelay(15); // 34cm/ms, 34*15 = 255*2
		int dist = this.us.getDistance();
		if (dist > MAX) {
			return MAX;
		}
		return dist;
	}

	/**
	 * Averaging filtering of Ultra-sonic readings.
	 * @return Filtered Ultra-sonic data
	 */
	public int poll() {
		return this.poll(LOOPS);
	}

	/**
	 * Averaging filtering of Ultra-sonic readings.
	 * @param count Number of readings to take
	 * @return Filtered Ultra-sonic data
	 */
	public int poll(int count) {
		int i, sum = 0;
		for (i = 0; i < count; i++) {
			sum += this.getFiltered();
		}
		return sum / count;
	}

}
