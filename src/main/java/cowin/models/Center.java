package cowin.models;

import lombok.Value;

/**
 * Class to represent vaccination centers.
 *
 * @author Kumar Pranjal
 */
@SuppressWarnings("ClassCanBeRecord")
@Value
public class Center {

  String centerName;
  int pinCode;
  int minAge;
  int maxAge;
  String vaccineName;
  String date;
  int dose1Count;
  int dose2Count;
  int precautionDoseCount;
  String feeType;
}
