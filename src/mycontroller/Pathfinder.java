package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.WaterTrap;
import utilities.Coordinate;
import world.World;
public class Pathfinder {
	
	TileFactory tileFactory = new TileFactory();
	final static int[] Origin = {0,0};
	final static int[] MaxPixel = {(World.MAP_WIDTH/World.MAP_PIXEL_SIZE), (World.MAP_HEIGHT/World.MAP_PIXEL_SIZE)};
	double maxDistance = calculateDistance(Origin,MaxPixel);
	
	public LinkedList<Node> A_Star(Coordinate startingPosition, Coordinate finishingPosition, HashMap<Coordinate, String> hmap, int health){
		LinkedList<Node> path = new LinkedList<>();
		ArrayList<Node> openList = new ArrayList<>();
		ArrayList<Node> closedList = new ArrayList<>();
		//System.out.println("Making New Node Map");
		HashMap<Coordinate, Node> nodeMap = createNodeMap(hmap);
		
		Node currentNode;
		int currentIndex;
		
		openList.add(nodeMap.get(startingPosition));
		currentNode = openList.get(0);
		currentNode.setValue(calculateHeuristic(currentNode.getPos(), finishingPosition, nodeMap));
		currentNode.setHP(health);
		if (finishingPosition == null) {
			return null;
		}
		while(!openList.isEmpty()) {
			//System.out.println("Looped");
			currentNode =  openList.get(0);
			currentIndex = 0;
			
			//System.out.println("currentNode = " + currentNode.getPos());
			
			int index = 0;
			for(Node node : openList) {
				if(node.getValue() < currentNode.getValue()) {
					currentNode = node;
					currentIndex = index;
				}
				index++;
			}
			
			//System.out.println("currentNode1 = " + currentNode.getPos());
			
			openList.remove(currentIndex);
			closedList.add(currentNode);
			
			if(currentNode.getPos().equals(finishingPosition)) {
				//System.out.println(currentNode.getParent());
				while(currentNode.getParent() != null) {
					path.offerFirst(currentNode);
					//System.out.println(path.toString());
					//System.out.println("Entered Loop");
					currentNode = currentNode.getParent();
				}
				System.out.println("Next coord is: " + path.toString());
				if(path.isEmpty()) {
					return null;
				}else {
					return path;
				}
			}
			//System.out.println((getPossibleMoves(currentNode.getPos(), nodeMap)).toString());
			//System.out.println("Got here");
			if(getPossibleMoves(currentNode.getPos(), nodeMap).size() > 0) {
				//System.out.println("Entered IF statement");
				for(Node movable: getPossibleMoves(currentNode.getPos(), nodeMap)) {
					//System.out.println(movable.getPos());
					//System.out.println("Entered For Loop");
					//System.out.println(movable.getHP());					
					//System.out.println("Entered Health loop");
					if(closedList.contains(movable)) {
						continue;
					}
					
					if(movable.getParent() == null) {
						movable.setParent(currentNode);
						//System.out.println("Set Parent");
						movable.setValue(calculateHeuristic(movable.getPos(), finishingPosition, nodeMap));
					}
					if(openList.contains(movable)) {
						continue;
					}
					//System.out.println(movable.getHP());
					if(movable.getHP() > 0) {
						//System.out.println("adding: " + movable.getPos());
						openList.add(movable);
					}else {
						closedList.add(movable);
					}
				}
			}
		}
		System.out.println("Nothing :P");
		return null;
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
	
	
	private HashMap<Coordinate, Node> createNodeMap(HashMap<Coordinate, String> hmap) {
		HashMap<Coordinate, Node> nodeMap = new HashMap<>();
		for(HashMap.Entry<Coordinate, String> entry: hmap.entrySet()) {
			//System.out.println(entry.getValue());
			if(!(entry.getValue().equalsIgnoreCase("WALL"))) {
				nodeMap.put(entry.getKey(), new Node(entry.getKey(), tileFactory.getTrapTile(entry.getValue())));
				//System.out.println(entry.getKey());
				
			}
		}
		return nodeMap;
	}
	
	private double calculateHeuristic(Coordinate coordinate1, Coordinate coordinate2, HashMap<Coordinate, Node> hmap) {
		double heuristic;
		heuristic = calculateDistance(coordinate1, coordinate2) + calculateTileValue(coordinate1, hmap);
		return heuristic;
	}
	
	private int calculateTileValue(Coordinate coordinate, HashMap<Coordinate, Node> hmap) {
		int tileValue = 0;
		if(hmap.get(coordinate).getTrapTile() instanceof WaterTrap) {
			tileValue += 2*maxDistance*5;
		}else if(hmap.get(coordinate).getTrapTile() instanceof LavaTrap) {
			tileValue += maxDistance;
		}else if (hmap.get(coordinate).getTrapTile() instanceof HealthTrap) {
			tileValue += 2*maxDistance;
		}
		return tileValue;
	}
	
	private double calculateDistance(int[] coordinate1,int[] coordinate2) {
		return (Math.abs((coordinate1[0] - coordinate2[0]) + Math.abs((coordinate1[1] - coordinate2[1]))));
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