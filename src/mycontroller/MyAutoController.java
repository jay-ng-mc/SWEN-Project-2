
package mycontroller;

import controller.CarController;
import world.Car;

import java.util.HashMap;

import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.*;

public class MyAutoController extends CarController{		
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;
		// Initialize a map of found coordinates
		private HashMap<Coordinate, String> map = new HashMap<Coordinate, String>();
		private Pathfinder pathfind = new Pathfinder();
		private HealthStrategy strat = new HealthStrategy();
		
		
		
		public MyAutoController(Car car) {
			super(car);
			//initialise the map with known info, set "Roads" to unknown
			
			for (Coordinate coord : getMap().keySet()) {
				if (getMap().get(coord).getType().toString().equals("WALL")) {
					map.put(coord, "WALL");
				}
				else {
					map.put(coord, "UNKNOWN");
				}
			}

		}
		@Override
		public void update() {
			// Gets what the car can see
			strat.updateMap(map, getView());
			if(getSpeed() < CAR_MAX_SPEED){ // Need speed to turn and progress toward the exit
				if(facingWall()) {
					applyReverseAcceleration();
				}
				else {
					applyForwardAcceleration();
				}
				
			}else {
				boolean enoughParcels = false;
				if (numParcelsFound() >= numParcels()) {
					enoughParcels = true;
				}
	            //get the goal coordinates
				Coordinate finishingPosition = strat.setGoal(map, getView(), new Coordinate(getPosition()), enoughParcels);
				System.out.println("Want to go to: " + finishingPosition.toString());
				Coordinate move = pathfind.A_Star(new Coordinate(getPosition()), finishingPosition, map, (int)getHealth());
				System.out.println("Going to: " + move + " from " + getPosition());
				if(move != null) {
					Direction finalDirection = checkDirections(move);
					System.out.println(finalDirection);
					if(WorldSpatial.changeDirection(getOrientation(), RelativeDirection.LEFT) == finalDirection) {
						turnLeft();
					}else if(WorldSpatial.changeDirection(getOrientation(), RelativeDirection.RIGHT) == finalDirection) {
						turnRight();
					}else if(WorldSpatial.reverseDirection(getOrientation()) == finalDirection) {
						applyReverseAcceleration();
					}
				}
				else {
					
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
			Coordinate toCheck = new Coordinate(currentPosition.x, currentPosition.y + 1);
			System.out.println("Comparing: " + toCheck.toString() + "to " + coordinate.toString());
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
		
		public boolean facingWall() {
			Coordinate currentPosition = new Coordinate(getPosition());
			boolean facing = false;
			if (getOrientation() == Direction.NORTH) {
				if(map.get(new Coordinate(currentPosition.x, currentPosition.y + 1)).equalsIgnoreCase("WALL")){
					facing = true;
				}
			}
			else if (getOrientation() == Direction.SOUTH) {
				if(map.get(new Coordinate(currentPosition.x, currentPosition.y - 1)).equalsIgnoreCase("WALL")){
					facing = true;
				}
			}
			else if (getOrientation() == Direction.EAST) {
				if(map.get(new Coordinate(currentPosition.x + 1, currentPosition.y)).equalsIgnoreCase("WALL")){
					facing = true;
				}
			}
			else if (getOrientation() == Direction.WEST) {
				if(map.get(new Coordinate(currentPosition.x - 1, currentPosition.y)).equalsIgnoreCase("WALL")){
					facing = true;
				}
			}
			return facing;
		}
}