package com.freakybyte.sunshine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public class Coord {

    @JsonProperty("lon")
    private Float lon;
    @JsonProperty("lat")
    private Float lat;

    /**
     * @return The lon
     */
    @JsonProperty("lon")
    public Float getLon() {
        return lon;
    }

    /**
     * @param lon The lon
     */
    @JsonProperty("lon")
    public void setLon(Float lon) {
        this.lon = lon;
    }

    /**
     * @return The lat
     */
    @JsonProperty("lat")
    public Float getLat() {
        return lat;
    }

    /**
     * @param lat The lat
     */
    @JsonProperty("lat")
    public void setLat(Float lat) {
        this.lat = lat;
    }

}