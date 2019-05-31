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
        

        Coordinate goalPosition = setGoal(map, view, pose, enoughParcels, health);
        Pathfinder pathfinder = new Pathfinder();
        Layers layers = new Layers();
        for (int i=0; i<4; i++){
            // progressively go to less restricted legal tiles
            ArrayList<String> allowedTiles = layers.getLayer(i);
            LinkedList<Node> coordPath = pathfinder.A_Star(pose.position, goalPosition, map, health);
            if (coordPath != null) {
            	return coordPath.getFirst().getPos();
            }
            // if car can find a path to goal, return the first step in this path
        }

        // failed to find path at all! car will die
        return null;
    }
 
    public Coordinate setGoal(HashMap<Coordinate, String> map,
                              HashMap<Coordinate, MapTile> view,
                              Pose pose,
                              boolean enoughParcels,
                              int health){
        //WORK IN PROGRESS
    	
        Coordinate[] goal = new Coordinate[1];  // wrapping goal in array to make lambda function work
        Pathfinder pathfinder = new Pathfinder();
        boolean foundGoal = false;
        int acceptableDelta = 0;
        while (foundGoal == false) {
        	for (Coordinate test : map.keySet()) {
        		if(map.get(test).equalsIgnoreCase("UNKNOWN") || map.get(test).equalsIgnoreCase("parcel")
        				|| map.get(test).equalsIgnoreCase("finish")) {
        			LinkedList<Node> toCheck = pathfinder.A_Star(pose.position, test, map, health);
        			if (toCheck.size() > 0 && Math.abs(toCheck.getLast().getHP() - health) <= acceptableDelta) {
        				foundGoal = true;
        				goal[0] = toCheck.getLast().getPos();
        			}

        		}
        	}
        	acceptableDelta += 5;
        }
        
        
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
