package mycontroller;

import tiles.*;
import utilities.Coordinate;

public class Node {
	Coordinate pos;
	TrapTile tile;
	double value;
	Node parent;
	int currentHP;
	
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
		this.currentHP = parent.getHP();
		if(this.tile instanceof LavaTrap) {
			this.currentHP -= 5;
		}else if(this.tile instanceof HealthTrap) {
			this.currentHP++;
		}else if(this.tile instanceof WaterTrap) {
			this.currentHP += 5;
		}
	}
	
	//Only used for the first Node
	public void setHP(int currentHP) {
		this.currentHP = currentHP;
	}
	
	public int getHP() {
		return this.currentHP;
	}
	
	public Node getParent() {
		return this.parent;
	}
}
