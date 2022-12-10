package com.codersarena.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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

//    @Retry(name="sample-api", fallbackMethod = "failbackResponse")
//    @CircuitBreaker(name = "default", fallbackMethod = "failbackResponse")
//    @RateLimiter(name = "default")
    @Bulkhead(name = "default")
    @GetMapping("/sample-api")
    public String sampleApi()  {
        logger.info("Received the API call");

//        ResponseEntity<String> response = new RestTemplate()
//                .getForEntity(
//                        "http://localhost:8080/some-dummy-url",
//                        String.class
//                );
//        return response.getBody();
        return "sample-api";
    }

    public String failbackResponse(Exception ex){
        return "Failback Response";
    }

}