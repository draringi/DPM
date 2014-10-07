import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static final double ROTATION_SPEED = 30, STOP = 0;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	private static final int D = 30, K = 2;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			robot.setRotationSpeed(ROTATION_SPEED);
			while(getFilteredData() < D);
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > D);
			angleA = odo.getAngle();
			// switch direction and wait until it sees no wall
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(getFilteredData() < D);
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > D);
			angleB = odo.getAngle();
			robot.setRotationSpeed(STOP);
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			double diagonalPoint = Odometer.fixDegAngle((angleA + angleB)/2);
			// update the odometer position (example to follow:)
			double theta = Odometer.fixDegAngle(odo.getAngle() - diagonalPoint + 45);
			odo.setPosition(new double [] {-15.0, -15.0, theta}, new boolean [] {true, true, true});
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			robot.setRotationSpeed(ROTATION_SPEED);
			while(getFilteredData() < D);
			while(getFilteredData() > D);
			angleA = odo.getAngle();
			while(getFilteredData() < D);
			angleB = odo.getAngle();
			robot.setRotationSpeed(STOP);
			double diagonalPoint = Odometer.fixDegAngle((angleA + angleB)/2);
			double theta = Odometer.fixDegAngle(odo.getAngle() - diagonalPoint + 225);
			odo.setPosition(new double [] {-15.0, -15.0, theta}, new boolean [] {true, true, true});
		}
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
				
		return distance;
	}

}
