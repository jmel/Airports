package org.databox.di.airport.domain;

/**
 *  Geo point class for ES
 *  @author Jason Melbourne
 */
public class GeoPoint {

    private Double lat;
    private Double lon;

    public GeoPoint(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
