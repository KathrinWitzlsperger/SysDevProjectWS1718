package de.lmu.datascience.sysdev.RoadNetwork;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.MultiPoint;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents the road network graph.
 * @author Kathrin Witzlsperger
 *
 */
public class Network {
	
	/**
	 * map containing all nodes of this road network graph
	 */
	private Map<Coordinate, Node> nodes;
	
	/**
	 * name of GeoJson file containing map data
	 */
	private final String mapDataFileName;

	/**
	 * Constructor
	 * @param mapDataFileName name of GeoJson File containing map data
	 */
	public Network(String mapDataFileName) {
		// create empty map for nodes
		this.nodes = new HashMap<Coordinate, Node>();
		this.mapDataFileName = mapDataFileName;
	}
	
	/**
	 * Adds an edge (undirected) to the road network graph.
	 * @param lat1 latitude position of first node
	 * @param lon1 longitude position of first node
	 * @param lat2 latitude position of second node
	 * @param lon2 longitude position of second node
	 * @param maxSpeed max speed allowed on road (edge) connecting the nodes
	 */
	public void addEdge(double lat1, double lon1, double lat2, double lon2, double maxSpeed) {
		
		// add nodes to nodes list
		addNode(lat1, lon1); 
		addNode(lat2, lon2);
		
		// get keys to find nodes in nodes list
		Coordinate coordinates1 = new Coordinate(lat1, lon1);
		Coordinate coordinates2 = new Coordinate(lat2, lon2);
		Node node1 = this.nodes.get(coordinates1);
		Node node2 = this.nodes.get(coordinates2);
		
		// add them as each others neighbor
		node1.addNeighbor(node2, maxSpeed);
		node2.addNeighbor(node1, maxSpeed);
	}
	
	/**
	 * Adds a node to the road network graph.
	 * @param lat latitude position
	 * @param lon longitude position
	 */
	public void addNode(double lat, double lon) {
		
		Coordinate coordinates = new Coordinate(lat, lon);
		
		// check if node was already added
		if (!this.nodes.containsKey(coordinates)) {
			Node node = new Node(coordinates);
			this.nodes.put(coordinates, node);
		}
	}
	
	/**
	 * Builds the road network graph by reading in a GeoJson File and generating its nodes and edges.
	 */
	public void buildRoadNetworkFromGeoJsonFile() {
	
		try {
			
			// load GeoJson File
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(mapDataFileName).getFile());
			
			// read content
			FeatureCollection featureCollection = new ObjectMapper().readValue(file, FeatureCollection.class);
			// get list of all features contained
			List<Feature> features = featureCollection.getFeatures();
			
			// iterate over all features
			for(Iterator<Feature> it = features.iterator(); it.hasNext();) {
				
				Feature feature = it.next();
				GeoJsonObject obj = feature.getGeometry();
				
				// if current GeoJsonObject is a road (LineString)
				if(obj instanceof LineString) {
					
					// coordinates of a road consisting of several edges
					List<LngLatAlt> coordinateList = ((LineString) obj).getCoordinates();
					// speed limit on road
					int maxSpeed = feature.getProperty("maxspeed");
					
					// iterate over coordinates and add edges to road network graph
					LngLatAlt current = null;
					LngLatAlt next = null;
					Iterator<LngLatAlt> it1 = coordinateList.iterator();
					while (it1.hasNext()) {
						next = it1.next();
						if (current != null) {
							addEdge(current.getLatitude(),
									current.getLongitude(),
									next.getLatitude(),
									next.getLongitude(),
									maxSpeed);
						}
						current = next;
					}
					
				// if current GeoJsonObject is a list of positions (MultiPoint)
				} else if(obj instanceof MultiPoint) {
					
					// coordinates list of MultiPoint consisting of several positions (nodes)
					List<LngLatAlt> coordinateList = ((MultiPoint) obj).getCoordinates();
					// iterate over coordinates and add nodes to road network graph
					for(Iterator<LngLatAlt> it2 = coordinateList.iterator(); it2.hasNext();) {
						LngLatAlt coordinates = it2.next();
						addNode(coordinates.getLatitude(), coordinates.getLongitude());
					}
					
				}
				
			}
		
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Returns all nodes of this road network graph.
	 * @return map of all nodes that can accessed via their latitude and longitude positions.
	 */
	public Map<Coordinate, Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Creates String representation of this road network graph.
	 * @return string representation of this road network graph
	 */
	@Override
	public String toString() {
		Node node = null;
		String nodes = "";
		Iterator<Entry<Coordinate, Node>> it = this.nodes.entrySet().iterator();
		while (it.hasNext()) {
			node = it.next().getValue();
			nodes += node.toString() + "\n";
		}
		return nodes;
	}

}
