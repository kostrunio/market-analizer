package com.kostro.analizer.json.bitbay.service;

import com.kostro.analizer.json.bitbay.domain.candle.CandleResponse;
import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BitBayService implements MarketService {
    private static final Logger log = LoggerFactory.getLogger(BitBayService.class);

    private ZoneId zoneId = ZoneId.systemDefault();
    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    public BitBayService(RestTemplateBuilder builder) {
        this.builder = builder;
        restTemplate = builder.build();
    }

    public List<Candle> getCandles(String market, Resolution resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://api.bitbay.net/rest/trading/candle/history/"+market+"/"+resolution.getSecs()+"?from="+ from.toEpochSecond(ZoneOffset.of("+2"))+"000&to="+to.toEpochSecond(ZoneOffset.of("+2"))+"000";
        log.info("REQUEST: " + url);
        return createCandles(restTemplate.getForObject(url, CandleResponse.class), resolution.getSecs());
    }

    public static List<Candle> createCandles(CandleResponse response, int resolution) {
        List<Candle> candles = new ArrayList<>();
        if (response.getItems() != null)
            for (List<Object> item : response.getItems()) {
                String timestamp = item.get(0).toString();
                double open = Double.parseDouble(((Map<String, String>)item.get(1)).get("o"));
                double close = Double.parseDouble(((Map<String, String>)item.get(1)).get("c"));
                double low = Double.parseDouble(((Map<String, String>)item.get(1)).get("l"));
                double high = Double.parseDouble(((Map<String, String>)item.get(1)).get("h"));
                double volume = Double.parseDouble(((Map<String, String>)item.get(1)).get("v"));
                candles.add(new Candle(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+2")), resolution, open, high, low, close, volume));
            }
        return candles;
    }
}
