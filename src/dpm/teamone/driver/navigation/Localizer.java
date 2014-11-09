package dpm.teamone.driver.navigation;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.FixedRangeScanner;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import dpm.teamone.driver.maps.GridMap;

/**
 * Localizer is a Navigation local class for ease of use of the Localization
 * system
 * 
 * @author Mehdi Benguerrah
 * @author Michael Williams
 *
 */
public class Localizer {

	private static final double WHEEL_RADIUS = 2.5;
	// Distance between centers of the left and right wheels
	private static final double TRACK_WIDTH = 15.5;
	private static NXTRegulatedMotor LEFT_MOTOR = Motor.A,
			RIGHT_MOTOR = Motor.B;
	private DifferentialPilot pilot = new DifferentialPilot(WHEEL_RADIUS,
			TRACK_WIDTH, LEFT_MOTOR, RIGHT_MOTOR);

	// The Ultrasonic sensor is not-mounted on a motor so instead of
	// only the sensor rotating, the whole robot will turn

	private FixedRangeScanner scanner = new FixedRangeScanner(this.pilot,
			new UltrasonicSensor(SensorPort.S3));
	private GridMap map;
	private static final int particles = 25, borders = 50;
	private MCLPoseProvider localisationAlgo;

	/**
	 * Sets up the Localization Algorithm to use for localizing
	 * 
	 * @param map
	 *            Map to be used to Localize against.
	 */
	protected Localizer(GridMap map) {
		LineMap lMap = this.map.getLineMap();
		this.localisationAlgo = new MCLPoseProvider(this.pilot, this.scanner,
				lMap, particles, borders);
	}

	/**
	 * Calculates robot's current position and heading using the Monte Carlo
	 * Localisation Algorithm
	 * 
	 * @return Robot's current position and heading
	 */
	public Pose performLocalisation() {
		return this.localisationAlgo.getPose();
	}

}
