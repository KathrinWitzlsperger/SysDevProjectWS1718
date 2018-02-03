/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.lmu.datascience.sysdev.TCPClientServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

import de.lmu.datascience.sysdev.QuadTree.QuadTree;
import de.lmu.datascience.sysdev.RoadNetwork.Network;


/**
 * Represents the listener thread for the server.
 * @author Kathrin Witzlsperger
 */
public class Listener implements Runnable {
    
    
    // data handed down from the server
    private final String mapDataFileName;
    
 // thread object for running the listener
    private Thread t;
    // flag to stop the thread
    private boolean stop;
    
    // port the listener is listening on
    private int PORT;
    
   /**
    * Constructor
    * @param port
    * @param mapDataFileName
    */
    public Listener(int port, String mapDataFileName){
        this.PORT = port;       
        this.mapDataFileName = mapDataFileName;
    }
    
    /**
     * Listens on the ServerSocket and starts RequestHandlers.
     */        
    public void run() {
        stop = false;
        try {
        	// create server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (!stop) {
            	// listen for a connection to be made to this socket and accepts it
                final Socket clientSocket = serverSocket.accept();
                // create and start new request handler threads
                RequestHandler handler = new RequestHandler(clientSocket, this.mapDataFileName);
                handler.start();
            }
            System.out.println("Stopping Listener");
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);                    
        }
    }
    
    /***
     * Starts up the listener if none is running.
     */    
    public void start(){     
      System.out.println("Starting Listener" );
      if (t == null) {
         t = new Thread (this,"Listener");
         t.start ();
      }
    }    
    
    /**
     * Tells the thread to exit the loop.
     */
    public void shutdown() {
       stop = true;
    }
    
}
