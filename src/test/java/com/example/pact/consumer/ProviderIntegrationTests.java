package com.example.pact.consumer;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderIntegrationTests {

	@Autowired
	private ProviderClient providerClient;

	@Test
	public void contextLoads() {
	}

	@Test
	@Ignore
	public void createNewPerson() {
		Person person = Person.builder().name("Harald Hårfagre").ssn("01039012345").build();

		Person response = providerClient.createPerson(person);

		System.out.println(response);

		assertEquals(response.getName(), person.getName());
		assertEquals(response.getSsn(), person.getSsn());
		assertTrue(response.getId() != null);
	}
}
