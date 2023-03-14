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
  CORBEVAX("CORBEVAX"),
  COVOVAX("COVOVAX"),
  SPUTNIK_V("SPUTNIK V"),
  ZYCOV_D("ZYCOV D"),
  INCOVACC("iNCOVACC");

  @Getter private final String value;

  VaccineName(String value) {
    this.value = value;
  }
}
