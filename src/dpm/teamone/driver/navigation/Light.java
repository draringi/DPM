package dpm.teamone.driver.navigation;

import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/**
 * Averaging filtering Light/Color reader
 * 
 * @author Michael Williams (260369438)
 * 
 */
public class Light {

	private static final int LOOPS = 1;

	private final ColorSensor sensor;

	public Light(SensorPort port) {
		// Change this to which ever port contains the US
		this.sensor = new ColorSensor(port);
		this.sensor.setFloodlight(true);
	}

	public int poll() {
		return sensor.getRawLightValue();
		//int i, sum = 0;
		//for (i = 0; i < LOOPS; i++) {
			//sum += sensor.getRawLightValue();
//			Delay.nsDelay(100);
//		}
	//	return sum / LOOPS;
	}

}
