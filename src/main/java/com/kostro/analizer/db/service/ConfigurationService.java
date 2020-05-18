package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.ConfigurationEntity;
import com.kostro.analizer.db.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    //1st Jan 2020
    private LocalDateTime lastCandel;

    private ConfigurationRepository configurationRepository;

    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public LocalDateTime getLastCandel() {
        return lastCandel;
    }

    public long getMaxPeriod() {
        //2 days
        return 60*60*24*2;
    }

    public String getMarket() {
        //bitcoin
        return "BTC-PLN";
    }

    public int getResolution() {
        //1 min
        return 60;
    }

    public void setLastCandel(LocalDateTime lastCandel) {
        this.lastCandel = lastCandel;
    }

    public double getLimitFor(int secs) {
        switch (secs) {
            case 60:
                return 20;
        }
        return 100;
    }

    public double getBuyChange() {
        return 0.985;
    }

    public double getSellChangeRise() {
        return 1.025;
    }

    public double getSellChangeFall() {
        return 0.985;
    }

    public boolean sendVolume() {
        return false;
    }

    public int getHoursToWait() {
        return 24;
    }

    public int getWaitAfterSold() {
        return 2;
    }

    public int getHugeFor(int secs) {
        switch (secs) {
            case 300: return 3;
            case 3600: return 7;
            case 7200: return 12;
        }
        return 20;
    }

}
