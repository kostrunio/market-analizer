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

    public void checkCandles(List<Candle> candles) {
        for (Candle candle : candles) {
            checkMaxLevel(candle);
            checkLevelStep(candle, true);
            checkHugeVolume(candle, true);
        }
    }

    public boolean checkHugeVolume(Candle candle, boolean checkSending) {
        if (candle.getVolume() > configurationService.getLimitFor(candle.getResolution())) {
            log.info("MIN: {} -> change: {}", candle, candle.getClose() - candle.getOpen());

            List<Candle> fiveMins60 = new ArrayList<>();
            List<Candle> fiveMins = new ArrayList<>();
            prepareLists(fiveMins60, fiveMins, Resolution.FIVE_MINS, candle);


            List<Candle> oneHour60 = new ArrayList<>();
            List<Candle> oneHour = new ArrayList<>();
            prepareLists(oneHour60, oneHour, Resolution.ONE_HOUR, candle);

            List<Candle> twoHours60 = new ArrayList<>();
            List<Candle> twoHours = new ArrayList<>();
            prepareLists(twoHours60, twoHours, Resolution.TWO_HOURS, candle);

            List<Candle> oneDay60 = new ArrayList<>();
            List<Candle> oneDay = new ArrayList<>();
            prepareLists(oneDay60, oneDay, Resolution.ONE_DAY, candle);

            if (checkSending && configurationService.isSendVolume())
                notification.volume(candle, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));
            return true;
        }
        return false;
    }

    public boolean checkLevelStep(Candle candle, boolean checkSending) {
        if (candle.getClose() > configurationService.getLastLevel() + configurationService.getLevelStep()) {
            configurationService.setLastLevel(((int)candle.getClose()/configurationService.getLevelStep())*configurationService.getLevelStep());
            sendLevel(checkSending, candle, true);
            return true;
        } else if (candle.getClose() < configurationService.getLastLevel() - configurationService.getLevelStep()) {
            configurationService.setLastLevel((((int)candle.getClose()/configurationService.getLevelStep())+1)*configurationService.getLevelStep());
            sendLevel(checkSending, candle, false);
            return true;
        }
        return false;
    }

    private void sendLevel(boolean checkSending, Candle candle, boolean rised) {
        log.info("LEVEL: {}, {} {} max: {}", configurationService.getLastLevel(), (int)(configurationService.getMaxLevel() - configurationService.getLastLevel()), rised ? "to" : "from", configurationService.getMaxLevel());
        if (checkSending && configurationService.isSendLevel())
            notification.level(candle, configurationService.getLastLevel(), configurationService.getMaxLevel(), rised);
    }

    private void checkMaxLevel(Candle candle) {
        if (candle.getHigh() > configurationService.getMaxLevel())
            configurationService.setMaxLevel(candle.getHigh());
    }

    private void prepareLists(List<Candle> fiveMins60, List<Candle> fiveMins, Resolution resolution, Candle candle) {
        LocalDateTime dateFrom = candle.getTime().minusSeconds(resolution.getSecs());
        LocalDateTime dateTo = candle.getTime().minusMinutes(1);
        fiveMins60.addAll(candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()));
        fiveMins.addAll(CandleUtils.prepareCandles(fiveMins60, resolution.getSecs(), dateFrom));
        fiveMins.stream().forEach(c -> log.info(resolution.name() + ": {} -> change: {}", c.toString(), candle.getClose() - c.getOpen()));
    }

}
