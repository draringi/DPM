import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 250;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		if(!satisfactoryDistance()){
			if ((this.distance - this.bandCenter) > 0){
				this.turnLeft();
			} else {
				this.turnRight();
			}
		} else {
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
		}
	}
	
	private void turnLeft() {
		leftMotor.setSpeed(motorLow);
		rightMotor.setSpeed(motorHigh+50);
	}

	private void turnRight() {
		leftMotor.setSpeed(motorHigh+100);
		rightMotor.setSpeed(motorLow-150);
	}

	private boolean satisfactoryDistance(){
		return (Math.abs(this.distance - this.bandCenter) < this.bandwith/2);
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
