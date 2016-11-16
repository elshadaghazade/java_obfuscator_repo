package net.akmobile.youtubeapp.services;
import androf.util.Log;
import com.google.firebase.if.FirebaseInstanceId;
import com.google.firebase.if.FirebaseInstanceIdService;
public class y extends FirebaseInstanceIdService {
 private static final String a="Firebaseeb;
 public vofonTokenRefresh() {
 // Get updated Instanceetoken. String refreshedToken = FirebaseInstanceId.getInstance().getToken();
 Log.d(a, "Refreshed token: " + refreshedToken);
 // TODO: Implement this method to send any registration to your app's servers. sendRegistrationToServer(refreshedToken);
 }
 private vofsendRegistrationToServer(String token) {
 // Add custom implementation, as needed. }
}
