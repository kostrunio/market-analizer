package com.kostro.analizer.json.bitbay.domain.candle;

import lombok.Data;

import java.util.List;

@Data
public class CandleResponse {
    private String status;
    private List<List<Object>> items;
}
