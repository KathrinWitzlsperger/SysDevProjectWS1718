package de.lmu.datascience.sysdev.RoadNetwork;

/**
 * Represents the coordinates of a node.
 * @author Kathrin Witzlsperger
 *
 */
public class Coordinate {
	
	/**
	 * latitude position
	 */
	private double lat;
	/**
	 * longitude position
	 */
	private double lon;
	
	/**
	 * Constructor
	 * @param lat latitude position
	 * @param lon longitude position
	 */
	public Coordinate(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * Returns the latitude position.
	 * @return latitude
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * Returns the longitude position.
	 * @return longitude
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Computes distance between this node and other node with latitude- and longitude-coordinates
	 * using haversine formula.
	 * @return distance between nodes in meters
	 */
	public double computeDistance(Coordinate other) {
		
		// radius of the earth
		final int EARTH_RADIUS_KM = 6371;

	    double latDistance = Math.toRadians(other.lat - this.lat);
	    double lonDistance = Math.toRadians(other.lon - this.lon);
	    
	    double a = Math.pow(Math.sin(latDistance / 2), 2) + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(this.lat)) * Math.pow(Math.sin(lonDistance / 2), 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    // convert to meters
	    double distance = Math.pow((EARTH_RADIUS_KM * c * 1000), 2); 
	    return Math.sqrt(distance);
		
	}
	
	/**
	 * Generates hash code for this coordinate that is needed to use it as key in HashMap.
	 * @return hash code representation of this coordinate
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Checks if obj and this coordinate are the same by looking at their latitude and longitude position.
	 * @return true if this and obj are the same, false if not
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		return true;
	}

	/**
	 * Creates String representation of this coordinate.
	 * @return string representation of this coordinate.
	 */
	@Override
	public String toString() {
		return "Lat: " + this.lat + ", Lon: " + this.lon;
	}

}
