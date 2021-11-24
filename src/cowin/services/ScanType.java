package cowin.services;

/**
 * ScanType enum which can be used for defining scan type while creating a new
 * {@code ScanService}.
 *
 * @author Kumar Pranjal
 */
public enum ScanType {
    /**
     * Used when scanning needs to be done using PIN code
     */
    PIN_CODE_SCAN,
    /**
     * Used when scanning needs to be done using district name
     */
    DISTRICT_SCAN
}
