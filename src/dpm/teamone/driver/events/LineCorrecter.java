package dpm.teamone.driver.events;

import lejos.geom.Point;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;
import dpm.teamone.driver.navigation.Direction;
import dpm.teamone.driver.navigation.NavigationController;

class LineCorrecter implements Behavior {

	private static final int LIGHT_LEVEL = 500;
	private static final SensorPort RIGHT_LIGHT_PORT = SensorPort.S2,
			LEFT_LIGHT_PORT = SensorPort.S3;
	private static final float SENSOR_DIFF = (float) 11.1;
	private static final float SENSOR_OFFSET = (float) 14.6;
	private boolean leftPassed, rightPassed, leftFirst;
	private final ColorSensor leftSensor, rightSensor;
	private final NavigationController nav;
	private Point passPoint;

	protected LineCorrecter(NavigationController nav) {
		this.leftSensor = new ColorSensor(LEFT_LIGHT_PORT);
		this.rightSensor = new ColorSensor(RIGHT_LIGHT_PORT);
		this.leftSensor.setFloodlight(true);
		this.rightSensor.setFloodlight(true);
		this.nav = nav;
		this.leftPassed = false;
		this.rightPassed = false;
		this.leftFirst = false;
		this.passPoint = null;
	}

	@Override
	public void action() {
		Pose pose = this.nav.getPose();
		if (this.leftPassed && this.rightPassed) {
			float distance;
			if (this.passPoint != null) {
				Sound.beepSequenceUp();
				distance = pose.distanceTo(this.passPoint);
			} else {
				distance = 0;
			}
			if (distance >= SENSOR_DIFF) {
				Sound.buzz();
				this.leftPassed = false;
				this.rightPassed = false;
				this.leftFirst = false;
				this.passPoint = null;
				return;
			}
			float theta = (float) Math.atan2(distance, SENSOR_DIFF);
			if (this.leftFirst) {
				theta = -theta;
			}
			theta = (float) ((theta / Math.PI) * 180);
			Direction dir = Direction.fromAngle(Math.round(pose.getHeading()));
			theta += dir.toAngle();
			float beleivedHeading = pose.getHeading();
			float correction = theta - beleivedHeading;
			float x = pose.getX();
			float y = pose.getY();
			theta = (float) ((theta / 180) * Math.PI);
			int line;
			float offset = SENSOR_OFFSET + (distance / 2);
			switch (dir) {
			case EAST:
			case WEST:
				line = this.nav.getMap().getGrid(x, true);
				x = (float) ((line * 30) + (Math.cos(theta) * offset));
				break;
			case NORTH:
			case SOUTH:
				line = this.nav.getMap().getGrid(y, true);
				y = (float) ((line * 30) + (Math.sin(theta) * offset));
				break;
			}
			pose = this.nav.getPose();
			pose.setLocation(x, y);
			pose.rotateUpdate(correction);
			if (LineLogger.isInit()) {
				LineLogger.addRecord(pose, distance, this.leftFirst,
						beleivedHeading, correction, pose.getHeading(), dir,
						this.nav.getPose());
			}
			this.nav.setPose(pose);
			this.leftPassed = false;
			this.rightPassed = false;
			this.leftFirst = false;
			this.passPoint = null;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			return;
		} else if (this.leftPassed) {
			this.leftFirst = true;
			this.passPoint = pose.getLocation();
		} else {
			this.leftFirst = false;
			this.passPoint = pose.getLocation();
		}
	}

	@Override
	public void suppress() {

	}

	@Override
	public boolean takeControl() {
		if (!EventManager.isRunning()) {
			return false;
		}
		boolean left, right, change;
		left = (this.leftSensor.getRawLightValue() < LIGHT_LEVEL);
		right = (this.rightSensor.getRawLightValue() < LIGHT_LEVEL);
		change = (left && !this.leftPassed) || (right && !this.rightPassed);
		if (!change) {
			return false;
		}
		this.rightPassed = this.rightPassed || right;
		this.leftPassed = this.leftPassed || left;
		return true;
	}

}
