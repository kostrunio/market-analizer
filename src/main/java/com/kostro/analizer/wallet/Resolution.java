package com.kostro.analizer.wallet;

public enum Resolution {

    ONE_MIN("1 min", 60),
    THREE_MINS("3 mins", 180),
    FIVE_MINS("5 mins", 300),
    FIFTEEN_MINS("15 mins", 900),
    THIRTY_MINS("30 mins", 1800),
    ONE_HOUR("1 hour", 3600),
    TWO_HOURS("2 hours", 7200),
    FOUR_HOURS("4 hours", 14400),
    SIX_HOURS("6 hours", 21600),
    TWELWE_HOURS("12 hours", 43200),
    ONE_DAY("1 day", 86400),
    THREE_DAYS("3 days", 259200),
    ONE_WEEK("1 week", 604800);

    String name;
    int secs;

    private Resolution(String name, int secs) {
        this.name = name;
        this.secs = secs;
    }

    public int getSecs() {
        return secs;
    }

    @Override
    public String toString() {
        return name;
    }


}
