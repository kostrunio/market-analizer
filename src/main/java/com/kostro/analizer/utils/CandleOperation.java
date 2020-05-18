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
        this.wallet = new Wallet(1000, 0.9953);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void resetWallet() {
        this.wallet = new Wallet(1000, 0.9953);
        lastHugeVolume = toBuy = bought = toSell = null;
        lastFail = lastSold = null;
    }

    public boolean checkHugeVolume(Candle Candle) {
        if (Candle.getVolume() > configurationService.getLimitFor(Candle.getResolution())) {
            lastHugeVolume = Candle;
            toBuy = null;
            return true;
        }
        return false;
    }

    public boolean checkIfBuy(Candle Candle) {
        if (Candle.getVolume() > configurationService.getLimitFor(Candle.getResolution())) {
            log.info("HUGE VOLUME: {} -> change: {}", Candle, Candle.getClose() > Candle.getOpen() ? Candle.getHigh() - Candle.getLow() : Candle.getLow() - Candle.getHigh());

            List<Candle> fiveMins60 = new ArrayList<>();
            List<Candle> fiveMins = new ArrayList<>();
            prepareLists(fiveMins60, fiveMins, Resolution.FIVE_MINS, Candle);


            List<Candle> oneHour60 = new ArrayList<>();
            List<Candle> oneHour = new ArrayList<>();
            prepareLists(oneHour60, oneHour, Resolution.ONE_HOUR, Candle);

            List<Candle> twoHours60 = new ArrayList<>();
            List<Candle> twoHours = new ArrayList<>();
            prepareLists(twoHours60, twoHours, Resolution.TWO_HOURS, Candle);

            List<Candle> oneDay60 = new ArrayList<>();
            List<Candle> oneDay = new ArrayList<>();
            prepareLists(oneDay60, oneDay, Resolution.ONE_DAY, Candle);

            if (isHugeAndWaitedEnough(Candle)) {
                if (configurationService.sendVolume())
                    SendEmail.volume(Candle, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));

                if (isEnoughHugeTransactions(fiveMins60, oneHour60, twoHours60)) {
                    toBuy = new Candle(Candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), Candle.getClose(), Candle.getClose(), Candle.getClose(), Candle.getClose(), 1000 / Candle.getClose());
                    log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getLow());
                    log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (Candle.getClose()) / lastHugeVolume.getLow() * 100);
                    if (sendingEmails)
                        SendEmail.buy(toBuy);
                    lastHugeVolume = null;
                    return true;
                }
            }

            if (Candle.getClose() <= lastHugeVolume.getLow() * configurationService.getBuyChange()) {
                toBuy = new Candle(Candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), Candle.getClose(), Candle.getClose(), Candle.getClose(), Candle.getClose(), 1000/Candle.getClose());
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getLow());
                log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (Candle.getClose())/lastHugeVolume.getLow()*100);
                if (sendingEmails)
                    SendEmail.buy(toBuy);
                lastHugeVolume = null;
                return true;
            }
        }
        return false;
    }

    private boolean isHugeAndWaitedEnough(Candle Candle) {
        return lastHugeVolume != null
                && toBuy == null && bought == null && toSell == null
                && (lastFail == null || (lastFail != null && lastFail.plusHours(configurationService.getHoursToWait()).isBefore(Candle.getTime())))
                && (lastSold == null || (lastSold != null && lastSold.plusHours(configurationService.getWaitAfterSold()).isBefore(Candle.getTime())));
    }

    private void prepareLists(List<Candle> fiveMins60, List<Candle> fiveMins, Resolution resolution, Candle Candle) {
        LocalDateTime dateFrom = Candle.getTime().minusSeconds(resolution.getSecs());
        LocalDateTime dateTo = Candle.getTime().minusMinutes(1);
        fiveMins60.addAll(candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs(), configurationService.getLimitFor(Resolution.ONE_MIN.getSecs())));
        fiveMins.addAll(CandleUtils.prepareCandles(fiveMins60, resolution.getSecs(), dateFrom, dateTo));
        fiveMins.stream().forEach(c -> log.info(resolution.name() + ": {} -> change: {}", c.toString(), Candle.getClose() - c.getHigh()));
    }

    private boolean isEnoughHugeTransactions(List<Candle> fiveMins60, List<Candle> oneHour60, List<Candle> twoHours60) {
        return (fiveMins60.size() > configurationService.getHugeFor(Resolution.FIVE_MINS.getSecs()) && fiveMins60.get(0).getOpen() < fiveMins60.get(fiveMins60.size()-1).getClose())
                || (oneHour60.size() > configurationService.getHugeFor(Resolution.ONE_HOUR.getSecs()) && oneHour60.get(0).getOpen() < oneHour60.get(oneHour60.size()-1).getClose())
                || (twoHours60.size() > configurationService.getHugeFor(Resolution.TWO_HOURS.getSecs()) && twoHours60.get(0).getOpen() < twoHours60.get(twoHours60.size()-1).getClose());
    }

    public boolean checkIfBought(Candle Candle) {
        if (toBuy != null)
            if (!toBuy.getTime().isBefore(Candle.getTime()) && toBuy.getOpen() >= Candle.getLow() && toBuy.getVolume() < Candle.getVolume()) {
                log.info("BOUGHT: {}", Candle);
                wallet.buy(Candle.getTime(), toBuy.getOpen());
                if (sendingEmails)
                    SendEmail.bought(toBuy);
                bought = toBuy;
                toBuy = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSell(Candle Candle) {
        if (lastHugeVolume != null && bought != null)
            if (Candle.getClose() >= bought.getOpen() * configurationService.getSellChangeRise()) {
                toSell = new Candle(Candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), Candle.getClose(), Candle.getClose(), Candle.getClose(), Candle.getClose(), 1000 / Candle.getClose());
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getOpen());
                log.info("TRYING TO SELL: {} -> rise: {}", toSell, (Candle.getClose()/ bought.getOpen() * 100));
                if (sendingEmails)
                    SendEmail.sell(toSell);
                bought = null;
                return true;
            } else if (Candle.getClose() <= bought.getOpen() * configurationService.getSellChangeFall() && lastHugeVolume != null && Candle.getTime().minusHours(1).isBefore(lastHugeVolume.getTime()) && lastHugeVolume.getOpen() < Candle.getLow()) {
                toSell = new Candle(Candle.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), Candle.getClose(), Candle.getClose(), Candle.getClose(), Candle.getClose(), 1000 / Candle.getClose());
                lastFail = Candle.getTime();
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getOpen());
                log.info("TRYING TO SELL: {} -> fall: {}", toSell, (Candle.getClose()/ bought.getOpen() * 100));
                if (sendingEmails)
                    SendEmail.sell(toSell);
                bought = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSold(Candle Candle) {
        if (toSell != null)
            if (!toSell.getTime().isBefore(Candle.getTime()) && toSell.getOpen() <= Candle.getHigh() && toSell.getVolume() < Candle.getVolume()) {
                lastSold = Candle.getTime();
                log.info("SOLD: {}", Candle);
                wallet.sell(Candle.getTime(), toSell.getOpen());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            } else {
                lastSold = Candle.getTime();
                log.info("SOLD: {}", Candle);
                wallet.sell(Candle.getTime(), Candle.getLow());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            }
        return false;
    }

    public void bought(Candle Candle) {
        log.info("BOUGHT: {}", Candle);
        wallet.buy(Candle.getTime(), Candle.getOpen());
        bought = Candle;
    }
}
