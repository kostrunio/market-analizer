package com.kostro.analizer.utils;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CandleOperation {
    private static final Logger log = LoggerFactory.getLogger(CandleOperation.class);

    private CandleService candleService;
    private ConfigurationService configurationService;

    private boolean sendingEmails;

    public CandleOperation(CandleService candleService, ConfigurationService configurationService, boolean sendingEmails) {
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.sendingEmails = sendingEmails;
    }

    public void checkCandles(List<Candle> candles) {
        for (Candle candle : candles) {
            checkHugeVolume(candle);
        }
    }

    public boolean checkHugeVolume(Candle candle) {
        if (candle.getVolume() > configurationService.getLimitFor(candle.getResolution())) {
            log.info("HUGE VOLUME: {} -> change: {}", candle, candle.getClose() > candle.getOpen() ? candle.getHigh() - candle.getLow() : candle.getLow() - candle.getHigh());

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

            if (configurationService.isSendVolume())
                SendEmail.volume(candle, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));

        }
        return false;
    }

    private void prepareLists(List<Candle> fiveMins60, List<Candle> fiveMins, Resolution resolution, Candle candle) {
        LocalDateTime dateFrom = candle.getTime().minusSeconds(resolution.getSecs());
        LocalDateTime dateTo = candle.getTime().minusMinutes(1);
        fiveMins60.addAll(candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()));
        fiveMins.addAll(CandleUtils.prepareCandles(fiveMins60, resolution.getSecs(), dateFrom));
        fiveMins.stream().forEach(c -> log.info(resolution.name() + ": {} -> change: {}", c.toString(), candle.getClose() - c.getHigh()));
    }

}
