package dpm.teamone.driver.events;

import dpm.teamone.driver.navigation.NavigationController;
import dpm.teamone.driver.navigation.Direction;
import lejos.geom.Point;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.DirectionFinder;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;


class LineCorrecter implements Behavior {
	
	private static final SensorPort RIGHT_LIGHT_PORT = SensorPort.S2, LEFT_LIGHT_PORT = SensorPort.S3;
	private ColorSensor leftSensor, rightSensor;
	private static final int LIGHT_LEVEL = 500;
	private static final float SENSOR_OFFSET = (float) 7.5; 
	private static final float SENSOR_DIFF = (float) 4.8;
	private NavigationController nav;
	private boolean leftPassed, rightPassed, leftFirst, passSet;
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
		this.passSet = false;
		this.passPoint = null;
	}
	
	@Override
	public void action() {
		Pose pose = nav.getPose();
		if(leftPassed && rightPassed){
			float distance;
			if(passPoint!=null){
				Sound.beepSequence();
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
			Direction dir = Direction.fromAngle(Math.round(pose.getHeading())); 
			theta += dir.toAngle();
			float beleivedHeading = pose.getHeading();
			float correction = theta - beleivedHeading;
			float x = pose.getX();
			float y = pose.getY();
			theta = (float) (theta / 180 * Math.PI); 
			int line;
			float offset = SENSOR_OFFSET + distance/2;
			switch(dir){
			case EAST:
			case WEST:
				line = nav.getMap().getGrid(x, true);
				x = (float) (line * 30 + Math.cos(theta) * offset);
				break;
			case NORTH:
			case SOUTH:
				line = nav.getMap().getGrid(y, true);
				y = (float) (line * 30 + Math.sin(theta) * offset);
				break;
			}
			pose = nav.getPose();
			pose.setLocation(x, y);
			pose.rotateUpdate(correction);
			if(LineLogger.isInit()){
				LineLogger.addRecord(pose, distance, leftFirst, beleivedHeading, correction, pose.getHeading(), dir, nav.getPose());
			}
			this.nav.setPose(pose);
			this.leftPassed = false;
			this.rightPassed = false;
			this.passPoint = null;
			this.passSet = false;
			Sound.beep();
			try{
				Thread.sleep(100);
			} catch (Exception e){
			}
			return;
		} else if(leftPassed && !passSet){
			this.leftFirst = true;
			this.passPoint = pose.getLocation();
			this.passSet = true;
			Sound.beepSequence();
		} else if (!passSet) {
			this.leftFirst = false;
			this.passPoint = pose.getLocation();
			this.passSet = true;
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
