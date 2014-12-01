package dpm.teamone.driver.navigation;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

/**
 * Averaging filtering Light/Color reader
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class Light {

	private static final int LOOPS = 40;

	private final ColorSensor sensor;

	public Light(SensorPort port) {
		// Change this to which ever port contains the US
		this.sensor = new ColorSensor(port);
		this.sensor.setFloodlight(true);
	}

	public int poll() {
		int i, sum = 0;
		for (i = 0; i < LOOPS; i++) {
			sum += sensor.getRawLightValue();
		}
		return sum / LOOPS;
	}

}
