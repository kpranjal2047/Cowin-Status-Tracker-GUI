package cowinjava.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cowinjava.exceptions.StatusNotOKException;
import cowinjava.output.Center;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 * Background update service which uses pin code to find vaccination centers.
 * 
 * @author Kumar Pranjal
 */
public class PincodeUpdateService extends ScheduledService<ArrayList<Center>> {

    private int pin_code;
    private int age;
    private String vaccinename;
    private int dosenumber;
    private String feetype;
    private static final String HOME = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=%d&date=%s";
    private static PincodeUpdateService service = null;

    /**
     * Private constructor to prevent multiple instance creation.
     */
    private PincodeUpdateService() {
    }

    /**
     * This methods returns any existing update service or else creates and returns
     * a new one.
     * 
     * @return {@code PincodeUpdateService} object
     */
    public static PincodeUpdateService getPincodeUpdateService() {
        if (service == null) {
            service = new PincodeUpdateService();
        }
        return service;
    }

    public void setValues(final int pincode, final int age, final String vaccinename, final int dosenumber,
            final String feetype) {
        this.pin_code = pincode;
        this.age = age;
        this.vaccinename = vaccinename;
        this.dosenumber = dosenumber;
        this.feetype = feetype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Task<ArrayList<Center>> createTask() {
        return new Task<ArrayList<Center>>() {
            /**
             * {@inheritDoc}
             */
            @Override
            protected ArrayList<Center> call() {
                final ArrayList<Center> out = new ArrayList<>();
                final LocalDate date = LocalDate.now();
                final String curdate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                try {
                    final URL url = new URL(String.format(HOME, pin_code, curdate));
                    final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    final int status = con.getResponseCode();
                    if (status == HttpsURLConnection.HTTP_OK) {
                        final StringBuffer content;
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                            String inputLine;
                            content = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                content.append(inputLine);
                            }
                        }
                        final String jsonstr = content.toString();
                        final JSONObject json = (JSONObject) new JSONParser().parse(jsonstr);
                        final JSONArray centers = (JSONArray) json.get("centers");
                        for (final Object c : centers) {
                            final JSONObject center = (JSONObject) c;
                            final String name = (String) center.get("name");
                            final long pincode = (long) center.get("pincode");
                            final String fee = (String) center.get("fee_type");
                            if (feetype != null && !fee.equalsIgnoreCase(feetype)) {
                                continue;
                            }
                            final JSONArray sessions = (JSONArray) center.get("sessions");
                            for (final Object s : sessions) {
                                final JSONObject session = (JSONObject) s;
                                final long minage = (long) session.get("min_age_limit");
                                final String vaccine = (String) session.get("vaccine");
                                final long dose1count = (long) session.get("available_capacity_dose1");
                                final long dose2count = (long) session.get("available_capacity_dose2");
                                final String sessiondate = (String) session.get("date");
                                if (minage <= age && ((dosenumber == 0 && dose1count + dose2count > 0)
                                        || (dosenumber == 1 && dose1count > 0) || (dosenumber == 2 && dose2count > 0))
                                        && (vaccinename == null || vaccine.equalsIgnoreCase(vaccinename))) {
                                    final Center available_center = new Center(minage, vaccine, sessiondate, dose1count,
                                            dose2count, fee, name, pincode);
                                    out.add(available_center);
                                }
                            }
                        }
                    } else {
                        throw new StatusNotOKException("Status code is not OK");
                    }
                    con.disconnect();
                } catch (UnknownHostException e) {
                    NotificationService.showErrorNotification("Network Error\nPlease check your internet connection",
                            "Cowin Status Tracker");
                } catch (StatusNotOKException e) {
                    NotificationService.showErrorNotification("Error\nServer did not reply", "Cowin Status Tracker");
                } catch (IOException | ParseException e) {
                }
                return out;
            }
        };
    }
}
