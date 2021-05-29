package cowinjava.data;

public class Center {

    private long minage;
    private String vaccinename;
    private String date;
    private long dose1count;
    private long dose2count;
    private String feetype;
    private String centername;
    private long pincode;

    public Center(long minage, String vaccinename, String date, long dose1count, long dose2count, String feetype,
            String centername, long pincode) {
        this.minage = minage;
        this.vaccinename = vaccinename;
        this.date = date;
        this.dose1count = dose1count;
        this.dose2count = dose2count;
        this.feetype = feetype;
        this.centername = centername;
        this.pincode = pincode;
    }

    public long getMinage() {
        return minage;
    }

    public void setMinage(long minage) {
        this.minage = minage;
    }

    public String getVaccinename() {
        return vaccinename;
    }

    public void setVaccinename(String vaccinename) {
        this.vaccinename = vaccinename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDose1count() {
        return dose1count;
    }

    public void setDose1count(long dose1count) {
        this.dose1count = dose1count;
    }

    public long getDose2count() {
        return dose2count;
    }

    public void setDose2count(long dose2count) {
        this.dose2count = dose2count;
    }

    public String getFeetype() {
        return feetype;
    }

    public void setFeetype(String feetype) {
        this.feetype = feetype;
    }

    public String getCentername() {
        return centername;
    }

    public void setCentername(String centername) {
        this.centername = centername;
    }

    public long getPincode() {
        return pincode;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

}
