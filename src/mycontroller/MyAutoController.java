
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

		private HashMap<Coordinate, String> map = new HashMap<Coordinate, String>();
		private int parcelsNeeded;
		private int parcelsGotten;
		private Pathfinder pathfind = new Pathfinder();
		private HealthStrategy strat = new HealthStrategy();
		private boolean firstCall = true;
		
		
		
		public MyAutoController(Car car) {
			super(car);
			for (Coordinate coord : getMap().keySet()) {
				if (getMap().get(coord).getType().toString().equals("WALL")) {
					map.put(coord, "WALL");
				}
				else {
					map.put(coord, "UNKNOWN");
				}
			}

		}
		
		private Pose pose = new Pose();
		// Coordinate initialGuess;
		// boolean notSouth = true;
		@Override
		public void update() {
			/*for (Coordinate coord : map.keySet()) {
				System.out.println(map.get(coord).toString());
			}*/
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			pose.position = new Coordinate(getPosition());
			pose.angle = getOrientation();
			pose.velocity = getVelocity();
			strat.updateMap(map, getView());
			//System.out.println("Position = " + pose.position);
			// checkStateChange();
			if(getSpeed() < CAR_MAX_SPEED){ // Need speed to turn and progress toward the exit
				Direction dir = getOrientation();
				Coordinate currentPosition = new Coordinate(getPosition());
				if(facingWall()) {
					applyReverseAcceleration();
				}
				else {
					applyForwardAcceleration();
				}
				
				// Tough luck if there's a wall in the way
			}else {
				//Need to implement finishingPosition
				boolean enoughParcels = false;
				if (numParcelsFound() >= numParcels()) {
					enoughParcels = true;
				}
	            for (Coordinate coords : map.keySet()) {
	            	//System.out.println(coords.toString());
	            }
				Coordinate finishingPosition = strat.setGoal(map, getView(), pose, enoughParcels);
				System.out.println("Want to go to: " + finishingPosition.toString());
				Coordinate move = pathfind.A_Star(new Coordinate(getPosition()), finishingPosition, map, (int)getHealth());
				System.out.println("Going to: " + move + " from " + getPosition());
				if(move != null) {
					//System.out.println("Moving to: " + move.toString());
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