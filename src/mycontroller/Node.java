package mycontroller;

import tiles.TrapTile;
import utilities.Coordinate;

public class Node {
	Coordinate pos;
	TrapTile tile;
	double value;
	Node parent;
	
	public Node(Coordinate coordinate ,TrapTile tile) {
		this.pos = coordinate; 
		this.tile = tile;
		this.parent = null;
	}
	
	public Coordinate getPos() {
		return this.pos;
	}
	
	public TrapTile getTrapTile() {
		return this.tile;
	}
	
	public void setTrapTile(TrapTile tile) {
		this.tile = tile;
	}
	
	public void setPos(Coordinate pos) {
		this.pos = pos;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return this.parent;
	}
}
