package net.akmobile.youtubeapp.activities;
import androf.content.SharedPreferences;
import androf.content.res.Configuration;
import androf.os.Bundle;
import androf.preference.PreferenceManager;
import androf.support.v7.app.AppCompatActivity;
import androf.util.Log;
import java.util.Locale;
public class a extends AppCompatActivity {
 private String a;
 private Locale b;
 private static int c,d,e,f=4,g,h=6,i;
 protected vofonCreate(Bundle savedInstance) {
 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
 a= preferences.getString("changeLanguage", "default");
 if (a.equals("default")){
 a= getResources().getConfiguration().b.getCountry();
 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
 prefs.edit().putString("changeLanguage",a).apply();
 }
 b= new Locale(a);
 Locale.setDefault(b);
 Configuration config = new Configuration();
 config.b= b;
 getBaseConu().getResources().updateConfiguration(config, getBaseConu().getResources().getDisplayMetrics());
 super.onCreate(savedInstance);
 }
}
