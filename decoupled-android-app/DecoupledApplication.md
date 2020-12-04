# Android ModelBank Decoupled Application

## Preparation and login page:

1. Pick and install appropriate apk to your android device or use an android emulator to run the application.
2. After launching the application you'll see the Login screen where you have to input your ModelBank OnlineBanking login and password into corresponding fields along with connection url.
   OnlineBanking backend URL hint:

    - **Localhost** - you'll need to input the OnlineBanking IP address Example: <u>**http://192.168.1.100:8090**</u>
    - **Dedicated installation** - just use your Example: <u>**https://onlinebanking.adorsys.de**</u>

3. After that you can press the LOGIN button.

In case of successful connection you will be forwarded to the next page with some notifications waiting for your subscription, or a message saying you are connected please wait to receive your messages.

## Receiving and confirming/rejecting decoupled Sca messages

While there are no messages, and you stay connected you see a screen with "Polling..." notification, and a running circle. As soon as a decoupled Sca operation starts you get a notification with 2 buttons(Accept & Cancel). Clicking "Accept
" button with confirm your sca operation and therefore execute payment/payment cancellation or make issued consent valid. Pressing "Cancel" button with fail the corresponding Sca operation and terminate/cancel the object of this authorization.

## Logout

You can logout any moment using appropriate button in the top-right corner of your screen.

If the sca operation have been issued before the user logs in to this application the message will be delivered as soon as user logs in.

**WARNING:** Any messages received, but not replied will be lost! Re-booting OnlineBanking backend application will reset connections and messages stored for future delivery.
