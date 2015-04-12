package com.nativedevelopment.smartgrid;


public class Location {
	protected double lat;
	protected double lon;

	public Location(double latitude, double longtitude) {
		this.lat = latitude;
		this.lon = longtitude;
	}

	/**
	 * @param location The location to compute the distance to
	 * @return The distance between the two locations in meters
	 */
	public double distanceTo(Location location) {
		double earth_radius = 6371000;
		double dlat = Math.toRadians(location.lat - this.lat);
		double dlon = Math.toRadians(location.lon - this.lon);
		double a = Math.sin(dlat/2) * Math.sin(dlat/2) + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(location.lat)) * Math.sin(dlon/2) * Math.sin(dlon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return earth_radius * c;
	}

}
