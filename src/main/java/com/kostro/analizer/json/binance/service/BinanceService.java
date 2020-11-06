package com.kostro.analizer.json.binance.service;

import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Primary
public class BinanceService implements MarketService {
    private static final Logger log = LoggerFactory.getLogger(BinanceService.class);

    private ZoneId zoneId = ZoneId.systemDefault();
    private RestTemplate restTemplate;

    @Autowired
    public BinanceService(RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    public List<Candle> getCandles(String market, Resolution resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://api.binance.com/api/v3/klines?symbol="+market+"&interval="+resolution.getCode()+"&startTime="+ from.toEpochSecond(ZoneOffset.of("+1"))+"000&endTime="+to.toEpochSecond(ZoneOffset.of("+1"))+"000&limit=1000";
        log.info("REQUEST: " + url);
        return createCandles(restTemplate.getForObject(url, String[][].class), resolution.getSecs());
    }

    public static List<Candle> createCandles(String[][] response, int resolution) {
        List<Candle> candles = new ArrayList<>();
        if (response != null)
            for (String[] item : response) {
                String timestamp = item[0];
                double open = Double.parseDouble(item[1]);
                double close = Double.parseDouble(item[2]);
                double low = Double.parseDouble(item[3]);
                double high = Double.parseDouble(item[4]);
                double volume = Double.parseDouble(item[5]);
                candles.add(new Candle(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+1")), resolution, open, high, low, close, volume));
            }
        return candles;
    }
}
