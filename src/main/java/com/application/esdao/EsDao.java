package com.application.esdao;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class EsDao {

    private static final Logger LOGGER = Logger.getLogger(EsDao.class);
    private static TransportClient client;

    public EsDao() {
        init();
    }

    /**
     * Intialize the transport client for querying elasticsearch, this is our datasource
     */
    private void init() {
        try {
            LOGGER.debug("Initializing Transport Client.....");

            Settings settings = Settings.builder()
                    .put("cluster.name", "elasticsearch")
                    .put("client.transport.sniff", false)
                    .build();
            InetSocketTransportAddress address =
                    new InetSocketTransportAddress(
                            InetAddress.getByName(System.getProperty("es.host")),
                            Integer.valueOf(System.getProperty("es.tcp.port")));

            client = TransportClient.builder()
                    .settings(settings)
                    .build()
                    .addTransportAddress(address);

            LOGGER.debug("ElasticSearch Connected.....");

        } catch (UnknownHostException e) {
            LOGGER.error("Error occured while connecting to elasticsearch. Please verify the host and port configurations. Also elasticsearch clustername is default");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            LOGGER.error("Invalid elasticsearch configuration. Are the System Variables set?.. es.host, and es.tcp.port are required as VM args");
            nfe.printStackTrace();
        } catch (Exception e) {
            LOGGER.error("Error occured while connecting to elasticsearch. Please debug for more clarity");
            e.printStackTrace();
        }
    }

    public SearchResponse getElasticResponse(String esIndex, String esQuery) throws Exception {
        return client
                .prepareSearch(esIndex)
                .setSource(esQuery)
                .execute()
                .actionGet();
    }
}