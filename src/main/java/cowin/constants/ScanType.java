package cowin.constants;

import cowin.services.ScanService;

/**
 * ScanType enum which can be used for defining scan type while creating a new {@link ScanService}.
 */
public enum ScanType {
  /** Used when scanning needs to be done using PIN code */
  PIN_CODE_SCAN,
  /** Used when scanning needs to be done using district name */
  DISTRICT_SCAN
}
