package de.lmu.datascience.sysdev.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO RequestBody containing two markers for origin and destination.
 * @author Kathrin Witzlsperger
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestBody {
	
	@JsonProperty("s")
	private Marker origin;
	@JsonProperty("t")
	private Marker destination;
	
	public Marker getOrigin() {
		return origin;
	}
	
	public Marker getDestination() {
		return destination;
	}
	
}
