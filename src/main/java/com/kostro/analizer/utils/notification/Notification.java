package com.kostro.analizer.utils.notification;

import com.kostro.analizer.wallet.Candle;

public interface Notification {

    void volume(String market, Candle candle, Candle fiveMins, Candle oneHour, Candle twoHours, Candle oneDay);

    void level(String market, Candle candle, double level, double max, boolean rised);
}
