package cowin.output;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
public class Center {

    private final int minAge;
    private final String vaccineName;
    private final String date;
    private final int dose1count;
    private final int dose2count;
    private final String feeType;
    private final String centerName;
    private final int pinCode;

    /**
     * Creates and returns a new {@code Center} with all necessary parameters
     * initialized.
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
    public Center(final int minAge, final String vaccineName, final String date, final int dose1count,
                  final int dose2count, final String feeType, final String centerName, final int pinCode) {
        this.minAge = minAge;
        this.vaccineName = vaccineName;
        this.date = date;
        this.dose1count = dose1count;
        this.dose2count = dose2count;
        this.feeType = feeType;
        this.centerName = centerName;
        this.pinCode = pinCode;
    }

    @SuppressWarnings("unused")
    public int getMinAge() {
        return minAge;
    }

    @SuppressWarnings("unused")
    public String getVaccineName() {
        return vaccineName;
    }

    @SuppressWarnings("unused")
    public String getDate() {
        return date;
    }

    @SuppressWarnings("unused")
    public int getDose1count() {
        return dose1count;
    }

    @SuppressWarnings("unused")
    public int getDose2count() {
        return dose2count;
    }

    @SuppressWarnings("unused")
    public String getFeeType() {
        return feeType;
    }

    @SuppressWarnings("unused")
    public String getCenterName() {
        return centerName;
    }

    @SuppressWarnings("unused")
    public int getPinCode() {
        return pinCode;
    }
}
