package cowinjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cowinjava.data.Center;
import cowinjava.exceptions.InvalidInputException;
import cowinjava.services.DistrictUpdateService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

/**
 * GUI FXML Controller class
 *
 * @author Kumar Pranjal
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
    private Spinner<Integer> refreshInput;
    @FXML
    private JFXCheckBox ageCheckbox;
    @FXML
    private Spinner<Integer> ageInput;
    @FXML
    private JFXCheckBox vaccineCheckbox;
    @FXML
    private JFXComboBox<String> vaccineInput;
    @FXML
    private JFXCheckBox doseCheckbox;
    @FXML
    private JFXToggleButton doseInput;
    @FXML
    private JFXCheckBox feeCheckbox;
    @FXML
    private JFXToggleButton feeInput;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXButton stopButton;
    @FXML
    private TableView<Center> resulttable;
    @FXML
    private TableColumn<Center, Long> agecolumn;
    @FXML
    private TableColumn<Center, String> vaccinecolumn;
    @FXML
    private TableColumn<Center, String> datecolumn;
    @FXML
    private TableColumn<Center, Long> dose1column;
    @FXML
    private TableColumn<Center, Long> dose2column;
    @FXML
    private TableColumn<Center, String> feetypecolumn;
    @FXML
    private TableColumn<Center, String> centernamecolumn;
    @FXML
    private TableColumn<Center, Long> pincodecolumn;

    private JSONObject map;
    private DistrictUpdateService dist_service;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try (InputStream inputStream = getClass().getResourceAsStream("dist_map.json")) {
            map = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(inputStream)));
        } catch (IOException | ParseException e) {
        }
        dist_service = new DistrictUpdateService();
        fillStates();
        fillVaccineNames();
        initializeTable();
        refreshInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 300, 60, 1));
        ageInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 18, 1));
        pincodeLabel.setTextFill(Paint.valueOf("#ee5c5c"));
        stateInput.setVisible(false);
        districtInput.setVisible(false);
        stopButton.setDisable(true);
        ageInput.setDisable(true);
        vaccineInput.setDisable(true);
        doseInput.setDisable(true);
        feeInput.setDisable(true);
    }

    /**
     * This method modifies GUI when "search by pin/district" toggle is switched.
     *
     * @param event ActionEvent object
     * @see #prepareSearchByPin()
     * @see #prepareSearchByDistrict()
     */
    @FXML
    private void pindistChangeHandler(ActionEvent event) {
        if (pindistToggle.isSelected()) {
            pincodeLabel.setTextFill(Paint.valueOf("black"));
            districtLabel.setTextFill(Paint.valueOf("#00bca9"));
            pinInput.setVisible(false);
            stateInput.setVisible(true);
            districtInput.setVisible(true);
        } else {
            pincodeLabel.setTextFill(Paint.valueOf("#ee5c5c"));
            districtLabel.setTextFill(Paint.valueOf("black"));
            pinInput.setVisible(true);
            stateInput.setVisible(false);
            districtInput.setVisible(false);
        }
    }

    /**
     * This method is called when new state is selected. The method fills district
     * names in district select combo box.
     *
     * @param event ActionEvent object
     * @see #fillDistricts(String)
     */
    @FXML
    private void stateChangeHandler(ActionEvent event) {
        String state = stateInput.getValue();
        fillDistricts(state);
    }

    /**
     * This method enables/disables age input when age checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void ageCheckBoxHandler(ActionEvent event) {
        ageInput.setDisable(!ageCheckbox.isSelected());
    }

    /**
     * This method enables/disables vaccine combo box when vaccine checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void vaccineCheckBoxHandler(ActionEvent event) {
        vaccineInput.setDisable(!vaccineCheckbox.isSelected());
    }

    /**
     * This method enables/disables dose number toggle when dose checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void doseCheckBoxHandler(ActionEvent event) {
        doseInput.setDisable(!doseCheckbox.isSelected());
    }

    /**
     * This method enables/disables fee type toggle when fee checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void feeCheckBoxHandler(ActionEvent event) {
        feeInput.setDisable(!feeCheckbox.isSelected());
    }

    /**
     * This method is called when Start button is clicked.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void startButtonHandler(ActionEvent event) {
        startButton.setText("Running...");
        startButton.setDisable(true);
        stopButton.setDisable(false);
        pindistToggle.setDisable(true);
        stateInput.setDisable(true);
        districtInput.setDisable(true);
        pinInput.setDisable(true);
        refreshInput.setDisable(true);
        ageCheckbox.setDisable(true);
        ageInput.setDisable(true);
        vaccineCheckbox.setDisable(true);
        vaccineInput.setDisable(true);
        doseCheckbox.setDisable(true);
        doseInput.setDisable(true);
        feeCheckbox.setDisable(true);
        feeInput.setDisable(true);

        int age;
        if (ageCheckbox.isSelected()) {
            try {
                age = Integer.parseInt(ageInput.getEditor().getText());
                if (age < 0 || age > 100) {
                    throw new InvalidInputException();
                }
            } catch (NumberFormatException | InvalidInputException e) {
                Alert a = new Alert(AlertType.ERROR, "Invalid age value");
                a.setTitle("Cowin Status Tracker");
                a.showAndWait();
                ageInput.getValueFactory().setValue(18);
                return;
            }
        } else {
            age = 100;
        }

        String vaccinename;
        if (vaccineCheckbox.isSelected()) {
            vaccinename = vaccineInput.getValue();
        } else {
            vaccinename = null;
        }

        int dosenumber;
        if (doseCheckbox.isSelected()) {
            dosenumber = doseInput.isSelected() ? 2 : 1;
        } else {
            dosenumber = 0;
        }

        String feetype;
        if (feeCheckbox.isSelected()) {
            feetype = feeInput.isSelected() ? "Paid" : "Free";
        } else {
            feetype = null;
        }

        int duration;
        try {
            duration = Integer.parseInt(refreshInput.getEditor().getText());
            if (duration < 3 || duration > 300) {
                throw new InvalidInputException();
            }
        } catch (NumberFormatException | InvalidInputException e) {
            Alert a = new Alert(AlertType.ERROR, "Invalid refresh duration value");
            a.setTitle("Cowin Status Tracker");
            a.showAndWait();
            ageInput.getValueFactory().setValue(60);
            return;
        }

        if (pindistToggle.isSelected()) {
            String state = stateInput.getValue();
            String district = districtInput.getValue();
            long id = (long) ((JSONObject) map.get(state)).get(district);
            scanByDistrict(id, duration, age, vaccinename, dosenumber, feetype);
        } else {
            int pincode;
            try {
                pincode = Integer.parseInt(pinInput.getText());
                if (pincode < 100000 || pincode > 999999) {
                    throw new InvalidInputException();
                }
            } catch (NumberFormatException | InvalidInputException e) {
                Alert a = new Alert(AlertType.ERROR, "Invalid Pincode");
                a.setTitle("Cowin Status Tracker");
                a.showAndWait();
                return;
            }
            scanByPincode(pincode, duration, age, vaccinename, dosenumber, feetype);
        }
    }

    /**
     * This method is called when Stop button is clicked.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void stopButtonHandler(ActionEvent event) {
        if (pindistToggle.isSelected()) {
            dist_service.cancel();
        } else {

        }

        startButton.setText("Start");
        startButton.setDisable(false);
        stopButton.setDisable(true);
        pindistToggle.setDisable(false);
        stateInput.setDisable(false);
        districtInput.setDisable(false);
        pinInput.setDisable(false);
        refreshInput.setDisable(false);
        ageCheckbox.setDisable(false);
        ageInput.setDisable(!ageCheckbox.isSelected());
        vaccineCheckbox.setDisable(false);
        vaccineInput.setDisable(!vaccineCheckbox.isSelected());
        doseCheckbox.setDisable(false);
        doseInput.setDisable(!doseCheckbox.isSelected());
        feeCheckbox.setDisable(false);
        feeInput.setDisable(!feeInput.isSelected());
    }

    /**
     * Method for filling state names during initialization.
     * 
     * @see #fillDistricts(String)
     */
    private void fillStates() {
        ArrayList<String> list = new ArrayList<>();
        for (Object s : map.keySet()) {
            String state = (String) s;
            list.add(state);
        }
        Collections.sort(list);
        stateInput.setItems(FXCollections.observableList(list));
        stateInput.setValue(list.get(0));
        fillDistricts(list.get(0));
    }

    /**
     * Method for filling vaccine names during initialization.
     */
    private void fillVaccineNames() {
        List<String> list = Arrays.asList("Covishield", "Covaxin", "Sputnik V");
        Collections.sort(list);
        vaccineInput.setItems(FXCollections.observableList(list));
        vaccineInput.setValue(list.get(0));
    }

    /**
     * Method for updating district names when new state is selected.
     *
     * @param state New state name
     * @see #stateChangeHandler(ActionEvent)
     */
    private void fillDistricts(String state) {
        ArrayList<String> list = new ArrayList<>();
        JSONObject district_id = (JSONObject) map.get(state);
        for (Object s : district_id.keySet()) {
            String district = (String) s;
            list.add(district);
        }
        Collections.sort(list);
        districtInput.setItems(FXCollections.observableList(list));
        districtInput.setValue(list.get(0));
    }

    private void initializeTable() {
        resulttable.setPlaceholder(new Label("No vaccination centers found!"));
        agecolumn.setCellValueFactory(new PropertyValueFactory<>("minage"));
        vaccinecolumn.setCellValueFactory(new PropertyValueFactory<>("vaccinename"));
        datecolumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dose1column.setCellValueFactory(new PropertyValueFactory<>("dose1count"));
        dose2column.setCellValueFactory(new PropertyValueFactory<>("dose2count"));
        feetypecolumn.setCellValueFactory(new PropertyValueFactory<>("feetype"));
        centernamecolumn.setCellValueFactory(new PropertyValueFactory<>("centername"));
        pincodecolumn.setCellValueFactory(new PropertyValueFactory<>("pincode"));
    }

    private void scanByDistrict(long dist_id, int duration, int age, String vaccinename, int dosenumber,
            String feetype) {
        dist_service.setDist_id(dist_id);
        dist_service.setPeriod(Duration.seconds(duration));
        dist_service.setAge(age);
        dist_service.setVaccinename(vaccinename);
        dist_service.setDosenumber(dosenumber);
        dist_service.setFeetype(feetype);
        dist_service.setOnSucceeded(e -> {
            ArrayList<Center> availablecenters = dist_service.getValue();
            resulttable.getItems().clear();
            resulttable.getItems().addAll(availablecenters);
        });
        dist_service.start();
    }

    private void scanByPincode(int pincode, int duration, int age, String vaccinename, int dosenumber, String feetype) {
        // TODO
    }
}
