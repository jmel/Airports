package org.databox.di.airport.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shield.ShieldPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Class to run ES transport Client
 * @author Jason Melbourne
 */
public class EsClient {

    private static final Logger log = LoggerFactory.getLogger(EsClient.class);

    private Client client;

    public EsClient() {
        setupClient();
    }

    /**
     * setup the ES transport client
     */
    private void setupClient (){

        // Get cluster properties
        Properties prop = new Properties();
        InputStream input = null;

        String esUser = "";
        String esPasswd = "";
        String esClusterID = "";
        String esRegion = "";
        boolean esEnableSsl = true;

        try {

            input = this.getClass().getResourceAsStream("/es.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            esUser = prop.getProperty("shield_user");
            esPasswd = prop.getProperty("shield_password"); // TODO: encrypt
            esClusterID = prop.getProperty("es_cluster_id");
            esRegion = prop.getProperty("es_cluster_region");
            esEnableSsl = Boolean.parseBoolean(prop.getProperty("es_enable_ssl"));

        } catch (IOException e) {
           log.error("Error reading properties file", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Error, could not close input stream", e);
                }
            }
        }

        try {
            // setup ES client settings
            Settings settings = Settings.settingsBuilder()
                    .put("transport.ping_schedule", "5s")
                    .put("cluster.name", esClusterID)
                    .put("action.bulk.compress", false)
                    .put("shield.transport.ssl", esEnableSsl)
                    .put("request.headers.X-Found-Cluster", esClusterID)
                    .put("shield.user", new StringBuilder()
                            .append(esUser)
                            .append(":")
                            .append(esPasswd)
                            .toString()) // shield username and password
                    .build();

            // construct hostname
            String hostname = new StringBuilder()
                    .append(esClusterID)
                    .append(".")
                    .append(esRegion)
                    .append(".aws.found.io")
                    .toString();

            // Build client
            client = TransportClient.builder()
                    .addPlugin(ShieldPlugin.class)
                    .settings(settings)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), 9343));
            log.info("Returning ES client ", hostname);
        }
        catch (UnknownHostException e) {
            log.error("Unable to connect to client", e);
        }
    }

    /**
     * setup ES index ahead of time, necessary if you want to do a mapping file
     * @param index
     */
    public void setupIndex(String index){
        client.admin().indices().create(new CreateIndexRequest(index)).actionGet();
    }

    /**
     * Add mapping file to ES index
     * @param index
     * @param type
     * @param json
     * @return
     */
    public PutMappingResponse setupMapping(String index, String type, String json) {
        return client.admin().indices()
                .preparePutMapping(index)
                .setType(type)
                .setSource(json)
                .execute().actionGet();
    }

    /**
     * Add mapping file to ES index
     * @param index
     * @param type
     * @param json
     * @return
     */
    public PutMappingResponse setupMapping(String index, String type, XContentBuilder json) {
        return client.admin().indices()
                .preparePutMapping(index)
                .setType(type)
                .setSource(json)
                .execute().actionGet();
    }

    /**
     * Add data to ES Index record by record
     * @param index
     * @param type
     * @param json
     * @return
     */
    public IndexResponse putData(String index, String type, String json) {
        return client.prepareIndex(index, type)
                .setSource(json)
                .get();
    }

    /**
     * Add data to ES Index record by record
     * @param index
     * @param type
     * @param json
     * @return
     */
    public IndexResponse putData(String index, String type, byte[] json) {
        return client.prepareIndex(index, type)
                .setSource(json)
                .get();
    }

    /**
     * close the ES transport client
     */
    public void close(){
        client.close();
    }

    /**
     * play with ES client
     * @param args
     */
    public static void main (String [] args){
        EsClient esClient = new EsClient();
        String index = "foodyyou";
        String type = "yummy";
        String json = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        esClient.setupIndex(index);
        IndexResponse response = esClient.putData(index, type, json);

        log.info("SUCCESS Have client and inserted record");
        esClient.close();
    }

    /**
     * return the client if needed
     * @return
     */
    public Client getClient() {
        return client;
    }
}
