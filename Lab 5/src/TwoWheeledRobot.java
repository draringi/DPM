import lejos.nxt.*;

public class TwoWheeledRobot {
	public static final double DEFAULT_LEFT_RADIUS = 2.123;
	public static final double DEFAULT_RIGHT_RADIUS = 2.123;
	public static final double DEFAULT_WIDTH = 14.645;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor,
						   NXTRegulatedMotor rightMotor,
						   double width,
						   double leftRadius,
						   double rightRadius) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		this.leftMotor.setAcceleration(1000);
		this.rightMotor.setAcceleration(1000);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(leftMotor, rightMotor, DEFAULT_WIDTH, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	public TwoWheeledRobot(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double width) {
		this(leftMotor, rightMotor, width, DEFAULT_LEFT_RADIUS, DEFAULT_RIGHT_RADIUS);
	}
	
	// accessors
	public double getDisplacement() {
		return (leftMotor.getTachoCount() * leftRadius +
				rightMotor.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftMotor.getTachoCount() * leftRadius -
				rightMotor.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(int speed) {
		if (speed > 0){
			leftMotor.setSpeed(speed);
			rightMotor.setSpeed(speed);
			leftMotor.forward();
			rightMotor.forward();
		} else {
			leftMotor.setSpeed(-speed);
			rightMotor.setSpeed(-speed);
			leftMotor.backward();
			rightMotor.backward();
		}
	}
	
	public void setRotationSpeed(int speed) {
		if (speed > 0){
			leftMotor.setSpeed(speed);
			rightMotor.setSpeed(speed);
			leftMotor.forward();
			rightMotor.backward();
		} else {
			leftMotor.setSpeed(-speed);
			rightMotor.setSpeed(-speed);
			leftMotor.backward();
			rightMotor.forward();
		}
	}
	
	public double leftRadius(){
		return DEFAULT_LEFT_RADIUS;
	}
	
	public double rightRadius(){
		return DEFAULT_RIGHT_RADIUS;
	}
	
	public double width(){
		return DEFAULT_WIDTH;
	}
	
	public void beep(){
		Sound.beepSequence();
	}
}
