package cowin.util;

/**
 * Record to represent vaccination centers.
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
 *
 * @author Kumar Pranjal
 */
public record Center(String centerName, int pinCode, Integer minAge, Integer maxAge,
        String vaccineName, String date, int dose1Count, int dose2Count,
        int precautionCount, String feeType) {

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
