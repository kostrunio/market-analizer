package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.ConfigurationEntity;
import com.kostro.analizer.db.repository.ConfigurationRepository;
import com.kostro.analizer.wallet.Resolution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConfigurationService {

    private ConfigurationRepository configurationRepository;

    private Integer maxPeriod;
    private String market;
    private Resolution resolution;
    private Boolean sendVolume;
    private Boolean runScheduler;
    private Boolean stopBuying;
    private Integer limit60;
    private Integer lastLevel;
    private Integer levelStep;
    private Boolean sendLevel;
    private Double maxLevel;

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

        if (isSendVolume() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("sendVolume");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (isRunScheduler() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("runScheduler");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (isStopBuying() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("stopBuying");
            entity.setValue("false");
            configurationRepository.save(entity);
        }

        if (getLimit60() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("limit60");
            entity.setValue("300");
            configurationRepository.save(entity);
        }

        if (getLastLevel() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("lastLevel");
            entity.setValue("17700");
            configurationRepository.save(entity);
        }

        if (getLevelStep() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("levelStep");
            entity.setValue("100");
            configurationRepository.save(entity);
        }

        if (isSendLevel() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("sendLevel");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (getMaxLevel() == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setName("maxLevel");
            entity.setValue("18965.90");
            configurationRepository.save(entity);
        }
    }

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public Integer getMaxPeriod() {
        if (maxPeriod != null) return maxPeriod;
        ConfigurationEntity entity = configurationRepository.findByName("maxPeriod");
        if (entity != null) {
            maxPeriod = Integer.parseInt(entity.getValue());
        }
        return maxPeriod;
    }

    public void setMaxPeriod(Integer value) {
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

    public Boolean isSendVolume() {
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

    public Boolean isRunScheduler() {
        if (runScheduler != null) return runScheduler;
        ConfigurationEntity entity = configurationRepository.findByName("runScheduler");
        if (entity != null) {
            runScheduler = Boolean.parseBoolean(entity.getValue());
        }
        return runScheduler;
    }

    public void setRunScheduler(boolean value) {
        runScheduler = value;
        ConfigurationEntity entity = configurationRepository.findByName("runScheduler");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public Boolean isStopBuying() {
        if (stopBuying != null) return stopBuying;
        ConfigurationEntity entity = configurationRepository.findByName("stopBuying");
        if (entity != null) {
            stopBuying = Boolean.parseBoolean(entity.getValue());
        }
        return stopBuying;
    }

    public void setStopBuying(boolean value) {
        stopBuying = value;
        ConfigurationEntity entity = configurationRepository.findByName("stopBuying");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public Integer getLimit60() {
        if (limit60 != null) return limit60;
        ConfigurationEntity entity = configurationRepository.findByName("limit60");
        if (entity != null) {
            limit60 = Integer.parseInt(entity.getValue());
        }
        return limit60;
    }

    public void setLimit60(Integer value) {
        limit60 = value;
        ConfigurationEntity entity = configurationRepository.findByName("limit60");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Integer getLastLevel() {
        if (lastLevel != null) return lastLevel;
        ConfigurationEntity entity = configurationRepository.findByName("lastLevel");
        if (entity != null) {
            lastLevel = Integer.parseInt(entity.getValue());
        }
        return lastLevel;
    }

    public void setLastLevel(Integer value) {
        lastLevel = value;
        ConfigurationEntity entity = configurationRepository.findByName("lastLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Integer getLevelStep() {
        if (levelStep != null) return levelStep;
        ConfigurationEntity entity = configurationRepository.findByName("levelStep");
        if (entity != null) {
            levelStep = Integer.parseInt(entity.getValue());
        }
        return levelStep;
    }

    public void setLevelStep(Integer value) {
        levelStep = value;
        ConfigurationEntity entity = configurationRepository.findByName("levelStep");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Boolean isSendLevel() {
        if (sendLevel != null) return sendLevel;
        ConfigurationEntity entity = configurationRepository.findByName("sendLevel");
        if (entity != null) {
            sendLevel = Boolean.parseBoolean(entity.getValue());
        }
        return sendLevel;
    }

    public void setSendLevel(Boolean value) {
        sendLevel = value;
        ConfigurationEntity entity = configurationRepository.findByName("sendLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Double getMaxLevel() {
        if (maxLevel != null) return maxLevel;
        ConfigurationEntity entity = configurationRepository.findByName("maxLevel");
        if (entity != null) {
            maxLevel = Double.parseDouble(entity.getValue());
        }
        return maxLevel;
    }

    public void setMaxLevel(Double value) {
        maxLevel = value;
        ConfigurationEntity entity = configurationRepository.findByName("maxLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public int getLimitFor(int secs) {
        switch (secs) {
            case 60:
                return getLimit60();
        }
        return 1000;
    }

}
