package com.kostro.analizer.ui.verifier;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.json.binance.service.BinanceService;
import com.kostro.analizer.json.bitbay.service.BitBayService;
import com.kostro.analizer.json.interfaces.MarketService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.utils.CandleUtils;
import com.kostro.analizer.wallet.Candle;
import com.kostro.analizer.wallet.Resolution;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.util.List;

@Route(value="verifier", layout = MainLayout.class)
@PageTitle("Verifier | Market Analizer")
public class VerifierView extends VerifierDesign {
    public static String VIEW_NAME = "Verifier";

    private MarketService marketService;
    private CandleService candleService;

    ComponentEventListener<ClickEvent<Button>> jsonButtonClicked = e -> {
        LocalDateTime fromDate = LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue());
        LocalDateTime toDate = LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue());
        List<Candle> Candles = marketService.getCandles("BTCUSDT", resolutionBox.getValue(), fromDate, toDate);
        for (Candle Candle : Candles) {
            candleService.save(CandleUtils.from(Candle));
        }
    };

    HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<ComboBox<Resolution>, Resolution>> CandleResolutionChanged = e -> {
        LocalDateTime fromDate = LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue());
        LocalDateTime toDate = LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue());
        List<Candle> Candles = candleService.find(fromDate, toDate, resolutionBox.getValue().getSecs());
        List<Candle> newResolutionList = CandleUtils.prepareCandles(Candles, CandleResolutionBox.getValue().getSecs(), fromDate, toDate);
        CandleGrid.setItems(newResolutionList);
    };

    public VerifierView(BinanceService bitBayService, CandleService candleService) {
        this.marketService = bitBayService;
        this.candleService = candleService;

        jsonButton.addClickListener(jsonButtonClicked);
        CandleResolutionBox.addValueChangeListener(CandleResolutionChanged);
    }
}
