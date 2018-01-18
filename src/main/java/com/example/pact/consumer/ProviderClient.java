package com.example.pact.consumer;

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


    public Person createPerson(Person person) {
        RestTemplate restTemplate = new RestTemplate();
        String url = providerConfig.getUrl()+"/person";
        Person response = restTemplate.postForObject(url, person, Person.class);
        log.info("Created new Person(id={}, name={}, ssn={})", response.getId(), response.getName(), response.getSsn());
        return response;
    }
}
