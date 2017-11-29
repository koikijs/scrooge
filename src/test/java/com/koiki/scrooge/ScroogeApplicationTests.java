package com.koiki.scrooge;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScroogeApplicationTests {
	@LocalServerPort
	private int port;

	@Test
	public void sample() {
		RestTemplate template = new RestTemplate();

		Health result = template.getForObject("http://localhost:" + port + "/application/health", Health.class);

		log.info("result: {}", result);
	}

}

@Data
class Health {
	String status;
}

