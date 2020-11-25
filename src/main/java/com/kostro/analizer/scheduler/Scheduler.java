package com.kostro.analizer.scheduler;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.utils.CandleOperation;
import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class Scheduler {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MarketService marketService;
    private CandleService candleService;
    private ConfigurationService configurationService;
    private CandleOperation candleOperation;

    @Autowired
    public Scheduler(MarketService marketService, CandleService candleService, ConfigurationService configurationService, CandleOperation candleOperation) {
        this.marketService = marketService;
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.candleOperation = candleOperation;
    }

    @Scheduled(cron = "6 * * * * *")
//    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void getData() {
        if (!configurationService.isRunScheduler()) {
            log.info("Scheduler stopped");
            return;
        }
        LocalDateTime dateFrom = candleService.getLastCandle();
        LocalDateTime dateTo = dateFrom.plusSeconds(configurationService.getMaxPeriod());
        if (dateTo.isAfter(LocalDateTime.now())) {
            dateTo = LocalDateTime.now().withSecond(59).withNano(999);
        }
        log.info("invoke getCandles for {} with resolution {} from {} to {}", configurationService.getMarket(), configurationService.getResolution(), formatter.format(dateFrom), formatter.format(dateTo));
        List<Candle> candles = marketService.getCandles(configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);

        candleService.refreshCandles(candles);

        //push event
        candleOperation.checkCandles(candles);

        candleService.setLastCandle(candles.get(candles.size()-1).getTime());
    }

}
