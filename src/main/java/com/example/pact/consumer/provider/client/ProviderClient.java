package com.example.pact.consumer.provider.client;

import com.example.pact.consumer.provider.config.ProviderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class ProviderClient {

    private ProviderConfig providerConfig;

    @Autowired
    public ProviderClient(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    public String getUuid(String data) {
        RestTemplate restTemplate = new RestTemplate();
        String url = providerConfig.getUrl()+"/uuid/"+data;
        String response = restTemplate.getForObject(url, String.class);
        log.info("Getting uuid: {}", response);

        return response;
    }
}
