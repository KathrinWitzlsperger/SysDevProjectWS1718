package de.lmu.datascience.sysdev.QuadTree;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.lmu.datascience.sysdev.RoadNetwork.Coordinate;
import de.lmu.datascience.sysdev.RoadNetwork.Network;
import de.lmu.datascience.sysdev.RoadNetwork.Node;

/**
 * Represents a point quad tree.
 * @author Kathrin Witzlsperger
 *
 */
public class QuadTree {
	
	/**
	 * root node of point quad tree
	 */
	private QuadNode root;
	
	/**
	 * Constructor
	 */
	public QuadTree() {
		this.root = null;
	}
	
	/**
	 * Builds quadtree from road network graph
	 * @param roadNetwork
	 */
	public void buildQuadTreeFromRoadNetwork(Network roadNetwork) {
		
		// compute value for root as center of all nodes in road network graph
		double minLat = Double.POSITIVE_INFINITY;
		double maxLat = Double.NEGATIVE_INFINITY;
		double minLon = Double.POSITIVE_INFINITY;
		double maxLon = Double.NEGATIVE_INFINITY;
		
		Iterator<Entry<Coordinate, Node>> it = roadNetwork.getNodes().entrySet().iterator(); 
        while(it.hasNext()) {
        	Map.Entry<Coordinate, Node> entry = it.next();
        	Coordinate coordinate = entry.getKey();
        	
        	if (coordinate.getLat() < minLat) {
        		minLat = coordinate.getLat();
        	} else if (coordinate.getLat() > maxLat) {
        		maxLat = coordinate.getLat();
        	}
        	if (coordinate.getLon() < minLon) {
        		minLon = coordinate.getLon();
        	} else if (coordinate.getLon() > maxLon) {
        		maxLon = coordinate.getLon();
        	}
        }
        this.root = new QuadNode(new Coordinate((maxLat + minLat) / 2, (maxLon + minLon) / 2));
        
        // insert all nodes in road network graph in quadtree
        it = roadNetwork.getNodes().entrySet().iterator(); 
        while(it.hasNext()) {
        	Map.Entry<Coordinate, Node> entry = it.next();
        	Coordinate coordinates = entry.getKey();
        	insert(coordinates);
        }
	}
	
	/**
	 * Inserts new node.
	 * @param coordinates
	 * @return true if insert was successful, false if not
	 */
	public boolean insert(Coordinate coordinates) {
		// first node inserted is root
		if(this.root == null) {
			this.root = new QuadNode(coordinates);
			return true;
			
		} else {
			
			// start at root
			QuadNode currentQuadNode = this.root;
			
			while(true) {
				
				// west
				if (coordinates.getLat() <= currentQuadNode.getCoordinates().getLat()) {
					// northwest
					if(coordinates.getLon() >= currentQuadNode.getCoordinates().getLon()) {
						// if no child insert
						if (currentQuadNode.getNorthWestQuadNode() == null) {
							currentQuadNode.setNorthWestQuadNode(new QuadNode(coordinates));
							return true;
						} else {
							// otherwise move to child
							currentQuadNode = currentQuadNode.getNorthWestQuadNode();
						}
					} 
					// southwest
					else {
						// if no child insert
						if (currentQuadNode.getSouthWestQuadNode() == null) {
							currentQuadNode.setSouthWestQuadNode(new QuadNode(coordinates));
							return true;
						} else {
							// otherwise move to child
							currentQuadNode = currentQuadNode.getSouthWestQuadNode();
						}
					}
				} 
				// east
				else {
					// northeast
					if (coordinates.getLon() >= currentQuadNode.getCoordinates().getLon()) {
						// if no child insert
						if (currentQuadNode.getNorthEastQuadNode() == null) {
							currentQuadNode.setNorthEastQuadNode(new QuadNode(coordinates));
							return true;
						} else {
							// otherwise move to child
							currentQuadNode = currentQuadNode.getNorthEastQuadNode();
						}
					}
					// southeast 
					else {
						// if no child insert
						if (currentQuadNode.getSouthEastQuadNode() == null) {
							currentQuadNode.setSouthEastQuadNode(new QuadNode(coordinates));
							return true;
						} else {
							// otherwise move to child
							currentQuadNode = currentQuadNode.getSouthEastQuadNode();
						}
					}
				}
			}
		}
	}
		
	/**
	 * Searches closest coordinates to given coordinates.
	 * @param coordinates to look for in quadtree
	 * @return closest coordinates found
	 */
	public Coordinate findClosestNode(Coordinate coordinates) {
		
		// closest coordinates in road network graph
		Coordinate closestCoordinates = null;
		// distance to closest coordinates
		double bestDistance = Double.POSITIVE_INFINITY;
		
		// closest nodes found in child quad trees
		Coordinate closestCoordinatesOfChildQuadTreeNorthWest = null;
		Coordinate closestCoordinatesOfChildQuadTreeNorthEast = null;
		Coordinate closestCoordinatesOfChildQuadTreeSouthWest = null;
		Coordinate closestCoordinatesOfChildQuadTreeSouthEast = null;
		
		// start at root but set it to infinite because root is no node of the road network graph
		double currentDistance = Double.POSITIVE_INFINITY;
		
		// west
		if (coordinates.getLat() <= this.root.getCoordinates().getLat() || (this.root.getCoordinates().getLat() - coordinates.getLat()) <= 0.0000001) {
			// northwest
			if (coordinates.getLon() >= this.root.getCoordinates().getLon() || (this.root.getCoordinates().getLon() - coordinates.getLon()) <= 0.0000001) {
				// search north west child quadtree
				if (this.root.getNorthWestQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeNorthWest = searchInChildQuadTree(coordinates, this.root.getNorthWestQuadNode(), closestCoordinates, bestDistance);
				}
			}
			// southwest
			if (coordinates.getLon() <= this.root.getCoordinates().getLon() || (coordinates.getLon() - this.root.getCoordinates().getLon()) <= 0.0000001) {
				// search south west child quadtree
				if (this.root.getSouthWestQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeSouthWest = searchInChildQuadTree(coordinates, this.root.getSouthWestQuadNode(), closestCoordinates, bestDistance);
				}
			}
		} 
		// east
		if (coordinates.getLat() >= this.root.getCoordinates().getLat() || (coordinates.getLat() - this.root.getCoordinates().getLat()) <= 0.0000001) {
			// northeast
			if (coordinates.getLon() >= this.root.getCoordinates().getLon() || (this.root.getCoordinates().getLon() - coordinates.getLon()) <= 0.0000001) {
				// search north east child quadtree
				if (this.root.getNorthEastQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeNorthEast = searchInChildQuadTree(coordinates, this.root.getNorthEastQuadNode(), closestCoordinates, bestDistance);
				}
			}
			// southeast
			if (coordinates.getLon() <= this.root.getCoordinates().getLon() || (coordinates.getLon() - this.root.getCoordinates().getLon()) <= 0.0000001) {
				// search south east child quadtree
				if (this.root.getSouthEastQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeSouthEast = searchInChildQuadTree(coordinates, this.root.getSouthEastQuadNode(), closestCoordinates, bestDistance);
				}
			}	
		}
		// check closest coordinates found in north west child
		if (closestCoordinatesOfChildQuadTreeNorthWest != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeNorthWest.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeNorthWest;
	    	}
			
		}
		// check closest coordinates found in north east child
		if (closestCoordinatesOfChildQuadTreeNorthEast != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeNorthEast.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeNorthEast;
	    	}
			
		}
		// check closest coordinates found in south west child
		if (closestCoordinatesOfChildQuadTreeSouthWest != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeSouthWest.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeSouthWest;
	    	}
			
		}
		// check closest coordinates found in south east child
		if (closestCoordinatesOfChildQuadTreeSouthEast != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeSouthEast.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeSouthEast;
	    	}
			
		}
		return closestCoordinates;
	}
	
	/**
	 * Searches closest coordinates to given coordinates in child quadtree.
	 * @param coordinates coordinates to look for
	 * @param currentQuadNode current position in quadtree
	 * @param closestQuadNode best found coordinates so far
	 * @param bestDistance distance to best found closest coordinates
	 * @return closest coordinates found in child quadtree
	 */
	public Coordinate searchInChildQuadTree(Coordinate coordinates, QuadNode currentQuadNode, Coordinate closestCoordinates, double bestDistance) {
		
		// closest nodes found in child quad trees
		Coordinate closestCoordinatesOfChildQuadTreeNorthWest = null;
		Coordinate closestCoordinatesOfChildQuadTreeNorthEast = null;
		Coordinate closestCoordinatesOfChildQuadTreeSouthWest = null;
		Coordinate closestCoordinatesOfChildQuadTreeSouthEast = null;
				
		// start at current quad node
		double currentDistance = currentQuadNode.getCoordinates().computeDistance(coordinates);
				
		if (currentDistance < bestDistance) {
    		bestDistance = currentDistance;
    		closestCoordinates = currentQuadNode.getCoordinates();
    	}
			
		// west
		if (coordinates.getLat() <= currentQuadNode.getCoordinates().getLat() || (currentQuadNode.getCoordinates().getLat() - coordinates.getLat()) <= 0.0000001) {
			// northwest
			if (coordinates.getLon() >= currentQuadNode.getCoordinates().getLon() || (coordinates.getLon() - currentQuadNode.getCoordinates().getLon()) <= 0.0000001) {
				if (currentQuadNode.getNorthWestQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeNorthWest = searchInChildQuadTree(coordinates, currentQuadNode.getNorthWestQuadNode(), closestCoordinates, bestDistance);
				}
			}
			// southwest
			if (coordinates.getLon() <= currentQuadNode.getCoordinates().getLon() || (currentQuadNode.getCoordinates().getLon() - coordinates.getLon()) <= 0.0000001) {
				if (currentQuadNode.getSouthWestQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeSouthWest = searchInChildQuadTree(coordinates, currentQuadNode.getSouthWestQuadNode(), closestCoordinates, bestDistance);
				}
			}
			
		} 
		// east
		if (coordinates.getLat() >= currentQuadNode.getCoordinates().getLat() || (coordinates.getLat() - currentQuadNode.getCoordinates().getLat()) <= 0.0000001) {
			// northeast
			if (coordinates.getLon() >= currentQuadNode.getCoordinates().getLon() || (coordinates.getLon() - currentQuadNode.getCoordinates().getLon()) <= 0.0000001) {
				if (currentQuadNode.getNorthEastQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeNorthEast = searchInChildQuadTree(coordinates, currentQuadNode.getNorthEastQuadNode(), closestCoordinates, bestDistance);
				}
			}
			// southeast
			if (coordinates.getLon() <= currentQuadNode.getCoordinates().getLon() || (currentQuadNode.getCoordinates().getLon() - coordinates.getLon()) <= 0.0000001) {
				if (currentQuadNode.getSouthEastQuadNode() != null) {
					closestCoordinatesOfChildQuadTreeSouthEast = searchInChildQuadTree(coordinates, currentQuadNode.getSouthEastQuadNode(), closestCoordinates, bestDistance);
				}
			}	
		}
		// check closest coordinates found in north west child
		if (closestCoordinatesOfChildQuadTreeNorthWest != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeNorthWest.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeNorthWest;
	    	}
			
		}
		// check closest coordinates found in north east child
		if (closestCoordinatesOfChildQuadTreeNorthEast != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeNorthEast.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeNorthEast;
	    	}
			
		}
		// check closest coordinates found in south west child
		if (closestCoordinatesOfChildQuadTreeSouthWest != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeSouthWest.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeSouthWest;
	    	}
			
		}
		// check closest coordinates found in south east child
		if (closestCoordinatesOfChildQuadTreeSouthEast != null) {
			currentDistance = closestCoordinatesOfChildQuadTreeSouthEast.computeDistance(coordinates);
			if (currentDistance < bestDistance) {
	    		bestDistance = currentDistance;
	    		closestCoordinates = closestCoordinatesOfChildQuadTreeSouthEast;
	    	}
			
		}
		return closestCoordinates;
	}
}
