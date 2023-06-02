package cowin.constants;

import lombok.Getter;

/**
 * Enum containing all dose number as constants
 *
 * @author Kumar Pranjal
 */
public enum DoseNumber {
  DOSE_1("Dose 1"),
  DOSE_2("Dose 2"),
  PRECAUTION_DOSE("Precaution dose");

  @Getter private final String name;

  DoseNumber(String name) {
    this.name = name;
  }
}
