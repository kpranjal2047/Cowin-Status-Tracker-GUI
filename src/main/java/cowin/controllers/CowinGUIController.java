package cowin.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cowin.alerts.ErrorDialog;
import cowin.alerts.TrayNotification;
import cowin.constants.DoseNumber;
import cowin.constants.ScanType;
import cowin.constants.VaccineName;
import cowin.exceptions.InvalidInputException;
import cowin.models.Center;
import cowin.services.ScanService;
import cowin.util.ResourceLoader;
import cowin.util.UIControl;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * GUI FXML controller class
 *
 * @author Kumar Pranjal
 */
public class CowinGUIController implements Initializable {

  private static JsonObject stateDistrictMap;

  static {
    try (final InputStreamReader inputStreamReader =
            new InputStreamReader(ResourceLoader.loadResourceAsStream("data/District_ID.json"));
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
      stateDistrictMap = new Gson().fromJson(bufferedReader, JsonObject.class);
    } catch (final IOException ignored) {
    }
  }

  @Setter private Stage stage;
  @FXML private HBox windowHeader;
  @FXML private MFXFontIcon closeIcon;
  @FXML private MFXFontIcon minimizeIcon;
  @FXML private MFXFontIcon alwaysOnTopIcon;
  @FXML private Label pinCodeLabel;
  @FXML private Label districtLabel;
  @FXML private MFXToggleButton pinDistToggle;
  @FXML private MFXTextField pinInput;
  @FXML private MFXFilterComboBox<String> stateInput;
  @FXML private MFXFilterComboBox<String> districtInput;
  @FXML private Spinner<Integer> refreshInput;
  @FXML private MFXToggleButton notificationToggle;
  @FXML private MFXCheckbox ageCheckbox;
  @FXML private MFXSlider ageInput;
  @FXML private MFXCheckbox vaccineCheckbox;
  @FXML private MFXFilterComboBox<String> vaccineInput;
  @FXML private MFXCheckbox doseCheckbox;
  @FXML private MFXComboBox<String> doseInput;
  @FXML private MFXCheckbox feeCheckbox;
  @FXML private MFXToggleButton feeInput;
  @FXML private MFXButton startButton;
  @FXML private MFXButton stopButton;
  @FXML private Label statusLabel;
  @FXML private MFXTableView<Center> resultTable;
  private ScanService scanService;
  private double xOffset;
  private double yOffset;

  /** {@inheritDoc} */
  @Override
  public void initialize(final URL url, final ResourceBundle rb) {
    closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> System.exit(0));
    //noinspection DuplicatedCode
    minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> stage.setIconified(true));
    alwaysOnTopIcon.addEventHandler(
        MouseEvent.MOUSE_CLICKED,
        event -> {
          boolean newVal = !stage.isAlwaysOnTop();
          alwaysOnTopIcon.pseudoClassStateChanged(
              PseudoClass.getPseudoClass("always-on-top"), newVal);
          stage.setAlwaysOnTop(newVal);
        });
    final Tooltip closeTooltip = new Tooltip("Close");
    closeTooltip.setShowDelay(Duration.seconds(0.5));
    Tooltip.install(closeIcon, closeTooltip);
    final Tooltip minimizeTooltip = new Tooltip("Minimize");
    closeTooltip.setShowDelay(Duration.seconds(0.5));
    Tooltip.install(minimizeIcon, minimizeTooltip);
    final Tooltip alwaysOnTopTooltip = new Tooltip("Always on Top");
    closeTooltip.setShowDelay(Duration.seconds(0.5));
    Tooltip.install(alwaysOnTopIcon, alwaysOnTopTooltip);
    windowHeader.setOnMousePressed(
        event -> {
          xOffset = stage.getX() - event.getScreenX();
          yOffset = stage.getY() - event.getScreenY();
        });
    windowHeader.setOnMouseDragged(
        event -> {
          stage.setX(event.getScreenX() + xOffset);
          stage.setY(event.getScreenY() + yOffset);
        });

    fillStates();
    fillVaccineNames();
    fillDoseNumbers();
    initializeTable();
    refreshInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 300, 60, 1));
    pinCodeLabel.setTextFill(Color.valueOf("#ee5c5c"));
    stateInput.setVisible(false);
    districtInput.setVisible(false);
    UIControl.disableNodes(stopButton, ageInput, vaccineInput, doseInput, feeInput);
  }

  /** This method modifies GUI when "search by pin/district" toggle is switched. */
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
   * This method is called when new state is selected. The method fills district names in district
   * select combo box.
   *
   * @see #fillDistricts(String)
   */
  @FXML
  private void stateChangeHandler() {
    final String state = stateInput.getValue();
    fillDistricts(state);
  }
  /** This method enables/disables age input when age checkbox is selected/deselected. */
  @FXML
  private void ageCheckBoxHandler() {
    ageInput.setDisable(!ageCheckbox.isSelected());
  }

  /**
   * This method enables/disables vaccine combo box when vaccine checkbox is selected/deselected.
   */
  @FXML
  private void vaccineCheckBoxHandler() {
    vaccineInput.setDisable(!vaccineCheckbox.isSelected());
  }

  /** This method enables/disables dose number toggle when dose checkbox is selected/deselected. */
  @FXML
  private void doseCheckBoxHandler() {
    doseInput.setDisable(!doseCheckbox.isSelected());
  }

  /** This method enables/disables fee type toggle when fee checkbox is selected/deselected. */
  @FXML
  private void feeCheckBoxHandler() {
    feeInput.setDisable(!feeCheckbox.isSelected());
  }

  /** This method is called when Start button is clicked. */
  @FXML
  private void startButtonHandler() {
    final int pinOrDistId;
    if (pinDistToggle.isSelected()) {
      final String state = stateInput.getValue();
      if (Objects.isNull(state)) {
        final ErrorDialog alert = new ErrorDialog(stage, "Please select a state.");
        alert.showDialog();
        return;
      }
      final String district = districtInput.getValue();
      pinOrDistId = stateDistrictMap.getAsJsonObject(state).getAsJsonPrimitive(district).getAsInt();
    } else {
      final String pin = pinInput.getText();
      if (Pattern.matches("[1-9]\\d{5}", pin)) {
        pinOrDistId = Integer.parseInt(pin);
      } else {
        final String errMsg =
            pin.isEmpty() ? "Please enter 6-digit PIN code" : "Invalid PIN Code - " + pin;
        final ErrorDialog alert = new ErrorDialog(stage, errMsg);
        alert.showDialog();
        return;
      }
    }

    int age = Integer.MAX_VALUE;
    if (ageCheckbox.isSelected()) {
      try {
        age = (int) ageInput.getValue();
        if (age < 0 || age > 100) {
          throw new InvalidInputException("Age out of range\nExpected range [0 - 100]");
        }
      } catch (final NumberFormatException | InvalidInputException e) {
        final ErrorDialog alert = new ErrorDialog(stage, "Invalid age value - " + e.getMessage());
        alert.showDialog();
        ageInput.setValue(18);
        return;
      }
    }

    String vaccineName = null;
    if (vaccineCheckbox.isSelected()) {
      vaccineName = vaccineInput.getValue();
    }

    String doseNumber = null;
    if (doseCheckbox.isSelected()) {
      doseNumber = doseInput.getValue();
    }

    String feeType = null;
    if (feeCheckbox.isSelected()) {
      feeType = feeInput.isSelected() ? "Paid" : "Free";
    }

    final int duration;
    try {
      duration = Integer.parseInt(refreshInput.getEditor().getText());
      if (duration < 3 || duration > 300) {
        throw new InvalidInputException("Duration out of range\nExpected range [3 - 300]");
      }
    } catch (final NumberFormatException | InvalidInputException e) {
      final ErrorDialog alert =
          new ErrorDialog(stage, "Invalid refresh duration value - " + e.getMessage());
      alert.showDialog();
      refreshInput.getValueFactory().setValue(60);
      return;
    }

    startButton.setText("Running...");
    UIControl.enableNodes(stopButton);
    UIControl.disableNodes(
        startButton,
        pinDistToggle,
        stateInput,
        districtInput,
        pinInput,
        refreshInput,
        ageCheckbox,
        ageInput,
        vaccineCheckbox,
        vaccineInput,
        doseCheckbox,
        doseInput,
        feeCheckbox,
        feeInput);

    scanService =
        new ScanService(
            pinDistToggle.isSelected() ? ScanType.DISTRICT_SCAN : ScanType.PIN_CODE_SCAN,
            pinOrDistId,
            age,
            vaccineName,
            doseNumber,
            feeType);
    scanService.setPeriod(Duration.seconds(duration));
    scanService.setOnSucceeded(this::updateResults);
    scanService.start();
  }

  /** This method is called when Stop button is clicked. */
  @FXML
  private void stopButtonHandler() {
    scanService.cancel();
    scanService.reset();
    startButton.setText("Start");
    UIControl.enableNodes(
        startButton,
        pinDistToggle,
        stateInput,
        districtInput,
        pinInput,
        refreshInput,
        ageCheckbox,
        vaccineCheckbox,
        doseCheckbox,
        feeCheckbox);
    UIControl.disableNodes(stopButton);
    ageInput.setDisable(!ageCheckbox.isSelected());
    vaccineInput.setDisable(!vaccineCheckbox.isSelected());
    doseInput.setDisable(!doseCheckbox.isSelected());
    feeInput.setDisable(!feeCheckbox.isSelected());
  }

  /** This method is called when Download Certificate button is clicked. */
  @FXML
  @SneakyThrows(IOException.class)
  private void downloadButtonHandler() {
    final FXMLLoader loader =
        new FXMLLoader(ResourceLoader.loadResource("fxml/CertificateDownloader.fxml"));
    final Parent root = loader.load();
    final CertificateDownloaderController controller = loader.getController();
    final Stage stage = new Stage();
    final Scene scene = new Scene(root);
    scene.setFill(Color.TRANSPARENT);
    stage.setScene(scene);
    stage.setTitle("Download Certificate");
    stage
        .getIcons()
        .add(
            new Image(
                ResourceLoader.loadResourceAsStream("images/Icon_Logo.png"), 0, 0, true, true));
    stage.setResizable(false);
    stage.initStyle(StageStyle.TRANSPARENT);
    controller.setStage(stage);
    stage.show();
  }

  /**
   * Method for filling state names during initialization.
   *
   * @see #fillDistricts(String)
   */
  private void fillStates() {
    final List<String> list = new ArrayList<>(stateDistrictMap.keySet());
    Collections.sort(list);
    stateInput.setItems(FXCollections.observableList(list));
  }

  /**
   * Method for updating district names when a state is selected.
   *
   * @param state State name
   * @see #stateChangeHandler()
   */
  private void fillDistricts(final String state) {
    final List<String> list = new ArrayList<>(stateDistrictMap.getAsJsonObject(state).keySet());
    Collections.sort(list);
    districtInput.setItems(FXCollections.observableList(list));
    districtInput.setValue(list.get(0));
  }

  /** Method for filling vaccine names during initialization. */
  private void fillVaccineNames() {
    final List<String> list =
        Arrays.stream(VaccineName.values()).map(VaccineName::getName).toList();
    vaccineInput.setItems(FXCollections.observableList(list));
    vaccineInput.setValue(list.get(0));
  }

  /** Method for filling dose numbers during initialization. */
  private void fillDoseNumbers() {
    final List<String> list = Arrays.stream(DoseNumber.values()).map(DoseNumber::getName).toList();
    doseInput.setItems(FXCollections.observableList(list));
    doseInput.setValue(list.get(0));
  }

  /** Method to initialize results table during initialization. */
  private void initializeTable() {
    final MFXTableColumn<Center> centerNameColumn =
        createColumn("Center Name", Center::getCenterName);
    final MFXTableColumn<Center> pinCodeColumn = createColumn("PIN Code", Center::getPinCode);
    final MFXTableColumn<Center> minAgeColumn = createColumn("Min Age", Center::getMinAge);
    final MFXTableColumn<Center> maxAgeColumn = createColumn("Max Age", Center::getMaxAge);
    final MFXTableColumn<Center> vaccineColumn =
        createColumn("Vaccine Name", Center::getVaccineName);
    final MFXTableColumn<Center> dateColumn = createColumn("Date", Center::getDate);
    final MFXTableColumn<Center> dose1Column = createColumn("Dose 1 Count", Center::getDose1Count);
    final MFXTableColumn<Center> dose2Column = createColumn("Dose 2 Count", Center::getDose2Count);
    final MFXTableColumn<Center> precautionDoseColumn =
        createColumn("Precaution Dose", Center::getPrecautionDoseCount);
    final MFXTableColumn<Center> feeTypeColumn = createColumn("Fee Type", Center::getFeeType);
    final MFXTableColumn<Center> fee = createColumn("Fee", Center::getFee);
    centerNameColumn.setPrefWidth(200);
    resultTable
        .getTableColumns()
        .addAll(
            List.of(
                centerNameColumn,
                pinCodeColumn,
                minAgeColumn,
                maxAgeColumn,
                vaccineColumn,
                dateColumn,
                dose1Column,
                dose2Column,
                precautionDoseColumn,
                feeTypeColumn,
                fee));
  }

  private <R extends Comparable<R>> @NonNull MFXTableColumn<Center> createColumn(
      final String name, final Function<Center, R> getter) {
    final MFXTableColumn<Center> tableColumn =
        new MFXTableColumn<>(name, true, Comparator.comparing(getter));
    tableColumn.setRowCellFactory(center -> new MFXTableRowCell<>(getter));
    return tableColumn;
  }

  /**
   * Method to print scan results in result table and notify user by sending notification and SMS.
   *
   * @param evt {@link WorkerStateEvent} to get results from.
   */
  private void updateResults(@NonNull final WorkerStateEvent evt) {
    @SuppressWarnings("unchecked")
    final Worker<List<Center>> worker = evt.getSource();
    final List<Center> availableCenters = worker.getValue();
    resultTable.getItems().clear();
    resultTable.getItems().addAll(availableCenters);
    statusLabel.setText("Last Updated: " + LocalTime.now());
    final int numCenters = availableCenters.size();
    if (numCenters > 0 && notificationToggle.isSelected()) {
      TrayNotification.showInfoNotification(
          numCenters + " vaccination center(s) found!", "Cowin Status Tracker");
    }
  }
}
