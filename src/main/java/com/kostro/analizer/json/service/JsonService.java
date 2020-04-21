package com.kostro.analizer.json.service;

import com.kostro.analizer.json.domain.ticker.TickerResponse;
import com.kostro.analizer.json.domain.candle.CandleResponse;
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
        String url = "https://api.bitbay.net/rest/trading/candle/history/"+market+"/"+resolution+"?from="+ from.toEpochSecond(ZoneOffset.of("+2"))+"000&to="+to.toEpochSecond(ZoneOffset.of("+2"))+"000";
        System.out.println("REQUEST: " + url);
        return restTemplate.getForObject(url, CandleResponse.class);
    }
}
