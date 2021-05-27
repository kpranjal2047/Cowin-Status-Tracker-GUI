package cowinjava;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;

/**
 * FXML Controller class
 *
 * @author kumar
 */
public class CowinGUIController implements Initializable {

    @FXML
    private Label pincodeLabel;
    @FXML
    private Label districtLabel;
    @FXML
    private JFXToggleButton pindistToggle;
    @FXML
    private JFXTextField pinInput;
    @FXML
    private JFXComboBox<String> stateInput;
    @FXML
    private JFXComboBox<String> districtInput;
    @FXML
    private JFXSlider refreshInput;
    @FXML
    private JFXCheckBox ageCheckbox;
    @FXML
    private JFXCheckBox vaccineCheckbox;
    @FXML
    private JFXCheckBox doseCheckbox;
    @FXML
    private JFXSlider ageInput;
    @FXML
    private JFXComboBox<String> vaccineInput;
    @FXML
    private JFXToggleButton doseInput;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXButton stopButton;

    private JSONObject map;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try (InputStream inputStream = getClass().getResourceAsStream("dist_map.json")) {
            map = (JSONObject) new JSONParser().parse(new InputStreamReader(inputStream));
        } catch (IOException | ParseException e) {
        }
        fillStates();
        prepareSearchByPin();
        ageInput.setDisable(true);
        vaccineInput.setDisable(true);
        doseInput.setDisable(true);
    }

    @FXML
    private void pindistChangeHandler(ActionEvent event) {
        if (pindistToggle.isSelected()) {
            prepareSearchByDistrict();
        } else {
            prepareSearchByPin();
        }
    }

    @FXML
    private void stateChangeHandler(ActionEvent event) {
        String state = stateInput.getValue();
        fillDistricts(state);
    }

    @FXML
    private void ageChangeHandler(ActionEvent event) {
        ageInput.setDisable(!ageCheckbox.isSelected());
    }

    @FXML
    private void vaccineChangeHandler(ActionEvent event) {
        vaccineInput.setDisable(!vaccineCheckbox.isSelected());
    }

    @FXML
    private void doseChangeHandler(ActionEvent event) {
        doseInput.setDisable(!doseCheckbox.isSelected());
    }

    @FXML
    private void startButtonHandler(ActionEvent event) {
    }

    @FXML
    private void stopButtonHandler(ActionEvent event) {
    }

    private void fillStates() {
        ArrayList<String> list = new ArrayList<>();
        for (Object s : map.keySet()) {
            String state = (String) s;
            list.add(state);
        }
        Collections.sort(list);
        stateInput.setItems(FXCollections.observableList(list));
    }

    private void fillDistricts(String state) {
        ArrayList<String> list = new ArrayList<>();
        JSONObject district_id = (JSONObject) map.get(state);
        for (Object s : district_id.keySet()) {
            String district = (String) s;
            list.add(district);
        }
        Collections.sort(list);
        districtInput.setItems(FXCollections.observableList(list));
    }

    private void prepareSearchByPin() {
        pincodeLabel.setTextFill(Paint.valueOf("#ee5c5c"));
        districtLabel.setTextFill(Paint.valueOf("black"));
        pinInput.setVisible(true);
        stateInput.setVisible(false);
        districtInput.setVisible(false);
    }

    private void prepareSearchByDistrict() {
        pincodeLabel.setTextFill(Paint.valueOf("black"));
        districtLabel.setTextFill(Paint.valueOf("#00bca9"));
        pinInput.setVisible(false);
        stateInput.setVisible(true);
        districtInput.setVisible(true);
    }
}
