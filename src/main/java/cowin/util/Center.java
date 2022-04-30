package cowin.util;

import lombok.Data;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
@Data
public class Center {

  private final String centerName;
  private final Integer pinCode;
  private final Integer minAge;
  private final Integer maxAge;
  private final String vaccineName;
  private final String date;
  private final Integer dose1Count;
  private final Integer dose2Count;
  private final Integer precautionCount;
  private final String feeType;
}
