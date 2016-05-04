package org.databox.di.airport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.databox.di.airport.domain.Airport;
import org.databox.di.airport.domain.GeoPoint;
import org.databox.di.airport.es.EsClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by jlmelbourne on 5/1/16.
 */
public class AirportLoader {

    private static final Logger log = LoggerFactory.getLogger(AirportLoader.class);

    private List<Airport> airports = new ArrayList<Airport>();


    public void readAirports () {
        ObjectMapper mapper = new ObjectMapper();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("org/databox/di/airport/data/airport.json").getFile());
        try {
            airports = mapper.readValue(file, new TypeReference<List<Airport>>(){});
        } catch (IOException e) {
            log.error("unable to read airport data" + e.toString());
        }
        for (Airport airport: airports){
            airport.setLocation(new GeoPoint(airport.getLat(), airport.getLng()));
        }
    }

    public static void main (String [] args){
        // setup some variables for example
        String index = "airport";
        String type = "data";

        // get the airport data
        AirportLoader airportLoader = new AirportLoader();
        airportLoader.readAirports();

        // get the esClient connection
        EsClient esClient = new EsClient();
        esClient.setupIndex(index);

        // setup mapping for airport
        try {
            XContentBuilder mapping = jsonBuilder().prettyPrint()
                    .startObject()
                    .startObject(type)
                    .startObject("properties")
                        .startObject("location")
                            .field("type", "geo_point")
                        .endObject()
                        .startObject("country")
                            .field("type", "string")
                            .field("index", "not_analyzed")
                        .endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            esClient.setupMapping(index, type, mapping);
        }
        catch (IOException e) {
            log.error("Error making mapping ", e);
            return;
        }

        // create object mapper
        ObjectMapper objectMapper = new ObjectMapper();
        for (Airport airport : airportLoader.getAirports()){
            try {
                byte[] json = objectMapper.writeValueAsBytes(airport);
                esClient.putData(index, type, json);
            }
            catch (JsonProcessingException e) {
                log.error("Error converting to json", e);
            }

        }
    }

    public List<Airport> getAirports() {
        return airports;
    }
}
