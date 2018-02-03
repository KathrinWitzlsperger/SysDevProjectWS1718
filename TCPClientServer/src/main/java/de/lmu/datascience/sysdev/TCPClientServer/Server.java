/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lmu.datascience.sysdev.TCPClientServer;

import java.io.IOException;

import de.lmu.datascience.sysdev.QuadTree.QuadTree;
import de.lmu.datascience.sysdev.RoadNetwork.Network;

/**
 * Represents the TCP Server.
 * @author Kathrin Witzlsperger
 */
public class Server {
    /**
     * name of GeoJson File with map data
     */
    public final String mapDataFileName;
    
    public int port = 9595;
    
    /**
     * listener thread for the server
     */
    Listener listener;    
    
    /**
     * Constructor
     */
    public Server(){
        this.mapDataFileName = "mapData.json";
    }
    
    /***
     * Starts the listener thread.
     */    
    private void startListener() {
       listener = new Listener(port, this.mapDataFileName);
       listener.start();
    }

    /***
     * Shutdowns the listener.
     */
    private void shutdown() {       
        listener.shutdown();                
    }
    
    /***
     * Starts a server and shuts it down when enter is pressed.
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException{
    	// create server
        Server server = new Server();
        
        server.startListener();
        System.out.println("SysDev TCP Server started on localhost: " + server.port);
        System.in.read();
        server.shutdown();
        System.exit(0);
    }

    
}
