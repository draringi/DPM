import lejos.nxt.LCD;


public class Navigation {
	// put your navigation code here 
	final static int FORWARD = 300, TURNING = 150, STOP = 0;
	static private final double TOLERANCE = 0.5, MIN_ANGLE = Math.PI/32;
	private Odometer odometer;
	private TwoWheeledRobot robot;
	
	public Navigation(Odometer odo) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
	}
	
	public void travelTo(double x, double y) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		double minAng;
		while (Math.abs(x - odometer.getX()) > TOLERANCE || Math.abs(y - odometer.getY()) > TOLERANCE) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng);
			robot.setForwardSpeed(FORWARD);
		}
		robot.setForwardSpeed(STOP);
	}
	
	public void turnTo(double angle) {
		double turnAngle;
		while(Math.abs(turnAngle = Odometer.minimumAngleFromTo(odometer.getAngle(), angle)) > MIN_ANGLE){
			if (turnAngle < 0){
				robot.setForwardSpeed(TURNING);
				robot.setRotationSpeed(FORWARD);
			} else {
				robot.setForwardSpeed(FORWARD);
				robot.setRotationSpeed(TURNING);
			}
		}
	}
}
