package de.lmu.datascience.sysdev.SysDevInterface;

import javax.ws.rs.core.MediaType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.lmu.datascience.sysdev.POJO.RequestBody;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.ws.rs.Consumes;

/**
 * Represents the root resource class using JAX-RS annotations.
 * @author Kathrin Witzlsperger
 *
 */
@Path("services/directions")
public class SysDevJerseyResource {
	
	private static final String GOOGLE_DIRECTIONS = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_KEY = "key=AIzaSyDD3mIAFIetKZUVJ8EGXxBU4iBDu4w93IU";
	
    /**
     * Receives input in form of URI, communicates with Google Directions server to receive routing information.
     * @param originLat latitude position of origin
     * @param originLon longitude position of origin
     * @param destinationLat latitude position of destination
     * @param destinationLon longitude position of destination
     * @return Json string with routing response
     */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/uri")
    public static String directionURI(@QueryParam("originLat") final double originLat,
    		          		   @QueryParam("originLon") final double originLon, 
    		                   @QueryParam("destinationLat") final double destinationLat, 
    		                   @QueryParam("destinationLon") final double destinationLon) {
		// use Google Directions server
		String output = googleDirectionsJersey(originLat, originLon, destinationLat, destinationLon);
    	return output;
    }
	
	/**
	 * Receives input in form of a Json Object, communicates with Google Directions server to receive routing information.
	 * @param input Json string representing a Json Object with origin and destination positions
	 * @return Json string with routing response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/obj")
	public static String directionOBJ(String input) {
		
		String output = "";
		try {
			
			// convert input string to POJO (Java Object)
			RequestBody requestBody = new ObjectMapper().readValue(input, RequestBody.class);
			
			// read input information
			double originLat = requestBody.getOrigin().getLat();
			double originLon = requestBody.getOrigin().getLon();
			double destinationLat = requestBody.getDestination().getLat();
			double destinationLon = requestBody.getDestination().getLon();
			
			// use Google Directions server
			output = googleDirectionsJersey(originLat, originLon, destinationLat, destinationLon);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return output;
	}
	
	/**
	 * Receives input in form of an Json Object, communicates with TCP Server to get routing information.
	 * @param input Json string representing a Json Object with origin and destination positions
	 * @return Json string with routing response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public static String shortestPath(String request) {
		 Socket socket = null; 
		 String response = "";
	        
	        try {
	        	// create socket
	            socket = new Socket("localhost", 9595);
		           
	            // send request
		        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		        dataOutputStream.writeUTF(request);
		        dataOutputStream.flush();
		        
		        // get routing response
		        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		        response = (String) dataInputStream.readUTF();

	        } catch (UnknownHostException e) {
	            System.out.println("Unknown Host...");
	            e.printStackTrace();
	        } catch (IOException e) {
	            System.out.println("IO problems...");
	            e.printStackTrace();
	        } finally {
	            if (socket != null)
	                try {
	                    socket.close();
	                    System.out.println("Socket closed...");
	                } catch (IOException e) {
	                    System.out.println("Closing Socket not possible...");
	                    e.printStackTrace();
	                }
	        } 
	        return response;
	 }
	
	/**
	 * Communicates with the Google Directions server, sends search query and receives response.
	 * @param originLat latitude position of origin
     * @param originLon longitude position of origin
     * @param destinationLat latitude position of destination
     * @param destinationLon longitude position of destination
	 * @return Json string with routing response
	 */
    public static String googleDirectionsJersey(double originLat, double originLon, double destinationLat, double destinationLon) {
        
    	// create client
    	Client client = Client.create();
    	
    	// send request
        WebResource webResource = client.resource(buildDirectionsQueryString(originLat, originLon, destinationLat, destinationLon));
        System.out.println(buildDirectionsQueryString(originLat, originLon, destinationLat, destinationLon));
        
        // get response
        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        
        // test if it was successful, if not throw exception
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed: HTTP error code: " + response.getStatus());
        }
        
        // convert response to String
        String responseString = response.getEntity(String.class);
        
        return responseString;
    }

    /**
     * Builds query string using input parameters that is needed for Google Directions server.
     * @param originLat latitude position of origin
     * @param originLon longitude position of origin
     * @param destinationLat latitude position of destination
     * @param destinationLon longitude position of destination
     * @return query string for Google Directions server
     */
    public static String buildDirectionsQueryString(double originLat, double originLon, double destinationLat, double destinationLon){
        String origin = "origin=" + originLat + "," + originLon;
        String destination = "destination=" + destinationLat + "," + destinationLon;
        return GOOGLE_DIRECTIONS + origin + "&" + destination + "&" + GOOGLE_KEY;
    }

}
