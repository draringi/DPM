/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private ColorSensor sensor;
    private int oldValue;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		sensor = new ColorSensor(SensorPort.S1);
		oldValue = sensor.getNormalizedLightValue();
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
					odometer.setX(nearestLine(odometer.getX()));
				} else {
					odometer.setY(nearestLine(odometer.getY()));
				}
			}

			// put your correction code here

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
	
	boolean isX(double theta){
		double val = theta % Math.PI;
		return val < Math.PI/4 || val > 3*Math.PI/4; 
	}
	
	double nearestLine(double current){
		double roughCount = (current - 15)/30;
		long count = Math.round(roughCount);
		return (double) count * 30 + 15;
	}
}