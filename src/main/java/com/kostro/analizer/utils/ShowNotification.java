package com.kostro.analizer.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

@Slf4j
public class ShowNotification {
    public void displayTray(String caption, String text) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        if (SystemTray.isSupported()) {
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray icon demo");
            tray.add(trayIcon);

            trayIcon.displayMessage("Hello, World", "notification demo", MessageType.INFO);
        }
    }
}
