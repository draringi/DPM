package dpm.teamone.driver.navigation;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

/**
 * Averaging filtering Light/Color reader. Unfortunately issues mean no actual
 * filtering happened, but this API allows filtering without changing API calls.
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class Light {

	/**
	 * Default loops to take place in averaging.
	 */
	private static final int LOOPS = 1;

	private final ColorSensor sensor;

	/**
	 * @param port
	 *            Sensor Port that sensor is on.
	 */
	public Light(SensorPort port) {
		// Change this to which ever port contains the US
		this.sensor = new ColorSensor(port);
		this.sensor.setFloodlight(true);
	}

	/**
	 * @return Raw Light data.
	 */
	public int poll() {
		return this.sensor.getRawLightValue();
		// int i, sum = 0;
		// for (i = 0; i < LOOPS; i++) {
		// sum += sensor.getRawLightValue();
		// Delay.nsDelay(100);
		// }
		// return sum / LOOPS;
	}

}
