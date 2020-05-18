package com.kostro.analizer.json.interfaces;

import com.kostro.analizer.wallet.Candel;

import java.time.LocalDateTime;
import java.util.List;

interface Market {
    List<String> getMarkets();

    List<Candel> getCandles(String market, Integer resolution, LocalDateTime from, LocalDateTime to);
}