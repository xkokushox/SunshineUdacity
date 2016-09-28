package com.freakybyte.sunshine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Jose Torres on 28/09/2016.
 */

public class City {
    public City() {
    }

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("coord")
    private Coord coord;
    @JsonProperty("country")
    private String country;
    @JsonProperty("population")
    private Integer population;

    /**
     * @return The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The coord
     */
    @JsonProperty("coord")
    public Coord getCoord() {
        return coord;
    }

    /**
     * @param coord The coord
     */
    @JsonProperty("coord")
    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    /**
     * @return The country
     */
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The population
     */
    @JsonProperty("population")
    public Integer getPopulation() {
        return population;
    }

    /**
     * @param population The population
     */
    @JsonProperty("population")
    public void setPopulation(Integer population) {
        this.population = population;
    }

}