import java.util.Queue;


public class Pathfinder {
	private Map map;
	private Queue<Pos> path;
	private static final int X=0, Y=1;
	private int [] cellList;
	private int size;
	
	public Pathfinder(Map map){
		this.map = map;
		this.path = new Queue<Pos>();
		this.size = map.getWidth()*map.getHeight();
		this.cellList = new int [size];
		for (int i = 0; i < size; i++){
			this.cellList[i]= -1;
		}
	}
	
	void findPath(int [] start, int [] end){
		int startIndex = map.getIndex(start[X], start[Y]);
		int endIndex = map.getIndex(end[X], end[Y]); 
		cellList[endIndex] = 0;
		while(cellList[startIndex] == -1){
			for(int i = 0; i < size; i++){
				if(cellList[i] != -1){
					int dist = cellList[i]; 
					int x = i % map.getWidth();
					int y = i / map.getWidth();
					if(!map.blocked(x, y+1) && cellList[map.getIndex(x, y+1)] == -1){
						cellList[map.getIndex(x, y + 1)] = dist + 1; 
					}
					if(!map.blocked(x, y-1) && cellList[map.getIndex(x, y-1)] == -1){
						cellList[map.getIndex(x, y - 1)] = dist + 1; 
					}
					if(!map.blocked(x+1, y) && cellList[map.getIndex(x+1, y)] == -1){
						cellList[map.getIndex(x + 1, y)] = dist + 1; 
					}
					if(!map.blocked(x-1, y) && cellList[map.getIndex(x-1, y)] == -1){
						cellList[map.getIndex(x - 1, y)] = dist + 1; 
					}
				}
			}
		}
		int loc = startIndex;
		while (loc != endIndex){
			int dist = cellList[loc]; 
			int x = loc % map.getWidth();
			int y = loc / map.getWidth();
			if(nextStep(x, y+1, dist)){
				path.addElement(new Pos(x, y+1));
				loc = map.getIndex(x, y+1);
				continue;
			}
			if(nextStep(x, y-1, dist)){
				path.addElement(new Pos(x, y-1));
				loc = map.getIndex(x, y-1);
				continue; 
			}
			if(nextStep(x+1, y, dist)){
				path.addElement(new Pos(x+1, y));
				loc = map.getIndex(x+1, y);
				continue; 
			}
			if(nextStep(x-1, y, dist)){
				path.addElement(new Pos(x, y));
				loc = map.getIndex(x-1, y);
				continue; 
			}
		}
	}
	
	private boolean nextStep(int x, int y, int dist){
		return map.valid(x, y) && (cellList[map.getIndex(x, y)] == dist - 1);
	}
	
	public boolean isPath(){
		return !path.isEmpty();
	}
	
	public int [] getNext(){
		Pos pos = (Pos) path.pop();
		return pos.toArray();
	}
	
	private class Pos{
		private int x, y;
		public Pos(int x, int y){
			this.x = x;
			this.y = y;
		}
		public int [] toArray(){
			int [] result = new int [2];
			result[X] = x;
			result[Y] = y;
			return result;
		}
		
	}
}
