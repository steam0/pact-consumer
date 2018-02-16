package com.example.pact.consumer;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderPactTests {

    private ObjectMapper objectMapper;

    @Before
    public void createObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createPerson() throws JsonProcessingException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        Person person = Person.builder().name("Roger Antonsen").socialSecurityNumber("71039012345").build();

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer").hasPactWith("pact-provider")
                .uponReceiving("Create new person request")
                    .path("/person/v2")
                    .method("POST")
                    .headers(headers)
                    .body(objectMapper.writeValueAsString(person))
                .willRespondWith()
                    .headers(headers)
                    .status(HttpStatus.CREATED.value())
                    .body(new PactDslJsonBody()
                            .stringValue("name", "Roger Antonsen")
                            .stringValue("socialSecurityNumber", "71039012345")
                            .integerType("id", 0)
                    )
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();

        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderClient providerClient = new ProviderClient(new ProviderConfig(mockServer.getUrl()));

            Person personResponse = providerClient.createPerson(person);

            assertEquals(person.getName(), personResponse.getName());
            assertEquals(person.getSocialSecurityNumber(), personResponse.getSocialSecurityNumber());
            assertTrue(personResponse.getId() != null);
        });

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }
}
