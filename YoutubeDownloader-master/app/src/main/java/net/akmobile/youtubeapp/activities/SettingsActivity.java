package net.akmobile.youtubeapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import net.akmobile.youtubeapp.R;

import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class SettingsActivity extends BaseActivity {

    public static final String OPEN_MUSIC_PLAYER_IF_PLAYING = "open";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new GeneralPreferenceFragment()).commit();
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("OnCreate Settings",prefs.getString("changeLanguage","default"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
                finish();
            return super.onOptionsItemSelected(item);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        private static final int LANGUAGE_CHANGED = 10;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            ListPreference preference = (ListPreference) findPreference("changeLanguage");
            preference.setOnPreferenceChangeListener(this);
        }

        public void setLocale(String lang) {
            SettingsActivity activity = (SettingsActivity) getActivity();
            Locale myLocale =  new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(activity,MainActivity.class);
            refresh.setAction(OPEN_MUSIC_PLAYER_IF_PLAYING);
            activity.setResult(LANGUAGE_CHANGED);
            startActivity(refresh);
            activity.finish();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putString("changeLanguage",newValue.toString()).apply();
            setLocale((String) newValue);
            return false;
        }

    }

}
