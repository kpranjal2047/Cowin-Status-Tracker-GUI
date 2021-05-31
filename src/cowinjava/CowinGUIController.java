package cowinjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;
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

import cowinjava.exceptions.InvalidInputException;
import cowinjava.output.Center;
import cowinjava.services.DistrictUpdateService;
import cowinjava.services.NotificationService;
import cowinjava.services.PincodeUpdateService;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
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
    private JFXToggleButton notificationToggle;
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
    private Label statusLabel;
    @FXML
    private TableView<Center> resultTable;
    @FXML
    private TableColumn<Center, Long> ageColumn;
    @FXML
    private TableColumn<Center, String> vaccineColumn;
    @FXML
    private TableColumn<Center, String> dateColumn;
    @FXML
    private TableColumn<Center, Long> dose1Column;
    @FXML
    private TableColumn<Center, Long> dose2Column;
    @FXML
    private TableColumn<Center, String> feeTypeColumn;
    @FXML
    private TableColumn<Center, String> centerNameColumn;
    @FXML
    private TableColumn<Center, Long> pincodeColumn;

    private JSONObject map;
    private final DistrictUpdateService dist_service;
    private final PincodeUpdateService pin_service;

    public CowinGUIController() {
        try (InputStream inputStream = getClass().getResourceAsStream("dist_map.json")) {
            map = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(inputStream)));
        } catch (IOException | ParseException e) {
        }
        dist_service = DistrictUpdateService.getDistrictUpdateService();
        pin_service = PincodeUpdateService.getPincodeUpdateService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
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
    private void pindistChangeHandler(final ActionEvent event) {
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
    private void stateChangeHandler(final ActionEvent event) {
        final String state = stateInput.getValue();
        fillDistricts(state);
    }

    /**
     * This method enables/disables age input when age checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void ageCheckBoxHandler(final ActionEvent event) {
        ageInput.setDisable(!ageCheckbox.isSelected());
    }

    /**
     * This method enables/disables vaccine combo box when vaccine checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void vaccineCheckBoxHandler(final ActionEvent event) {
        vaccineInput.setDisable(!vaccineCheckbox.isSelected());
    }

    /**
     * This method enables/disables dose number toggle when dose checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void doseCheckBoxHandler(final ActionEvent event) {
        doseInput.setDisable(!doseCheckbox.isSelected());
    }

    /**
     * This method enables/disables fee type toggle when fee checkbox is
     * selected/deselected.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void feeCheckBoxHandler(final ActionEvent event) {
        feeInput.setDisable(!feeCheckbox.isSelected());
    }

    /**
     * This method is called when Start button is clicked.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void startButtonHandler(final ActionEvent event) {
        int age;
        if (ageCheckbox.isSelected()) {
            try {
                age = Integer.parseInt(ageInput.getEditor().getText());
                if (age < 0 || age > 100) {
                    throw new InvalidInputException("Age out of range");
                }
            } catch (NumberFormatException | InvalidInputException e) {
                final Alert a = new Alert(AlertType.ERROR, "Invalid age value - " + e.getMessage());
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
                throw new InvalidInputException("Duration out of range");
            }
        } catch (NumberFormatException | InvalidInputException e) {
            final Alert a = new Alert(AlertType.ERROR, "Invalid refresh duration value - " + e.getMessage());
            a.setTitle("Cowin Status Tracker");
            a.showAndWait();
            ageInput.getValueFactory().setValue(60);
            return;
        }

        long id = 0L;
        int pincode = 0;
        if (pindistToggle.isSelected()) {
            final String state = stateInput.getValue();
            final String district = districtInput.getValue();
            id = (long) ((JSONObject) map.get(state)).get(district);
        } else {
            try {
                pincode = Integer.parseInt(pinInput.getText());
                if (pincode < 100000 || pincode > 999999) {
                    throw new InvalidInputException("Entered pincode is not 6-digit");
                }
            } catch (NumberFormatException | InvalidInputException e) {
                final Alert a = new Alert(AlertType.ERROR, "Invalid Pincode - " + e.getMessage());
                a.setTitle("Cowin Status Tracker");
                a.showAndWait();
                return;
            }
        }

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

        if (pindistToggle.isSelected()) {
            scanByDistrict(id, duration, age, vaccinename, dosenumber, feetype);
        } else {
            scanByPincode(pincode, duration, age, vaccinename, dosenumber, feetype);
        }
    }

    /**
     * This method is called when Stop button is clicked.
     *
     * @param event ActionEvent object
     */
    @FXML
    private void stopButtonHandler(final ActionEvent event) {
        if (pindistToggle.isSelected()) {
            dist_service.cancel();
            dist_service.reset();
        } else {
            pin_service.cancel();
            pin_service.reset();
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
        feeInput.setDisable(!feeCheckbox.isSelected());
    }

    /**
     * Method for filling state names during initialization.
     * 
     * @see #fillDistricts(String)
     */
    private void fillStates() {
        final ArrayList<String> list = new ArrayList<>();
        for (final Object s : map.keySet()) {
            final String state = (String) s;
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
        final List<String> list = Arrays.asList("Covishield", "Covaxin", "Sputnik V");
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
    private void fillDistricts(final String state) {
        final ArrayList<String> list = new ArrayList<>();
        final JSONObject district_id = (JSONObject) map.get(state);
        for (final Object s : district_id.keySet()) {
            final String district = (String) s;
            list.add(district);
        }
        Collections.sort(list);
        districtInput.setItems(FXCollections.observableList(list));
        districtInput.setValue(list.get(0));
    }

    /**
     * Method to initialize results table during initialization.
     */
    private void initializeTable() {
        resultTable.setPlaceholder(new Label("No vaccination centers found!"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("minage"));
        vaccineColumn.setCellValueFactory(new PropertyValueFactory<>("vaccinename"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dose1Column.setCellValueFactory(new PropertyValueFactory<>("dose1count"));
        dose2Column.setCellValueFactory(new PropertyValueFactory<>("dose2count"));
        feeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("feetype"));
        centerNameColumn.setCellValueFactory(new PropertyValueFactory<>("centername"));
        pincodeColumn.setCellValueFactory(new PropertyValueFactory<>("pincode"));
    }

    /**
     * Method for getting the results using district id. The methods starts a
     * background service for update checking and displays the result whenever a
     * vaccination slot is found. The background service runs indefinitely until
     * stopped by 'Stop' button.
     * 
     * @param dist_id     District ID
     * @param duration    Refresh interval duration
     * @param age         Age of the person
     * @param vaccinename Preferred vaccine name
     * @param dosenumber  Dose number
     * @param feetype     Fee type (Free / Paid)
     */
    private void scanByDistrict(final long dist_id, final int duration, final int age, final String vaccinename,
            final int dosenumber, final String feetype) {
        dist_service.setValues(dist_id, age, vaccinename, dosenumber, feetype);
        dist_service.setPeriod(Duration.seconds(duration));
        dist_service.setOnSucceeded(e -> {
            updateResults(dist_service);
        });
        dist_service.start();
    }

    /**
     * Method for getting the results using pin code. The methods starts a
     * background service for update checking and displays the result whenever a
     * vaccination slot is found. The background service runs indefinitely until
     * stopped by 'Stop' button.
     * 
     * @param pincode     Pin Code
     * @param duration    Refresh interval duration
     * @param age         Age of the person
     * @param vaccinename Preferred vaccine name
     * @param dosenumber  Dose number
     * @param feetype     Fee type (Free / Paid)
     */
    private void scanByPincode(final int pincode, final int duration, final int age, final String vaccinename,
            final int dosenumber, final String feetype) {
        pin_service.setValues(pincode, age, vaccinename, dosenumber, feetype);
        pin_service.setPeriod(Duration.seconds(duration));
        pin_service.setOnSucceeded(e -> {
            updateResults(pin_service);
        });
        pin_service.start();
    }

    private void updateResults(ScheduledService<ArrayList<Center>> s) {
        final ArrayList<Center> availablecenters = s.getValue();
        resultTable.getItems().clear();
        resultTable.getItems().addAll(availablecenters);
        statusLabel.setText("Last Updated: " + LocalTime.now());
        if (availablecenters.size() > 0 && notificationToggle.isSelected()) {
            NotificationService.showInfoNotification("Some vaccination slots found!", "Cowin Status Tracker");
        }
    }
}
