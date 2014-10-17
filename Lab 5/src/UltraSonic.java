import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


/**
 * Averaging filtering Ultrasonic reader
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 *
 */
public class UltraSonic {
	private UltrasonicSensor us;
	private static final int MAX = 90, LOOPS = 10; 
	public UltraSonic() {
		 us = new UltrasonicSensor(SensorPort.S2);
	}

	public int poll(){
		int i, sum = 0;
		for(i=0; i<LOOPS; i++){
			sum += getFiltered();
		}
		return sum/LOOPS;
	}
	
	private int getFiltered(){
		us.ping();
		try { Thread.sleep(40); } catch (InterruptedException e) {}
		int dist = us.getDistance();
		if(dist > MAX){
			return MAX;
		}
		return dist;
	}
}
