package com.kostro.analizer.json.binance.service;

import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Primary
public class BinanceService implements MarketService {

    private ZoneId zoneId = ZoneId.systemDefault();

    private RestTemplate rest;

    public BinanceService() {
        this.rest = new RestTemplate();
    }

    public List<Candle> getCandles(String market, Resolution resolution, LocalDateTime from, LocalDateTime to) {
        String url = "https://api.binance.com/api/v3/klines?symbol="+market+"&interval="+resolution.getCode()+"&startTime="+ from.toEpochSecond(ZoneOffset.of("+2"))+"000&endTime="+to.toEpochSecond(ZoneOffset.of("+2"))+"000&limit=1000";
        log.debug("REQUEST: " + url);
        return createCandles(rest.getForObject(url, String[][].class), resolution.getSecs());
    }

    public static List<Candle> createCandles(String[][] response, int resolution) {
        List<Candle> candles = new ArrayList<>();
        if (response != null)
            for (int i=0; i < response.length-1; i++) { //without last one
                String[] item = response[i];
                String timestamp = item[0];
                double open = Double.parseDouble(item[1]);
                double high = Double.parseDouble(item[2]);
                double low = Double.parseDouble(item[3]);
                double close = Double.parseDouble(item[4]);
                double volume = Double.parseDouble(item[5]);
                candles.add(new Candle(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp.substring(0, 10)), Integer.parseInt(timestamp.substring(11)), ZoneOffset.of("+2")), resolution, open, high, low, close, volume));
            }
        return candles;
    }
}
