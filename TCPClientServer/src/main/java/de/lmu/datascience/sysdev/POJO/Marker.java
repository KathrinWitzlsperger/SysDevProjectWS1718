package de.lmu.datascience.sysdev.POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO Marker containing its latitude and longitude position.
 * @author Kathrin Witzlsperger
 *
 */
public class Marker {

	@JsonProperty("lat")
	private Double lat;
	@JsonProperty("lon")
	private Double lon;
	
	
	public Double getLat() {
		return lat;
	}
	
	public Double getLon() {
		return lon;
	}
}
