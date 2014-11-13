package dpm.teamone.driver.events;

import dpm.teamone.driver.navigation.NavigationController;
import dpm.teamone.driver.navigation.Direction;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.DirectionFinder;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;


class LineCorrecter implements Behavior {
	
	private static final SensorPort LIGHT_PORT = SensorPort.S1;
	private ColorSensor sensor;
	private static final int LIGHT_LEVEL = 400;
	private static final float SENSOR_OFFSET = -12; // ? Need to measure...
	private NavigationController nav;

	protected LineCorrecter(NavigationController nav){
		this.sensor = new ColorSensor(LIGHT_PORT);
		this.sensor.setFloodlight(true);
		this.nav = nav;
	}
	
	@Override
	public void action() {
		Pose pose = nav.getPose();
		Direction dir = Direction.fromAngle(Math.round(pose.getHeading()));
		float x = pose.getX();
		float y = pose.getY();
		int line;
		switch(dir){
		case EAST:
		case WEST:
			x = x/30;
			line = Math.round(x);
			x = (float) (line * 30 + Math.cos(pose.getHeading()) * SENSOR_OFFSET);
			break;
		case NORTH:
		case SOUTH:
			y = x/30;
			line = Math.round(y);
			y = (float) (line * 30 + Math.sin(pose.getHeading()) * SENSOR_OFFSET);
			break;
		}
		pose.setLocation(x, y);
		nav.setPose(pose);
	}

	@Override
	public void suppress() {
		
	}

	@Override
	public boolean takeControl() {
		return (sensor.getRawLightValue() < LIGHT_LEVEL);
	}

}