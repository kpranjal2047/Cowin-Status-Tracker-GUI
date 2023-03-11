package cowin.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import cowin.util.SHA256;
import cowin.util.UIControl;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * CertificateDownloader FXML controller class
 *
 * @author Kumar Pranjal
 */
public class CertificateDownloaderController implements Initializable {

  @Setter private Stage stage;
  @FXML private HBox windowHeader;
  @FXML private MFXFontIcon closeIcon;
  @FXML private MFXFontIcon minimizeIcon;
  @FXML private MFXFontIcon alwaysOnTopIcon;
  @FXML private MFXTextField phoneNumberInput;
  @FXML private Label phoneMsgLabel;
  @FXML private MFXButton sendOtpButton;
  @FXML private MFXTextField otpInput;
  @FXML private MFXButton verifyOtpButton;
  @FXML private Label otpMsgLabel;
  @FXML private MFXTextField idInput;
  @FXML private MFXButton downloadButton;
  @FXML private Label idMsgLabel;
  private final Gson defaultGson;
  private final Executor executor;
  private String txnId;
  private String token;
  private double xOffset;
  private double yOffset;
  /** Constructor */
  public CertificateDownloaderController() {
    defaultGson = new Gson();
    executor =
        Executors.newCachedThreadPool(
            runnable ->
                new Thread(runnable) {
                  {
                    setDaemon(true);
                  }
                });
  }

  /** {@inheritDoc} */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> stage.close());
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
    fireSendOtpError(null);
  }

  /** This method is called when Send OTP button is clicked. */
  @FXML
  @SneakyThrows(UnsupportedEncodingException.class)
  private void sendOtpButtonHandler() {
    fireSendOtpError(null);
    final String HOME = "https://cdn-api.co-vin.in/api/v2/auth/public/generateOTP";
    final String phoneNo = phoneNumberInput.getText();
    if (Pattern.matches("[1-9]\\d{9}", phoneNo)) {
      final Map<String, String> json = new HashMap<>();
      json.put("mobile", phoneNo);
      final HttpPost request = new HttpPost(HOME);
      request.setEntity(new StringEntity(defaultGson.toJson(json)));
      request.setHeader(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
      request.setHeader("Content-type", "application/json");
      handleRequestAsync(request, this::sendOtpResponseHandler);
    } else {
      fireSendOtpError("Invalid phone number");
    }
  }

  /** This method is called when Verify OTP button is clicked. */
  @FXML
  @SneakyThrows(UnsupportedEncodingException.class)
  private void verifyOtpButtonHandler() {
    fireVerifyOtpError(null);
    final String HOME = "https://cdn-api.co-vin.in/api/v2/auth/public/confirmOTP";
    final String otp = SHA256.encode(otpInput.getText());
    if (Objects.nonNull(otp) && !otp.isEmpty()) {
      final Map<String, String> json = new HashMap<>();
      json.put("otp", otp);
      json.put("txnId", txnId);
      final HttpPost request = new HttpPost(HOME);
      request.setEntity(new StringEntity(defaultGson.toJson(json)));
      request.setHeader(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
      request.setHeader("Content-type", "application/json");
      handleRequestAsync(request, this::verifyOtpResponseHandler);
    } else {
      fireVerifyOtpError("Please enter OTP!");
    }
  }

  /** This method is called when Download button is clicked. */
  @FXML
  private void downloadButtonHandler() {
    fireDownloadError(null);
    final String HOME =
        "https://cdn-api.co-vin.in/api/v2/registration/certificate/public/download?beneficiary_reference_id=%s";
    final String id = idInput.getText();
    if (Objects.nonNull(id) && !id.isEmpty()) {
      idMsgLabel.setText("Downloading...");
      idMsgLabel.setTextFill(Color.BLACK);
      final HttpGet request = new HttpGet(String.format(HOME, id));
      request.setHeader(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
      request.setHeader("Authorization", "Bearer " + token);
      handleRequestAsync(request, this::downloadResponseHandler);
    } else {
      fireDownloadError("Please enter ID!");
    }
  }

  /** This method is called when phone number input field is clicked. */
  @FXML
  private void phoneInputClickListener() {
    sendOtpButton.setDefaultButton(true);
    verifyOtpButton.setDefaultButton(false);
    downloadButton.setDefaultButton(false);
  }

  /** This method is called when otp input field is clicked. */
  @FXML
  private void otpInputClickListener() {
    sendOtpButton.setDefaultButton(false);
    verifyOtpButton.setDefaultButton(true);
    downloadButton.setDefaultButton(false);
  }

  /** This method is called when booking id input field is clicked. */
  @FXML
  private void idInputClickListener() {
    sendOtpButton.setDefaultButton(false);
    verifyOtpButton.setDefaultButton(false);
    downloadButton.setDefaultButton(true);
  }

  /**
   * Method to send HTTP request and get response asynchronously.
   *
   * @param request The {@link HttpUriRequest} subclass object with request details
   * @param callback Method to execute after successfully obtaining response
   */
  private void handleRequestAsync(
      final HttpUriRequest request, final EventHandler<WorkerStateEvent> callback) {
    final Task<CloseableHttpResponse> getResponseTask =
        new Task<>() {
          @Override
          protected CloseableHttpResponse call() throws Exception {
            //noinspection resource
            return HttpClients.createDefault().execute(request);
          }
        };
    getResponseTask.setOnSucceeded(callback);
    executor.execute(getResponseTask);
  }

  /**
   * Callback method when Send OTP request is sent successfully.
   *
   * @param event {@link WorkerStateEvent} object containing response object
   */
  @SneakyThrows(IOException.class)
  private void sendOtpResponseHandler(@NonNull final WorkerStateEvent event) {
    @SuppressWarnings("unchecked")
    final Worker<CloseableHttpResponse> worker = event.getSource();
    @Cleanup final CloseableHttpResponse response = worker.getValue();
    final int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    switch (status) {
      case HttpStatus.SC_OK -> {
        if (Objects.nonNull(entity)) {
          final String jsonStr = EntityUtils.toString(entity);
          final JsonObject json = defaultGson.fromJson(jsonStr, JsonObject.class);
          txnId = json.getAsJsonPrimitive("txnId").getAsString();
          phoneMsgLabel.setText("OTP sent!");
          phoneMsgLabel.setTextFill(Color.GREEN);
          UIControl.enableNodes(otpInput, verifyOtpButton, otpMsgLabel);
          otpInput.requestFocus();
          otpInputClickListener();
        } else {
          fireSendOtpError("Some Error Occurred!");
        }
      }
      case HttpStatus.SC_BAD_REQUEST -> {
        if (Objects.nonNull(entity) && EntityUtils.toString(entity).equals("OTP Already Sent")) {
          fireSendOtpError("OTP already sent! Try again after sometime.");
        } else {
          fireSendOtpError("Bad Request!");
        }
      }
      case HttpStatus.SC_UNAUTHORIZED -> fireSendOtpError("Unauthorized Access!");
      case HttpStatus.SC_INTERNAL_SERVER_ERROR -> fireSendOtpError("Server Error!");
      default -> fireSendOtpError("Unknown Error!");
    }
  }

  /**
   * Callback method when Verify OTP request is sent successfully.
   *
   * @param event {@link WorkerStateEvent} object containing response object
   */
  @SneakyThrows(IOException.class)
  private void verifyOtpResponseHandler(@NonNull final WorkerStateEvent event) {
    @SuppressWarnings("unchecked")
    final Worker<CloseableHttpResponse> worker = event.getSource();
    @Cleanup final CloseableHttpResponse response = worker.getValue();
    final int status = response.getStatusLine().getStatusCode();
    final HttpEntity entity = response.getEntity();
    switch (status) {
      case HttpStatus.SC_OK -> {
        if (Objects.nonNull(entity)) {
          final String jsonStr = EntityUtils.toString(entity);
          final JsonObject json = defaultGson.fromJson(jsonStr, JsonObject.class);
          token = json.getAsJsonPrimitive("token").getAsString();
          otpMsgLabel.setText("OTP verified!");
          otpMsgLabel.setTextFill(Color.GREEN);
          UIControl.enableNodes(idInput, downloadButton, idMsgLabel);
          final String newOtp = "*".repeat(otpInput.getText().length());
          otpInput.setText(newOtp);
          otpInput.setEditable(false);
          UIControl.disableNodes(verifyOtpButton);
          idInput.requestFocus();
          idInputClickListener();
        } else {
          fireVerifyOtpError("Some Error Occurred!");
        }
      }
      case HttpStatus.SC_BAD_REQUEST -> {
        if (Objects.nonNull(entity)) {
          try {
            final String jsonStr = EntityUtils.toString(entity);
            final JsonObject json = defaultGson.fromJson(jsonStr, JsonObject.class);
            final String errorMsg = json.getAsJsonPrimitive("error").getAsString();
            fireVerifyOtpError(errorMsg);
          } catch (final IllegalArgumentException | JsonSyntaxException e) {
            fireVerifyOtpError("Bad Request!");
          }
        } else {
          fireVerifyOtpError("Bad Request!");
        }
      }
      case HttpStatus.SC_UNAUTHORIZED -> fireVerifyOtpError("Unauthorized Access!");
      case HttpStatus.SC_INTERNAL_SERVER_ERROR -> fireVerifyOtpError("Server Error!");
      default -> fireVerifyOtpError("Unknown Error!");
    }
  }

  /**
   * Callback method when Download Certificate request is sent successfully.
   *
   * @param event {@link WorkerStateEvent} object containing response object
   */
  @SneakyThrows(IOException.class)
  private void downloadResponseHandler(@NonNull final WorkerStateEvent event) {
    @SuppressWarnings("unchecked")
    final Worker<CloseableHttpResponse> worker = event.getSource();
    @Cleanup final CloseableHttpResponse response = worker.getValue();
    final int status = response.getStatusLine().getStatusCode();
    switch (status) {
      case HttpStatus.SC_OK -> {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Vaccination Certificate");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF File", "*.pdf"));
        final File selectedFile = fileChooser.showSaveDialog(stage);
        final HttpEntity entity = response.getEntity();
        if (Objects.nonNull(entity)) {
          if (Objects.nonNull(selectedFile)) {
            final InputStream stream = entity.getContent();
            Files.copy(stream, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            idMsgLabel.setText("Certificate downloaded!");
            idMsgLabel.setTextFill(Color.GREEN);
          } else {
            fireDownloadError("Aborted by user!");
          }
        } else {
          fireDownloadError("Some Error Occurred!");
        }
      }
      case HttpStatus.SC_BAD_REQUEST -> fireDownloadError("Bad Request!");
      case HttpStatus.SC_UNAUTHORIZED -> fireDownloadError("Unauthorized Access!");
      case HttpStatus.SC_INTERNAL_SERVER_ERROR -> fireDownloadError("Server Error!");
      default -> fireDownloadError("Unknown Error!");
    }
  }

  private void fireSendOtpError(final String message) {
    phoneMsgLabel.setText(message);
    phoneMsgLabel.setTextFill(Color.RED);
    UIControl.disableNodes(
        otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
    phoneNumberInput.requestFocus();
  }

  private void fireVerifyOtpError(final String message) {
    otpMsgLabel.setText(message);
    otpMsgLabel.setTextFill(Color.RED);
    UIControl.disableNodes(idInput, downloadButton, idMsgLabel);
    otpInput.requestFocus();
  }

  private void fireDownloadError(final String message) {
    idMsgLabel.setText(message);
    idMsgLabel.setTextFill(Color.RED);
    idInput.requestFocus();
  }
}
