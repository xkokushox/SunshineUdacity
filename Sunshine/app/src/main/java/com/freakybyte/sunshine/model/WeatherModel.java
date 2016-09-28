package com.freakybyte.sunshine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public class WeatherModel {

    @JsonProperty("city")
    private City city;
    @JsonProperty("cod")
    private String cod;
    @JsonProperty("message")
    private Double message;
    @JsonProperty("cnt")
    private Integer cnt;
    @JsonProperty("list")
    private java.util.List<ListModel> list = new ArrayList<>();

    public WeatherModel() {
    }

    /**
     * @return The city
     */
    @JsonProperty("city")
    public City getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    @JsonProperty("city")
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return The cod
     */
    @JsonProperty("cod")
    public String getCod() {
        return cod;
    }

    /**
     * @param cod The cod
     */
    @JsonProperty("cod")
    public void setCod(String cod) {
        this.cod = cod;
    }

    /**
     * @return The message
     */
    @JsonProperty("message")
    public Double getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    @JsonProperty("message")
    public void setMessage(Double message) {
        this.message = message;
    }

    /**
     * @return The cnt
     */
    @JsonProperty("cnt")
    public Integer getCnt() {
        return cnt;
    }

    /**
     * @param cnt The cnt
     */
    @JsonProperty("cnt")
    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    /**
     * @return The list
     */
    @JsonProperty("list")
    public java.util.List<ListModel> getList() {
        return list;
    }

    /**
     * @param list The list
     */
    @JsonProperty("list")
    public void setList(java.util.List<ListModel> list) {
        this.list = list;
    }



}