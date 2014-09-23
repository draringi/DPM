/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private static final double SENSOR_OFFSET = 4.5;
	private Odometer odometer;
	private ColorSensor sensor;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		sensor = new ColorSensor(SensorPort.S1);
		Sound.setVolume(100);
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		int value;
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			value = sensor.getRawLightValue();
			if(value < 400){
				Sound.beepSequence();
				double theta = odometer.getTheta();
				if(isX(theta)){
					odometer.setX(nearestLine(odometer.getX(), getDirectionalMultiplier(theta)));
				} else {
					odometer.setY(nearestLine(odometer.getY(), getDirectionalMultiplier(theta)));
				}
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	/**
	 * Determines if the robot is pointing roughly in the positive or negative X direction.
	 * If not, it is pointing in the Y direction
	 * @param theta Directional angle of the robot
	 * @return True if X-axis, False if Y-axis
	 */
	boolean isX(double theta){
		theta = Math.abs(theta) % 360;
		return theta < Math.PI/4 || theta > 3*Math.PI/4 && theta < 5*Math.PI/4 || theta > 7*Math.PI/4; 
	}
	
	/**
	 * Determines the multiplier to use for directional purposes.
	 * @param theta
	 * @return 1 on positive X and Y , -1 on negative X and Y
	 */
	int getDirectionalMultiplier(double theta){
		theta = theta % 360;
		if(theta > -Math.PI/4 && theta < 3*Math.PI/4 || theta > 7*Math.PI/4){
			return 1;
		} else {
			return -1;
		}
	}
	
	/**
	 * Determine the nearest line, given a location.
	 * @param current location on the given axis
	 * @return corrected location
	 */
	double nearestLine(double current, int direction){
		double roughCount = (current - 15)/30;
		long count = Math.round(roughCount);
		return (double) count * 30 + 15 - SENSOR_OFFSET * direction;
	}
}