/** 
 * Deterministic move algorithm for the Orientation 
 * @author Michael Williams (260369438)
 * @author Leonardo Siracusa (260585931)
 * /
public class DeterministicOrientation extends Orientation {
	private Navigation nav;
	
	
	public DeterministicOrientation(Map map, Odometer odo) {
		super(map, odo);
		
	}

	@Override
	public void move(boolean wall, int direction, Navigation nav) {
		if(wall){
			nav.turn(-90);
		} else {
			nav.travelTile(direction);
		}
	}

}
