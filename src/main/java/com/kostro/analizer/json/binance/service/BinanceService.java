package com.kostro.analizer.json.binance.service;

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

@Service
public class BinanceService implements MarketService {
    private static final Logger log = LoggerFactory.getLogger(BinanceService.class);

    private ZoneId zoneId = ZoneId.systemDefault();
    private RestTemplateBuilder builder;
    private RestTemplate restTemplate;

    public BinanceService(RestTemplateBuilder builder) {
        this.builder = builder;
        restTemplate = builder.build();
    }

    public List<Candle> getCandles(String market, Resolution resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://fapi.binance.com/fapi/v1/klines?symbol="+market+"&interval="+resolution.getCode()+"&startTime="+ from.toEpochSecond(ZoneOffset.of("+2"))+"000&endTime="+to.toEpochSecond(ZoneOffset.of("+2"))+"000&limit=1500";
        log.info("REQUEST: " + url);
        return createCandles(restTemplate.getForObject(url, String[][].class), resolution.getSecs());
    }

    public static List<Candle> createCandles(String[][] response, int resolution) {
        List<Candle> Candles = new ArrayList<>();
        if (response != null)
            for (String[] item : response) {
                String timestamp = item[0];
                double open = Double.parseDouble(item[1]);
                double high = Double.parseDouble(item[2]);
                double low = Double.parseDouble(item[3]);
                double close = Double.parseDouble(item[4]);
                double volume = Double.parseDouble(item[5]);
                Candles.add(new Candle(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+2")), resolution, open, high, low, close, volume));
            }
        return Candles;
    }
}
