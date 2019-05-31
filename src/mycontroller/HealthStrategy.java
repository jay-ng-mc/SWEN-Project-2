package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import java.util.*;

public class HealthStrategy extends Strategy{
	
	//Finds next move by calling upon the goal and the pathfinder
	
    public Coordinate nextMove(HashMap<Coordinate, String> map,HashMap<Coordinate, MapTile> view,Coordinate position, boolean enoughParcels, int health){
        updateMap(map, view);
        Pathfinder pathfinder = new Pathfinder();

        Coordinate goalPosition = setGoal(map, view, position, enoughParcels);
        for (int i=0; i<4; i++){
            Coordinate coordPath = pathfinder.A_Star(position, goalPosition, map, health);

            // if car can find a path to goal, return the first step in this path
            if (coordPath != null) {
            	return coordPath;
            }
        }

        // failed to find path at all! car will die
        return null;
    }
    
    //Sets the goal in order of FINISH (if enough parcels) -> Parcel -> Most unknowns (without dying)

    public Coordinate setGoal(HashMap<Coordinate, String> map,
                              HashMap<Coordinate, MapTile> view,
                              Coordinate position,
                              boolean enoughParcels){
        Coordinate[] goal = new Coordinate[1];  
        for (Coordinate coordinate : view.keySet()) {
        	
        	//check if we should route to the exit
        	
        	if (enoughParcels) {
        		for (Coordinate coord : map.keySet()) {
        			if (map.get(coord).equalsIgnoreCase("finish")){
        				goal[0]= coord;
                        return goal[0];
        			}
        		}
        	}
        	
        	//check for a parcel if we can safely obtain it
            else if (view.get(coordinate).getType() == MapTile.Type.TRAP){
                // if we see a parcel, set it as goal
                TrapTile trapTile = (TrapTile) view.get(coordinate);
                if (trapTile.getTrap().equalsIgnoreCase("parcel") && enoughParcels == false ){
                	System.out.println("Found a parcel!!");
                    goal[0] = coordinate;
                    return goal[0];
                }
            }

            // we have not found an exit or parcel
            // need to check which side to move to gives us more unknowns to explore
            Coordinate pos = position;
            Coordinate[] north = new Coordinate[9];
            Coordinate[] east = new Coordinate[9];
            Coordinate[] south = new Coordinate[9];
            Coordinate[] west = new Coordinate[9];

            for(int i=0; i<9; i++){
                // populate north east south and west with coordinates of the tiles-
                // that we will visit if we move in that direction
                // iterate from west to east, north to south
                north[i] = new Coordinate(pos.x+i-4, pos.y+5);
                east[i] = new Coordinate(pos.x+5, pos.y+i-4);
                south[i] = new Coordinate(pos.x+i-4, pos.y-5);
                west[i] = new Coordinate(pos.x-5, pos.y+i-4);
            }

            int northUnknowns = 0, eastUnknowns = 0, southUnknowns = 0, westUnknowns = 0;
            for (int count = 0; count < 9; count ++) {
            }

            for(int i=0; i<9; i++){
            	if (map.containsKey(north[i])) {
            		if (map.get(north[i]).equalsIgnoreCase("UNKNOWN")) northUnknowns++;
            	}
            	if (map.containsKey(east[i])) {
            		if (map.get(east[i]).equalsIgnoreCase("UNKNOWN")) eastUnknowns++;
            	}
            	if (map.containsKey(south[i])) {
            		if (map.get(south[i]).equalsIgnoreCase("UNKNOWN")) southUnknowns++;
            	}
            	if (map.containsKey(west[i])) {
            		if (map.get(west[i]).equalsIgnoreCase("UNKNOWN")) westUnknowns++;
            	}
            }

            boolean tied = false;
            // this if tree decides which direction to go towards, then sets the closest tile in that direction as the goal
            if(northUnknowns > southUnknowns){
                if (eastUnknowns > westUnknowns){
                    if (northUnknowns > eastUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y+1);
                    }
                    else if (northUnknowns < eastUnknowns){
                        goal[0] = new Coordinate(pos.x+1, pos.y);
                    }
                    else{
                        tied = true;
                    }
                }
                else{
                    if (northUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y+1);
                    }
                    else if (northUnknowns < westUnknowns){
                        goal[0] = new Coordinate(pos.x-1, pos.y);
                    }
                    else {
                        tied = true;
                    }
                }
            }
            else if (northUnknowns > southUnknowns){
                if (eastUnknowns > westUnknowns){
                    if (southUnknowns > eastUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y-1);
                    }
                    else if (southUnknowns < eastUnknowns){
                        goal[0] = new Coordinate(pos.x+1, pos.y);
                    }
                    else {
                        tied = true;
                    }
                }
                else{
                    if (southUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y-1);
                    }
                    else if (southUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x-1, pos.y);
                    }
                    else{
                        tied = true;
                    }
                }
            }
            else{
                tied = true;
            }

            if (tied){
                goal[0] = getNearestUnknown(map, position);
            }
            if(goal[0] != null && map.get(goal[0]).equalsIgnoreCase("WALL")) {
            	goal[0] = getNearestUnknown(map, position);
            }
        };
        return goal[0];
    }

    //Get closest unexplored tile's coordinates
    
    private Coordinate getNearestUnknown(HashMap<Coordinate, String> map, Coordinate position){
    	Coordinate nearest = null;
    	int closestdist = 9999999; //arbitrary large integer
    	for (Coordinate coord : map.keySet()) {
    		int distanceTo = Math.abs(coord.x-position.x) + Math.abs(coord.y-position.y);
    		if (map.get(coord).equalsIgnoreCase("UNKNOWN") && distanceTo < closestdist && 
    				!(coord.equals(position))) {
    			nearest = coord;
    			closestdist = distanceTo;
    		}
    	}
    	if (nearest != null) {
    		return nearest;
    	}
    	else {
    		System.out.println("No more unknowns");
    		return null;
    	}
        
    }
}