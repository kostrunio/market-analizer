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

    private ConfigurationRepository configurationRepository;

    private Long maxPeriod;
    private String market;
    private Resolution resolution;
    private Boolean sendVolume;

    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        setInitConfiguration();
    }

    private void setInitConfiguration() {
        if (getMaxPeriod() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("maxPeriod");
            entity.setValue((1000*60)+"");
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

        if (getSendVolume() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("sendVolume");
            entity.setValue("true");
            configurationRepository.save(entity);
        }
    }

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public Long getMaxPeriod() {
        if (maxPeriod != null) return maxPeriod;
        ConfigurationEntity entity = configurationRepository.findByName("maxPeriod");
        if (entity != null) {
            maxPeriod = Long.parseLong(entity.getValue());
        }
        return maxPeriod;
    }

    public void setMaxPeriod(Long value) {
        maxPeriod = value;
        ConfigurationEntity entity = configurationRepository.findByName("maxPeriod");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public String getMarket() {
        if (market != null) return market;
        ConfigurationEntity entity = configurationRepository.findByName("market");
        if (entity != null) {
            market = entity.getValue();
        }
        return market;
    }

    public void setMarket(String value) {
        market = value;
        ConfigurationEntity entity = configurationRepository.findByName("market");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public Resolution getResolution() {
        if (resolution != null) return resolution;
        ConfigurationEntity entity = configurationRepository.findByName("resolution");
        if (entity != null)
            switch (entity.getValue()) {
                case "1 min":
                    resolution = Resolution.ONE_MIN;
                case "3 min":
                    resolution = Resolution.THREE_MINS;
                case "5 min":
                    resolution = Resolution.FIVE_MINS;
                case "15 min":
                    resolution = Resolution.FIFTEEN_MINS;
                case "30 min":
                    resolution = Resolution.THIRTY_MINS;
                case "1 hour":
                    resolution = Resolution.ONE_HOUR;
                case "2 hours":
                    resolution = Resolution.TWO_HOURS;
                case "4 hours":
                    resolution = Resolution.FOUR_HOURS;
                case "6 hours":
                    resolution = Resolution.SIX_HOURS;
                case "12 hours":
                    resolution = Resolution.TWELWE_HOURS;
                case "1 day":
                    resolution = Resolution.ONE_DAY;
                case "3 days":
                    resolution = Resolution.THREE_DAYS;
                case "1 week":
                    resolution = Resolution.ONE_WEEK;
                default:
                    resolution = Resolution.ONE_MIN;
            }
        return resolution;
    }

    public void setResolution(String value) {
        ConfigurationEntity entity = configurationRepository.findByName("resolution");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public Boolean getSendVolume() {
        if (sendVolume != null) return sendVolume;
        ConfigurationEntity entity = configurationRepository.findByName("sendVolume");
        if (entity != null) {
            sendVolume = Boolean.parseBoolean(entity.getValue());
        }
        return sendVolume;
    }

    public void setSendVolume(boolean value) {
        sendVolume = value;
        ConfigurationEntity entity = configurationRepository.findByName("sendVolume");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public int getLimitFor(int secs) {
        switch (secs) {
            case 60:
                return 500;
        }
        return 1000;
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

    public int getNumberOfTransactions(int resolution) {
        switch (resolution) {
            case 60: return 3;
        }
        return 3;
    }
}
