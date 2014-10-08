import lejos.nxt.ColorSensor;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	private static final int LINE_VALUE = 240, ROTATION_SPEED = 100, STOP = 0;;
	private static final double OFFSET = 10.1;
	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		
		// drive to location listed in tutorial
		Navigation nav = new Navigation(odo);
		nav.travelTo(-3, -3);
	
		// start rotating and clock all 4 gridlines
		//nav.rotate();
		robot.setRotationSpeed(ROTATION_SPEED);
		//robot.rotate(true);
		
		double[][] positionList = new double[4][3];
		for (int i = 0; i < 4; i++){
			// Wait till line
			while(ls.getNormalizedLightValue() > LINE_VALUE );
			// Fill slot with position data
			odo.getPosition(positionList[i]);
			robot.beep();
			// Wait till past line to continue
			while(ls.getNormalizedLightValue() < LINE_VALUE + 30 );
		}
		
		//Stop the robot while we do the calculations
		//nav.stopRotate();
		robot.setRotationSpeed(STOP);
		//robot.stop();
		ls.setFloodlight(false);
		
		// do trig to compute (0,0) and 0 degrees
		double thetaX = (Odometer.fixDegAngle(positionList[0][Odometer.THETA] - positionList[2][Odometer.THETA]));
		double thetaY = (Odometer.fixDegAngle(positionList[1][Odometer.THETA] - positionList[3][Odometer.THETA]));
		
		// Sum up the calculated delta Thetas, and then divide by 4 to get the average
		double deltaSum = 180 - thetaX/2 - positionList[0][Odometer.THETA]; //delta theta @ x-
		deltaSum += 270 - thetaY/2 - positionList[1][Odometer.THETA]; //delta theta @ y+
		deltaSum += 180 + thetaX/2 - positionList[2][Odometer.THETA]; //delta theta @ x+
		deltaSum += 270 + thetaY/2 - positionList[3][Odometer.THETA]; //delta theta @ y-
		
		//Update the odometer
		odo.setPosition(new double[] {-OFFSET*Math.abs(Math.cos(thetaY/2)), -OFFSET*Math.abs(Math.cos(thetaX/2)), Odometer.fixDegAngle(odo.getAngle() + (deltaSum/4))}, new boolean[] {true, true, true});

		// when done travel to (0,0) and turn to 0 degrees
		
		nav.travelTo(0, 0);
		nav.turnToABS(0);
	}

}
