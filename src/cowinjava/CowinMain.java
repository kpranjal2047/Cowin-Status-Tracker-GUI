package cowinjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Class
 * 
 * @author Kumar Pranjal
 */
public class CowinMain extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final Stage stage) throws Exception {
        final Parent root = FXMLLoader.load(getClass().getResource("CowinGUI.fxml"));
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Cowin Status Tracker");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Main function
     * 
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
