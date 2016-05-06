package org.databox.di.airport.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Class for weather data
 *  @author Jason Melbourne
 */
@JsonIgnoreProperties("meta")
public class Weather {
    private Double visibility;
    private String weather;
    private String temp;
    private Double tempF;
    private Double tempC;
    private String wind;
    private Double windVel;
    private Double windDir;

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Double getTempF() {
        return tempF;
    }

    public void setTempF(Double tempF) {
        this.tempF = tempF;
    }

    public Double getTempC() {
        return tempC;
    }

    public void setTempC(Double tempC) {
        this.tempC = tempC;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public Double getWindVel() {
        return windVel;
    }

    public void setWindVel(Double windVel) {
        this.windVel = windVel;
    }

    public Double getWindDir() {
        return windDir;
    }

    public void setWindDir(Double windDir) {
        this.windDir = windDir;
    }
}
