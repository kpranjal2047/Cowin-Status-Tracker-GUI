package cowin.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import cowin.exceptions.InvalidResponseException;
import cowin.util.SHA256;
import cowin.util.TrayNotification;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * CertificateDownloader FXML controller class
 *
 * @author Kumar Pranjal
 */
public class CertificateDownloaderController implements Initializable {

    @FXML
    private JFXTextField phoneNumberInput;
    @FXML
    private Label phoneMsgLabel;
    @FXML
    private JFXButton sendOtpButton;
    @FXML
    private JFXTextField otpInput;
    @FXML
    private JFXButton verifyOtpButton;
    @FXML
    private Label otpMsgLabel;
    @FXML
    private JFXTextField idInput;
    @FXML
    private JFXButton downloadButton;
    @FXML
    private Label idMsgLabel;

    private final Executor executor;
    private String txnId;
    private String token;
    private Stage stage;

    /**
     * Constructor
     */
    public CertificateDownloaderController() {
        executor = Executors.newCachedThreadPool(runnable -> {
            final Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
        sendOtpButton.setDefaultButton(true);
    }

    /**
     * This method is called when Send OTP button is clicked.
     */
    @FXML
    private void sendOtpButtonHandler() {
        final String HOME = "https://cdn-api.co-vin.in/api/v2/auth/public/generateOTP";
        final String phoneNo = phoneNumberInput.getText();
        phoneMsgLabel.setText(null);
        if (Pattern.matches("[1-9][0-9]{9}", phoneNo)) {
            try {
                final Map<String, String> json = new HashMap<>();
                json.put("mobile", phoneNo);
                final HttpPost request = new HttpPost(HOME);
                request.setEntity(new StringEntity(new Gson().toJson(json)));
                request.setHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
                request.setHeader("Content-type", "application/json");
                handleRequestAsync(request, this::sendOtpResponseHandler);
            } catch (final UnsupportedEncodingException e) {
                disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
            }
        } else {
            phoneMsgLabel.setText("Invalid phone number");
            phoneMsgLabel.setTextFill(Color.RED);
            disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
        }
    }

    /**
     * This method is called when Verify OTP button is clicked.
     */
    @FXML
    private void verifyOtpButtonHandler() {
        final String HOME = "https://cdn-api.co-vin.in/api/v2/auth/public/confirmOTP";
        final String otp = SHA256.encode(otpInput.getText());
        otpMsgLabel.setText(null);
        if (otp != null && !otp.isEmpty()) {
            try {
                final Map<String, String> json = new HashMap<>();
                json.put("otp", otp);
                json.put("txnId", txnId);
                final HttpPost request = new HttpPost(HOME);
                request.setEntity(new StringEntity(new Gson().toJson(json)));
                request.setHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
                request.setHeader("Content-type", "application/json");
                handleRequestAsync(request, this::verifyOtpResponseHandler);
            } catch (final UnsupportedEncodingException e) {
                disableNodes(idInput, downloadButton, idMsgLabel);
            }
        } else {
            otpMsgLabel.setText("Please enter OTP!");
            otpMsgLabel.setTextFill(Color.RED);
            disableNodes(idInput, downloadButton, idMsgLabel);
        }
    }

    /**
     * This method is called when Download button is clicked.
     */
    @FXML
    private void downloadButtonHandler() {
        final String HOME = "https://cdn-api.co-vin.in/api/v2/registration/certificate/public/download?beneficiary_reference_id=%s";
        final String id = idInput.getText();
        idMsgLabel.setText(null);
        if (id != null && !id.isEmpty()) {
            idMsgLabel.setText("Downloading...");
            idMsgLabel.setTextFill(Color.BLACK);
            final HttpGet request = new HttpGet(String.format(HOME, id));
            request.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
            request.setHeader("Authorization", "Bearer " + token);
            handleRequestAsync(request, this::downloadResponseHandler);
        } else {
            idMsgLabel.setText("Please enter ID!");
            idMsgLabel.setTextFill(Color.RED);
        }
    }

    /**
     * This method is called when phone number input field is clicked.
     */
    @FXML
    private void phoneInputClickListener() {
        sendOtpButton.setDefaultButton(true);
        verifyOtpButton.setDefaultButton(false);
        downloadButton.setDefaultButton(false);
    }

    /**
     * This method is called when otp input field is clicked.
     */
    @FXML
    private void otpInputClickListener() {
        sendOtpButton.setDefaultButton(false);
        verifyOtpButton.setDefaultButton(true);
        downloadButton.setDefaultButton(false);
    }

    /**
     * This method is called when booking id input field is clicked.
     */
    @FXML
    private void idInputClickListener() {
        sendOtpButton.setDefaultButton(false);
        verifyOtpButton.setDefaultButton(false);
        downloadButton.setDefaultButton(true);
    }

    /**
     * Method to send HTTP request and get response asynchronously.
     *
     * @param request  The {@link HttpRequestBase} subclass object with request
     *                 details
     * @param callback Method to execute after successfully obtaining response
     */
    private void handleRequestAsync(final HttpRequestBase request, final EventHandler<WorkerStateEvent> callback) {
        final Task<CloseableHttpResponse> getResponseTask = new Task<CloseableHttpResponse>() {
            @Override
            protected CloseableHttpResponse call() throws Exception {
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
    private void sendOtpResponseHandler(@NotNull final WorkerStateEvent event) {
        @SuppressWarnings("unchecked")
        final Worker<CloseableHttpResponse> worker = event.getSource();
        try (CloseableHttpResponse response = worker.getValue()) {
            final int status = response.getStatusLine().getStatusCode();
            final HttpEntity entity = response.getEntity();
            if (status == 200) {
                if (entity != null) {
                    final String jsonStr = EntityUtils.toString(entity);
                    final JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
                    txnId = json.getAsJsonPrimitive("txnId").getAsString();
                    phoneMsgLabel.setText("OTP sent!");
                    phoneMsgLabel.setTextFill(Color.GREEN);
                    enableNodes(otpInput, verifyOtpButton, otpMsgLabel);
                    disableNodes(idInput, downloadButton, idMsgLabel);
                } else {
                    throw new InvalidResponseException();
                }
            } else if (status == 400) {
                if (entity != null && EntityUtils.toString(entity).equals("OTP Already Sent")) {
                    phoneMsgLabel.setText("OTP already sent! Try again after sometime.");
                } else {
                    phoneMsgLabel.setText("Bad Request!");
                }
                phoneMsgLabel.setTextFill(Color.RED);
                disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
            } else if (status == 401) {
                phoneMsgLabel.setText("Unauthorized Access!");
                phoneMsgLabel.setTextFill(Color.RED);
                disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
            } else if (status == 500) {
                phoneMsgLabel.setText("Server Error!");
                phoneMsgLabel.setTextFill(Color.RED);
                disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
            } else {
                phoneMsgLabel.setText("Unknown Error!");
                phoneMsgLabel.setTextFill(Color.RED);
                disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
            }
        } catch (final InvalidResponseException e) {
            TrayNotification.showErrorNotification("Error: Empty Response", "Cowin Status Tracker");
            disableNodes(otpInput, verifyOtpButton, otpMsgLabel, idInput, downloadButton, idMsgLabel);
        } catch (final IOException ignored) {

        }
    }

    /**
     * Callback method when Verify OTP request is sent successfully.
     *
     * @param event {@link WorkerStateEvent} object containing response object
     */
    private void verifyOtpResponseHandler(@NotNull final WorkerStateEvent event) {
        @SuppressWarnings("unchecked")
        final Worker<CloseableHttpResponse> worker = event.getSource();
        try (CloseableHttpResponse response = worker.getValue()) {
            final int status = response.getStatusLine().getStatusCode();
            final HttpEntity entity = response.getEntity();
            if (status == 200) {
                if (entity != null) {
                    final String jsonStr = EntityUtils.toString(entity);
                    final JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
                    token = json.getAsJsonPrimitive("token").getAsString();
                    otpMsgLabel.setText("OTP verified!");
                    otpMsgLabel.setTextFill(Color.GREEN);
                    disableNodes(verifyOtpButton);
                    enableNodes(idInput, downloadButton, idMsgLabel);
                    otpInput.setEditable(false);
                } else {
                    throw new InvalidResponseException();
                }
            } else if (status == 400) {
                if (entity != null) {
                    try {
                        final String jsonStr = EntityUtils.toString(entity);
                        final JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
                        final String errorMsg = json.getAsJsonPrimitive("error").getAsString();
                        otpMsgLabel.setText(errorMsg);
                    } catch (IllegalArgumentException | JsonSyntaxException e) {
                        otpMsgLabel.setText("Bad Request!");
                    }
                } else {
                    otpMsgLabel.setText("Bad Request!");
                }
                otpMsgLabel.setTextFill(Color.RED);
                disableNodes(idInput, downloadButton, idMsgLabel);
            } else if (status == 401) {
                otpMsgLabel.setText("Unauthorized Access!");
                otpMsgLabel.setTextFill(Color.RED);
                disableNodes(idInput, downloadButton, idMsgLabel);
            } else if (status == 500) {
                otpMsgLabel.setText("Server Error!");
                otpMsgLabel.setTextFill(Color.RED);
                disableNodes(idInput, downloadButton, idMsgLabel);
            } else {
                otpMsgLabel.setText("Unknown Error!");
                otpMsgLabel.setTextFill(Color.RED);
                disableNodes(idInput, downloadButton, idMsgLabel);
            }
        } catch (final InvalidResponseException e) {
            TrayNotification.showErrorNotification("Error: Empty Response", "Cowin Status Tracker");
            disableNodes(idInput, downloadButton, idMsgLabel);
        } catch (final IOException ignored) {

        }
    }

    /**
     * Callback method when Download Certificate request is sent successfully.
     *
     * @param event {@link WorkerStateEvent} object containing response object
     */
    private void downloadResponseHandler(@NotNull final WorkerStateEvent event) {
        @SuppressWarnings("unchecked")
        final Worker<CloseableHttpResponse> worker = event.getSource();
        try (CloseableHttpResponse response = worker.getValue()) {
            final int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Vaccination Certificate");
                fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF File", "*.pdf"));
                final File selectedFile = fileChooser.showSaveDialog(stage);
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    if (selectedFile != null) {
                        final InputStream stream = entity.getContent();
                        Files.copy(stream, selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        idMsgLabel.setText("Certificate downloaded!");
                        idMsgLabel.setTextFill(Color.GREEN);
                    } else {
                        idMsgLabel.setText("Aborted by user!");
                        idMsgLabel.setTextFill(Color.RED);
                    }
                } else {
                    throw new InvalidResponseException();
                }
            } else if (status == 400) {
                idMsgLabel.setText("Bad Request!");
                idMsgLabel.setTextFill(Color.RED);
            } else if (status == 401) {
                idMsgLabel.setText("Unauthorized Access!");
                idMsgLabel.setTextFill(Color.RED);
            } else if (status == 500) {
                idMsgLabel.setText("Server Error!");
                idMsgLabel.setTextFill(Color.RED);
            } else {
                idMsgLabel.setText("Unknown Error!");
                idMsgLabel.setTextFill(Color.RED);
            }
        } catch (final InvalidResponseException e) {
            TrayNotification.showErrorNotification("Error: Empty Response", "Cowin Status Tracker");
        } catch (final IOException ignored) {

        }
    }

    /**
     * Utility method to disable nodes
     *
     * @param nodes Nodes to disable
     */
    private void disableNodes(final Node @NotNull... nodes) {
        for (final Node node : nodes) {
            if (node instanceof JFXTextField) {
                ((JFXTextField) node).setText(null);
            } else if (node instanceof Label) {
                ((Label) node).setText(null);
            }
            node.setDisable(true);
        }
    }

    /**
     * Utility method to enable nodes
     *
     * @param nodes Nodes to enable
     */
    private void enableNodes(final Node @NotNull... nodes) {
        for (final Node node : nodes) {
            node.setDisable(false);
        }
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }
}
