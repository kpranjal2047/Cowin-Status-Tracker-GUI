package cowinjava.output;

/**
 * Class to represent vaccination centers.
 * 
 * @author Kumar Pranjal
 */
public class Center {

    private final long minage;
    private final String vaccinename;
    private final String date;
    private final long dose1count;
    private final long dose2count;
    private final String feetype;
    private final String centername;
    private final long pincode;

    /**
     * Constructor initializing all necessary details.
     * 
     * @param minage      Minimum age
     * @param vaccinename Available vaccine name
     * @param date        Session date
     * @param dose1count  Dose 1 availability
     * @param dose2count  Dose 2 availability
     * @param feetype     Fee type (Free / Paid)
     * @param centername  Center name
     * @param pincode     Center pincode
     */
    public Center(final long minage, final String vaccinename, final String date, final long dose1count,
            final long dose2count, final String feetype, final String centername, final long pincode) {
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

    public String getVaccinename() {
        return vaccinename;
    }

    public String getDate() {
        return date;
    }

    public long getDose1count() {
        return dose1count;
    }

    public long getDose2count() {
        return dose2count;
    }

    public String getFeetype() {
        return feetype;
    }

    public String getCentername() {
        return centername;
    }

    public long getPincode() {
        return pincode;
    }

}
