package com.kostro.analizer.wallet;

import java.util.*;

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

    private final String name;
    private final int secs;
    private final String code;

    private static final List<Resolution> resolutions = new ArrayList<>();
    private static final Map<Integer, Resolution> resolutionMapInteger = new HashMap<>();
    private static final Map<String, Resolution> resolutionMapString = new HashMap<>();

    private Resolution(String name, int secs, String code) {
        this.name = name;
        this.secs = secs;
        this.code = code;
    }

    public static Resolution of(int secs) {
        if (resolutionMapInteger.size() == 0) {
            loadResolutionsToIntegerMap();
        }
        return resolutionMapInteger.get(secs);
    }

    private static synchronized void loadResolutionsToIntegerMap() {
        if (resolutionMapInteger.size() == 0) {
            for (Resolution resolution : getResolutions()) {
                resolutionMapInteger.put(resolution.getSecs(), resolution);
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getSecs() {
        return secs;
    }

    public String getCode() {
        return code;
    }

    public static List<Resolution> getResolutions() {
        if (resolutions.size() == 0) {
            prepareList();
        }
        return resolutions;
    }

    private static synchronized void prepareList() {
        if (resolutions.size() == 0) {
            resolutions.addAll(Arrays.asList(ONE_MIN, THREE_MINS, FIVE_MINS, FIFTEEN_MINS, THREE_MINS,
                    ONE_HOUR, TWO_HOURS, FOUR_HOURS, SIX_HOURS, TWELWE_HOURS,
                    ONE_DAY, THREE_DAYS, ONE_WEEK));
        }
    }
    
    public static Resolution of(String name) {
        if (resolutionMapString.size() == 0) {
            loadResolutionsToStringMap();
        }
        return resolutionMapString.get(name);
    }

    private static synchronized void loadResolutionsToStringMap() {
        if (resolutionMapString.size() == 0) {
            for (Resolution resolution : getResolutions()) {
                resolutionMapString.put(resolution.getName(), resolution);
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

}
