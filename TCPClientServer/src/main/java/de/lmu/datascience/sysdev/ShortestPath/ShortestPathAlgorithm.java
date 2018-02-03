package de.lmu.datascience.sysdev.ShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import de.lmu.datascience.sysdev.RoadNetwork.Edge;
import de.lmu.datascience.sysdev.RoadNetwork.Network;
import de.lmu.datascience.sysdev.RoadNetwork.Node;

/**
 * Represents the Shortest Path (A*) algorithm.
 * @author Kathrin Witzlsperger
 *
 */
public class ShortestPathAlgorithm {
	
	/**
	 * road network graph
	 */
	private Network roadNetwork;
	
	/**
	 * origin node
	 */
	private Node source;
	
	/**
	 * destination node
	 */
	private Node target;
	
	/**
	 * Constructor
	 * @param roadNetwork
	 * @param origin node where to start
	 * @param destination node where to end
	 */
	public ShortestPathAlgorithm(Network roadNetwork, Node origin, Node destination) {
		
		this.roadNetwork = roadNetwork;
		
		this.source = origin;
		System.out.println("Source:\n" + this.source.toString());
		this.target = destination;
		System.out.println("Target:\n" + this.target.toString());
	}
	
	
	
	/**
	 * Finds shortest path between source and target (using A* algorithm)
	 */
	public void findShortestPath() {
	
		// create priority queue to store nodes that have to be visited
		Queue<Node> nodesToVisit = new PriorityQueue<Node>(1, new Comparator<Node>() {
			public int compare(Node node1, Node node2) {
				return node1.compareTo(node2);
			}
		});
		
		// find node that equals source in network, set its distanceFromSource and travelTimeFromSource to 0 and add it to priority queue
		Node sourceInNetwork = this.roadNetwork.getNodes().get(this.source.getCoordinates());
	    sourceInNetwork.setDistanceFromSource(0.0);
	    sourceInNetwork.setTravelTimeFromSource(0.0);
		nodesToVisit.add(sourceInNetwork);
		
		// main loop, stop when priority queue is empty (no more nodes to visit)
		while (!nodesToVisit.isEmpty()) {
			
			// remove node with minimum distanceFromSource from priority queue and use it as activeNode
	        Node activeNode = nodesToVisit.remove();
	        
	       // stop if path to target was found
    		if (activeNode.equals(this.target)) {
    			
    			// generate path from source to target
    			List<Node> path = path();
    			
    			// print result to console
    			String pathString = "";
    			for (Node node : path) {
    				pathString += node.toString() + "\n";
    			}
    			System.out.println("--------------------------------------------------------------------------------------");
    			System.out.println("Shortest Path was found!");
    			System.out.println("");
    			System.out.println("distance from source to target: " + distanceToTarget() + " km");
    			System.out.println("travel time from source to target: " + travelTimeToTarget() + " min");
    			System.out.println("");
    			System.out.println("Path:");
    			System.out.println(pathString);
    			return;
    		}
	        
	        // mark activeNode as visited in nodes list of road network graph
    		this.roadNetwork.getNodes().get(activeNode.getCoordinates()).setVisited(true);
	        
	        // look at all not yet visited neighbors of activeNode
	        Iterator<Entry<Node, Edge>> it = activeNode.getNeighborNodes().entrySet().iterator(); 
	        while(it.hasNext()) {
	        	
	        	Map.Entry<Node, Edge> neighborEntry = it.next();
	        	Node neighbor = this.roadNetwork.getNodes().get(neighborEntry.getKey().getCoordinates());
	        	
	        	// skip if neighbor was already visited
	        	if(neighbor.isVisited()) {
	        		continue;
	        	}
				
				// otherwise compute temporary distance of neighbor by summing up the distance of the activeNode with the length of the connecting edge
        		double tentativeDistanceFromSource = activeNode.getDistanceFromSource() + neighborEntry.getValue().getDistance();
        		// otherwise compute temporary travel time of neighbor by summing up the travel time of the activeNode with the travel time of the connecting edge
        		double tentativeTravelTimeFromSource = activeNode.getTravelTimeFromSource() + neighborEntry.getValue().getTravelTime();
        		
        		// skip if neighbor already contained in priority queue and its old distance is smaller/equal to temporary distance
        		if(nodesToVisit.contains(neighbor) && tentativeDistanceFromSource >= neighbor.getDistanceFromSource()) {
        			continue;
        		}
        		
        		// otherwise
        		// set parent node
        		neighbor.setParent(activeNode);
        		// update distanceFromSource
        		neighbor.setDistanceFromSource(tentativeDistanceFromSource);
        		// update travelTimeFromSource
        		neighbor.setTravelTimeFromSource(tentativeTravelTimeFromSource);
        		
        		// estimate distance  to target
        		double estimatedDistanceToTarget = neighbor.getCoordinates().computeDistance(this.target.getCoordinates());
        		// estimate total path length from source to target using current neighbor
        		double estimatedPathLength = neighbor.getDistanceFromSource() + estimatedDistanceToTarget;
        		neighbor.setEstimatedPathLength(estimatedPathLength);
        		
        		// refresh estimatedPathLength of neighbor in nodesToVisit or add it if not yet contained
        		if (nodesToVisit.contains(neighbor)) {
        			nodesToVisit.remove(neighbor);
        		}
        		nodesToVisit.add(neighbor);
	        }  
	    }
	}

	/**
	 * Returns distance from source to target (shortest path result).
	 * @return distance in km
	 */
	public double distanceToTarget() {
		// distance from source to target in km
		return this.roadNetwork.getNodes().get(this.target.getCoordinates()).getDistanceFromSource() / 1000;
	}
	
	/**
	 * Returns travel time from source to target on shortest path.
	 * @return time in min
	 */
	public double travelTimeToTarget() {
		// travel time from source to target in min
		return this.roadNetwork.getNodes().get(this.target.getCoordinates()).getTravelTimeFromSource() / 60;
	}
	
	/**
	 * Generates path by building a ordered list of all nodes contained in shortest path between source and target.
	 * @return list of nodes describing shortest path between source and target
	 */
	public List<Node> path() {
		List<Node>  path = new ArrayList<Node>();
		// start with target node
		Node node = this.roadNetwork.getNodes().get(this.target.getCoordinates());
		while(node != null) {
			path.add(node);
			if (node.getParent() != null) {
				// add parent nodes until reaching source node
				node = this.roadNetwork.getNodes().get(node.getParent().getCoordinates());
			} else {
				break;
			}
		}
		// reverse list so that it starts with source node and ends with target node
		Collections.reverse(path);
		return path;
	}
}
