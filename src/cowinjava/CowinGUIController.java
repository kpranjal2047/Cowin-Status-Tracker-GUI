package cowinjava;

import com.jfoenix.controls.*;
import cowinjava.exceptions.InvalidInputException;
import cowinjava.exceptions.SecretsFileNotFoundException;
import cowinjava.output.Center;
import cowinjava.services.ScanService;
import cowinjava.services.ScanType;
import cowinjava.services.SmsNotificationService;
import cowinjava.services.TrayNotificationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

/**
 * GUI FXML Controller class
 *
 * @author Kumar Pranjal
 */
public class CowinGUIController implements Initializable {

    @FXML
    private Label pinCodeLabel;
    @FXML
    private Label districtLabel;
    @FXML
    private JFXToggleButton pinDistToggle;
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
    private JFXToggleButton smsToggle;
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
    private TableColumn<Center, Long> pinCodeColumn;

    private Map<String, Map<String, String>> map;
    private ScanService scanService;
    private SmsNotificationService smsService;

    /**
     * Constructor
     */
    public CowinGUIController() {
        try (InputStream inputStream = getClass().getResourceAsStream("dist_map.json")) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
            @SuppressWarnings("unchecked")
            final Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) new JSONParser().parse(br);
            this.map = map;
        } catch (IOException | ParseException ignored) {
        }
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
        pinCodeLabel.setTextFill(Paint.valueOf("#ee5c5c"));
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
     */
    @FXML
    private void pinDistChangeHandler() {
        if (pinDistToggle.isSelected()) {
            pinCodeLabel.setTextFill(Paint.valueOf("black"));
            districtLabel.setTextFill(Paint.valueOf("#00bca9"));
            pinInput.setVisible(false);
            stateInput.setVisible(true);
            districtInput.setVisible(true);
        } else {
            pinCodeLabel.setTextFill(Paint.valueOf("#ee5c5c"));
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
     * @see #fillDistricts(String)
     */
    @FXML
    private void stateChangeHandler() {
        final String state = stateInput.getValue();
        fillDistricts(state);
    }

    /**
     * This method is called when 'Receive SMS' toggle is switched.
     */
    @FXML
    private void smsChangeHandler() {
        if (smsToggle.isSelected()) {
            try {
                smsService = SmsNotificationService.getSmsService();
            } catch (final SecretsFileNotFoundException e) {
                smsToggle.setSelected(false);
                final Alert a = new Alert(AlertType.ERROR, e.getMessage());
                a.setTitle("Cowin Status Tracker");
                a.showAndWait();
            }
        }
    }

    /**
     * This method enables/disables age input when age checkbox is
     * selected/deselected.
     */
    @FXML
    private void ageCheckBoxHandler() {
        ageInput.setDisable(!ageCheckbox.isSelected());
    }

    /**
     * This method enables/disables vaccine combo box when vaccine checkbox is
     * selected/deselected.
     */
    @FXML
    private void vaccineCheckBoxHandler() {
        vaccineInput.setDisable(!vaccineCheckbox.isSelected());
    }

    /**
     * This method enables/disables dose number toggle when dose checkbox is
     * selected/deselected.
     */
    @FXML
    private void doseCheckBoxHandler() {
        doseInput.setDisable(!doseCheckbox.isSelected());
    }

    /**
     * This method enables/disables fee type toggle when fee checkbox is
     * selected/deselected.
     */
    @FXML
    private void feeCheckBoxHandler() {
        feeInput.setDisable(!feeCheckbox.isSelected());
    }

    /**
     * This method is called when Start button is clicked.
     */
    @FXML
    private void startButtonHandler() {
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

        String vaccineName;
        if (vaccineCheckbox.isSelected()) {
            vaccineName = vaccineInput.getValue();
        } else {
            vaccineName = null;
        }

        int doseNumber;
        if (doseCheckbox.isSelected()) {
            doseNumber = doseInput.isSelected() ? 2 : 1;
        } else {
            doseNumber = 0;
        }

        String feeType;
        if (feeCheckbox.isSelected()) {
            feeType = feeInput.isSelected() ? "Paid" : "Free";
        } else {
            feeType = null;
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
            refreshInput.getValueFactory().setValue(60);
            return;
        }

        int pin_id;
        if (pinDistToggle.isSelected()) {
            final String state = stateInput.getValue();
            final String district = districtInput.getValue();
            pin_id = (int) (long) ((JSONObject) map.get(state)).get(district);
        } else {
            try {
                pin_id = Integer.parseInt(pinInput.getText());
                if (pin_id < 100000 || pin_id > 999999) {
                    throw new InvalidInputException("Entered pin code is not 6-digit");
                }
            } catch (NumberFormatException | InvalidInputException e) {
                final Alert a = new Alert(AlertType.ERROR, "Invalid pin code - " + e.getMessage());
                a.setTitle("Cowin Status Tracker");
                a.showAndWait();
                return;
            }
        }

        startButton.setText("Running...");
        startButton.setDisable(true);
        stopButton.setDisable(false);
        pinDistToggle.setDisable(true);
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

        if (pinDistToggle.isSelected()) {
            scanService = new ScanService(ScanType.DISTRICT_SCAN, pin_id, age, vaccineName, doseNumber, feeType);
        } else {
            scanService = new ScanService(ScanType.PIN_CODE_SCAN, pin_id, age, vaccineName, doseNumber, feeType);
        }
        scanService.setPeriod(Duration.seconds(duration));
        scanService.setOnSucceeded(e -> updateResults(scanService));
        scanService.start();
    }

    /**
     * This method is called when Stop button is clicked.
     */
    @FXML
    private void stopButtonHandler() {
        scanService.cancel();
        scanService.reset();
        startButton.setText("Start");
        startButton.setDisable(false);
        stopButton.setDisable(true);
        pinDistToggle.setDisable(false);
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
        final List<String> list = new ArrayList<>(map.keySet());
        Collections.sort(list);
        stateInput.setItems(FXCollections.observableList(list));
        stateInput.setValue(list.get(0));
        fillDistricts(list.get(0));
    }

    /**
     * Method for updating district names when a state is selected.
     *
     * @param state State name
     * @see #stateChangeHandler()
     */
    private void fillDistricts(final String state) {
        final List<String> list = new ArrayList<>(map.get(state).keySet());
        Collections.sort(list);
        districtInput.setItems(FXCollections.observableList(list));
        districtInput.setValue(list.get(0));
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
     * Method to initialize results table during initialization.
     */
    private void initializeTable() {
        resultTable.setPlaceholder(new Label("No vaccination centers found!"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("minAge"));
        vaccineColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dose1Column.setCellValueFactory(new PropertyValueFactory<>("dose1count"));
        dose2Column.setCellValueFactory(new PropertyValueFactory<>("dose2count"));
        feeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("feeType"));
        centerNameColumn.setCellValueFactory(new PropertyValueFactory<>("centerName"));
        pinCodeColumn.setCellValueFactory(new PropertyValueFactory<>("pinCode"));
    }

    /**
     * Method to print scan results in result table and notify user by sending
     * notification and SMS.
     *
     * @param service {@code ScanService} to get results from.
     */
    private void updateResults(final @NotNull ScanService service) {
        final List<Center> availableCenters = service.getValue();
        resultTable.getItems().clear();
        resultTable.getItems().addAll(availableCenters);
        statusLabel.setText("Last Updated: " + LocalTime.now());
        if (availableCenters.size() > 0) {
            if (notificationToggle.isSelected()) {
                TrayNotificationService.showInfoNotification("Some vaccination slots found!", "Cowin Status Tracker");
            }
            if (smsToggle.isSelected()) {
                smsService.sendSms("Some vaccination slots found! Please check Cowin Status Tracker.");
            }
        }
    }
}
