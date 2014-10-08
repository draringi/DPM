import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		LightSensor ls = new LightSensor(SensorPort.S1);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE);
		usl.doLocalization();
		Navigation nav = new Navigation(odo);
		nav.turnTo(0);
		//nav.travelTo(-2,-2);
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, ls);
		//lsl.doLocalization();			
		
		Button.waitForAnyPress();
	}

}
