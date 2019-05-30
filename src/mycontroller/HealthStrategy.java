package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import java.util.*;

public class HealthStrategy extends Strategy{
    public Path.Move nextMove(HashMap<Coordinate, String> map,
                              HashMap<Coordinate, MapTile> view,
                              Pose pose,
                              boolean enoughParcels){
        updateMap(map, view);
        Pathfinder pathfinder = new Pathfinder();

        Coordinate goalPosition = setGoal(map, view, pose, enoughParcels);

        Layers layers = new Layers();
        for (int i=0; i<4; i++){
            // progressively go to less restricted legal tiles
            ArrayList<String> allowedTiles = layers.getLayer(i);
            LinkedList<Coordinate> coordPath = pathfinder.A_Star(pose.position, goalPosition, map);

            // if car can find a path to goal, return the first step in this path
            if (!coordPath.isEmpty()) return path.first();
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
        view.forEach((coordinate, mapTile) -> {
            if (enoughParcels && mapTile.getType() == MapTile.Type.FINISH){
                // if we have enough parcels, set exit as goal
                goal[0]= coordinate;
            }
            else if (mapTile.getType() == MapTile.Type.TRAP){
                // if we see a parcel, set it as goal
                TrapTile trapTile = (TrapTile) mapTile;
                if (trapTile.getTrap() == "parcel"){
                    goal[0] = coordinate;
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

            for(int i=0; i<9; i++){
                if (map.get(north[i]).equalsIgnoreCase("UNKNOWN")) northUnknowns++;
                if (map.get(east[i]).equalsIgnoreCase("UNKNOWN")) eastUnknowns++;
                if (map.get(south[i]).equalsIgnoreCase("UNKNOWN")) southUnknowns++;
                if (map.get(west[i]).equalsIgnoreCase("UNKNOWN")) westUnknowns++;
            }

            boolean tied = false;
            // this if tree decides which direction to go towards, then sets the tile 5 units in that direction as goal
            if(northUnknowns > southUnknowns){
                if (eastUnknowns > westUnknowns){
                    if (northUnknowns > eastUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y+5);
                    }
                    else if (northUnknowns < eastUnknowns){
                        goal[0] = new Coordinate(pos.x+5, pos.y);
                    }
                    else{
                        tied = true;
                    }
                }
                else{
                    if (northUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y+5);
                    }
                    else if (northUnknowns < westUnknowns){
                        goal[0] = new Coordinate(pos.x-5, pos.y);
                    }
                    else {
                        tied = true;
                    }
                }
            }
            else if (northUnknowns > southUnknowns){
                if (eastUnknowns > westUnknowns){
                    if (southUnknowns > eastUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y-5);
                    }
                    else if (southUnknowns < eastUnknowns){
                        goal[0] = new Coordinate(pos.x+5, pos.y);
                    }
                    else {
                        tied = true;
                    }
                }
                else{
                    if (southUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x, pos.y-5);
                    }
                    else if (southUnknowns > westUnknowns){
                        goal[0] = new Coordinate(pos.x-5, pos.y);
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
                goal[0] = getNearestUnknown(map, pose);
            }

        });

        return goal[0];
    }


    private Coordinate getNearestUnknown(HashMap<Coordinate, String> map, Pose pose){
    	Coordinate nearest = null;
    	int closestdist = 9999999;
    	for (Coordinate coord : map.keySet()) {
    		int distanceTo = Math.abs(coord.x-pose.position.x) + Math.abs(coord.y-pose.position.y);
    		if (map.get(coord).equalsIgnoreCase("UNKNOWN") && distanceTo < closestdist) {
    			nearest = coord;
    			closestdist = distanceTo;
    		}
    	}
        return nearest;
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
