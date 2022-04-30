package cowin.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class containing SHA-256 encoder method.
 *
 * @author Kumar Pranjal
 */
@UtilityClass
public class SHA256 {

  private MessageDigest messageDigest;

  static {
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (final NoSuchAlgorithmException ignored) {
    }
  }

  /**
   * Static method to encode input string into SHA-256 string
   *
   * @param input String to encode
   * @return SHA-256 encoded string
   */
  public String encode(final String input) {
    if (Objects.isNull(input)) {
      return null;
    }
    return bytesToHex(messageDigest.digest(input.getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * Internal method to convert byte array to string
   *
   * @param hash Byte array
   * @return The converted string
   */
  private @NonNull String bytesToHex(final byte @NonNull [] hash) {
    final StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (final byte b : hash) {
      final String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
