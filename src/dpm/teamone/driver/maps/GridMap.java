package dpm.teamone.driver.maps;

import java.util.ArrayList;
import java.util.BitSet;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.pathfinding.FourWayGridMesh;
import lejos.geom.Rectangle;
import lejos.geom.Line;

public class GridMap {

	private BitSet bitset;

	private int width, height;
	
	private LineMap linemap;
	private FourWayGridMesh mesh;
	
	private static final float TILE_SIZE = 30;
	private static final float CLEARANCE = 1;

	public GridMap(int width, int height){
		this.width = width;
		this.height = height;
		this.bitset = new BitSet(width*height);
	}
	
	private int getIndex(int x, int y){
		return width*y+x;
	}
	
	protected void set(int x, int y){
		this.bitset.set(getIndex(x, y));
	}
	
	public int getHeight(){
		return this.height;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	
	public FourWayGridMesh getGridMesh() {
		if(mesh == null){
			mesh = generateGridMesh();
		}
		return mesh;
	}
	
	private FourWayGridMesh generateGridMesh(){
		return new FourWayGridMesh(this.getLineMap(), TILE_SIZE, CLEARANCE);
	}

	public LineMap getLineMap(){
		if(linemap == null){
			linemap = generateLineMap();
		}
		return linemap;
	}
	
	private LineMap generateLineMap() {
		Rectangle rect = new Rectangle(-TILE_SIZE, -TILE_SIZE, width*TILE_SIZE, height*TILE_SIZE);
		ArrayList<Line> lines = new ArrayList<Line>();
		for(int x=0; x < width; x++){
			for(int y=0; y < height; y++){
				if(bitset.get(getIndex(x, y))){
					lines.add(new Line((x-1)*TILE_SIZE, x*TILE_SIZE, y*TILE_SIZE, y*TILE_SIZE));
					lines.add(new Line((x-1)*TILE_SIZE, x*TILE_SIZE, (y-1)*TILE_SIZE, (y-1)*TILE_SIZE));
					lines.add(new Line((x-1)*TILE_SIZE, (x-1)*TILE_SIZE, (y-1)*TILE_SIZE, y*TILE_SIZE));
					lines.add(new Line(x*TILE_SIZE, x*TILE_SIZE, (y-1)*TILE_SIZE, y*TILE_SIZE));
				}
			}
		}
		return new LineMap(lines.toArray(new Line[lines.size()]), rect);
	}

}