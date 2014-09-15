import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 200;
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
		
		
		// TODO: process a movement based on the us distance passed in (P style)
		this.distance = distance;
		rightMotor.setSpeed(getSpeed());
	}
	
	private boolean satisfactoryDistance(){
		return (Math.abs(this.distance - this.bandCenter) < this.bandwith/2);
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	private int getSpeed(){
		return 25/4*this.distance - 65;
	}

}
