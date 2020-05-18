package com.kostro.analizer.wallet;

public enum Resolution {

    ONE_MIN("1 min", 60, "1m"),
    THREE_MINS("3 mins", 180, "3m"),
    FIVE_MINS("5 mins", 300, "5m"),
    FIFTEEN_MINS("15 mins", 900, "15m"),
    THIRTY_MINS("30 mins", 1800, "30m"),
    ONE_HOUR("1 hour", 3600, "1h"),
    TWO_HOURS("2 hours", 7200, "2h"),
    FOUR_HOURS("4 hours", 14400, "4h"),
    SIX_HOURS("6 hours", 21600, "6h"),
    TWELWE_HOURS("12 hours", 43200, "12h"),
    ONE_DAY("1 day", 86400, "1d"),
    THREE_DAYS("3 days", 259200, "3d"),
    ONE_WEEK("1 week", 604800, "1w");

    String name;
    int secs;
    String code;

    private Resolution(String name, int secs, String code) {
        this.name = name;
        this.secs = secs;
        this.code = code;
    }

    public int getSecs() {
        return secs;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }


}
