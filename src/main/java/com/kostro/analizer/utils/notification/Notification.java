package com.kostro.analizer.utils.notification;

import com.kostro.analizer.wallet.Candle;

public interface Notification {

    void volume(Candle candle, Candle fiveMins, Candle oneHour, Candle twoHours, Candle oneDay);

    void level(Candle candle, int level, double max, boolean rised);
}
