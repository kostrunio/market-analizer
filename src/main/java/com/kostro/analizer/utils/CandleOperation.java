package com.kostro.analizer.utils;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import com.kostro.analizer.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CandleOperation {
    private static final Logger log = LoggerFactory.getLogger(CandleOperation.class);

    private CandleService candleService;
    private ConfigurationService configurationService;

    private Wallet wallet;
    private Candle lastHugeVolume;
    private Candle toBuy;
    private Candle bought;
    private Candle toSell;
    private LocalDateTime lastFail;
    private LocalDateTime lastSold;

    private boolean sendingEmails;

    public CandleOperation(CandleService candleService, ConfigurationService configurationService, boolean sendingEmails) {
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.sendingEmails = sendingEmails;
        this.wallet = new Wallet(1000, 0.999);
    }

    public void checkCandles(List<Candle> candles) {
        for (Candle candle : candles) {
            checkHugeVolume(candle);
//            checkIfBuy(candle);
//            checkIfBought(candle);
//            checkIfSell(candle);
//            checkIfSold(candle);
        }
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void resetWallet() {
        this.wallet = new Wallet(1000, 0.999);
        lastHugeVolume = toBuy = bought = toSell = null;
        lastFail = lastSold = null;
    }

    public boolean checkHugeVolume(Candle candle) {
        if (candle.getVolume() > configurationService.getLimitFor(candle.getResolution())) {
            lastHugeVolume = candle;
            toBuy = null;
            return true;
        }
        return false;
    }

    public boolean checkIfBuy(Candle candle) {
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

            if (configurationService.sendVolume())
                SendEmail.volume(candle, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));

                if (trendToUp(candle) && toBuy == null && bought == null && toSell == null) {
                    buy(candle);
                    return true;
                }

/*            if (isHugeAndWaitedEnough(candle)) {

                if (isEnoughHugeTransactions(fiveMins60, oneHour60, twoHours60)) {
                    buy(candle);
                    return true;
                }
            }

            if (candle.getClose() <= lastHugeVolume.getLow() * configurationService.getBuyChange()) {
                buy(candle);
                return true;
            }*/
        }
        return false;
    }

    private boolean trendToUp(Candle candle) {
        List<Candle> list = candleService.findLastHuge(candle.getTime(), candle.getResolution(), configurationService.getLimitFor(candle.getResolution()), configurationService.getNumberOfTransactions(candle.getResolution()));
        for (int i=1; i < list.size(); i++) {
            if (list.get(i).getClose() - list.get(i-1).getClose() < 0)
                return false;
        }
        return true;
    }

    private void buy(Candle candle) {
        toBuy = new Candle(candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candle.getClose(), candle.getClose(), candle.getClose(), candle.getClose(), 1000/candle.getClose());
        log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getLow());
        log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (candle.getClose())/lastHugeVolume.getLow()*100);
        if (sendingEmails)
            SendEmail.buy(toBuy);
        lastHugeVolume = null;
    }

    private boolean isHugeAndWaitedEnough(Candle candle) {
        return lastHugeVolume != null
                && toBuy == null && bought == null && toSell == null
                && (lastFail == null || (lastFail != null && lastFail.plusHours(configurationService.getHoursToWait()).isBefore(candle.getTime())))
                && (lastSold == null || (lastSold != null && lastSold.plusHours(configurationService.getWaitAfterSold()).isBefore(candle.getTime())));
    }

    private void prepareLists(List<Candle> fiveMins60, List<Candle> fiveMins, Resolution resolution, Candle candle) {
        LocalDateTime dateFrom = candle.getTime().minusSeconds(resolution.getSecs());
        LocalDateTime dateTo = candle.getTime().minusMinutes(1);
        fiveMins60.addAll(candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs(), configurationService.getLimitFor(Resolution.ONE_MIN.getSecs())));
        fiveMins.addAll(CandleUtils.prepareCandles(fiveMins60, resolution.getSecs(), dateFrom));
        fiveMins.stream().forEach(c -> log.info(resolution.name() + ": {} -> change: {}", c.toString(), candle.getClose() - c.getHigh()));
    }

    private boolean isEnoughHugeTransactions(List<Candle> fiveMins60, List<Candle> oneHour60, List<Candle> twoHours60) {
        return (fiveMins60.size() > configurationService.getHugeFor(Resolution.FIVE_MINS.getSecs()) && fiveMins60.get(0).getOpen() < fiveMins60.get(fiveMins60.size()-1).getClose())
                || (oneHour60.size() > configurationService.getHugeFor(Resolution.ONE_HOUR.getSecs()) && oneHour60.get(0).getOpen() < oneHour60.get(oneHour60.size()-1).getClose())
                || (twoHours60.size() > configurationService.getHugeFor(Resolution.TWO_HOURS.getSecs()) && twoHours60.get(0).getOpen() < twoHours60.get(twoHours60.size()-1).getClose());
    }

    public boolean checkIfBought(Candle candle) {
        if (toBuy != null)
            if (!toBuy.getTime().isBefore(candle.getTime()) && toBuy.getOpen() >= candle.getLow() && toBuy.getVolume() < candle.getVolume()) {
                log.info("BOUGHT: {}", candle);
                wallet.buy(candle.getTime(), toBuy.getOpen());
                if (sendingEmails)
                    SendEmail.bought(toBuy);
                bought = toBuy;
                toBuy = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSell(Candle candle) {
        if (candle.getVolume() > configurationService.getLimitFor(candle.getResolution()) && bought != null) {
            if (trendToDown(candle)) {
                sell(candle);
                return true;
            }
        }
        /*if (lastHugeVolume != null && bought != null)
            if (candle.getClose() >= bought.getOpen() * configurationService.getSellChangeRise()) {
                sell(candle);
                return true;
            } else if (candle.getClose() <= bought.getOpen() * configurationService.getSellChangeFall() && lastHugeVolume != null && candle.getTime().minusHours(1).isBefore(lastHugeVolume.getTime()) && lastHugeVolume.getOpen() < candle.getLow()) {
                lastFail = candle.getTime();
                sell(candle);
                return true;
            }*/
        return false;
    }

    private void sell(Candle candle) {
        toSell = new Candle(candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candle.getClose(), candle.getClose(), candle.getClose(), candle.getClose(), 1000 / candle.getClose());
        log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getOpen());
        log.info("TRYING TO SELL: {} -> rise: {}", toSell, (candle.getClose()/ bought.getOpen() * 100));
        if (sendingEmails)
            SendEmail.sell(toSell);
        bought = null;
    }

    private boolean trendToDown(Candle candle) {
        List<Candle> list = candleService.findLastHuge(candle.getTime(), candle.getResolution(), configurationService.getLimitFor(candle.getResolution()), configurationService.getNumberOfTransactions(candle.getResolution()));
        for (int i=1; i < list.size(); i++) {
            if (list.get(i).getClose() - list.get(i-1).getClose() > 0)
                return false;
        }
        return true;
    }

    public boolean checkIfSold(Candle candle) {
        if (toSell != null)
            if (!toSell.getTime().isBefore(candle.getTime()) && toSell.getOpen() <= candle.getHigh() && toSell.getVolume() < candle.getVolume()) {
                lastSold = candle.getTime();
                log.info("SOLD: {}", candle);
                wallet.sell(candle.getTime(), toSell.getOpen());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            } else {
                lastSold = candle.getTime();
                log.info("SOLD: {}", candle);
                wallet.sell(candle.getTime(), candle.getLow());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            }
        return false;
    }

    public void bought(Candle candle) {
        log.info("BOUGHT: {}", candle);
        wallet.buy(candle.getTime(), candle.getOpen());
        bought = candle;
    }
}
