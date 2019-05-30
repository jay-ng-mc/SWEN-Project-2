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
	
	public LinkedList<Coordinate> A_Star(Coordinate startingPosition, Coordinate finishingPosition, HashMap<Coordinate, String> hmap){
		LinkedList<Coordinate> path = new LinkedList<>();
		ArrayList<Node> openList = new ArrayList<>();
		ArrayList<Node> closedList = new ArrayList<>();
		
		HashMap<Coordinate, Node> nodeMap = createNodeMap(hmap);
		
		
		Node currentNode;
		int currentIndex;
		
		openList.add(nodeMap.get(startingPosition));
		currentNode = openList.get(0);
		currentNode.setValue(calculateHeuristic(currentNode.getPos(), finishingPosition, nodeMap));
		
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
					if(movable.getHP() > 0) {
						if(closedList.contains(movable)) {
							continue;
						}
					
						if(movable.getParent() != null) {
							movable.setParent(currentNode);
							movable.setValue(calculateHeuristic(movable.getPos(), finishingPosition, nodeMap));
						}
					
						for(Node node : openList) {
							if(node.getValue() < currentNode.getValue()) {
								continue;
							}
						}
					
						openList.add(movable);
					}else {
						closedList.add(movable);
					}
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
	
	
	private HashMap<Coordinate, Node> createNodeMap(HashMap<Coordinate, String> hmap) {
		HashMap<Coordinate, Node> nodeMap = new HashMap<>();
		for(HashMap.Entry<Coordinate, String> entry: hmap.entrySet()) {
			if(entry.getValue() != "WALL" || entry.getValue() != "UNKOWN") {
				nodeMap.put(entry.getKey(), new Node(entry.getKey(), tileFactory.getTrapTile(entry.getValue())));
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
