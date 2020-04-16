package com.vaadin.tutorial.crm.service;

import com.vaadin.tutorial.crm.domain.CandleResponse;
import com.vaadin.tutorial.crm.domain.TickerResponse;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class JsonService {

    private ZoneId zoneId = ZoneId.systemDefault();
    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    public JsonService(RestTemplateBuilder builder) {
        this.builder = builder;
        restTemplate = builder.build();
    }

    public TickerResponse getMarkets(String name) {
        return restTemplate.getForObject("https://api.bitbay.net/rest/trading/ticker", TickerResponse.class);
    }

    public CandleResponse getCandles(String market, Integer resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://api.bitbay.net/rest/trading/candle/history/"+market+"/"+resolution+"?from="+ from.toEpochSecond(ZoneOffset.of("+1"))+"000&to="+to.toEpochSecond(ZoneOffset.of("+1"))+"000";
//        System.out.println("from: " + from + "; to: " + to);
//        System.out.println("REQUEST: " + url);
        return restTemplate.getForObject(url, CandleResponse.class);
    }
}
