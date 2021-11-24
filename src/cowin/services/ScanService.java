package cowin.services;

import cowin.exceptions.StatusNotOKException;
import cowin.output.Center;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing periodic scanning service.
 *
 * @author Kumar Pranjal
 */
public class ScanService extends ScheduledService<List<Center>> {

    private final String HOME;
    private final int pin_id;
    private final int age;
    private final String vaccineName;
    private final int doseNumber;
    private final String feeType;

    /**
     * Creates and returns a {@code ScanService} with all necessary parameters
     * initialized.
     *
     * @param scanType    DISTRICT_SCAN or PIN_CODE_SCAN
     * @param pin_id      PIN code or district ID
     * @param age         Age of the person
     * @param vaccineName Required vaccine name
     * @param doseNumber  Required dose number
     * @param feeType     "Free" or "Paid"
     */
    public ScanService(final ScanType scanType, final int pin_id, final int age, final String vaccineName,
                       final int doseNumber, final String feeType) {
        if (scanType == ScanType.PIN_CODE_SCAN) {
            HOME = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%d&date=%s";
        } else {
            HOME = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=%d&date=%s";
        }
        this.pin_id = pin_id;
        this.age = age;
        this.vaccineName = vaccineName;
        this.doseNumber = doseNumber;
        this.feeType = feeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Task<List<Center>> createTask() {
        return new Task<List<Center>>() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected List<Center> call() {
                return scan();
            }
        };
    }

    /**
     * This method searches available vaccination centers and returns list of
     * available vaccination centers ONLY ONCE.
     *
     * @return List of available vaccination centers
     */
    private @NotNull List<Center> scan() {
        final List<Center> out = new ArrayList<>();
        final LocalDate date = LocalDate.now();
        final String curDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        try {
            final URL url = new URL(String.format(HOME, pin_id, curDate));
            final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            final int status = con.getResponseCode();
            if (status == HttpsURLConnection.HTTP_OK) {
                final StringBuilder content = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                }
                final String jsonStr = content.toString();
                final JSONObject json = (JSONObject) new JSONParser().parse(jsonStr);
                final JSONArray centers = (JSONArray) json.get("centers");
                for (final Object c : centers) {
                    final JSONObject center = (JSONObject) c;
                    final String name = (String) center.get("name");
                    final int pinCode = (int) (long) center.get("pincode");
                    final String fee = (String) center.get("fee_type");
                    if (feeType != null && !fee.equalsIgnoreCase(feeType)) {
                        continue;
                    }
                    final JSONArray sessions = (JSONArray) center.get("sessions");
                    for (final Object s : sessions) {
                        final JSONObject session = (JSONObject) s;
                        final int minAge = (int) (long) session.get("min_age_limit");
                        final String vaccine = (String) session.get("vaccine");
                        final int dose1count = (int) (long) session.get("available_capacity_dose1");
                        final int dose2count = (int) (long) session.get("available_capacity_dose2");
                        final String sessionDate = (String) session.get("date");
                        if (minAge <= age
                                && ((doseNumber == 0 && dose1count + dose2count > 0)
                                || (doseNumber == 1 && dose1count > 0) || (doseNumber == 2 && dose2count > 0))
                                && (vaccineName == null || vaccine.equalsIgnoreCase(vaccineName))) {
                            final Center available_center = new Center(minAge, vaccine, sessionDate, dose1count,
                                    dose2count, fee, name, pinCode);
                            out.add(available_center);
                        }
                    }
                }
            } else {
                throw new StatusNotOKException("Status code is not OK");
            }
            con.disconnect();
        } catch (final UnknownHostException e) {
            TrayNotificationService.showErrorNotification("Network Error\nPlease check your internet connection",
                    "Cowin Status Tracker");
        } catch (final StatusNotOKException e) {
            TrayNotificationService.showErrorNotification("Error\nServer did not reply", "Cowin Status Tracker");
        } catch (IOException | ParseException ignored) {
        }
        return out;
    }
}
