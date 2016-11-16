package net.akmobile.youtubeapp.services;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
/** * Created by Rahimli Rahim on 23/09/2016. * ragim95@gmail.com * https: */
public class y extends FirebaseInstanceIdService {
private static final String a= "FirebaseIDService";
@Override
public void onTokenRefresh() {
String refreshedToken = FirebaseInstanceId.getInstance().getToken();
Log.d(a, "Refreshed token: " + refreshedToken);
sendRegistrationToServer(refreshedToken);
}
/** * Persist token to third-party servers. * * Modify this method to associate the user's FCM InstanceID token with any server-side account * maintained by your application. * * @param token The new token. */
private void sendRegistrationToServer(String token) {
}
}
