package com.codersarena.currencyexchangeservice;

import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {
    public Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @Retry(name="sample-api", fallbackMethod = "failbackResponse")
    @GetMapping("/sample-api")
    public String sampleApi()  {
        logger.info("Received the API call");

        ResponseEntity<String> response = new RestTemplate()
                .getForEntity(
                        "http://localhost:8080/some-dummy-url",
                        String.class
                );
        return response.getBody();
    }

    public String failbackResponse(Exception ex){
        return "Failback Response";
    }

}