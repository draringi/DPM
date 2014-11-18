package dpm.teamone.driver.maps;
import java.util.ArrayList;
import java.util.Collection;

import lejos.robotics.pathfinding.NavigationMesh;
import lejos.robotics.pathfinding.Node;

public class GridMesh implements NavigationMesh {
	
	private GridMap map;
	private ArrayList<Node> nodes;
	
	protected GridMesh(GridMap map){
		this.map = map;
		this.nodes = new ArrayList<Node>();
		this.regenerate();
	}

	@Override
	public int addNode(Node node, int neighbors) {
		int x = map.getGrid(node.x);
		int y = map.getGrid(node.y);
		int count = 0;
		for(int i = 0; i < 4; i++){
			int xn, yn;
			switch(i){
			case 0:
				xn = x;
				yn = y + 1;
				break;
			case 1:
				xn = x + 1;
				yn = y;
				break;
			case 2:
				xn = x;
				yn = y - 1;
				break;
			case 3:
				xn = x - 1;
				yn = y;
				break;
			default:
				xn = x;
				yn = y;
			}
			if(this.connect(node, buildNode(xn, yn))){
				count++;
			}
		}
		return count;
	}

	@Override
	public boolean removeNode(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean connect(Node node1, Node node2) {
		int x1 = map.getGrid(node1.x);
		int y1 = map.getGrid(node1.y);
		int x2 = map.getGrid(node2.x);
		int y2 = map.getGrid(node2.y);
		if(map.blocked(x1, y1)||map.blocked(x2, y2)){
			return false;
		}
		if(x1==x2&&y1==y2){
			return false;
		}
		if(!(x1==x2||y1==y2)){
			return false;
		}
		int ydiff = Math.abs(y1-y2);
		int xdiff = Math.abs(x1-x2);
		if(ydiff > 1|| xdiff > 1){
			return false;
		}
		node1.addNeighbor(node2);
		return true;
	}

	@Override
	public boolean disconnect(Node node1, Node node2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Node> getMesh() {
		return nodes;
	}
	
	private Node buildNode(int x, int y){
		return new Node((float)map.getPos(x), (float)map.getPos(y));
	}

	@Override
	public void regenerate() {
		for(int x=0; x < map.getWidth(); x++){
			for(int y=0; y < map.getHeight(); y++){
				if(!map.isObstacle(x, y)){
					Node node = buildNode(x, y);
					this.addNode(node, 4);
				}
			}
		}
	}

}
