package com.example.pact.consumer;

import au.com.dius.pact.consumer.*;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.example.pact.consumer.provider.client.ProviderClient;
import com.example.pact.consumer.provider.config.ProviderConfig;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ProviderTest  {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("pact-provider", this);

    @Pact(provider="pact-provider", consumer="pact-consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        String string = "test";
        return builder
                .uponReceiving("Send test, return te")
                .path("/uuid/"+string)
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(string.substring(0,2))
                .toPact();
    }

    @Test
    @PactVerification("pact-provider")
    public void runTest() {
        ProviderClient providerClient = new ProviderClient(new ProviderConfig(mockProvider.getUrl()));
        String response = providerClient.getUuid("test");
        assertEquals("te", response);
    }

    /*

    @Test
    public void testSecondPact() {
        String uuid = UUID.randomUUID().toString();

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("pact-consumer")
                .hasPactWith("pact-provider")
                .uponReceiving("Send uuid, return shortened uuid")
                .path("/uuid/"+uuid)
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(uuid.substring(0,2))
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();

        PactVerificationResult result = runConsumerTest(pact, config, mockProvider -> {
                assertEquals(new ProviderClient(new ProviderConfig(mockProvider.getUrl())).getUuid(uuid), uuid.substring(0,2));
        });
    }
     */
}
