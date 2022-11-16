package cowin;

import cowin.controllers.CowinGUIController;
import cowin.util.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    final FXMLLoader loader = new FXMLLoader(ResourceLoader.loadResource("fxml/CowinGUI.fxml"));
    final Parent root = loader.load();
    final CowinGUIController controller = loader.getController();
    controller.setStage(stage);
    final Scene scene = new Scene(root);
    scene.setFill(Color.TRANSPARENT);
    stage.setScene(scene);
    stage.setTitle("Cowin Status Tracker");
    stage
        .getIcons()
        .add(
            new Image(
                ResourceLoader.loadResourceAsStream("images/Icon_Logo.png"), 0, 0, true, true));
    stage.setResizable(false);
    stage.setOnCloseRequest(e -> System.exit(0));
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.show();
  }
}
