import lejos.nxt.Motor;


public class Navigation {
	// put your navigation code here 
	final static int FORWARD = 175, TURNING = 150, STOP = 0;
	static private final double TOLERANCE = 1.5, MIN_ANGLE = 5, MIN_ANGLE_ABS = 1;
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
			minAng = Odometer.fixDegAngle((Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI));
			if(Math.abs(Odometer.minimumAngleFromTo(odometer.getAngle(), minAng)) > MIN_ANGLE){
				this.turnTo(minAng);
			}
			robot.setForwardSpeed(FORWARD);
			try { Thread.sleep(20); } catch (InterruptedException e) {}
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
	
	public void turnToABS(double angle) {
		double turnAngle;
		while(Math.abs(turnAngle = Odometer.minimumAngleFromTo(odometer.getAngle(), angle)) > MIN_ANGLE_ABS){
			if (turnAngle < 0){
				robot.setRotationSpeed(-TURNING);
			} else {
				robot.setRotationSpeed(TURNING);
			}
		}
		robot.setRotationSpeed(STOP);
	}
	
	public void rotate(){
		Motor.A.setSpeed(100);
		Motor.B.setSpeed(100);
		Motor.A.forward();
		Motor.B.backward();
	}
	
	public void stopRotate(){
		Motor.A.setSpeed(0);
		Motor.B.setSpeed(0);
	}
}