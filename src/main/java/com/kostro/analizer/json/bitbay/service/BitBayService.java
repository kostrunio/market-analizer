package com.kostro.analizer.json.bitbay.service;

import com.kostro.analizer.json.bitbay.domain.ticker.TickerResponse;
import com.kostro.analizer.json.bitbay.domain.candle.CandleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class BitBayService {
    private static final Logger log = LoggerFactory.getLogger(BitBayService.class);

    private ZoneId zoneId = ZoneId.systemDefault();
    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    public BitBayService(RestTemplateBuilder builder) {
        this.builder = builder;
        restTemplate = builder.build();
    }

    public TickerResponse getMarkets() {
        return restTemplate.getForObject("https://api.bitbay.net/rest/trading/ticker", TickerResponse.class);
    }

    public CandleResponse getCandles(String market, Integer resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://api.bitbay.net/rest/trading/candle/history/"+market+"/"+resolution+"?from="+ from.toEpochSecond(ZoneOffset.of("+2"))+"000&to="+to.toEpochSecond(ZoneOffset.of("+2"))+"000";
        log.info("REQUEST: " + url);
        return restTemplate.getForObject(url, CandleResponse.class);
    }
}
