package net.akmobile.youtubeapp.activities;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Locale;
/** * Created by Rahimli Rahim on 05/10/2016. * ragim95@gmail.com * https: */
public class a extends AppCompatActivity {
private String a;
private Locale b;
private static int c, d, e, f= 4, g, h= 6, i;
@Override
protected void onCreate(Bundle savedInstance) {
SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
a= preferences.getString("changeLanguage", "default");
if (a.equals("default")){
a= getResources().getConfiguration().locale.getCountry();
SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
prefs.edit().putString("changeLanguage",a).apply();
}
b= new Locale(a);
Locale.setDefault(b);
Configuration config = new Configuration();
config.locale = b;
getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
super.onCreate(savedInstance);
}
}
