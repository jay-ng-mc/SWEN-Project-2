package mycontroller;

import tiles.MapTile;
import utilities.Coordinate;

import java.util.*;

public class HealthStrategy extends Strategy{
    public Path.Move nextMove(HashMap<Coordinate, String> map, HashMap<Coordinate, MapTile> view, Pose pose){
        updateMap(map, view);
        Pathfinder pathfinder = new Pathfinder();

        Coordinate goalPosition = setGoal(map, pose);

        Layers layers = new Layers();
        for (int i=0; i<4; i++){
            // progressively go to less restricted legal tiles
            ArrayList<String> allowedTiles = layers.getLayer(i);
            LinkedList<Coordinate> coordPath = pathfinder.A_Star(pose.position, goalPosition);

            // if car can find a path to goal, return the first step in this path
            if (!coordPath.isEmpty()) return path.first();
        }

        // failed to find path at all! car will die
        return null;
    }

    public Coordinate setGoal(HashMap<Coordinate, String> map, HashMap<Coordinate, MapTile> view, Pose pose){
        //WORK IN PROGRESS

        return null;
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
