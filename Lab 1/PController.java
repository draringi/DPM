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
		rightMotor.setSpeed(this.getSpeed(this.distance));
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	private int getSpeed( int distance){
		return Math.max(Math.min(200/(this.bandCenter)*distance - 60, 550),1);
	}

}
