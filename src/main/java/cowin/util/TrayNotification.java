package cowin.util;

import java.awt.*;
import java.awt.TrayIcon.MessageType;

/**
 * This class provides static methods for displaying error and info notification
 * using Java AWT {@link SystemTray}.
 *
 * @author Kumar Pranjal
 */
public class TrayNotification {

    private static TrayIcon trayIcon;

    static {
        if (SystemTray.isSupported()) {
            final SystemTray tray = SystemTray.getSystemTray();
            final Image image = Toolkit.getDefaultToolkit()
                    .createImage(TrayNotification.class.getResource("/images/Icon_Logo.png"));
            trayIcon = new TrayIcon(image, "Cowin Status Tracker");
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (final AWTException ignored) {
            }
        }
    }

    /**
     * Private constructor to prevent object creation externally.
     */
    private TrayNotification() {
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
