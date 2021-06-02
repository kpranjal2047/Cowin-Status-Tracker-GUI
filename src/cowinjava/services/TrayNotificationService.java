package cowinjava.services;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

/**
 * Abstract class representing System tray notification service. Static methods
 * are provided for displaying notifications.
 * 
 * @author Kumar Pranjal
 */
public abstract class TrayNotificationService {

    private static TrayIcon trayIcon;
    static {
        if (SystemTray.isSupported()) {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image image = Toolkit.getDefaultToolkit().createImage("");
            trayIcon = new TrayIcon(image, null);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (final AWTException e) {
            }
        }
    }

    /**
     * Static method for displaying info notifications.
     * 
     * @param content Notification body
     * @param header  Notification title
     */
    public static void showInfoNotification(final String content, final String header) {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(header, content, MessageType.INFO);
        }
    }

    /**
     * Static method for displaying error notifications.
     * 
     * @param content Notification body
     * @param header  Notification title
     */
    public static void showErrorNotification(final String content, final String header) {
        if (SystemTray.isSupported()) {
            trayIcon.displayMessage(header, content, MessageType.ERROR);
        }
    }
}
