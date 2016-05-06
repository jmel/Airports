package org.databox.di.airport.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Class to hold airport status data
 *  @author Jason Melbourne
 */
@JsonIgnoreProperties("city")
public class AirportStatus {

    @JsonProperty
    private String delay;

    @JsonProperty("IATA")
    private String iata;

    @JsonProperty("ICAO")
    private String icao;

    @JsonProperty
    private String state;

    @JsonProperty
    private String name;

    @JsonProperty
    private Weather weather;

    @JsonProperty
    private DelayStatus status;

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public DelayStatus getStatus() {
        return status;
    }

    public void setStatus(DelayStatus status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
