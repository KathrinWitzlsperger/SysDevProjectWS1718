package de.lmu.datascience.sysdev.SysDevInterface;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Represents the server.
 * @author Kathrin Witzlsperger
 *
 */
public class SysDevJerseyServer {
	
	/**
	 *  base URL the Grizzly HTTP server will listen on
	 */
    private static String BASE_URL = "http://localhost:9090/sysdev/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String uri) {
        
    	// create a ResourceConfig that scans for JAX-RS resources and providers in the corresponding package
        ResourceConfig rc = new ResourceConfig().packages("de.lmu.datascience.sysdev.SysDevInterface");
        rc.register(new CORSFilter());
        
        // create and start a new instance of grizzly HttpServer exposing the Jersey application at BASE_URL
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(uri), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	// start server
        final HttpServer server = startServer(BASE_URL);
        System.out.println(String.format("SysDev Jersey Server started with WADL available at %sapplication.wadl\nHit enter to shut server down.", BASE_URL));
        // read
        System.in.read();
        // shutdown
        server.shutdown();
    }

    @Provider
    public static class CORSFilter implements ContainerResponseFilter {

        public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
            response.getHeaders().add("Access-Control-Allow-Origin", "*");
            response.getHeaders().add("Access-Control-Allow-Headers","origin, content-type, accept, authorization");
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            response.getHeaders().add("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS, HEAD");
        }
    }

}
