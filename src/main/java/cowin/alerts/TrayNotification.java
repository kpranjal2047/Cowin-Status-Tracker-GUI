package cowin.alerts;

import cowin.util.ResourceLoader;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import lombok.experimental.UtilityClass;

/**
 * This class provides static methods for displaying error and info notification using Java AWT
 * {@link SystemTray}.
 *
 * @author Kumar Pranjal
 */
@UtilityClass
public class TrayNotification {

  private TrayIcon trayIcon;

  static {
    if (SystemTray.isSupported()) {
      final SystemTray tray = SystemTray.getSystemTray();
      final Image image =
          Toolkit.getDefaultToolkit()
              .createImage(ResourceLoader.loadResource("images/Icon_Logo.png"));
      trayIcon = new TrayIcon(image, "Cowin Status Tracker");
      trayIcon.setImageAutoSize(true);
      try {
        tray.add(trayIcon);
      } catch (final AWTException ignored) {
      }
    }
  }

  /**
   * Static method for displaying info notifications.
   *
   * @param content Notification body
   * @param header Notification title
   */
  public void showInfoNotification(final String content, final String header) {
    if (SystemTray.isSupported()) {
      trayIcon.displayMessage(header, content, MessageType.INFO);
    }
  }

  /**
   * Static method for displaying error notifications.
   *
   * @param content Notification body
   * @param header Notification title
   */
  public void showErrorNotification(final String content, final String header) {
    if (SystemTray.isSupported()) {
      trayIcon.displayMessage(header, content, MessageType.ERROR);
    }
  }
}
