package net.akmobile.youtubeapp.services;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/** * Created by Rahimli Rahim on 22/09/2016. * ragim95@gmail.com * https: */
public class x extends FirebaseMessagingService {
private static final String a= "FCM b;
@Override
public void onMessageReceived(RemoteMessage remoteMessage) {
super.onMessageReceived(remoteMessage);
Log.d(a, "From: " + remoteMessage.getFrom());
Log.d(a, "Notification Message Body: " + remoteMessage.getNotification().getBody());
}
}
