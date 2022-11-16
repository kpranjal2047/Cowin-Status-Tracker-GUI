package cowin.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cowin.alerts.TrayNotification;
import cowin.constants.DoseNumber;
import cowin.constants.VaccineName;
import cowin.models.Center;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Class representing periodic scanning service.
 *
 * @author Kumar Pranjal
 */
@AllArgsConstructor
public class ScanService extends ScheduledService<List<Center>> {

  private final ScanType scanType;
  private final int pinOrDistId;
  private final int age;
  private final String vaccineName;
  private final String doseNumber;
  private final String feeType;

  /** {@inheritDoc} */
  @Override
  protected Task<List<Center>> createTask() {
    return new Task<>() {
      @Override
      protected List<Center> call() {
        return getScanResults();
      }
    };
  }

  /**
   * This method searches available vaccination centers and returns list of available vaccination
   * centers ONLY ONCE.
   *
   * @return List of available vaccination centers
   */
  @SneakyThrows(IOException.class)
  private @NonNull List<Center> getScanResults() {
    final String home =
        scanType == ScanType.PIN_CODE_SCAN
            ? "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%d&date=%s"
            : "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=%d&date=%s";
    final List<Center> out = new ArrayList<>();
    final LocalDate date = LocalDate.now();
    final String curDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    final HttpGet request = new HttpGet(String.format(home, pinOrDistId, curDate));
    request.setHeader(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
    @Cleanup final CloseableHttpClient client = HttpClients.createDefault();
    @Cleanup final CloseableHttpResponse response = client.execute(request);
    final int status = response.getStatusLine().getStatusCode();
    if (status == HttpStatus.SC_OK) {
      final HttpEntity entity = response.getEntity();
      if (Objects.nonNull(entity)) {
        final String jsonStr = EntityUtils.toString(entity);
        final JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
        final JsonArray centers = json.getAsJsonArray("centers");
        for (final JsonElement c : centers) {
          final JsonObject center = c.getAsJsonObject();
          final String name = center.getAsJsonPrimitive("name").getAsString();
          final int pinCode = center.getAsJsonPrimitive("pincode").getAsInt();
          final String fee = center.getAsJsonPrimitive("fee_type").getAsString();
          if (Objects.nonNull(feeType) && !feeType.equals(fee)) {
            continue;
          }
          final JsonArray sessions = center.getAsJsonArray("sessions");
          for (final JsonElement s : sessions) {
            final JsonObject session = s.getAsJsonObject();
            final String sessionDate = session.getAsJsonPrimitive("date").getAsString();
            final String vaccine = session.getAsJsonPrimitive("vaccine").getAsString();
            if (Objects.nonNull(vaccineName) && !vaccineName.equals(vaccine)) {
              continue;
            }
            final int minAge = session.getAsJsonPrimitive("min_age_limit").getAsInt();
            if (age < minAge) {
              continue;
            }
            final int maxAge;
            if (vaccine.equals(VaccineName.CORBEVAX)
                || vaccine.equals(VaccineName.COVOVAX)
                || vaccine.equals(VaccineName.ZYCOV_D)) {
              maxAge = 14;
            } else {
              maxAge = 0;
            }
            final int totalCount = session.getAsJsonPrimitive("available_capacity").getAsInt();
            if (totalCount == 0) {
              continue;
            }
            final int dose1Count =
                session.getAsJsonPrimitive("available_capacity_dose1").getAsInt();
            final int dose2Count =
                session.getAsJsonPrimitive("available_capacity_dose2").getAsInt();
            final int precautionDoseCount = totalCount - dose1Count - dose2Count;
            if (Objects.nonNull(doseNumber)
                && ((doseNumber.equals(DoseNumber.DOSE_1) && dose1Count == 0)
                    || (doseNumber.equals(DoseNumber.DOSE_2) && dose2Count == 0)
                    || (doseNumber.equals(DoseNumber.PRECAUTION_DOSE)
                        && precautionDoseCount == 0))) {
              continue;
            }
            out.add(
                new Center(
                    name,
                    pinCode,
                    minAge,
                    maxAge,
                    vaccine,
                    sessionDate,
                    dose1Count,
                    dose2Count,
                    precautionDoseCount,
                    fee));
          }
        }
      } else {
        TrayNotification.showErrorNotification("Some Error Occurred!", "Cowin Status Tracker");
      }
    } else {
      TrayNotification.showErrorNotification("Some Error Occurred!", "Cowin Status Tracker");
    }
    return out;
  }

  /**
   * ScanType enum which can be used for defining scan type while creating a new {@link
   * ScanService}.
   */
  public enum ScanType {
    /** Used when scanning needs to be done using PIN code */
    PIN_CODE_SCAN,
    /** Used when scanning needs to be done using district name */
    DISTRICT_SCAN
  }
}
