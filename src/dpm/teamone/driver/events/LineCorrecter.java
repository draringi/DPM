package dpm.teamone.driver.events;

import lejos.geom.Point;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;
import dpm.teamone.driver.navigation.Direction;
import dpm.teamone.driver.navigation.Light;
import dpm.teamone.driver.navigation.NavigationController;

/**
 * The Line Corrector checks both light sensors for the grid lines, and uses those to correct both its heading,
 * and its location in the x and y directions.  
 * @author Michael Williams
 * @see EventManager
 */
class LineCorrecter implements Behavior {

	/**
	 * Minimum light level required for the system to consider what is under a sensor as being a black line.
	 * Value: {@value} 
	 */
	private static final int LIGHT_LEVEL = 500;
	
	/**
	 * Sensor ports associated with Light Sensor.
	 */
	private static final SensorPort RIGHT_LIGHT_PORT = SensorPort.S2,
			LEFT_LIGHT_PORT = SensorPort.S3;
	
	/**
	 * Distance between the two light/colour sensors.
	 * Value {@value}
	 */
	private static final float SENSOR_DIFF = (float) 11.1;
	
	/**
	 * Offset of the light/colour sensors
	 */
	private static final float SENSOR_OFFSET = (float) 14.6;
	
	/**
	 * Stores if the given light sensor has seen a light.
	 */
	private boolean leftPassed, rightPassed;
	/**
	 * Stores which light sensor saw a line first.
	 * True: Left.
	 * False: Right.
	 */
	private boolean leftFirst;
	
	/**
	 * The filtered light sensors.
	 */
	private final Light leftSensor, rightSensor;
	private final NavigationController nav;
	
	/**
	 * The point at which the first sensor saw the line.
	 * If both sensors have seen a line, and this is null, then both sensors saw the line at the same time.
	 */
	private Point passPoint;

	/**
	 * 
	 * @param nav Navigation Controller
	 */
	protected LineCorrecter(NavigationController nav) {
		this.leftSensor = new Light(LEFT_LIGHT_PORT);
		this.rightSensor = new Light(RIGHT_LIGHT_PORT);
		this.nav = nav;
		this.leftPassed = false;
		this.rightPassed = false;
		this.leftFirst = false;
		this.passPoint = null;
	}

	/**
	 * Action taken when a Line is seen.
	 * If only one is seen, then which sensor saw it is recorded, as is the current location.
	 * If both have been seen, the heading is corrected, and then using the new heading, either the x or y location is updated.
	 * @see lejos.robotics.subsumption.Behavior#action()
	 */
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
			Delay.msDelay(500);
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
		// Ideally this stops the action, but there is pretty no time when this is called to my knowledge in our program.
	}

	/**
	 * Checks for new lines, and triggers if there is one.
	 * @return true if a new line is seen, false otherwise.
	 * @see lejos.robotics.subsumption.Behavior#takeControl()
	 */
	@Override
	public boolean takeControl() {
		if (!EventManager.isRunning()) {
			return false;
		}
		boolean left, right, change;
		left = (this.leftSensor.poll() < LIGHT_LEVEL);
		right = (this.rightSensor.poll() < LIGHT_LEVEL);
		change = (left && !this.leftPassed) || (right && !this.rightPassed);
		if (!change) {
			return false;
		}
		this.rightPassed = this.rightPassed || right;
		this.leftPassed = this.leftPassed || left;
		return true;
	}

}
