# [Cowin Status Tracker v2](https://github.com/kpranjal2047/Cowin-Status-Tracker-GUI)

This application allows you to view available vaccination slots for a pin code or district. If slots are available it
can show a toast notification. It can also notify about available slots through SMS (optional).

You can also download vaccination certificate using this application.

NEW: Project has been migrated to JavaFX 17 with Gradle 7.2

## Usage

~~This application runs only on Java 8. You can download it
from [here](https://www.oracle.com/java/technologies/downloads/#java8).~~

The application now runs through Gradle and requires JDK17 SDK to run. Make sure you have set up Java correctly before
running the application. You can also download JDK17 from
here- [Windows Installer](https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe)
, [Linux Deb](https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.deb).

### Steps to run

1. Clone the [repository](https://github.com/kpranjal2047/Cowin-Status-Tracker-GUI.git) into your system.
2. Open terminal in the cloned folder and type `.\gradlew run` (for Windows) or `./gradlew run` (for \*nix)

Note: You can verify Java version from your terminal by running `java --version`. It should output something like

```
java 17.0.2 2022-01-18 LTS
Java(TM) SE Runtime Environment (build 17.0.2+8-LTS-86)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.2+8-LTS-86, mixed mode, sharing)
```

## Enabling SMS Notifications (Optional)

1. Create a [Twilio Account](https://www.twilio.com/)
2. Login to your account and get a trial phone number.
3. Get ACCOUNT SID, AUTH TOKEN and PHONE NUMBER from [Twilio Console](https://www.twilio.com/console)
4. Create a new file `secrets.env` in the project directory and paste the info in this format:

```
accountSID = your-account-sid
authToken = your-auth-token
myTwilioNumber = your-twilio-generated-phone-number
destNumber = your-personal-registered-number-with-country-code
```

SMS will be sent to your registered phone number if 'Receive SMS' toggle is ON and slots are available.

## Created By

Kumar Pranjal ([GitHub](https://github.com/kpranjal2047))
