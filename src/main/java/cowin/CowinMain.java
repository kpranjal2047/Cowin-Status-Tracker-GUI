package cowin;

import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.NonNull;

/**
 * Main Class
 *
 * @author Kumar Pranjal
 */
public class CowinMain extends Application {

  /**
   * Main function
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch(args);
  }

  /** {@inheritDoc} */
  @Override
  public void start(@NonNull final Stage stage) throws Exception {
    final Parent root =
        FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/CowinGUI.fxml")));
    stage.setScene(new Scene(root));
    stage.setTitle("Cowin Status Tracker");
    stage
        .getIcons()
        .add(
            new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/images/Icon_Logo.png")),
                0,
                0,
                true,
                true));
    stage.setResizable(false);
    stage.setOnCloseRequest(e -> System.exit(0));
    stage.show();
  }
}
