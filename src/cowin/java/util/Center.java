package cowin.java.util;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
public class Center {

    private final Integer minAge;
    private final Integer maxAge;
    private final String vaccineName;
    private final String date;
    private final int dose1Count;
    private final int dose2Count;
    private final int precautionCount;
    private final String feeType;
    private final String centerName;
    private final int pinCode;

    /**
     * Creates and returns a new {@code Center} with all necessary parameters
     * initialized.
     *
     * @param minAge          Minimum age
     * @param maxAge          Maximum age
     * @param vaccineName     Available vaccine name
     * @param date            Session date
     * @param dose1Count      Dose 1 availability
     * @param dose2Count      Dose 2 availability
     * @param precautionCount Precaution dose availability
     * @param feeType         Fee type (Free / Paid)
     * @param centerName      Center name
     * @param pinCode         Center pin code
     */
    public Center(final String centerName, final int pinCode, final Integer minAge, final Integer maxAge,
            final String vaccineName, final String date, final int dose1Count,
            final int dose2Count, final int precautionCount, final String feeType) {
        this.centerName = centerName;
        this.pinCode = pinCode;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.vaccineName = vaccineName;
        this.date = date;
        this.dose1Count = dose1Count;
        this.dose2Count = dose2Count;
        this.precautionCount = precautionCount;
        this.feeType = feeType;
    }

    @SuppressWarnings("unused")
    public String getCenterName() {
        return centerName;
    }

    @SuppressWarnings("unused")
    public int getPinCode() {
        return pinCode;
    }

    @SuppressWarnings("unused")
    public Integer getMinAge() {
        return minAge;
    }

    @SuppressWarnings("unused")
    public Integer getMaxAge() {
        return maxAge;
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
    public int getDose1Count() {
        return dose1Count;
    }

    @SuppressWarnings("unused")
    public int getDose2Count() {
        return dose2Count;
    }

    @SuppressWarnings("unused")
    public int getPrecautionCount() {
        return precautionCount;
    }

    @SuppressWarnings("unused")
    public String getFeeType() {
        return feeType;
    }
}
