package net.akmobile.youtubeapp.services;
import androf.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class x extends FirebaseMessagingService {
 private static final String a= "FCM b;
 public vofonMessageReceived(RemoteMessage remoteMessage) {
 super.onMessageReceived(remoteMessage);
 // TODO: Handle FCM messages here. // If the application is in the foreground handle both data and notification messages here. // Also if you intend on generating your own notifications as a result of a received FCM // message, here is where that should be initiated. Log.d(a, "From: " + remoteMessage.getFrom());
 Log.d(a, "Notification Message Body: " + remoteMessage.getNotification().getBody());
 }
}
