public class DeterministicOrientation extends Orientation {
	private Navigation nav;
	
	
	public DeterministicOrientation(Map map, Odometer odo) {
		super(map, odo);
		nav = new Navigation(odo);
	}

	@Override
	public void move(boolean wall, int direction) {
		if(wall){
			nav.turn(-90);
		} else {
			nav.travelTile(direction);
		}
	}

}
