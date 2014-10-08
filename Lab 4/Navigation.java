import lejos.nxt.LCD;


public class Navigation {
	// put your navigation code here 
	final static int FORWARD = 10, TURNING = 15, STOP = 0;
	static private final double TOLERANCE = 1, MIN_ANGLE = 2;
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
			if(Odometer.minimumAngleFromTo(odometer.getAngle(), minAng) > MIN_ANGLE){
				robot.setForwardSpeed(STOP);
				this.turnTo(minAng);
			}
			robot.setForwardSpeed(FORWARD);
			try { Thread.sleep(50); } catch (InterruptedException e) {}
		}
		robot.setForwardSpeed(STOP);
		robot.beep();
	}
	
	public void turnTo(double angle) {
		double turnAngle;
		while(Math.abs(turnAngle = Odometer.minimumAngleFromTo(odometer.getAngle(), angle)) > MIN_ANGLE){
			if (turnAngle < 0){
				robot.setRotationSpeed(-TURNING);
			} else {
				robot.setRotationSpeed(TURNING);
			}
		}
		robot.setRotationSpeed(STOP);
	}
	
	public void forward(double dist){
		robot.forward(dist);
	}
}
