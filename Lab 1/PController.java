import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 300;
	private final int panicZone = 20;
	private final int MAX_SPEED = 550;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	private int currentLeftSpeed;
	
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		rightMotor.setSpeed(this.getSpeed(this.distance));
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	/**
	 * Determines the speed at which the Right wheel
	 * @param distance As reported by sensor
	 * @return Right Wheel Speed
	 */
	private int getSpeed( int distance){
		int magnitude = (motorStraight/(this.bandCenter-this.panicZone));
		return Math.min(Math.max((magnitude*(distance - panicZone)),1),MAX_SPEED);
	}

}
