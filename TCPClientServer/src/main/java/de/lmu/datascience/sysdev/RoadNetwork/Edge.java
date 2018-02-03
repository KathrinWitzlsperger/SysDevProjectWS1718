package de.lmu.datascience.sysdev.RoadNetwork;

/**
 * Represents an edge of the road network graph connecting two nodes.
 * @author Kathrin Witzlsperger
 * 
 */
public class Edge {

	/**
	 * distance between nodes, length of edge
	 */
	private double distance;
	/**
	 * time needed to travel the edge
	 */
	private double travelTime;
	/**
	 * maximum speed allowed to travel on edge
	 */
	private double maxSpeed;
	
	/**
	 * Constructor
	 * @param maxSpeed max speed allowed on the road represented by this edge
	 * @param distance length of this edge (distance between two nodes connected by this edge)
	 */
	public Edge (double maxSpeed, double distance) {
		this.maxSpeed = maxSpeed;
		this.distance = distance;
		this.travelTime = computeTravelTime();
	}
	
	/**
	 * Computes time needed to pass edge (road).
	 * @return travel time in seconds
	 */
	public double computeTravelTime() {
		double travelTime = this.distance / ((double) this.maxSpeed / 3.6); // in seconds
		return travelTime;
	}

	/**
	 * Returns length of this edge (distance between two nodes connected by this edge)
	 * @return distance between two nodes connected by this edge
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Returns time need to pass edge (road).
	 * @return travel time on this edge (road)
	 */
	public double getTravelTime() {
		return travelTime;
	}
	
	/**
	 * Creates String representation of this edge.
	 * @return  string representation of this edge
	 */
	@Override
	public String toString() {
		return "EDGE: " + "Distance: " + this.distance + " Travel_Time: " + this.travelTime + " MaxSpeed: " + this.maxSpeed;
	}

}
