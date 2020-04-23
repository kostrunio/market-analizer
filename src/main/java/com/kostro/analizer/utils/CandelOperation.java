package com.kostro.analizer.utils;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.db.service.ConfigurationService;
import com.kostro.analizer.wallet.Candel;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class CandelOperation {
    private static final Logger log = LoggerFactory.getLogger(CandelOperation.class);

    private CandleService candleService;
    private ConfigurationService configurationService;

    private Candel lastHugeVolume;
    private Candel toBuy;
    private Candel bought;
    private Candel toSell;

    private boolean sendingEmails;

    public CandelOperation(CandleService candleService, ConfigurationService configurationService, boolean sendingEmails) {
        this.candleService = candleService;
        this.configurationService = configurationService;
        this.sendingEmails = sendingEmails;
    }

    public boolean checkHugeVolume(Candel candel) {
        if (candel.getVolume() > configurationService.getLimitFor(candel.getResolution())) {
            if (sendingEmails)
                log.info("HUGE VOLUME: {} -> change: {}", candel, candel.getClose() > candel.getOpen() ? candel.getHigh()-candel.getLow() : candel.getLow() - candel.getHigh());
            lastHugeVolume = candel;
            LocalDateTime dateTo = candel.getTime().minusMinutes(1);


            LocalDateTime dateFrom = candel.getTime().minusSeconds(Resolution.FIVE_MINS.getSecs());
            List<Candel> fiveMins = CandelUtils.prepareCandels(
                    candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                    Resolution.FIVE_MINS.getSecs(),
                    dateFrom,
                    dateTo);
            if (sendingEmails)
                fiveMins.stream().forEach(c -> log.info("5 MINS: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

            dateFrom = candel.getTime().minusSeconds(Resolution.ONE_HOUR.getSecs());
            List<Candel> oneHour = CandelUtils.prepareCandels(
                    candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                    Resolution.ONE_HOUR.getSecs(),
                    dateFrom,
                    dateTo);
            if (sendingEmails)
                oneHour.stream().forEach(c -> log.info("1 HOUR: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

            dateFrom = candel.getTime().minusSeconds(Resolution.TWO_HOURS.getSecs());
            List<Candel> twoHours = CandelUtils.prepareCandels(
                    candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                    Resolution.TWO_HOURS.getSecs(),
                    dateFrom,
                    dateTo);
            if (sendingEmails)
                twoHours.stream().forEach(c -> log.info("2 HOURS: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

            dateFrom = candel.getTime().minusSeconds(Resolution.ONE_DAY.getSecs());
            List<Candel> oneDay = CandelUtils.prepareCandels(
                    candleService.find(dateFrom, dateTo, Resolution.ONE_MIN.getSecs()),
                    Resolution.ONE_DAY.getSecs(),
                    dateFrom,
                    dateTo);
            if (sendingEmails)
                oneDay.stream().forEach(c -> log.info("1 DAY: {} -> change: {}", c.toString(), candel.getClose() - c.getHigh()));

            if (sendingEmails)
                SendEmail.volume(candel, fiveMins.get(0), oneHour.get(0), twoHours.get(0), oneDay.get(0));
            toBuy = null;
            return true;
        }
        return false;
    }

    public boolean checkIfBuy(Candel candel) {
        if (lastHugeVolume != null && toBuy == null && bought == null && toSell == null) {
            if (candel.getClose() <= lastHugeVolume.getLow() * configurationService.getBuyChange()) {
                toBuy = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000/candel.getClose());
                log.info("TRYING TO BUY: {} -> worth: {}", toBuy, (candel.getClose())/lastHugeVolume.getLow()*100);
                if (sendingEmails)
                    SendEmail.buy(toBuy);
                lastHugeVolume = null;
                return true;
            }
        }
        return false;
    }

    public boolean checkIfBought(Candel candel) {
        if (toBuy != null)
            if (!toBuy.getTime().isBefore(candel.getTime()) && toBuy.getOpen() > candel.getLow() && toBuy.getVolume() < candel.getVolume()) {
                log.info("BOUGHT: {}", candel);
                if (sendingEmails)
                    SendEmail.bought(toBuy);
                bought = toBuy;
                toBuy = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSell(Candel candel) {
        if (bought != null)
            if (candel.getClose() >= bought.getOpen() * configurationService.getSellChange()) {
                toSell = new Candel(candel.getTime().plusMinutes(1), Resolution.ONE_MIN.getSecs(), candel.getClose(), candel.getClose(), candel.getClose(), candel.getClose(), 1000 / candel.getClose());
                log.info("TRYING TO SELL: {} -> rise: {}", toSell, (candel.getClose()/ bought.getOpen() * 100));
                if (sendingEmails)
                    SendEmail.sell(toSell);
                bought = null;
                return true;
            }
        return false;
    }

    public boolean checkIfSold(Candel candel) {
        if (toSell != null)
            if (!toSell.getTime().isBefore(candel.getTime()) && toSell.getOpen() > candel.getLow() && toSell.getVolume() < candel.getVolume()) {
                log.info("SOLD: {}", candel);
                if (sendingEmails)
                    SendEmail.sold(toSell);
                toSell = null;
                return true;
            }
        return false;
    }
}
