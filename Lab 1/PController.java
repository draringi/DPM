import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 300;
	private int panicZone;
	private final int MAX_SPEED = 550;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		this.panicZone = this.bandCenter - 5;
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
	 * Determined by the formula speed = m*d-c
	 * Where m = magnitude = left wheel speed / (bandCenter - panic zone)
	 * and c = panic zone x magnitude
	 * which results in the right wheel being equal to the left wheel at bandCenter
	 * @param distance As reported by sensor
	 * @return Right Wheel Speed
	 */
	private int getSpeed( int distance){
		int magnitude = (motorStraight/(this.bandCenter-this.panicZone));
		return Math.min(Math.max((magnitude*(distance - panicZone)),1),MAX_SPEED);
	}

}
