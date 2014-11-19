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
	private static final float SENSOR_OFFSET = (float) 7.5; 
	private static final float SENSOR_DIFF = (float) 4.8;
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
			if(distance >= SENSOR_DIFF*4 ){
				leftPassed = false;
				rightPassed = false;
				passPoint = null;
				return;
			}
			float theta = (float) Math.atan2(distance, SENSOR_DIFF);
			if(leftFirst){
				theta = -theta;
			}
			theta = (float) (theta/Math.PI * 180);
			Direction dir = Direction.fromAngle(Math.round(theta)); 
			theta += dir.toAngle();
			float beleivedHeading = pose.getHeading();
			float correction = theta - beleivedHeading;
			pose.rotateUpdate(correction);
			
			float x = pose.getX();
			float y = pose.getY();
			theta = (float) (theta / 180 * Math.PI); 
			int line;
			float offset = SENSOR_OFFSET + distance/2;
			dir = Direction.fromAngle(Math.round(pose.getHeading()));
			switch(dir){
			case EAST:
			case WEST:
				line = nav.getMap().getGrid(x);
				x = (float) (line * 30 + Math.sin(theta) * offset);
				break;
			case NORTH:
			case SOUTH:
				line = nav.getMap().getGrid(x);
				y = (float) (line * 30 + Math.sin(theta) * offset);
				break;
			}
			pose.setLocation(x, y);
			nav.setPose(pose);
			if(LineLogger.isInit()){
				LineLogger.addRecord(pose, distance, leftFirst, beleivedHeading, correction, pose.getHeading(), dir);
			}
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
		if(!EventManager.isRunning()){
			return false;
		}
		boolean left, right;
		left = (leftSensor.getRawLightValue() < LIGHT_LEVEL);
		right = (rightSensor.getRawLightValue() < LIGHT_LEVEL);
		this.rightPassed = rightPassed || right;
		this.leftPassed = leftPassed || left;
		
		return (left || right);
	}

}
