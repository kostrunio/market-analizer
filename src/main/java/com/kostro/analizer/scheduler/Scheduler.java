package com.kostro.analizer.scheduler;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.utils.CandelOperation;
import com.kostro.analizer.utils.CandelUtils;
import com.kostro.analizer.wallet.Candel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private JsonService jsonService;
    private CandleService candleService;
    private ConfigurationService configurationService;
    private CandelOperation candelOperation;

    public Scheduler(JsonService jsonService, CandleService candleService, ConfigurationService configurationService) {
        this.jsonService = jsonService;
        this.candleService = candleService;
        this.configurationService = configurationService;
        candelOperation = new CandelOperation(candleService, configurationService, true);
    }

    @Scheduled(cron = "0 * * * * *")
    public void getData() {
        LocalDateTime dateFrom = configurationService.getLastCandel();
        if (dateFrom == null)
            dateFrom = candleService.getLastDate().minusMinutes(5);
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
        for (Candel candel : candels) {
            candelOperation.checkHugeVolume(candel);
            candelOperation.checkIfBuy(candel);
            candelOperation.checkIfBought(candel);
            candelOperation.checkIfSell(candel);
            candelOperation.checkIfSold(candel);
        }
    }
}