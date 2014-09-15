import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	private int filterControl;
	final private int FILTER_OUT = 20;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
		this.filterControl = 0;
	}
	
	public void run() {
		while (true) {
			//process collected data
			int distance = us.getDistance();
			// rudimentary filter
			if (distance == 255 && filterControl < FILTER_OUT) {
				// bad value, do not set the distance var, however do increment the filter value
				filterControl ++;
			} else if (distance == 255){
				// true 255, therefore set distance to 255
				cont.processUSData(distance);
			} else {
				// distance went below 255, therefore reset everything.
				filterControl = 0;
				cont.processUSData(distance);
			}
			
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
