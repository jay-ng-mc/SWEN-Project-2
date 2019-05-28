package mycontroller;

import controller.CarController;
import tiles.LavaTrap;
import tiles.TrapTile;
import tiles.WaterTrap;
import world.Car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.MapTile;
import utilities.Coordinate;
import world.WorldSpatial;

public class MyAutoController extends CarController{		
		// How many minimum units the wall is away from the player.
		private int wallSensitivity = 1;
		
		private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
		
		// Car Speed to move at
		private final int CAR_MAX_SPEED = 1;

		private HashMap<Coordinate, String> map;
		
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
			}
			if (isFollowingWall) {
				// If wall no longer on left, turn left
				if(!checkFollowingWall(getOrientation(), currentView)) {
					turnLeft();
				} else {
					// If wall on left and wall straight ahead, turn right
					if(checkWallAhead(getOrientation(), currentView)) {
						turnRight();
					}
				}
			} else {
				// Start wall-following (with wall on left) as soon as we see a wall straight ahead
				if(checkWallAhead(getOrientation(),currentView)) {
					turnRight();
					isFollowingWall = true;
				}
			}
		}

    /**
     * Checks if the car can survive a given path (health > 0)
     * @param path sequence of actions on the car
     * @param pose position, orientation, and velocity of the car
     * @return boolean
     */
		private boolean survival(Path path, Pose pose){
		    float health = getHealth();

		    while (path.getLength() > 0){
		        if (health < 0){
		            // don't take the path if you will die
		            return false;
                }

		        Path.Move move = path.first();

		        updatePose(pose, move);
		        String nextTileType = this.map.get(pose.position);

		        switch (nextTileType){
                    case "lava":
                        health -= LavaTrap.HealthDelta;
                        break;
                    case "water":
                        health += WaterTrap.Yield;
                        break;
                    case "ice":
                        // you can camp the ice tile to get infinite health, you will survive
                        return true;
                }
            }
		    return true;
        }

    /**
     * Given the current pose of car and an action, return the new pose of the car
     * @param move this is an action given to the car, such as acceleration or turning
     * @param pose this includes the position, orientation, and velocity of the car
     * no return, pose is modified
     */
        private void updatePose(Pose pose, Path.Move move){
            int[] displacement = new int[2];

            switch(move){
                // decide how the move affects vector of car
                case BRAKE:
                    pose.velocity = 0;
                    break;
                case BACKWARD:
                    pose.velocity--;
                    break;
                case FORWARD:
                    pose.velocity++;
                    break;
                case LEFT:
                    pose.angle = WorldSpatial.changeDirection(pose.angle, WorldSpatial.RelativeDirection.LEFT);
                    break;
                case RIGHT:
                    pose.angle = WorldSpatial.changeDirection(pose.angle, WorldSpatial.RelativeDirection.RIGHT);
                    break;
                case PASS:
                    // do nothing
                    break;
            }

            // decide how vector of car translates current coords to next coords
            switch(pose.angle){
                case NORTH:
                    displacement[1] = 1;
                    break;
                case EAST:
                    displacement[0] = 1;
                    break;
                case SOUTH:
                    displacement[1] = -1;
                    break;
                case WEST:
                    displacement[0] = -1;
                    break;
            }
            displacement[0] *= pose.velocity;
            displacement[1] *= pose.velocity;

            pose.position.x += displacement[0];
            pose.position.y += displacement[1];
        }

        private int[] translate(Path path, Pose pose){
		    return new int[1];
        }

        private Path translate(Coordinate[] TilePath){
		    return new Path();
        }

		/**
		 * Check if you have a wall in front of you!
		 * @param orientation the orientation we are in based on WorldSpatial
		 * @param currentView what the car can currently see
		 * @return
		 */
		private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
			switch(orientation){
			case EAST:
				return checkEast(currentView);
			case NORTH:
				return checkNorth(currentView);
			case SOUTH:
				return checkSouth(currentView);
			case WEST:
				return checkWest(currentView);
			default:
				return false;
			}
		}
		
		/**
		 * Check if the wall is on your left hand side given your orientation
		 * @param orientation
		 * @param currentView
		 * @return
		 */
		private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
			
			switch(orientation){
			case EAST:
				return checkNorth(currentView);
			case NORTH:
				return checkWest(currentView);
			case SOUTH:
				return checkEast(currentView);
			case WEST:
				return checkSouth(currentView);
			default:
				return false;
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
		public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
			// Check tiles to my right
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to my left
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles to towards the top
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
		public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
			// Check tiles towards the bottom
			Coordinate currentPosition = new Coordinate(getPosition());
			for(int i = 0; i <= wallSensitivity; i++){
				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				if(tile.isType(MapTile.Type.WALL)){
					return true;
				}
			}
			return false;
		}
		
	public LinkedList<Coordinate> A_Star(Coordinate startingPosition, Coordinate finishingPosition){
			LinkedList<Coordinate> path = new LinkedList<>();
			ArrayList<Node> openList = new ArrayList<>();
			ArrayList<Node> closedList = new ArrayList<>();
			
			HashMap<Coordinate, Node> nodeMap = createNodeMap();
			
			Node currentNode;
			int currentIndex;
			
			openList.add(nodeMap.get(startingPosition));
			currentNode = openList.get(0);
			currentNode.setValue(calculateDistance(currentNode.getPos(), finishingPosition));
			
			while(!openList.isEmpty()) {
				currentNode =  openList.get(0);
				currentIndex = 0;
				
				int index = 0;
				for(Node node : openList) {
					if(node.getValue() < currentNode.getValue()) {
						currentNode = node;
						currentIndex = index;
					}
					index++;
				}
				
				openList.remove(currentIndex);
				closedList.add(currentNode);
				
				if(currentNode.getPos() == finishingPosition) {
					while(currentNode.getParent() != null) {
						path.offerFirst(currentNode.getPos());
						currentNode = currentNode.getParent();
					}
					path.offerFirst(currentNode.getPos());
					return path;
				}
				
				if(getPossibleMoves(currentNode.getPos(), nodeMap).size() > 0) {
					for(Node movable: getPossibleMoves(currentNode.getPos(), nodeMap)) {
						if(closedList.contains(movable)) {
							continue;
						}
						
						if(movable.getParent() != null) {
							movable.setParent(currentNode);
							movable.setValue(calculateDistance(movable.getPos(), finishingPosition));
						}
						
						openList.add(movable);
						
					}
				}
			}
			return path;
		}
		
		private ArrayList<Node> getPossibleMoves(Coordinate pos, HashMap<Coordinate, Node> map) {
			ArrayList<Node> possiblemoves = new ArrayList<>();
			Coordinate coordinate;
			coordinate = new Coordinate((getX(pos) + 1)+","+getY(pos));
			if(map.containsKey(coordinate)){
				possiblemoves.add(map.get(coordinate));
			}
			coordinate = new Coordinate((getX(pos) - 1)+","+getY(pos));
			if(map.containsKey(coordinate)){
				possiblemoves.add(map.get(coordinate));
			}
			coordinate = new Coordinate(getX(pos)+","+(getY(pos) + 1));
			if(map.containsKey(coordinate)){
				possiblemoves.add(map.get(coordinate));
			}
			coordinate = new Coordinate(getX(pos)+","+(getY(pos) - 1));
			if(map.containsKey(coordinate)){
				possiblemoves.add(map.get(coordinate));
			}
			return possiblemoves;
		}

		//Need to implement this
		private HashMap<Coordinate, Node> createNodeMap() {
			HashMap<Coordinate, Node> nodeMap = new HashMap<>();
			return nodeMap;
		}
		
		private double calculateDistance(Coordinate coordinate1, Coordinate coordinate2) {
			double dist;
			int x0 = getX(coordinate1);
			int y0 = getY(coordinate1);
			
			int x1 = getX(coordinate2);
			int y1 = getY(coordinate2);
			dist = Math.abs((x0 - x1)) + Math.abs((y0 - y1));
			return dist;
		}
		
		private int getX(Coordinate coordinate) {
			String[] splitCoordinate = coordinate.toString().split(",");
			int x = Integer.parseInt(splitCoordinate[0]);
			return x;
		}
		
		private int getY(Coordinate coordinate) {
			String[] splitCoordinate = coordinate.toString().split(",");
			int y = Integer.parseInt(splitCoordinate[1]);
			return y;
		}
	}
