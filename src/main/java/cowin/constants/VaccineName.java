package cowin.constants;

import lombok.Getter;

/**
 * Enum containing all vaccine names as constants
 *
 * @author Kumar Pranjal
 */
public enum VaccineName {
  COVISHIELD("COVISHIELD"),
  COVAXIN("COVAXIN"),
  SPUTNIK_V("SPUTNIK V"),
  CORBEVAX("CORBEVAX"),
  COVOVAX("COVOVAX"),
  ZYCOV_D("ZYCOV D");

  @Getter private final String value;

  VaccineName(String value) {
    this.value = value;
  }
}
