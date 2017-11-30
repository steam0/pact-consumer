package com.example.pact.consumer;

import au.com.dius.pact.consumer.*;

import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.pact.consumer.provider.client.ProviderClient;
import com.example.pact.consumer.provider.client.models.Person;
import com.example.pact.consumer.provider.config.ProviderConfig;
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


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderPacts {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("pact-provider", this);

    @Test
    public void testPact() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer")
                .hasPactWith("pact-provider")
                .uponReceiving("Send test, return te")
                .path("/uuid/"+"test")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body("test".substring(0,2))
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockProvider -> {
            assertEquals(new ProviderClient(new ProviderConfig(mockProvider.getUrl())).getUuid("test"), "test".substring(0,2));
        });

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    @Test
    public void secondTestPact() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");

        String uuid = UUID.randomUUID().toString();

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer")
                .hasPactWith("pact-provider")
                .uponReceiving("Send uuid, return shortened uuid")
                .path("/uuid/"+uuid)
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body(uuid.substring(0,2))
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockProvider -> {
            assertEquals(new ProviderClient(new ProviderConfig(mockProvider.getUrl())).getUuid(uuid), uuid.substring(0,2));
        });

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    @Test
    public void createPerson() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        String uuid = UUID.randomUUID().toString();

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer")
                .hasPactWith("pact-provider")
                .uponReceiving("Create new person")
                .path("/person")
                .method("POST")
                .willRespondWith()
                .headers(headers)
                .status(HttpStatus.CREATED.value())
                .body("{\n" +
                        "  \"name\": \"Harald Hårfagre\",\n" +
                        "  \"ssn\": \"01039012345\"\n" +
                        "}")
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockProvider -> {
            Person expectedResponse = Person.builder().name("Harald Hårfagre").ssn("01039012345").build();
            Person person = new ProviderClient(new ProviderConfig(mockProvider.getUrl())).createPerson();

            assertEquals(person.getName(), expectedResponse.getName());
            assertEquals(person.getSsn(), expectedResponse.getSsn());
        });

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

}
