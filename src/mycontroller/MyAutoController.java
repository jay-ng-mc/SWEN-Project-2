
package mycontroller;

import controller.CarController;
import tiles.LavaTrap;
import tiles.TrapTile;
import tiles.WaterTrap;
import world.Car;

import java.util.Arrays;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.*;

public class MyAutoController extends CarController{		
		
		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;

		private HashMap<Coordinate, String> map;
		private int parcelsNeeded;
		private int parcelsGotten;
		private Pathfinder pathfind = new Pathfinder();
		
		public MyAutoController(Car car) {
			super(car);
		}
		
		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			
			// checkStateChange();
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}else {
				//Need to implement finishingPosition
				Coordinate move = pathfind.A_Star(getPosition(), finishingPosition, map);
				// If there is a move
				if(!move.equals(null)) {
					Direction finalDirection = checkDirections(move);
					if(WorldSpatial.changeDirection(getOrientation(), RelativeDirection.LEFT) == finalDirection) {
						turnLeft();
					}else if(WorldSpatial.changeDirection(getOrientation(), RelativeDirection.RIGHT) == finalDirection) {
						turnRight();
					}else if(WorldSpatial.reverseDirection(getOrientation()) == finalDirection) {
						applyReverseAcceleration();
					}
				}else { //Not enough hp to arrive to destination implement hp restoring tactics or destination is impossible to reach
					
				}
			}
		}
		
		public Direction checkDirections(Coordinate coordinate) {
			if(checkEast(coordinate)) {
				return Direction.EAST;
			}else if(checkNorth(coordinate)) {
				return Direction.NORTH;
			}else if(checkWest(coordinate)) {
				return Direction.WEST;
			}else {
				return Direction.SOUTH;
			}
		}
		
		/**
		 * Method below just iterates through the list and check in the correct coordinates.
		 * i.e. Given your current position is 10,10
		 * checkEast will check up to wallSensitivity amount of tiles to the right.
		 * checkWest will check up to wallSensitivity amount of tiles to the left.
		 * checkNorth will check up to wallSensitivity amount of tiles to the top.
		 * checkSouth will check up to wallSensitivity amount of tiles below.
		 */
		public boolean checkEast(Coordinate coordinate){
			// Check tiles to my right
			Coordinate currentPosition = new Coordinate(getPosition());
			if(new Coordinate(currentPosition.x+1, currentPosition.y).equals(coordinate)){
				return true;
			}
			return false;
		}
		
		public boolean checkWest(Coordinate coordinate){
			// Check tiles to my left
			Coordinate currentPosition = new Coordinate(getPosition());
			if(new Coordinate(currentPosition.x-1, currentPosition.y).equals(coordinate)){
				return true;
			}
			return false;
		}
		
		public boolean checkNorth(Coordinate coordinate){
			// Check tiles to towards the top	
			Coordinate currentPosition = new Coordinate(getPosition());
			if(new Coordinate(currentPosition.x, currentPosition.y+1).equals(coordinate)){
				return true;
			}
			return false;
		}
		
		public boolean checkSouth(Coordinate coordinate){
			// Check tiles towards the bottom
			Coordinate currentPosition = new Coordinate(getPosition());
			if(new Coordinate(currentPosition.x, currentPosition.y-1).equals(coordinate)){
				return true;
			}
			return false;
		}
		
}
