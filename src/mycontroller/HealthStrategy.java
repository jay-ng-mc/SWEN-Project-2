package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import java.util.*;

public class HealthStrategy extends Strategy{
	/*public HealthStrategy(Hashmap<Coordinate, String> map) {
		this.map = map;
	}*/
	
    public Coordinate nextMove(HashMap<Coordinate, String> map,
                              HashMap<Coordinate, MapTile> view,
                              Pose pose,
                              boolean enoughParcels, int health){
        updateMap(map, view);
        Pathfinder pathfinder = new Pathfinder();

        Coordinate goalPosition = setGoal(map, view, pose, enoughParcels);

        Layers layers = new Layers();
        for (int i=0; i<4; i++){
            // progressively go to less restricted legal tiles
            ArrayList<String> allowedTiles = layers.getLayer(i);
            Coordinate coordPath = pathfinder.A_Star(pose.position, goalPosition, map, health);

            // if car can find a path to goal, return the first step in this path
            if (coordPath != null) {
            	return coordPath;
            }
        }

        // failed to find path at all! car will die
        return null;
    }

    public Coordinate setGoal(HashMap<Coordinate, String> map,
                              HashMap<Coordinate, MapTile> view,
                              Pose pose,
                              boolean enoughParcels){
        //WORK IN PROGRESS
        Coordinate[] goal = new Coordinate[1];  // wrapping goal in array to make lambda function work
        for (Coordinate coordinate : view.keySet()) {
        	if (enoughParcels) {
        		for (Coordinate coord : map.keySet()) {
        			if (map.get(coord).equalsIgnoreCase("finish")){
        				goal[0]= coord;
                        return goal[0];
        			}
        		}
        	}
            if (enoughParcels &&  view.get(coordinate).getType() == MapTile.Type.FINISH){
                // if we have enough parcels, set exit as goal
                goal[0]= coordinate;
                return goal[0];
            }
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
            Coordinate pos = pose.position;
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
            	//System.out.println("Update Call");
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
            //System.out.println("North: " + northUnknowns + " East: " + eastUnknowns + " South: " + southUnknowns + " West: " + westUnknowns);

            boolean tied = false;
            // this if tree decides which direction to go towards, then sets the tile 5 units in that direction as goal
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
            	//System.out.println("Tied vote");
                goal[0] = getNearestUnknown(map, pose);
                //System.out.println("Aiming for: " + goal[0].toString());
            }
            if(goal[0] != null && map.get(goal[0]).equalsIgnoreCase("WALL")) {
            	goal[0] = getNearestUnknown(map, pose);
            }
        };
        //System.out.println("Aiming for: " + goal[0].toString());
        
        return goal[0];
    }


    private Coordinate getNearestUnknown(HashMap<Coordinate, String> map, Pose pose){
    	Coordinate nearest = null;
    	int closestdist = 9999999;
    	for (Coordinate coord : map.keySet()) {
    		int distanceTo = Math.abs(coord.x-pose.position.x) + Math.abs(coord.y-pose.position.y);
    		if (map.get(coord).equalsIgnoreCase("UNKNOWN") && distanceTo < closestdist && 
    				!(coord.equals(pose.position))) {
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

/**
 * Lists the tile types that the car is allowed to pass through
 * Each layer contains progressively less stringent constraints
 */
class Layers{
    private ArrayList<String> avoidAll =
            new ArrayList<>(Arrays.asList("START", "FINISH", "ROAD", "PARCEL"));
    private ArrayList<String> allowUnknowns =
            new ArrayList<>(Arrays.asList("START", "FINISH", "ROAD", "PARCEL", "UNKNOWN"));
    private ArrayList<String> allowLava =
            new ArrayList<>(Arrays.asList("START", "FINISH", "ROAD", "PARCEL", "UNKNOWN", "LAVA"));
    private ArrayList<String> allowHealth =
            new ArrayList<>(Arrays.asList("START", "FINISH", "ROAD", "UNKNOWN", "LAVA", "HEALTH", "WATER"));

    ArrayList<String> getLayer(int index){
        switch(index){
            case 0:
                return avoidAll;
            case 1:
                return allowUnknowns;
            case 2:
                return allowLava;
            case 3:
                return allowHealth;
        }

        // we should not ever get here, if we get here it means there is an index error
        return null;
    }
}