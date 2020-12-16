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
import java.util.stream.Collectors;

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

    @Scheduled(cron = "*/10 * * * * *")
//    @Scheduled(fixedDelay = 1000, initialDelay = 3000)
    public void getData() {
        if (!configurationService.isRunScheduler()) {
            log.info("Scheduler stopped");
            return;
        }
        LocalDateTime dateFrom = candleService.getLastCandle();
        LocalDateTime dateTo = dateFrom.plusSeconds(configurationService.getMaxPeriod());
        if (dateTo.isAfter(LocalDateTime.now())) {
            dateTo = LocalDateTime.now();
        }
        log.debug("invoke getCandles for {} with resolution {} from {} to {}", configurationService.getMarket(), configurationService.getResolution(), formatter.format(dateFrom), formatter.format(dateTo));
        List<Candle> candles = marketService.getCandles(configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);

        List<Candle> newCandles = candles.stream().filter(c -> c.getTime().isAfter(candleService.getLastCandle())).collect(Collectors.toList());
        if (newCandles.size() == 0) return;

        candleService.refreshCandles(newCandles);

        //push event
        candleOperation.checkCandles(newCandles);

        candleService.setLastCandle(newCandles.get(newCandles.size()-1).getTime());
    }

}
