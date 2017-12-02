package com.example.pact.consumer;

import au.com.dius.pact.consumer.*;

import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderPacts {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("pact-provider", this);

    @Test
    public void createPerson() throws JsonProcessingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        Person person = Person.builder().name("Roger Antonsen").ssn("01039012345").build();

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer")
                .hasPactWith("pact-provider")
                .uponReceiving("Create new person")
                .path("/person")
                .method("POST")
                .headers(headers)
                .body(objectMapper.writeValueAsString(person))
                .willRespondWith()
                .headers(headers)
                .status(HttpStatus.CREATED.value())
                .body("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"name\": \"Roger Antonsen\",\n" +
                        "  \"ssn\": \"01039012345\"\n" +
                        "}")
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockProvider -> {
            Person response = new ProviderClient(new ProviderConfig(mockProvider.getUrl())).createPerson(person);

            assertEquals(response.getName(), person.getName());
            assertEquals(response.getSsn(), person.getSsn());
            assertTrue(response.getId() != null);
        });

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

}
