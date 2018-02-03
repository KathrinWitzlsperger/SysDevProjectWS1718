package de.lmu.datascience.sysdev.TCPClientServer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.lmu.datascience.sysdev.POJO.RequestBody;
import de.lmu.datascience.sysdev.QuadTree.QuadTree;
import de.lmu.datascience.sysdev.RoadNetwork.Coordinate;
import de.lmu.datascience.sysdev.RoadNetwork.Network;
import de.lmu.datascience.sysdev.RoadNetwork.Node;
import de.lmu.datascience.sysdev.ShortestPath.ShortestPathAlgorithm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.Geometry;
import org.geojson.LineString;
import org.geojson.LngLatAlt;

/**
 * Represents Requesthandler threads that are responsible for finding the shortest path for a given request.
 * @author Kathrin Witzlsperger
 */
public class RequestHandler implements Runnable{
    // client socket to read the request 
    private Socket socket;
    
    // server data being handed down from the listener
    private Network roadNetwork;
    private QuadTree quadTree;
    
    // thread for running the RequestHandler
    private Thread thread;
    // number for generating new ThreadIDs
    private static long threadID = 0;
    
    
    /**
     * Constructor
     * @param socket
     * @param mapDataFileName
     */
    public RequestHandler(Socket socket, String mapDataFileName){
       this.socket = socket;
       this.roadNetwork = new Network(mapDataFileName);
       this.quadTree = new QuadTree();
    }
    
    
    /***
     * Reads the request from the socket and outputs the result as a GeoJson String.
     */
    public void run() {
        InputStream inputStream = null;
        DataOutputStream outputStream = null;
        String response = "";
        
        try {
        	// create input stream
            inputStream = this.socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            // read request string
            String request = dataInputStream.readUTF();
            
            try {
	            // convert request string to POJO (Java Object)
				RequestBody requestBody = new ObjectMapper().readValue(request, RequestBody.class);
				
				// access request information
				double originLat = requestBody.getOrigin().getLat();
				double originLon = requestBody.getOrigin().getLon();
				System.out.println("origin: " + originLat + ", " + originLon);
				double destinationLat = requestBody.getDestination().getLat();
				double destinationLon = requestBody.getDestination().getLon();
				System.out.println("destination: " + destinationLat + ", " + destinationLon);
	            
				// build road network graph
		        this.roadNetwork.buildRoadNetworkFromGeoJsonFile();
		        // build quad tree needed to determine closest node in road network
		        this.quadTree.buildQuadTreeFromRoadNetwork(this.roadNetwork);
		        
				// find corresponding nodes in road network graph
				Node origin = findCoordinatesInRoadNetworkGraph(originLat, originLon);
				Node destination = findCoordinatesInRoadNetworkGraph(destinationLat, destinationLon);
				
				// try to find shortest path
				ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(this.roadNetwork, origin, destination);
			    algorithm.findShortestPath();
				List<Node> path = algorithm.path();
				
				// needed for GeoJson response
				double distance = algorithm.distanceToTarget(); // shortest distance
				double travelTime = algorithm.travelTimeToTarget(); // corresponding travel time
				// convert shortest path found to GeoJson string
				response = convertPathToJson(path, distance, travelTime);
				
            } catch (JsonParseException e) {
    			e.printStackTrace();
    		} catch (JsonMappingException e) {
    			e.printStackTrace();
    		}
            
            // create output stream
            outputStream = new DataOutputStream(socket.getOutputStream());
            // output GeoJson string
            outputStream.writeUTF(response);
            outputStream.flush();
            outputStream.close();
            
        } catch (IOException e) {
            System.err.println("IOException occurred while processing request.");
            e.printStackTrace();
        }
    }
    
    /**
	 * Finds node (position) in road network graph that corresponds to given latitude and longitude (closest position)
	 * @param lat latitude position to look for
	 * @param lon longitude position to look for
	 * @return closest position (node) in road network graph
	 */
	private Node findCoordinatesInRoadNetworkGraph(double lat, double lon) {
		
		Coordinate coordinates = new Coordinate(lat, lon);
		
		// check if there exists a node with the same coordinates in road network graph
		if (this.roadNetwork.getNodes().containsKey(coordinates)) {
			return this.roadNetwork.getNodes().get(coordinates);
		}
		// if not look for closest node
		return new Node(this.quadTree.findClosestNode(coordinates));
	}
    
    /**
     * Converts found shortest path to GeoJson String.
     * @param path list of nodes forming shortest path
     * @param distance from source to target
     * @param travelTime from source to target
     * @return GeoJson string representation of shortest path found
     */
    private String convertPathToJson(List<Node> path, double distance, double travelTime) {
    	
    	String response = "";
    	
    	// generate new feature collection and feature
	    FeatureCollection featureCollection = new FeatureCollection();
	    Feature feature = new Feature();
	    
	    // generate LngLatAlt positions list from shortest path's node list
	    List<LngLatAlt> coordinatesOfShortestPath = new ArrayList<LngLatAlt>();
	    Node node = null;
	    for (Iterator<Node> it = path.iterator(); it.hasNext();) {
	    	node = it.next();
	    	LngLatAlt lngLatAlt = new LngLatAlt(node.getCoordinates().getLon(), node.getCoordinates().getLat());
	    	coordinatesOfShortestPath.add(lngLatAlt);
	    }
	    // generate LineString with LngLatAlt positions representing the shortest path
	    Geometry<LngLatAlt> shortestPath = new LineString();
	    shortestPath.setCoordinates(coordinatesOfShortestPath);
	    feature.setGeometry(shortestPath);
	    
	    // add properties for shortest path found
	    Map<String, Object> costs = new HashMap<String, Object>();
	    costs.put("Distance", distance);
	    costs.put("Travel_Time", travelTime);
	    feature.setProperty("costs" , costs);
	    
	    // add it to feature collection
	    featureCollection.add(feature);
	    
	    // generate GeoJson String output
		try {
			response = new ObjectMapper().writeValueAsString(featureCollection);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return response;
    }
    
    /***
     * Starts a new runnable.
     */
    public void start() {
        threadID++;
        String threadName = "RequestHandler" + (threadID - 1);
        this.thread = new Thread(this, threadName);
        this.thread.start();
    }
}
