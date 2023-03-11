package cowin.util;

import cowin.CowinMain;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import lombok.experimental.UtilityClass;

/**
 * Utility class containing methods for loading resource files
 *
 * @author Kumar Pranjal
 */
@UtilityClass
public class ResourceLoader {

  /**
   * Loads resource from file path and returns its URL
   *
   * @param name filename with path
   * @return loaded resource URL
   */
  public URL loadResource(final String name) {
    return Objects.requireNonNull(CowinMain.class.getResource(name));
  }

  /**
   * Loads resource from file path and returns it as stream
   *
   * @param name filename with path
   * @return loaded resource as stream
   */
  public InputStream loadResourceAsStream(final String name) {
    return Objects.requireNonNull(CowinMain.class.getResourceAsStream(name));
  }
}
