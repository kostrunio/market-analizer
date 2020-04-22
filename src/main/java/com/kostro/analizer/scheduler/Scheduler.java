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
                log.info("HUGE VOLUME: {}", candel);
                List<Candel> fiveMins = CandelUtils.prepareCandels(candels, Resolution.FIVE_MINS.getSecs(), candel.getTime().minusSeconds(Resolution.FIVE_MINS.getSecs()), candel.getTime().minusMinutes(1));
                fiveMins.stream().forEach(c -> log.info(c.toString()));
                List<Candel> oneHour = CandelUtils.prepareCandels(candels, Resolution.ONE_HOUR.getSecs(), candel.getTime().minusSeconds(Resolution.ONE_HOUR.getSecs()), candel.getTime().minusMinutes(1));
                oneHour.stream().forEach(c -> log.info(c.toString()));
                List<Candel> twoHours = CandelUtils.prepareCandels(candels, Resolution.TWO_HOURS.getSecs(), candel.getTime().minusSeconds(Resolution.TWO_HOURS.getSecs()), candel.getTime().minusMinutes(1));
                twoHours.stream().forEach(c -> log.info(c.toString()));
                List<Candel> oneDay = CandelUtils.prepareCandels(candels, Resolution.ONE_DAY.getSecs(), candel.getTime().minusSeconds(Resolution.ONE_DAY.getSecs()), candel.getTime().minusMinutes(1));
                oneDay.stream().forEach(c -> log.info(c.toString()));
                SendEmail.volume(candel, fiveMins, oneHour, twoHours, oneDay);
            }
        }
    }

}
