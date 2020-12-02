package com.kostro.analizer.utils.notification;

import com.kostro.analizer.wallet.Candle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.text.MessageFormat;

@Slf4j
@Service
@Primary
public class ShowNotification implements Notification {

    private SystemTray tray;
    private TrayIcon trayIcon;

    public ShowNotification() {
        try {
            tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void volume(Candle candle, Candle fiveMins, Candle oneHour, Candle twoHours, Candle oneDay) {
        String caption = MessageFormat.format("{0} {1, number, #.##}",
                candle.getClose() > candle.getOpen() ? "RISING" : "FALLING",
                candle.getClose() - candle.getOpen());
        String text = MessageFormat.format("to {0, number, #.##} with v:{1, number, #.##}",
                candle.getClose(),
                candle.getVolume());
        displayTray(caption, text);
    }

    @Override
    public void level(Candle candle, int level, double max, boolean rised) {
        String caption = MessageFormat.format("{0} {1, number, #}",
                rised ? "ABOVE" : "BELOW",
                level);
        String text = MessageFormat.format("{0, number, #.} {1} max: {2}",
                max - level,//0
                rised ? "to" : "from",//1
                max);//2
        displayTray(caption, text);
    }

    private void displayTray(String caption, String text) {
        trayIcon.displayMessage(caption, text, MessageType.INFO);
    }
}
