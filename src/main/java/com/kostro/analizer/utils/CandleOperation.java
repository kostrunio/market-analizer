package com.kostro.analizer.utils;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.utils.notification.Notification;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CandleOperation {

    private CandleService candleService;
    private ConfigurationService configurationService;
    private Notification notification;

    public CandleOperation(CandleService candleService, ConfigurationService configurationService, Notification notification) {
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.notification = notification;
    }

    public void checkCandles(String market, List<Candle> candles) {
        for (Candle candle : candles) {
            checkMaxLevel(market, candle);
            checkLevelStep(market, candle, true);
            checkHugeVolume(market, candle, true);
        }
    }

    public boolean checkHugeVolume(String market, Candle candle, boolean checkSending) {
        if (candle.getVolume() > configurationService.getLimitFor(market, candle.getResolution(market))) {
            if (candle.getOpen() > 1000)
                log.info(String.format("%7s - %9s: %s -> change: %5.0f", market, Resolution.ONE_MIN.name(), candle, candle.getClose() - candle.getOpen()));
            else
                log.info(String.format("%7s - %9s: %s -> change: %5.5f", market, Resolution.ONE_MIN.name(), candle, candle.getClose() - candle.getOpen()));

            List<Candle> fiveMins60 = new ArrayList<>();
            List<Candle> fiveMins = new ArrayList<>();
            prepareLists(market, fiveMins60, fiveMins, Resolution.FIVE_MINS, candle);


            List<Candle> oneHour60 = new ArrayList<>();
            List<Candle> oneHour = new ArrayList<>();
            prepareLists(market, oneHour60, oneHour, Resolution.ONE_HOUR, candle);

            List<Candle> twoHours60 = new ArrayList<>();
            List<Candle> twoHours = new ArrayList<>();
            prepareLists(market, twoHours60, twoHours, Resolution.TWO_HOURS, candle);

            List<Candle> oneDay60 = new ArrayList<>();
            List<Candle> oneDay = new ArrayList<>();
            prepareLists(market, oneDay60, oneDay, Resolution.ONE_DAY, candle);
            log.info("");

            if (checkSending && configurationService.isSendVolume(market) && candle.getTime().plusMinutes(10).isAfter(LocalDateTime.now()))
                notification.volume(market, candle, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));
            return true;
        }
        return false;
    }

    public boolean checkLevelStep(String market, Candle candle, boolean checkSending) {
        if (candle.getClose() > configurationService.getLastLevel(market) + configurationService.getLevelStep(market)) {
            double newLastLevel = configurationService.getLastLevel(market);
            while (candle.getClose() > newLastLevel + configurationService.getLevelStep(market)) {
                newLastLevel += configurationService.getLevelStep(market);
            }
            configurationService.setLastLevel(market, newLastLevel);
            sendLevel(market, checkSending, candle, true);
            return true;
        } else if (candle.getClose() < configurationService.getLastLevel(market) - configurationService.getLevelStep(market)) {
            double newLastLevel = configurationService.getLastLevel(market);
            while (candle.getClose() < newLastLevel - configurationService.getLevelStep(market)) {
                newLastLevel -= configurationService.getLevelStep(market);
            }
            configurationService.setLastLevel(market, newLastLevel);
            sendLevel(market, checkSending, candle, false);
            return true;
        }
        return false;
    }

    private void sendLevel(String market, boolean checkSending, Candle candle, boolean rised) {
        if (configurationService.getLastLevel(market) > 1000)
            log.info(String.format("%7s - %9s: %5.0f, %4.0f %4s max: %5.0f", market, "LEVEL", configurationService.getLastLevel(market), configurationService.getMaxLevel(market) - configurationService.getLastLevel(market), rised ? "to" : "from", configurationService.getMaxLevel(market)));
        else
            log.info(String.format("%7s - %9s: %5.3f, %4.3f %4s max: %5.3f", market, "LEVEL", configurationService.getLastLevel(market), configurationService.getMaxLevel(market) - configurationService.getLastLevel(market), rised ? "to" : "from", configurationService.getMaxLevel(market)));
        if (checkSending && configurationService.isSendLevel(market) && candle.getTime().plusMinutes(10).isAfter(LocalDateTime.now()))
            notification.level(market, candle, configurationService.getLastLevel(market), configurationService.getMaxLevel(market), rised);
    }

    private void checkMaxLevel(String market, Candle candle) {
        if (candle.getHigh() > configurationService.getMaxLevel(market))
            configurationService.setMaxLevel(market, candle.getHigh());
    }

    private void prepareLists(String market, List<Candle> fiveMins60, List<Candle> fiveMins, Resolution resolution, Candle candle) {
        LocalDateTime dateFrom = candle.getTime().minusSeconds(resolution.getSecs()).plusMinutes(1);
        LocalDateTime dateTo = candle.getTime();
        fiveMins60.addAll(candleService.find(market, dateFrom, dateTo, Resolution.ONE_MIN.getSecs()));
        fiveMins.addAll(CandleUtils.prepareCandles(fiveMins60, resolution.getSecs(), dateFrom));
        fiveMins.stream().forEach(c -> {
            if (c.getOpen() > 1000)
                log.info(String.format("%7s - %9s: %s -> change: %5.0f", market, resolution.name(), c, candle.getClose() - c.getOpen()));
            else
                log.info(String.format("%7s - %9s: %s -> change: %5.5f", market, resolution.name(), c, candle.getClose() - c.getOpen()));
        });
    }

}
