package cowin.alerts;

import cowin.util.ResourceLoader;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ButtonType;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import java.awt.Toolkit;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Customized error dialog {@link MFXStageDialog} which can produce sound on popup.
 *
 * @author Kumar Pranjal
 */
public class ErrorDialog extends MFXStageDialog {
  private static final Runnable runnableSound =
      (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.asterisk");
  private static final Image errorIcon =
      new Image(ResourceLoader.loadResourceAsStream("images/Icon_Error.gif"), 50, 50, true, true);
  private static final String errorText = "Error!";
  private static final ImageView imageView = new ImageView(errorIcon);

  /**
   * Constructs an {@code ErrorDialog} with given parent and contentText.
   *
   * @param stage The parent {@link Stage}
   * @param contentText Alert body text
   */
  public ErrorDialog(final Stage stage, final String contentText) {
    final MFXButton button = new MFXButton("OK");
    button.setFont(new Font(14));
    button.setButtonType(ButtonType.RAISED);
    button.setDefaultButton(true);
    button.setRippleColor(Color.valueOf("#7d7d75"));
    button.setStyle("-fx-background-color: #b4b4ac;");
    button.setOnAction(e -> close());
    final MFXGenericDialog dialogContent =
        MFXGenericDialogBuilder.build()
            .setHeaderIcon(imageView)
            .setHeaderText(errorText)
            .setContentText(contentText)
            .addActions(button)
            .setShowAlwaysOnTop(false)
            .setShowClose(false)
            .setShowMinimize(false)
            .get();
    this.setContent(dialogContent);
    this.initOwner(stage);
    this.initModality(Modality.APPLICATION_MODAL);
    this.setTitle("Dialogs Preview");
    this.setScrimPriority(ScrimPriority.WINDOW);
    this.setScrimOwner(true);
  }

  /** {@inheritDoc} */
  @Override
  public void showDialog() {
    if (Objects.nonNull(runnableSound)) {
      runnableSound.run();
    }
    super.showDialog();
  }
}
