package cowin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final @NotNull Stage stage) throws Exception {
        final Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CowinGUI.fxml")));
        stage.setScene(new Scene(root));
        stage.setTitle("Cowin Status Tracker");
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }
}
