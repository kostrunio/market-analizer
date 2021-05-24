package com.kostro.analizer.db.service;

import com.kostro.analizer.db.model.ConfigurationEntity;
import com.kostro.analizer.db.repository.ConfigurationRepository;
import com.kostro.analizer.ui.configuration.btcusdt.BNBUSDTConfigurationView;
import com.kostro.analizer.ui.configuration.btcusdt.BTCUSDTConfigurationView;
import com.kostro.analizer.ui.configuration.btcusdt.TWTUSDTConfigurationView;
import com.kostro.analizer.wallet.Resolution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class ConfigurationService {

    private ConfigurationRepository configurationRepository;

    private ConcurrentMap<String, Integer> maxPeriod = new ConcurrentHashMap<>();
    private List<String> markets;
    private ConcurrentMap<String, Resolution> resolution = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> sendVolume = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> runScheduler = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> stopBuying = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Integer> limit60 = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Double> lastLevel = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Double> levelStep = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Boolean> sendLevel = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Double> maxLevel = new ConcurrentHashMap<>();

    private ConcurrentMap<String, ConcurrentMap> maps = new ConcurrentHashMap<>();

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;

        setInitConfiguration(BTCUSDTConfigurationView.MARKET);
    }

    private void setInitConfiguration(String market) {
        if (getMaxPeriod(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("maxPeriod");
            entity.setValue((1000*60)+"");
            configurationRepository.save(entity);
        }
        if (getMarkets() == null || getMarkets().size() == 0) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket("ALL");
            entity.setName("markets");
            entity.setValue(BTCUSDTConfigurationView.MARKET);
            configurationRepository.save(entity);
        }
        if (getResolution(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("resolution");
            entity.setValue("1 min");
            configurationRepository.save(entity);
        }

        if (isSendVolume(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("sendVolume");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (isRunScheduler(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("runScheduler");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (isStopBuying(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("stopBuying");
            entity.setValue("false");
            configurationRepository.save(entity);
        }

        if (getLimit60(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("limit60");
            entity.setValue("300");
            configurationRepository.save(entity);
        }

        if (getLastLevel(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("lastLevel");
            entity.setValue("30000");
            configurationRepository.save(entity);
        }

        if (getLevelStep(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("levelStep");
            entity.setValue("500");
            configurationRepository.save(entity);
        }

        if (isSendLevel(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("sendLevel");
            entity.setValue("true");
            configurationRepository.save(entity);
        }

        if (getMaxLevel(market) == null) {
            ConfigurationEntity entity = new ConfigurationEntity();
            entity.setMarket(market);
            entity.setName("maxLevel");
            entity.setValue("30000");
            configurationRepository.save(entity);
        }
    }

    public List<ConfigurationEntity> findAll() {
        return configurationRepository.findAll();
    }

    public Integer getMaxPeriod(String market) {
        if (maxPeriod.containsKey(market)) return maxPeriod.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "maxPeriod");
        if (entity != null) {
            maxPeriod.put(market, Integer.parseInt(entity.getValue()));
        }
        return maxPeriod.get(market);
    }

    public void setMaxPeriod(String market, Integer value) {
        maxPeriod.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "maxPeriod");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public List<String> getMarkets() {
        if (markets != null) return markets;
        ConfigurationEntity entity = configurationRepository.findByMarketAndName("ALL", "markets");
        if (entity != null) {
            markets = Arrays.asList(entity.getValue().split(","));
        }
        return markets;
    }

    public void setMarkets(String market, List<String> value) {
        markets = value;
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "markets");
        entity.setValue(String.join(",", markets));
        configurationRepository.save(entity);
    }

    public Resolution getResolution(String market) {
        if (!resolution.containsKey(market)) {
            resolution.put(market, Resolution.of(configurationRepository.findByMarketAndName(market, "resolution").getValue()));
        }
        return resolution.get(market);
    }

    public void setResolution(String market, String value) {
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "resolution");
        entity.setValue(value);
        configurationRepository.save(entity);
    }

    public Boolean isSendVolume(String market) {
        if (sendVolume.containsKey(market)) return sendVolume.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "sendVolume");
        if (entity != null) {
            sendVolume.put(market, Boolean.parseBoolean(entity.getValue()));
        }
        return sendVolume.get(market);
    }

    public void setSendVolume(String market, boolean value) {
        sendVolume.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "sendVolume");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public Boolean isRunScheduler(String market) {
        if (runScheduler.containsKey(market)) return runScheduler.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "runScheduler");
        if (entity != null) {
            runScheduler.put(market, Boolean.parseBoolean(entity.getValue()));
        }
        return runScheduler.get(market);
    }

    public void setRunScheduler(String market, boolean value) {
        runScheduler.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "runScheduler");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public Boolean isStopBuying(String market) {
        if (stopBuying.containsKey(market)) return stopBuying.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "stopBuying");
        if (entity != null) {
            stopBuying.put(market, Boolean.parseBoolean(entity.getValue()));
        }
        return stopBuying.get(market);
    }

    public void setStopBuying(String market, boolean value) {
        stopBuying.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "stopBuying");
        entity.setValue(Boolean.toString(value));
        configurationRepository.save(entity);
    }

    public Integer getLimit60(String market) {
        if (limit60.containsKey(market)) return limit60.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "limit60");
        if (entity != null) {
            limit60.put(market, Integer.parseInt(entity.getValue()));
        }
        return limit60.get(market);
    }

    public void setLimit60(String market, Integer value) {
        limit60.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "limit60");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Double getLastLevel(String market) {
        if (lastLevel.containsKey(market)) return lastLevel.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "lastLevel");
        if (entity != null) {
            lastLevel.put(market, Double.parseDouble(entity.getValue()));
        }
        return lastLevel.get(market);
    }

    public void setLastLevel(String market, Double value) {
        lastLevel.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "lastLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Double getLevelStep(String market) {
        if (levelStep.containsKey(market)) return levelStep.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "levelStep");
        if (entity != null) {
            levelStep.put(market, Double.parseDouble(entity.getValue()));
        }
        return levelStep.get(market);
    }

    public void setLevelStep(String market, Double value) {
        levelStep.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "levelStep");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Boolean isSendLevel(String market) {
        if (sendLevel.containsKey(market)) return sendLevel.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "sendLevel");
        if (entity != null) {
            sendLevel.put(market, Boolean.parseBoolean(entity.getValue()));
        }
        return sendLevel.get(market);
    }

    public void setSendLevel(String market, Boolean value) {
        sendLevel.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "sendLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public Double getMaxLevel(String market) {
        if (maxLevel.containsKey(market)) return maxLevel.get(market);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "maxLevel");
        if (entity != null) {
            maxLevel.put(market, Double.parseDouble(entity.getValue()));
        }
        return maxLevel.get(market);
    }

    public void setMaxLevel(String market, Double value) {
        maxLevel.put(market, value);
        ConfigurationEntity entity = configurationRepository.findByMarketAndName(market, "maxLevel");
        entity.setValue(value+"");
        configurationRepository.save(entity);
    }

    public int getLimitFor(String market, int secs) {
        switch (secs) {
            case 60:
                return getLimit60(market);
        }
        return 1000;
    }

}
