package de.lmu.datascience.sysdev.RoadNetwork;

import java.util.HashMap;
import java.util.Map;
/**
 * Represents a node of the road network graph.
 * @author Kathrin Witzlsperger
 *
 */
public class Node implements Comparable<Node> {

	/**
	 * coordinates object describing this node's latitude and longitude position
	 */
	private final Coordinate coordinates;
	
	/**
	 * map containing all neighbor nodes and the connecting edges 
	 * with information about distance, travel time and max speed
	 */
	private Map<Node, Edge> neighborNodes;
	
	// needed for Dijkstra
	private double distanceFromSource;
	private double travelTimeFromSource;
	private boolean visited;
	private Node parent;
	
	// additionally needed for A*
	private double estimatedPathLength;
	
	/**
	 * Constructor
	 * @param coordinates object to describe this node's latitude and longitude position
	 */
	public Node(Coordinate coordinates) {
		
		this.coordinates = coordinates;
		this.neighborNodes = new HashMap<Node, Edge>();
		
		// start values of each node for Dijkstra
		this.distanceFromSource = Double.POSITIVE_INFINITY;
		this.travelTimeFromSource = Double.POSITIVE_INFINITY;
		this.visited = false;
		this.parent = null;
		
		// start value for each node for A*
		this.estimatedPathLength = Double.POSITIVE_INFINITY;
	}
	
	
	
	/**
	 * Adds neighbor node and its connecting edge
	 * with information about distance, travel time and max speed to neighborNodes.
	 * @param neighbor neighbor node
	 * @param maxSpeed max speed allowed on edge connecting this node and its neighbor node
	 */
	public void addNeighbor(Node neighbor, double maxSpeed) {
		Edge edge = new Edge(maxSpeed, this.getCoordinates().computeDistance(neighbor.getCoordinates()));
		//System.out.println(edge.toString());
		this.neighborNodes.put(neighbor, edge);
	}
	
	/**
	 * Compares this node with other node for order by looking at its distance to the target node.
	 * @return a negative integer, zero, or a positive integer as this node is less than, equal to,
	 * or greater than the other obj
	 */
	public int compareTo(Node other) {
        return Double.compare(this.estimatedPathLength, other.estimatedPathLength);
    }
	
	/**
	 * Generates hash code for this node that is needed to use it as key in HashMap.
	 * @return hash code representation of this node
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
		return result;
	}

	/**
	 * Checks if obj and this node are the same by looking at their coordinates.
	 * @return true if this and obj are the same, false if not
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		return true;
	}
	
	/**
	 * Returns coordinates of this node.
	 * @return coordinate object with latitude and longitude coordinates
	 */
	public Coordinate getCoordinates() {
		return coordinates;
	}

	/**
	 * Returns neighbor nodes of this node with connecting edge information.
	 * @return map containing all neighbor nodes and the connecting edges
	 * with information about distance, travel time and max speed.
	 */
	public Map<Node, Edge> getNeighborNodes() {
		return neighborNodes;
	}

	
	/**
	 * Returns parent node of this node (used for Dijkstra).
	 * @return parent node of this node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Sets parent node of this node (used for Dijkstra).
	 * @param parent node of this node
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	/**
	 * Returns if node was already visited (used for Dijkstra).
	 * @return true if it was already visited, false if not
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * Sets if node was already visited (used for Dijkstra).
	 * @param visited true if it was already visited, false if not
	 */
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	/**
	 * Returns the distance from the source node to this node.
	 * @return distance in meters
	 */
	public double getDistanceFromSource() {
		return distanceFromSource;
	}

	/**
	 * Sets the distance from the source node to this node.
	 * @param distanceFromSource distance in meters
	 */
	public void setDistanceFromSource(double distanceFromSource) {
		this.distanceFromSource = distanceFromSource;
	}
	
	/**
	 * Sets the estimated path length of this node to the target node.
	 * @param estimatedPathLength in meters
	 */
	public void setEstimatedPathLength(double estimatedPathLength) {
		this.estimatedPathLength = estimatedPathLength;
	}

	/**
	 * Returns the travel time from the source node to this node.
	 * @return travel time in seconds
	 */
	public double getTravelTimeFromSource() {
		return travelTimeFromSource;
	}

	/**
	 * Sets the travel time from the source node to this node.
	 * @param travelTimeFromSource in seconds
	 */
	public void setTravelTimeFromSource(double travelTimeFromSource) {
		this.travelTimeFromSource = travelTimeFromSource;
	}

	/**
	 * Creates a String representation of this node.
	 * @return string representation of this node
	 */
	@Override
	public String toString() {
		String node = "NODE: " + this.coordinates.toString() + ", distanceFromSource: " + this.distanceFromSource + ", travelTimeFromSource: " + this.travelTimeFromSource + ", estimatedPathLength: " + this.estimatedPathLength;
		if (getParent() != null) {
			node += ", parent: " + this.parent.coordinates.toString();
		}
		return  node;
	}
}
