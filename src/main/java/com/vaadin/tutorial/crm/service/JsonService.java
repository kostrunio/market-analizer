package com.vaadin.tutorial.crm.service;

import com.vaadin.tutorial.crm.domain.TickerResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JsonService {

    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    public JsonService(RestTemplateBuilder builder) {
        this.builder = builder;
        restTemplate = builder.build();
    }

    public TickerResponse getMarkets(String name) {
        return restTemplate.getForObject("https://api.bitbay.net/rest/trading/ticker", TickerResponse.class);
    }
}
