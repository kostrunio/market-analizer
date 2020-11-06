package com.kostro.analizer.scheduler;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.utils.CandleOperation;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    private MarketService marketService;
    private CandleService candleService;
    private ConfigurationService configurationService;
    private CandleOperation candleOperation;

    @Autowired
    public Scheduler(MarketService marketService, CandleService candleService, ConfigurationService configurationService) {
        this.marketService = marketService;
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.candleOperation = new CandleOperation(candleService, configurationService, true);
    }

    @Scheduled(cron = "0 * * * * *")
    public void getData() {
        LocalDateTime dateFrom = candleService.getLastCandle();
        LocalDateTime dateTo = dateFrom.plusSeconds(configurationService.getMaxPeriod());
        if (dateTo.isAfter(LocalDateTime.now())) {
            dateTo = LocalDateTime.now().withSecond(0).withNano(0);
        }
        log.info("invoke getCandles for {} with resolution {} from {} to {}", configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);
        List<Candle> candles = marketService.getCandles(configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);

        candleService.refreshCandles(candles);

        //push event
        candleOperation.checkCandles(candles);

        candleService.setLastCandle(candles.get(candles.size()-1).getTime());
    }

}
