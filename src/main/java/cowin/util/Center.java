package cowin.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
@Getter
@AllArgsConstructor
public class Center {

    private String centerName;
    private int pinCode;
    private Integer minAge;
    private Integer maxAge;
    private String vaccineName;
    private String date;
    private int dose1Count;
    private int dose2Count;
    private int precautionCount;
    private String feeType;
}
