package mycontroller;

import tiles.TrapTile;
import utilities.Coordinate;

public class Node {
	Coordinate position;
	TrapTile trapTile;
	double value;
	Node parent;
	
	public Node(Coordinate coordinate ,TrapTile trapTile) {
		this.position = coordinate;
		this.trapTile = trapTile;
		this.parent = null;
	}
	
	public Coordinate getPos() {
		return this.position;
	}
	
	public TrapTile getTrapTile() {
		return this.trapTile;
	}
	
	public void setTrapTile(TrapTile trapTile) {
		this.trapTile = trapTile;
	}
	
	public void setPos(Coordinate position) {
		this.position = position;
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
