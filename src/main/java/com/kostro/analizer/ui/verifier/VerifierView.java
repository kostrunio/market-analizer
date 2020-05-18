package com.kostro.analizer.ui.verifier;

import com.kostro.analizer.db.service.CandleService;
import com.kostro.analizer.json.domain.candle.CandleResponse;
import com.kostro.analizer.json.service.JsonService;
import com.kostro.analizer.ui.MainLayout;
import com.kostro.analizer.utils.CandelUtils;
import com.kostro.analizer.wallet.Candel;
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

    private JsonService jsonService;
    private CandleService candleService;

    ComponentEventListener<ClickEvent<Button>> jsonButtonClicked = e -> {
        LocalDateTime fromDate = LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue());
        LocalDateTime toDate = LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue());
        CandleResponse response = jsonService.getCandles("BTC-PLN", resolutionBox.getValue().getSecs(), fromDate, toDate);
        List<Candel> candels = CandelUtils.createCandels(response, resolutionBox.getValue().getSecs());
        for (Candel candel : candels) {
            candleService.save(CandelUtils.from(candel));
        }
    };

    HasValue.ValueChangeListener<? super AbstractField.ComponentValueChangeEvent<ComboBox<Resolution>, Resolution>> candelResolutionChanged = e -> {
        LocalDateTime fromDate = LocalDateTime.of(fromDatePicker.getValue(), fromTimePicker.getValue());
        LocalDateTime toDate = LocalDateTime.of(toDatePicker.getValue(), toTimePicker.getValue());
        List<Candel> candels = candleService.find(fromDate, toDate, resolutionBox.getValue().getSecs());
        List<Candel> newResolutionList = CandelUtils.prepareCandels(candels, candelResolutionBox.getValue().getSecs(), fromDate, toDate);
        candelGrid.setItems(newResolutionList);
    };

    public VerifierView(JsonService jsonService, CandleService candleService) {
        this.jsonService = jsonService;
        this.candleService = candleService;

        jsonButton.addClickListener(jsonButtonClicked);
        candelResolutionBox.addValueChangeListener(candelResolutionChanged);
    }
}
