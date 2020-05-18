package com.kostro.analizer.utils;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import com.kostro.analizer.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CandelOperation {
    private static final Logger log = LoggerFactory.getLogger(CandelOperation.class);

    private CandleService candleService;
    private ConfigurationService configurationService;

    private Wallet wallet;
    private Candel lastHugeVolume;
    private Candel toBuy;
    private Candel bought;
    private Candel toSell;
    private LocalDateTime lastFail;
    private LocalDateTime lastSold;

    private boolean sendingEmails;

    public CandelOperation(CandleService candleService, ConfigurationService configurationService, boolean sendingEmails) {
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

    public boolean checkHugeVolume(Candel candel) {
        if (candel.getVolume() > configurationService.getLimitFor(candel.getResolution())) {
            lastHugeVolume = candel;
            toBuy = null;
            return true;
        }
        return false;
    }

    public boolean checkIfBuy(Candel candel) {
        if (candel.getVolume() > configurationService.getLimitFor(candel.getResolution())) {
            log.info("HUGE VOLUME: {} -> change: {}", candel, candel.getClose() > candel.getOpen() ? candel.getHigh() - candel.getLow() : candel.getLow() - candel.getHigh());

            List<Candel> fiveMins60 = new ArrayList<>();
            List<Candel> fiveMins = new ArrayList<>();
            prepareLists(fiveMins60, fiveMins, Resolution.FIVE_MINS, candel);


            List<Candel> oneHour60 = new ArrayList<>();
            List<Candel> oneHour = new ArrayList<>();
            prepareLists(oneHour60, oneHour, Resolution.ONE_HOUR, candel);

            List<Candel> twoHours60 = new ArrayList<>();
            List<Candel> twoHours = new ArrayList<>();
            prepareLists(twoHours60, twoHours, Resolution.TWO_HOURS, candel);

            List<Candel> oneDay60 = new ArrayList<>();
            List<Candel> oneDay = new ArrayList<>();
            prepareLists(oneDay60, oneDay, Resolution.ONE_DAY, candel);

            if (isHugeAndWaitedEnough(candel)) {
                if (configurationService.sendVolume())
                    SendEmail.volume(candel, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));

                if (isEnoughHugeTransactions(fiveMins60, oneHour60, twoHours60)) {
                    toBuy = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000 / candel.getClose());
                    log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getLow());
                    log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (candel.getClose()) / lastHugeVolume.getLow() * 100);
                    if (sendingEmails)
                        SendEmail.buy(toBuy);
                    lastHugeVolume = null;
                    return true;
                }
            }

            if (candel.getClose() <= lastHugeVolume.getLow() * configurationService.getBuyChange()) {
                toBuy = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000/candel.getClose());
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getLow());
                log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (candel.getClose())/lastHugeVolume.getLow()*100);
                if (sendingEmails)
                    SendEmail.buy(toBuy);
                lastHugeVolume = null;
                return true;
            }
        }
        return false;
    }

    private boolean isHugeAndWaitedEnough(Candel candel) {
        return lastHugeVolume != null
                && toBuy == null && bought == null && toSell == null
                && (lastFail == null || (lastFail != null && lastFail.plusHours(configurationService.getHoursToWait()).isBefore(candel.getTime())))
                && (lastSold == null || (lastSold != null && lastSold.plusHours(configurationService.getWaitAfterSold()).isBefore(candel.getTime())));
    }

    private void prepareLists(List<Candel> fiveMins60, List<Candel> fiveMins, Resolution resolution, Candel candel) {
        LocalDateTime dateFrom = candel.getTime().minusSeconds(resolution.getSecs());
        LocalDateTime dateTo = candel.getTime().minusMinutes(1);
        fiveMins60.addAll(candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs(), configurationService.getLimitFor(Resolution.ONE_MIN.getSecs())));
        fiveMins.addAll(CandelUtils.prepareCandels(fiveMins60, resolution.getSecs(), dateFrom, dateTo));
        fiveMins.stream().forEach(c -> log.info(resolution.name() + ": {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));
    }

    private boolean isEnoughHugeTransactions(List<Candel> fiveMins60, List<Candel> oneHour60, List<Candel> twoHours60) {
        return (fiveMins60.size() > configurationService.getHugeFor(Resolution.FIVE_MINS.getSecs()) && fiveMins60.get(0).getOpen() < fiveMins60.get(fiveMins60.size()-1).getClose())
                || (oneHour60.size() > configurationService.getHugeFor(Resolution.ONE_HOUR.getSecs()) && oneHour60.get(0).getOpen() < oneHour60.get(oneHour60.size()-1).getClose())
                || (twoHours60.size() > configurationService.getHugeFor(Resolution.TWO_HOURS.getSecs()) && twoHours60.get(0).getOpen() < twoHours60.get(twoHours60.size()-1).getClose());
    }

    public boolean checkIfBought(Candel candel) {
        if (toBuy != null)
            if (!toBuy.getTime().isBefore(candel.getTime()) && toBuy.getOpen() >= candel.getLow() && toBuy.getVolume() < candel.getVolume()) {
                log.info("BOUGHT: {}", candel);
                wallet.buy(candel.getTime(), toBuy.getOpen());
                if (sendingEmails)
                    SendEmail.bought(toBuy);
                bought = toBuy;
                toBuy = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSell(Candel candel) {
        if (lastHugeVolume != null && bought != null)
            if (candel.getClose() >= bought.getOpen() * configurationService.getSellChangeRise()) {
                toSell = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000 / candel.getClose());
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getOpen());
                log.info("TRYING TO SELL: {} -> rise: {}", toSell, (candel.getClose()/ bought.getOpen() * 100));
                if (sendingEmails)
                    SendEmail.sell(toSell);
                bought = null;
                return true;
            } else if (candel.getClose() <= bought.getOpen() * configurationService.getSellChangeFall() && lastHugeVolume != null && candel.getTime().minusHours(1).isBefore(lastHugeVolume.getTime()) && lastHugeVolume.getOpen() < candel.getLow()) {
                toSell = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000 / candel.getClose());
                lastFail = candel.getTime();
                log.info("last HUGE {} for {}", lastHugeVolume.getTime(), lastHugeVolume.getOpen());
                log.info("TRYING TO SELL: {} -> fall: {}", toSell, (candel.getClose()/ bought.getOpen() * 100));
                if (sendingEmails)
                    SendEmail.sell(toSell);
                bought = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSold(Candel candel) {
        if (toSell != null)
            if (!toSell.getTime().isBefore(candel.getTime()) && toSell.getOpen() <= candel.getHigh() && toSell.getVolume() < candel.getVolume()) {
                lastSold = candel.getTime();
                log.info("SOLD: {}", candel);
                wallet.sell(candel.getTime(), toSell.getOpen());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            } else {
                lastSold = candel.getTime();
                log.info("SOLD: {}", candel);
                wallet.sell(candel.getTime(), candel.getLow());
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                log.info("wallet: {}", wallet.getMoney());
                return true;
            }
        return false;
    }

    public void bought(Candel candel) {
        log.info("BOUGHT: {}", candel);
        wallet.buy(candel.getTime(), candel.getOpen());
        bought = candel;
    }
}
