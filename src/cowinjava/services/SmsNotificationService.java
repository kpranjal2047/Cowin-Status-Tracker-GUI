package cowinjava.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import cowinjava.exceptions.SecretsFileNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Class representing SMS notification service.
 * 
 * @author Kumar Pranjal
 */
public class SmsNotificationService {

    private final String accountSID;
    private final String authToken;
    private final PhoneNumber myTwilioNumber;
    private final PhoneNumber destNumber;
    private static SmsNotificationService service = null;

    /**
     * Private constructor to prevent object creation externally.
     * 
     * @throws SecretsFileNotFoundException
     */
    private SmsNotificationService() throws SecretsFileNotFoundException {
        Dotenv dotenv;
        try {
            dotenv = Dotenv.load();
        } catch (final Exception e) {
            throw new SecretsFileNotFoundException("Secrets file (.env) not found!");
        }
        accountSID = dotenv.get("accountSID");
        authToken = dotenv.get("authToken");
        myTwilioNumber = new PhoneNumber(dotenv.get("myTwilioNumber"));
        destNumber = new PhoneNumber(dotenv.get("destNumber"));
        Twilio.init(accountSID, authToken);
    }

    /**
     * This methods returns any existing sms service or else creates and returns a
     * new one.
     * 
     * @return {@code SmsNotificationService} object
     * @throws SecretsFileNotFoundException When secrets file cannot be read
     */
    public static SmsNotificationService getSmsService() throws SecretsFileNotFoundException {
        if (service == null) {
            service = new SmsNotificationService();
        }
        return service;
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
