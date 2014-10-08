import lejos.nxt.LightSensor;
import lejos.robotics.navigation.Navigator;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private static final int LINE_VALUE = 400;
	private static final double OFFSET = 5, ROTATION_SPEED = 30, STOP = 0;
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		robot.setRotationSpeed(ROTATION_SPEED);
		// start rotating and clock all 4 gridlines
		double[][] positionList = new double[4][3];

		for (int i = 0; i < 4; i++){
			// Wait till line
			while(ls.getNormalizedLightValue() > LINE_VALUE );
			// Fill slot with position data
			odo.getPosition(positionList[i]);
			// Wait till past line to continue
			while(ls.getNormalizedLightValue() < LINE_VALUE );
		}
		//Stop the robot while we do the calculations
		robot.setRotationSpeed(STOP);
		// do trig to compute (0,0) and 0 degrees
		double thetaX = Odometer.minimumAngleFromTo(positionList[0][Odometer.THETA], positionList[2][Odometer.THETA]);
		double thetaY = Odometer.minimumAngleFromTo(positionList[1][Odometer.THETA], positionList[3][Odometer.THETA]);
		odo.setPosition(new double[] {-OFFSET*Math.cos(thetaY/2), -OFFSET*Math.cos(thetaX/2), 0}, new boolean[] {true, true, false});
		// when done travel to (0,0) and turn to 0 degrees
		Navigation nav = new Navigation(odo);
		nav.travelTo(0, 0);
		nav.turnTo(0);
	}

}
