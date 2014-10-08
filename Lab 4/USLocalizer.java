import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static final int ROTATION_SPEED = 150, STOP = 0;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private int filterControl;
	final private int FILTER_OUT = 20;
	private int lastDistance;
	
	private static final int D = 25, K = 1;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		filterControl = 21;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			robot.setRotationSpeed(ROTATION_SPEED);
			while(getFilteredData() < D + K );
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > D - K );
			angleA = odo.getAngle();
			// switch direction and wait until it sees no wall
			robot.setRotationSpeed(-ROTATION_SPEED);
			while(getFilteredData() < D + K );
			// keep rotating until the robot sees a wall, then latch the angle
			while(getFilteredData() > D - K) ;
			angleB = odo.getAngle();
			robot.setRotationSpeed(STOP);
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			double diagonalPoint = Odometer.fixDegAngle((angleA + angleB)/2);
			// update the odometer position (example to follow:)
			double theta = Odometer.fixDegAngle(odo.getAngle() - diagonalPoint + getModifier(angleA, angleB));
			odo.setPosition(new double [] {-15.0, -15.0, theta}, new boolean [] {true, true, true});
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			robot.setRotationSpeed(ROTATION_SPEED);
			while(getFilteredData() < D + K);
			while(getFilteredData() > D - K);
			angleA = odo.getAngle();
			while(getFilteredData() < D + K);
			angleB = odo.getAngle();
			robot.setRotationSpeed(STOP);
			double diagonalPoint = Odometer.fixDegAngle((angleA + angleB)/2);
			double theta = Odometer.fixDegAngle(odo.getAngle() - diagonalPoint + getModifier(angleA, angleB));
			odo.setPosition(new double [] {-15.0, -15.0, theta}, new boolean [] {true, true, true});
		}
		Navigation nav = new Navigation(odo);
		nav.turnTo(0);
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(40); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		
		if (distance >= D && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
			distance = lastDistance;
		} else if (distance >= D){
			// true D, therefore set distance to D
			lastDistance = distance;
		} else {
			// distance went below D, therefore reset everything.
			filterControl = 0;
			lastDistance = distance;
		}
		
		return distance;
	}
	
	private double getModifier(double a, double b){
		if (a < b){
			return 225;
		} else {
			return 45;
		}
	}

}
