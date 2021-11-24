# [Cowin Status Tracker](https://github.com/kpranjal2047/Cowin-Status-Tracker-GUI)

This application allows you to view available vaccination slots for a pin code or district. If slots are available it
can show a toast notification. It can also notify about available slots through SMS (optional).

## Usage

This application runs only on Java 8. You can download it
from [here](https://www.oracle.com/java/technologies/downloads/#java8).

Once you have Java 8 installed, you can directly run the .jar file by double-click, or you can use the following command
on terminal

`java -jar <filename>.jar`

Note: You can verify Java version from your terminal by running `java -version`. It should output something like

```
java version "1.8.x_xxx"
...
...
```

If you have Java 8 installed and see some other version on terminal then you will need to run

`{java_8_install_location}/bin/java -jar <filename>.jar`

Note: If you have multiple versions of Java installed in your system then double-click method may not work. In that case
you will need to run the application using terminal.

## Enabling SMS Notifications (Optional)

1. Create a [Twilio Account](https://www.twilio.com/)
2. Login to your account and get a trial phone number.
3. Get ACCOUNT SID, AUTH TOKEN and PHONE NUMBER from [Twilio Console](https://www.twilio.com/console)
4. Create a new file `secrets.env` in the same directory as .jar file and paste the info in this format:

```
accountSID = your-account-sid
authToken = your-auth-token
myTwilioNumber = your-twilio-generated-phone-number
destNumber = your-personal-registered-number-with-country-code
```

SMS will be sent to your registered phone number if 'Receive SMS' toggle is ON and slots are available.

## Created By

Kumar Pranjal ([GitHub](https://github.com/kpranjal2047))