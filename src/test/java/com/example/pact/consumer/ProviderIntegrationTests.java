package com.example.pact.consumer;

import com.example.pact.consumer.provider.client.ProviderClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderIntegrationTests {

	@Autowired
	private ProviderClient providerClient;

	@Test
	public void contextLoads() {
	}

	@Test
	public void loadUuid() {
		//providerClient.getUuid("test");
	}
}
