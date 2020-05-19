package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.ConfigurationEntity;
import com.kostro.analizer.db.repository.ConfigurationRepository;
import com.kostro.analizer.wallet.Resolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

    private LocalDateTime lastCandle;

    private ConfigurationRepository configurationRepository;

    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        setInitConfiguration();
    }

    private void setInitConfiguration() {
        if (getMaxPeriod() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("maxPeriod");
            entity.setValue((1500*60)+"");
            configurationRepository.save(entity);
        }
        if (getMarket() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("market");
            entity.setValue("BTCUSDT");
            configurationRepository.save(entity);
        }
        if (getResolution() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("resolution");
            entity.setValue("1 min");
            configurationRepository.save(entity);
        }
    }

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public LocalDateTime getLastCandle() {
        return lastCandle;
    }

    public Long getMaxPeriod() {
        ConfigurationEntity entity = configurationRepository.findByName("maxPeriod");
        if (entity != null)
            return Long.parseLong(entity.getValue());
        return null;
    }

    public void setMaxPeriod(String value) {
        ConfigurationEntity entity = configurationRepository.findByName("maxPeriod");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public String getMarket() {
        ConfigurationEntity entity = configurationRepository.findByName("market");
        if (entity != null)
            return entity.getValue();
        return null;
    }

    public void setMarket(String value) {
        ConfigurationEntity entity = configurationRepository.findByName("market");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public Resolution getResolution() {
        ConfigurationEntity entity = configurationRepository.findByName("resolution");
        if (entity != null)
            switch (entity.getValue()) {
                case "1 min":
                    return Resolution.ONE_MIN;
                case "3 min":
                    return Resolution.THREE_MINS;
                case "5 min":
                    return Resolution.FIVE_MINS;
                case "15 min":
                    return Resolution.FIFTEEN_MINS;
                case "30 min":
                    return Resolution.THIRTY_MINS;
                case "1 hour":
                    return Resolution.ONE_HOUR;
                case "2 hours":
                    return Resolution.TWO_HOURS;
                case "4 hours":
                    return Resolution.FOUR_HOURS;
                case "6 hours":
                    return Resolution.SIX_HOURS;
                case "12 hours":
                    return Resolution.TWELWE_HOURS;
                case "1 day":
                    return Resolution.ONE_DAY;
                case "3 days":
                    return Resolution.THREE_DAYS;
                case "1 week":
                    return Resolution.ONE_WEEK;
                default:
                    return Resolution.ONE_MIN;
            }
        return null;
    }

    public void setResolution(String value) {
        ConfigurationEntity entity = configurationRepository.findByName("resolution");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public void setLastCandle(LocalDateTime lastCandle) {
        this.lastCandle = lastCandle;
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
