package cowin.util;

import cowin.CowinMain;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceLoader {

  public URL loadResource(final String name) {
    return Objects.requireNonNull(CowinMain.class.getResource(name));
  }

  public InputStream loadResourceAsStream(final String name) {
    return Objects.requireNonNull(CowinMain.class.getResourceAsStream(name));
  }
}
