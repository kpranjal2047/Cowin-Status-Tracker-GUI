package cowin.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import cowin.exceptions.SecretsFileNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Class representing SMS notification service.
 *
 * @author Kumar Pranjal
 */
public class SmsNotificationService {

  private final PhoneNumber myTwilioNumber;
  private final PhoneNumber destNumber;

  /**
   * Creates a new {@code SmsNotificationService} from secrets file (secrets.env)
   *
   * @throws SecretsFileNotFoundException If secrets.env file is missing
   */
  public SmsNotificationService() throws SecretsFileNotFoundException {
    Dotenv dotenv;
    try {
      dotenv = Dotenv.configure().filename("secrets.env").load();
    } catch (final Exception e) {
      throw new SecretsFileNotFoundException();
    }
    final String accountSID = dotenv.get("accountSID");
    final String authToken = dotenv.get("authToken");
    myTwilioNumber = new PhoneNumber(dotenv.get("myTwilioNumber"));
    destNumber = new PhoneNumber(dotenv.get("destNumber"));
    Twilio.init(accountSID, authToken);
  }

  /**
   * Method to send SMS to Twilio registered number
   *
   * @param msg Message body
   */
  public void sendSms(final String msg) {
    Message.creator(destNumber, myTwilioNumber, msg).create();
  }
}
