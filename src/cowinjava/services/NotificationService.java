package cowinjava.services;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public abstract class NotificationService {

    private static TrayIcon trayIcon;
    static {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("");
            trayIcon = new TrayIcon(image, null);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
            }
        }
    }

    public static void showInfoNotification(String content, String header) {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(header, content, MessageType.INFO);
        }
    }

    public static void showErrorNotification(String content, String header) {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(header, content, MessageType.ERROR);
        }
    }
}
