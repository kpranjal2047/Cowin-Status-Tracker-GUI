package cowinjava.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cowinjava.output.Center;
import cowinjava.exceptions.DataCommunicationException;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

public class DistrictUpdateService extends ScheduledService<ArrayList<Center>> {

    private long dist_id;
    private int age;
    private String vaccinename;
    private int dosenumber;
    private String feetype;
    private static final String home = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=%d&date=%s";

    public long getDist_id() {
        return dist_id;
    }

    public void setDist_id(long dist_id) {
        this.dist_id = dist_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getVaccinename() {
        return vaccinename;
    }

    public void setVaccinename(String vaccinename) {
        this.vaccinename = vaccinename;
    }

    public int getDosenumber() {
        return dosenumber;
    }

    public void setDosenumber(int dosenumber) {
        this.dosenumber = dosenumber;
    }

    public String getFeetype() {
        return feetype;
    }

    public void setFeetype(String feetype) {
        this.feetype = feetype;
    }

    @Override
    protected Task<ArrayList<Center>> createTask() {
        return new Task<ArrayList<Center>>() {
            @Override
            protected ArrayList<Center> call() {
                ArrayList<Center> out = new ArrayList<>();
                LocalDate date = LocalDate.now();
                String curdate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                try {
                    URL url = new URL(String.format(home, dist_id, curdate));
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/5.0");
                    con.setRequestMethod("GET");
                    int status = con.getResponseCode();
                    if (status == HttpsURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer content = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
                        String jsonstr = content.toString();
                        JSONObject json = (JSONObject) new JSONParser().parse(jsonstr);
                        JSONArray centers = (JSONArray) json.get("centers");
                        for (Object c : centers) {
                            JSONObject center = (JSONObject) c;
                            String name = (String) center.get("name");
                            long pincode = (long) center.get("pincode");
                            String fee = (String) center.get("fee_type");
                            if (feetype != null && !fee.equalsIgnoreCase(feetype)) {
                                continue;
                            }
                            JSONArray sessions = (JSONArray) center.get("sessions");
                            for (Object s : sessions) {
                                JSONObject session = (JSONObject) s;
                                long minage = (long) session.get("min_age_limit");
                                String vaccine = (String) session.get("vaccine");
                                long dose1count = (long) session.get("available_capacity_dose1");
                                long dose2count = (long) session.get("available_capacity_dose2");
                                String sessiondate = (String) session.get("date");
                                if (minage <= age
                                        && ((dose1count > 0 && (dosenumber == 1 || dosenumber == 0))
                                                || (dose2count > 0 && (dosenumber == 2 || dosenumber == 0)))
                                        && (vaccinename == null || vaccine.equalsIgnoreCase(vaccinename))) {
                                    Center available_center = new Center(minage, vaccine, sessiondate, dose1count,
                                            dose2count, fee, name, pincode);
                                    out.add(available_center);
                                }
                            }
                        }
                    } else {
                        throw new DataCommunicationException();
                    }
                    con.disconnect();
                } catch (MalformedURLException e) {
                    System.err.println(1);
                } catch (DataCommunicationException e) {
                    System.err.println(2);
                } catch (IOException e) {
                    System.err.println(3);
                } catch (ParseException e) {
                    System.err.println(4);
                }
                return out;
            }
        };
    }
}
