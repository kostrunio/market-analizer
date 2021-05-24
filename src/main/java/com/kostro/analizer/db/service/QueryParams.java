package com.kostro.analizer.db.service;

import com.kostro.analizer.wallet.Resolution;

import java.time.LocalDateTime;

public class QueryParams {
    public final String market;
    public final LocalDateTime startDate;
    public final LocalDateTime endDate;
    public final Resolution resolution;
    public final int volume;

    public static class Builder {
        private final String market;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;
        private final Resolution resolution;

        private int volume = 0;

        public Builder(String market, LocalDateTime startDate, LocalDateTime endDate, Resolution resolution) {
            this.market = market;
            this.startDate = startDate;
            this.endDate = endDate;
            this.resolution = resolution;
        }

        public Builder volume(int volume) {
            this.volume = volume;
            return this;
        }

        public QueryParams build() {
            return new QueryParams(this);
        }
    }

    public QueryParams(Builder builder) {
        market = builder.market;
        startDate = builder.startDate;
        endDate = builder.endDate;
        resolution = builder.resolution;
        volume = builder.volume;
    }
}
