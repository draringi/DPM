package dpm.teamone.driver.events;

import dpm.teamone.driver.navigation.NavigationController;
import dpm.teamone.driver.navigation.Direction;
import lejos.geom.Point;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.DirectionFinder;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;


class LineCorrecter implements Behavior {
	
	private static final SensorPort RIGHT_LIGHT_PORT = SensorPort.S2, LEFT_LIGHT_PORT = SensorPort.S3;
	private ColorSensor leftSensor, rightSensor;
	private static final int LIGHT_LEVEL = 400;
	private static final float SENSOR_OFFSET = 8; // ? Need to measure...
	private static final float SENSOR_DIFF = 5;
	private NavigationController nav;
	private boolean leftPassed, rightPassed, leftFirst;
	private Point passPoint;

	protected LineCorrecter(NavigationController nav){
		this.leftSensor = new ColorSensor(LEFT_LIGHT_PORT);
		this.rightSensor = new ColorSensor(RIGHT_LIGHT_PORT);
		this.leftSensor.setFloodlight(true);
		this.rightSensor.setFloodlight(true);
		this.nav = nav;
		this.leftPassed = false;
		this.rightPassed = false;
		this.leftFirst = false;
	}
	
	@Override
	public void action() {
		if(leftPassed && rightPassed){
			Pose pose = nav.getPose();
			float distance;
			if(passPoint!=null){
				distance = pose.distanceTo(passPoint);
			} else {
				distance = 0;
			}
			float theta = (float) Math.atan2(distance, SENSOR_DIFF);
			if(leftFirst){
				theta = -theta;
			}
			theta = (float) (theta/Math.PI * 180);
			float beleivedHeading = (pose.getHeading() % 90);
			if(beleivedHeading > 45){
				beleivedHeading = 90 - beleivedHeading;
			}
			float correction = beleivedHeading - theta;
			pose.rotateUpdate(correction);
			
			float x = pose.getX();
			float y = pose.getY();
			theta = (float) (theta / 180 * Math.PI); 
			int line;
			float offset = SENSOR_OFFSET + distance/2;
			switch(Direction.fromAngle(Math.round(pose.getHeading()))){
			case EAST:
			case WEST:
				x = x/30;
				line = Math.round(x);
				x = (float) (line * 30 + Math.sin(theta) * offset);
				break;
			case NORTH:
			case SOUTH:
				y = x/30;
				line = Math.round(y);
				y = (float) (line * 30 + Math.sin(theta) * offset);
				break;
			}
			pose.setLocation(x, y);
			nav.setPose(pose);
			leftPassed = false;
			rightPassed = false;
			passPoint = null;
		} else if(leftPassed){
			leftFirst = true;
			Pose pose = nav.getPose();
			passPoint = pose.getLocation();
		} else {
			leftFirst = false;
			Pose pose = nav.getPose();
			passPoint = pose.getLocation();
		}
	}

	@Override
	public void suppress() {
		
	}

	@Override
	public boolean takeControl() {
		boolean left, right;
		left = (leftSensor.getRawLightValue() < LIGHT_LEVEL);
		right = (rightSensor.getRawLightValue() < LIGHT_LEVEL);
		this.rightPassed = rightPassed || right;
		this.leftPassed = leftPassed || left;
		
		return (left || right);
	}

}
