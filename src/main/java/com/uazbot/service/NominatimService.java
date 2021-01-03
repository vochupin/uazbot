package com.uazbot.service;

import com.uazbot.AppConfig;
import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
public class NominatimService {
    private static final Logger log = Logger.getLogger(NominatimService.class);

    @Autowired
    AppConfig appConfig;

    private static JsonNominatimClient nominatimClient;

    @PostConstruct
    public void initClient() {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        final String baseUrl = appConfig.getNominatimServerUrl();
        final String email = appConfig.getNominatimHeaderEmail();
        nominatimClient = new JsonNominatimClient(baseUrl, httpClient, email);
    }

    public Address getAddress(String from) {
        try {
            List<Address> addressList = nominatimClient.search(from);

            return addressList != null && !addressList.isEmpty() ? addressList.get(0) : null;
        } catch (IOException e) {
            log.error("Nominatim error: " + e.getMessage());
            return null;
        }
    }

}
