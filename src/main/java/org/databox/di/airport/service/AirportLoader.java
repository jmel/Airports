package org.databox.di.airport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.databox.di.airport.domain.Airport;
import org.databox.di.airport.domain.AirportStatus;
import org.databox.di.airport.domain.GeoPoint;
import org.databox.di.airport.es.EsClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 *  Class to aggregate airport data and send to ES
 *  @author Jason Melbourne
 */
public class AirportLoader {

    private static final Logger log = LoggerFactory.getLogger(AirportLoader.class);

    private static final String AIRPORT_LIST_PATH = "org/databox/di/airport/data/airport.json";
    private static final String ES_INDEX = "airport"; // TODO:use alias to create index name
    private static final String ES_TYPE = "data";

    private List<Airport> airports = new ArrayList<Airport>();

    public AirportLoader() {
        loadAirports();
    }

    /**
     * Method to load airport list from JSON
     */
    private void loadAirports () {
        ObjectMapper mapper = new ObjectMapper();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(AIRPORT_LIST_PATH).getFile());
        try {
            airports = mapper.readValue(file, new TypeReference<List<Airport>>(){});
        } catch (IOException e) {
            log.error("Unable to read airport data" + e.toString());
        }
        for (Airport airport: airports){
            airport.setLocation(new GeoPoint(airport.getLat(), airport.getLng()));
        }
    }

    /**
     * Method to update the airport status from the FAA
     * e.g., record current weather and any delays
     */
    public void getAirportStatus(){
        ObjectMapper mapper = new ObjectMapper();
        for (Airport airport : airports){
            log.info("Getting status for airport code {}, in country {}", airport.getCode(), airport.getCountry());
            if ("United States".equals(airport.getCountry())) {
                try {
                    URL url = new URL("http://services.faa.gov/airport/status/" + airport.getCode() + "?format=application/json");
                    AirportStatus airportStatus = mapper.readValue(url, AirportStatus.class);
                    airport.setAirportStatus(airportStatus);
                } catch (MalformedURLException e) {
                    log.error("Malformed URL", e);
                } catch (FileNotFoundException e){
                    log.info("No status update available for ", airport.getCode());
                } catch (IOException e) {
                    log.error("Unable to get airport status for " + airport.getCode(), e);
                }
            }
        }
    }

    /**
     * Method to send airport data to ES
     */
    public void sendToEs() {
        // get the esClient connection
        EsClient esClient = new EsClient();
        esClient.setupIndex(ES_INDEX);

        // setup ES mapping for airport
        // TODO: put this into a JSON mapping file and specify mapping for more fields
        // TODO: write a method to read in mapping file
        try {
            XContentBuilder mapping = jsonBuilder().prettyPrint()
                    .startObject()
                    .startObject(ES_TYPE)
                    .startObject("properties")
                        .startObject("location")
                            .field("ES_TYPE", "geo_point")
                        .endObject()
                        .startObject("country")
                            .field("ES_TYPE", "string")
                            .field("ES_INDEX", "not_analyzed")
                        .endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            esClient.setupMapping(ES_INDEX, ES_TYPE, mapping);
        }
        catch (IOException e) {
            log.error("Error making mapping ", e);
            return;
        }

        // create object mapper and send JSON to ES
        ObjectMapper objectMapper = new ObjectMapper();
        for (Airport airport : this.airports){
            try {
                byte[] json = objectMapper.writeValueAsBytes(airport);
                esClient.putData(ES_INDEX, ES_TYPE, json);
            }
            catch (JsonProcessingException e) {
                log.error("Error converting to json", e);
            }
        }
        esClient.close();
    }

    /**
     * Main method to play with code
     * @param args
     */
    public static void main (String [] args){
        // get the airport data
        AirportLoader airportLoader = new AirportLoader();

        // get current airport status
        airportLoader.getAirportStatus();

        // send data to ES
        airportLoader.sendToEs();
    }

    /**
     * get method for airport data
     * @return
     */
    public List<Airport> getAirports() {
        return airports;
    }
}
