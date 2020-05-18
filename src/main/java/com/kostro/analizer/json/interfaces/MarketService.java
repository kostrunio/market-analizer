package com.kostro.analizer.json.interfaces;

import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;

import java.time.LocalDateTime;
import java.util.List;

public interface MarketService {
    List<Candle> getCandles(String market, Resolution resolution, LocalDateTime from, LocalDateTime to);
}