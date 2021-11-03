package cowinjava.output;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
public class Center {

    private final long minAge;
    private final String vaccineName;
    private final String date;
    private final long dose1count;
    private final long dose2count;
    private final String feeType;
    private final String centerName;
    private final long pinCode;

    /**
     * Constructor initializing all necessary details.
     *
     * @param minAge      Minimum age
     * @param vaccineName Available vaccine name
     * @param date        Session date
     * @param dose1count  Dose 1 availability
     * @param dose2count  Dose 2 availability
     * @param feeType     Fee type (Free / Paid)
     * @param centerName  Center name
     * @param pinCode     Center pin code
     */
    public Center(final long minAge, final String vaccineName, final String date, final long dose1count,
            final long dose2count, final String feeType, final String centerName, final long pinCode) {
        this.minAge = minAge;
        this.vaccineName = vaccineName;
        this.date = date;
        this.dose1count = dose1count;
        this.dose2count = dose2count;
        this.feeType = feeType;
        this.centerName = centerName;
        this.pinCode = pinCode;
    }

    public long getMinAge() {
        return minAge;
    }

    public String getVaccineName() {
        return vaccineName;
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

    public String getFeeType() {
        return feeType;
    }

    public String getCenterName() {
        return centerName;
    }

    public long getPinCode() {
        return pinCode;
    }
}
