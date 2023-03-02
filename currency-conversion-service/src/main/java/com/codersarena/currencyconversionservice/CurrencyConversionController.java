package com.codersarena.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.HashMap;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {

    // IMPORTANT! To instrument RestTemplate you must inject the RestTemplateBuilder
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
@RestController
public class CurrencyConversionController {

    private Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {

        logger.info("calculateCurrencyConversion called with {} to {} with {} quantity",from, to, quantity);

        HashMap<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        CurrencyConversion currencyConversion = restTemplate
                .getForEntity(
                        "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversion.class,
                        uriVariables
                ).getBody();

        if(currencyConversion != null) {

            currencyConversion.setQuantity(quantity);
            currencyConversion.setTotalCalculatedAmount(
                    currencyConversion.getConversionMultiple().multiply(quantity)
            );
            currencyConversion.setEnvironment(currencyConversion.getEnvironment().concat(" RestTemplate"));

        } else {
            throw new RuntimeException("No currency exchange result found from " + from + " to " + to);
        }

        return  currencyConversion;

    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {

        logger.info("calculateCurrencyConversionFeign called with {} to {} with {} quantity",from , to, quantity);

        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);

        if(currencyConversion != null) {

            currencyConversion.setQuantity(quantity);
            currencyConversion.setTotalCalculatedAmount(
                    currencyConversion.getConversionMultiple().multiply(quantity)
            );
            currencyConversion.setEnvironment(currencyConversion.getEnvironment().concat(" Feign"));

        } else {
            throw new RuntimeException("No currency exchange result found from " + from + " to " + to);
        }

        return  currencyConversion;

    }

}
