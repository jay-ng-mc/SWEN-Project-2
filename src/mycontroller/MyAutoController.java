
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
			//Need to change this
			if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
				applyForwardAcceleration();   // Tough luck if there's a wall in the way
			}else {
				//Need to implement finishingPosition
				Coordinate move = pathfind.A_Star(getPosition(), finishingPosition, map);
				Direction finalDirection = checkDirections(move);
				changeDirection(getOrientation(), finalDirection);
			}
		}

		public float orientationConvert(Direction targetDirection) {
			float currentAngle;
			if(targetDirection == Direction.NORTH) {
				currentAngle = WorldSpatial.NORTH_DEGREE;
			}else if(targetDirection == Direction.EAST) {
				currentAngle = WorldSpatial.EAST_DEGREE_MIN;
			}else if(targetDirection == Direction.SOUTH) {
				currentAngle = WorldSpatial.SOUTH_DEGREE;
			}else{
				currentAngle = WorldSpatial.WEST_DEGREE;
			}
			return currentAngle;
		}
		
		public void changeDirection(Direction currentDirection, Direction targetDirection) {
			float currentAngle = orientationConvert(currentDirection);
			float finalAngle = orientationConvert(targetDirection);
			if((finalAngle - currentAngle) <= 90 && (finalAngle - currentAngle) > 0) {
				turnLeft();
			}else if((finalAngle - currentAngle) < 0 && (finalAngle - currentAngle) >= -90) {
				turnRight();
			}else if(Math.abs(finalAngle - currentAngle) == 180) {
				applyReverseAcceleration();
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
