package net.akmobile.youtubeapp.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Rahimli Rahim on 05/10/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */

public class BaseActivity extends AppCompatActivity {
    private String lang;
    private Locale locale;
    private static int aaa, bbb, ccc,
            ddd = 4, eee, fff = 6,
            ggg
            ;

    @Override
    protected void onCreate(Bundle savedInstance) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        lang = preferences.getString("changeLanguage", "default");
        if (lang.equals("default")){
            lang = getResources().getConfiguration().locale.getCountry();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("changeLanguage",lang).apply();
        }
        locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstance);
    }
}
