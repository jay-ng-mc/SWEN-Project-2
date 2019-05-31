package mycontroller;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

import java.util.HashMap;

public abstract class Strategy {
    abstract Coordinate nextMove(HashMap<Coordinate, String> map,
                                HashMap<Coordinate, MapTile> view,
                                Pose pose,
                                boolean enoughParcels, int health);
    abstract Coordinate setGoal(HashMap<Coordinate, String> map,
                                HashMap<Coordinate, MapTile> view,
                                Pose pose,
                                boolean enoughParcels, int health);


    /**
     * Update the full map with what is observed by the car's 9x9 view
     * @param map full map where tile types are stored as string
     * @param view 9x9 grid around the car
     */
    void updateMap(HashMap<Coordinate, String> map, HashMap<Coordinate, MapTile> view) {
        view.forEach((coordinate, mapTile) -> {
            switch (mapTile.getType()) {
                case TRAP:
                    // if is trap, put trap type
                    TrapTile trapTile = (TrapTile) mapTile;
                    map.put(coordinate, trapTile.getTrap());
                    break;
                default:
                    // not a trap, just put MapTile type
                    switch (mapTile.getType()) {
                        case FINISH:
                            map.put(coordinate, "finish");
                            break;
                        case START:
                            map.put(coordinate, "start");
                            break;
                        case ROAD:
                            map.put(coordinate, "road");
                            break;
                        case WALL:
                            map.put(coordinate, "wall");
                            break;
                        case EMPTY:
                            // not in map, don't add
                            // map.put(coordinate, "empty");
                            break;
                        case UTILITY:
                            map.put(coordinate, "utility");
                    }


            }
        });
    }
}