package com.kostro.analizer.scheduler;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.utils.CandelUtils;
import com.kostro.analizer.utils.SendEmail;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private JsonService jsonService;
    private CandleService candleService;
    private ConfigurationService configurationService;

    public Scheduler(JsonService jsonService, CandleService candleService, ConfigurationService configurationService) {
        this.jsonService = jsonService;
        this.candleService = candleService;
        this.configurationService = configurationService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void getData() {
        LocalDateTime dateFrom = configurationService.getLastCandel();
        LocalDateTime dateTo = dateFrom.plusSeconds(configurationService.getMaxPeriod());
        if (dateTo.isAfter(LocalDateTime.now())) {
            dateTo = LocalDateTime.now().withSecond(0).withNano(0);
        }
        log.info("invoke getCandles for {} with resolution {} from {} to {}", configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);
        CandleResponse candleResponse = jsonService.getCandles(configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);
        List<Candel> candels = CandelUtils.createCandels(candleResponse, configurationService.getResolution());

        candleService.refreshCandels(candels);

        //push event
        checkCandels(candels);

        configurationService.setLastCandel(dateTo);
    }

    private void checkCandels(List<Candel> candels) {
        for(Candel candel : candels) {
            if (candel.getVolume() > configurationService.getLimitFor(candel.getResolution())) {
                log.info("HUGE VOLUME: {} -> change: {}", candel, candel.getClose() > candel.getOpen() ? candel.getHigh()-candel.getLow() : candel.getLow() - candel.getHigh());
                LocalDateTime dateTo = candel.getTime().minusMinutes(1);


                LocalDateTime dateFrom = candel.getTime().minusSeconds(Resolution.FIVE_MINS.getSecs());
                List<Candel> fiveMins = CandelUtils.prepareCandels(
                        candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                        Resolution.FIVE_MINS.getSecs(),
                        dateFrom,
                        dateTo);
                fiveMins.stream().forEach(c -> log.info("5 MINS: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

                dateFrom = candel.getTime().minusSeconds(Resolution.ONE_HOUR.getSecs());
                List<Candel> oneHour = CandelUtils.prepareCandels(
                        candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                        Resolution.ONE_HOUR.getSecs(),
                        dateFrom,
                        dateTo);
                oneHour.stream().forEach(c -> log.info("1 HOUR: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

                dateFrom = candel.getTime().minusSeconds(Resolution.TWO_HOURS.getSecs());
                List<Candel> twoHours = CandelUtils.prepareCandels(
                        candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                        Resolution.TWO_HOURS.getSecs(),
                        dateFrom,
                        dateTo);
                twoHours.stream().forEach(c -> log.info("2 HOURS: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

                dateFrom = candel.getTime().minusSeconds(Resolution.ONE_DAY.getSecs());
                List<Candel> oneDay = CandelUtils.prepareCandels(
                        candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                        Resolution.ONE_DAY.getSecs(),
                        dateFrom,
                        dateTo);
                oneDay.stream().forEach(c -> log.info("1 DAY: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

                SendEmail.volume(candel, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));
            }
        }
    }

}
