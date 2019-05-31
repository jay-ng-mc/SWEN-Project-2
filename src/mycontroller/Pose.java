package mycontroller;

import utilities.Coordinate;
import world.WorldSpatial;

public class Pose {
    public Coordinate position;                 // position
    public WorldSpatial.Direction angle;        // orientation
    public int velocity;                        // velocity, + forwards - backwards
}