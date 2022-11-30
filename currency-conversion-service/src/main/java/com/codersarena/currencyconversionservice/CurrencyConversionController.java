package com.codersarena.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {

        HashMap<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        CurrencyConversion currencyConversion = new RestTemplate()
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
