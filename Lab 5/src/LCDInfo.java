import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private Orientation orienteer;
	
	// arrays for displaying data
	private double [] pos;
	private int [] start;
	
	public LCDInfo(Odometer odo, Orientation orienteer) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.orienteer = orienteer;
		// initialise the arrays for displaying data
		pos = new double [3];
		start = new int [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		int options = orienteer.optionsLeft();
		LCD.clear();
		if(options != 1){
			LCD.drawString("X: ", 0, 0);
			LCD.drawString("Y: ", 0, 1);
			LCD.drawString("H: ", 0, 2);
			LCD.drawString("C: ", 0, 3);
			LCD.drawString("O: ", 0, 4);
			LCD.drawInt((int)(pos[0] * 10), 3, 0);
			LCD.drawInt((int)(pos[1] * 10), 3, 1);
			LCD.drawInt((int)pos[2], 3, 2);
			LCD.drawInt((int)orienteer.getCount(), 3, 3);
			LCD.drawInt((int)options, 3, 4);
		} else {
			orienteer.getOption(start);
			LCD.drawString("X: ", 0, 0);
			LCD.drawString("Y: ", 0, 1);
			LCD.drawString("H: ", 0, 2);
			LCD.drawString("C: ", 0, 3);
			LCD.drawString("O: ", 0, 4);
			LCD.drawInt((int)start[0], 3, 0);
			LCD.drawInt((int)start[1], 3, 1);
			LCD.drawInt((int)start[2], 3, 2);
			LCD.drawInt((int)orienteer.getCount(), 3, 3);
			LCD.drawInt((int)options, 3, 4);
		}
	}
}
