package cowin.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import java.awt.Toolkit;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Customized error {@link JFXDialog} which can produce sound on popup.
 *
 * @author Kumar Pranjal
 */
public class ErrorAlert extends JFXDialog {

    private static final Runnable runnableSound =
            (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.asterisk");
    private static final Image errorIcon = new Image(
            Objects.requireNonNull(ErrorAlert.class.getResourceAsStream("/images/Icon_Error.gif")),
            50, 50, true, true);
    private static final Text errorText = new Text("Error!");
    private static final ImageView imageView = new ImageView(errorIcon);

    static {
        errorText.setFont(new Font(24));
    }

    /**
     * Constructs an {@code ErrorAlert} with given parent and contentText.
     *
     * @param parent      The parent {@link StackPane}
     * @param contentText Alert body text
     */
    public ErrorAlert(final StackPane parent, final String contentText) {
        final Text bodyText = new Text(contentText);
        bodyText.setFont(new Font(14));
        final JFXButton button = new JFXButton("OK");
        button.setFont(new Font(14));
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setDefaultButton(true);
        button.setRipplerFill(Color.valueOf("#7d7d75"));
        button.setStyle("-fx-background-color: #b4b4ac;");
        button.setOnAction(e -> close());
        final JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new TilePane(errorText, imageView));
        layout.setBody(bodyText);
        layout.setActions(button);
        setDialogContainer(parent);
        setContent(layout);
        setTransitionType(DialogTransition.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        if (runnableSound != null) {
            runnableSound.run();
        }
        super.show();
    }
}
