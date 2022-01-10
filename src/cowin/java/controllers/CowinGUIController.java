package cowin.java.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jfoenix.controls.*;
import cowin.java.exceptions.InvalidInputException;
import cowin.java.exceptions.SecretsFileNotFoundException;
import cowin.java.services.ScanService;
import cowin.java.services.ScanService.ScanType;
import cowin.java.services.SmsNotificationService;
import cowin.java.services.TrayNotificationService;
import cowin.java.util.Center;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

/**
 * GUI FXML controller class
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
    private JFXComboBox<String> doseInput;
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
    private TableColumn<Center, String> centerNameColumn;
    @FXML
    private TableColumn<Center, Integer> pinCodeColumn;
    @FXML
    private TableColumn<Center, Integer> minAgeColumn;
    @FXML
    private TableColumn<Center, Integer> maxAgeColumn;
    @FXML
    private TableColumn<Center, String> vaccineColumn;
    @FXML
    private TableColumn<Center, String> dateColumn;
    @FXML
    private TableColumn<Center, Integer> dose1Column;
    @FXML
    private TableColumn<Center, Integer> dose2Column;
    @FXML
    private TableColumn<Center, Integer> precautionDoseColumn;
    @FXML
    private TableColumn<Center, String> feeTypeColumn;

    private JsonObject map;
    private ScanService scanService;
    private SmsNotificationService smsService;

    /**
     * Constructor
     */
    public CowinGUIController() {
        try (final BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/District_ID.json"))))) {
            map = new Gson().fromJson(br, JsonObject.class);
        } catch (final IOException ignored) {
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        fillStates();
        fillVaccineNames();
        fillDoseNumbers();
        initializeTable();
        refreshInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 300, 60, 1));
        ageInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 18, 1));
        pinCodeLabel.setTextFill(Color.valueOf("#ee5c5c"));
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
            pinCodeLabel.setTextFill(Color.BLACK);
            districtLabel.setTextFill(Color.valueOf("#00bca9"));
            pinInput.setVisible(false);
            stateInput.setVisible(true);
            districtInput.setVisible(true);
        } else {
            pinCodeLabel.setTextFill(Color.valueOf("#ee5c5c"));
            districtLabel.setTextFill(Color.BLACK);
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
            age = -1;
        }

        String vaccineName;
        if (vaccineCheckbox.isSelected()) {
            vaccineName = vaccineInput.getValue();
        } else {
            vaccineName = null;
        }

        String doseNumber;
        if (doseCheckbox.isSelected()) {
            doseNumber = doseInput.getValue();
        } else {
            doseNumber = null;
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
                throw new InvalidInputException("Duration out of range\nExpected range [3 - 300]");
            }
        } catch (NumberFormatException | InvalidInputException e) {
            final Alert a = new Alert(AlertType.ERROR, "Invalid refresh duration value - " + e.getMessage());
            a.setTitle("Cowin Status Tracker");
            a.showAndWait();
            refreshInput.getValueFactory().setValue(60);
            return;
        }

        int pinOrDistId;
        if (pinDistToggle.isSelected()) {
            final String state = stateInput.getValue();
            final String district = districtInput.getValue();
            pinOrDistId = map.getAsJsonObject(state).getAsJsonPrimitive(district).getAsInt();
        } else {
            try {
                try {
                    pinOrDistId = Integer.parseInt(pinInput.getText());
                } catch (final NumberFormatException e) {
                    final String[] errWords = e.getMessage().split(" ");
                    throw new InvalidInputException("Invalid pin code - " + errWords[errWords.length - 1]);
                }
                if (pinOrDistId < 100000 || pinOrDistId > 999999) {
                    throw new InvalidInputException("Entered pin code is not 6-digit");
                }
            } catch (final InvalidInputException e) {
                final Alert a = new Alert(AlertType.ERROR, e.getMessage());
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
            scanService = new ScanService(ScanType.DISTRICT_SCAN, pinOrDistId, age, vaccineName, doseNumber, feeType);
        } else {
            scanService = new ScanService(ScanType.PIN_CODE_SCAN, pinOrDistId, age, vaccineName, doseNumber, feeType);
        }
        scanService.setPeriod(Duration.seconds(duration));
        scanService.setOnSucceeded(this::updateResults);
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
     * This method is called when Download Certificate button is clicked.
     * 
     * @throws IOException Should be ignored. It is never thrown.
     */
    @FXML
    private void downloadButtonHandler() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/CertificateDownloader.fxml")));
        final Parent root = loader.load();
        final CertificateDownloaderController controller = loader.getController();
        final Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Download Certificate");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Icon.png"))));
        stage.setResizable(false);
        controller.setStage(stage);
        stage.show();
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
        final List<String> list = new ArrayList<>(map.getAsJsonObject(state).keySet());
        Collections.sort(list);
        districtInput.setItems(FXCollections.observableList(list));
        districtInput.setValue(list.get(0));
    }

    /**
     * Method for filling vaccine names during initialization.
     */
    private void fillVaccineNames() {
        final List<String> list = Arrays.asList("Covaxin", "Covishield", "Sputnik V");
        vaccineInput.setItems(FXCollections.observableList(list));
        vaccineInput.setValue(list.get(0));
    }

    /**
     * Method for filling dose numbers during initialization.
     */
    private void fillDoseNumbers() {
        final List<String> list = Arrays.asList("Dose 1", "Dose 2", "Precaution dose");
        doseInput.setItems(FXCollections.observableList(list));
        doseInput.setValue(list.get(0));
    }

    /**
     * Method to initialize results table during initialization.
     */
    private void initializeTable() {
        resultTable.setPlaceholder(new Label("No vaccination centers found!"));
        centerNameColumn.setCellValueFactory(new PropertyValueFactory<>("centerName"));
        pinCodeColumn.setCellValueFactory(new PropertyValueFactory<>("pinCode"));
        minAgeColumn.setCellValueFactory(new PropertyValueFactory<>("minAge"));
        maxAgeColumn.setCellValueFactory(new PropertyValueFactory<>("maxAge"));
        vaccineColumn.setCellValueFactory(new PropertyValueFactory<>("vaccineName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dose1Column.setCellValueFactory(new PropertyValueFactory<>("dose1Count"));
        dose2Column.setCellValueFactory(new PropertyValueFactory<>("dose2Count"));
        precautionDoseColumn.setCellValueFactory(new PropertyValueFactory<>("precautionCount"));
        feeTypeColumn.setCellValueFactory(new PropertyValueFactory<>("feeType"));
    }

    /**
     * Method to print scan results in result table and notify user by sending
     * notification and SMS.
     *
     * @param evt {@link WorkerStateEvent} to get results from.
     */
    private void updateResults(final @NotNull WorkerStateEvent evt) {
        @SuppressWarnings("unchecked")
        final Worker<List<Center>> worker = evt.getSource();
        final List<Center> availableCenters = worker.getValue();
        resultTable.getItems().clear();
        resultTable.getItems().addAll(availableCenters);
        statusLabel.setText("Last Updated: " + LocalTime.now());
        final int numCenters = availableCenters.size();
        if (numCenters > 0) {
            if (notificationToggle.isSelected()) {
                TrayNotificationService.showInfoNotification(numCenters + " vaccination center(s) found!",
                        "Cowin Status Tracker");
            }
            if (smsToggle.isSelected()) {
                smsService.sendSms(numCenters + " vaccination center(s) found! Please check Cowin Status Tracker.");
            }
        }
    }
}
