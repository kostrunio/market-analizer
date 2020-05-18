package com.kostro.analizer.scheduler;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.json.binance.service.BinanceService;
import com.kostro.analizer.json.bitbay.service.BitBayService;
import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.utils.CandleOperation;
import com.kostro.analizer.wallet.Candle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private CandleOperation CandleOperation;

    public Scheduler(BinanceService marketService, CandleService candleService, ConfigurationService configurationService) {
        this.marketService = marketService;
        this.candleService = candleService;
        this.configurationService = configurationService;
        CandleOperation = new CandleOperation(candleService, configurationService, true);
//        CandleOperation.bought(new Candle(LocalDateTime.of(2020, 5, 6, 12, 50, 00), 60, 38889.97, 38889.97, 38889.97, 38889.97, 0.025713570877015333));
    }

    @Scheduled(cron = "0 * * * * *")
    public void getData() {
        LocalDateTime dateFrom = configurationService.getLastCandle();
        if (dateFrom == null) {
            LocalDateTime candle = candleService.getLastDate();
            if (candle != null)
                dateFrom = candle.minusMinutes(5);
        }
        if (dateFrom == null)
            dateFrom = LocalDateTime.of(2020, 01, 01, 00, 00, 00);
        LocalDateTime dateTo = dateFrom.plusSeconds(configurationService.getMaxPeriod());
        if (dateTo.isAfter(LocalDateTime.now())) {
            dateTo = LocalDateTime.now().withSecond(0).withNano(0);
        }
        log.info("invoke getCandles for {} with resolution {} from {} to {}", configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);
        List<Candle> Candles = marketService.getCandles(configurationService.getMarket(), configurationService.getResolution(), dateFrom, dateTo);

        candleService.refreshCandles(Candles);

        //push event
//        checkCandles(Candles);

        configurationService.setLastCandle(dateTo);
    }

    private void checkCandles(List<Candle> Candles) {
        for (Candle Candle : Candles) {
            CandleOperation.checkHugeVolume(Candle);
            CandleOperation.checkIfBuy(Candle);
            CandleOperation.checkIfBought(Candle);
            CandleOperation.checkIfSell(Candle);
            CandleOperation.checkIfSold(Candle);
        }
    }
}
