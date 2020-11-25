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
        String text = MessageFormat.format("at {0}",
                candle.getTime());
        displayTray(caption, text);
    }

    private static void displayTray(String caption, String text) {
        SystemTray tray = SystemTray.getSystemTray();
        if (SystemTray.isSupported()) {
            try {
                Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
                TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip("System tray icon demo");
                tray.add(trayIcon);
                trayIcon.displayMessage(caption, text, MessageType.INFO);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
