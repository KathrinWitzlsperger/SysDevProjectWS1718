package de.lmu.datascience.sysdev.QuadTree;

import de.lmu.datascience.sysdev.RoadNetwork.Coordinate;

/**
 * Represents a node of the QuadTree.
 * @author Kathrin Witzlsperger
 *
 */
public class QuadNode {
	
	private Coordinate coordinates;

	// children of this node
	private QuadNode northWestQuadNode;
	private QuadNode northEastQuadNode;
	private QuadNode southWestQuadNode;
	private QuadNode southEastQuadNode;
	
	/**
	 * Constructor
	 * @param coordinate
	 */
	public QuadNode(Coordinate coordinates) {
		this.coordinates = coordinates;
		this.northWestQuadNode = null;
		this.northEastQuadNode = null;
		this.southWestQuadNode = null;
		this.southEastQuadNode = null;
	}
	
	/**
	 * Checks if node has children
	 * @return true if it has at least one child, otherwise false
	 */
	public boolean hasChildren() {
		if(this.northWestQuadNode == null && this.northEastQuadNode == null && this.southWestQuadNode == null && this.southEastQuadNode == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * Sets upper left child node.
	 * @param northWestQuadNode
	 */
	public void setNorthWestQuadNode(QuadNode northWestQuadNode) {
		this.northWestQuadNode = northWestQuadNode;
	}

	/**
	 * Sets upper right child node.
	 * @param northEastQuadNode
	 */
	public void setNorthEastQuadNode(QuadNode northEastQuadNode) {
		this.northEastQuadNode = northEastQuadNode;
	}

	/**
	 * Sets lower left child node.
	 * @param southWestQuadNode
	 */
	public void setSouthWestQuadNode(QuadNode southWestQuadNode) {
		this.southWestQuadNode = southWestQuadNode;
	}

	/**
	 * Sets lower right child node.
	 * @param southEastQuadNode
	 */
	public void setSouthEastQuadNode(QuadNode southEastQuadNode) {
		this.southEastQuadNode = southEastQuadNode;
	}

	/**
	 * Returns upper left child node.
	 * @return upper left child
	 */
	public QuadNode getNorthWestQuadNode() {
		return northWestQuadNode;
	}

	/**
	 * Returns upper right child node.
	 * @return upper right child
	 */
	public QuadNode getNorthEastQuadNode() {
		return northEastQuadNode;
	}

	/**
	 * Returns lower left child node.
	 * @return lower left child
	 */
	public QuadNode getSouthWestQuadNode() {
		return southWestQuadNode;
	}

	/**
	 * Returns lower right child node.
	 * @return lower right child
	 */
	public QuadNode getSouthEastQuadNode() {
		return southEastQuadNode;
	}

	/**
	 * Returns coordinate of this node.
	 * @return coordinate
	 */
	public Coordinate getCoordinates() {
		return coordinates;
	}
	

}
