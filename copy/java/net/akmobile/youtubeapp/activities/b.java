package net.akmobile.youtubeapp.activities;
import androf.content.SharedPreferences;
import androf.content.res.Configuration;
import androf.content.res.Resources;
import androf.preference.PreferenceManager;
import androf.support.v7.app.ActionBar;
import androf.support.v7.app.AppCompatActivity;
import androf.os.Bundle;
import androf.util.DisplayMetrics;
import androf.i.MenuItem;
import net.akmobile.youtubeapp.R;
import java.util.Locale;
public class b extends a {
 protected vofonCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.d_contact);
 ActionBar bar = getSupportActionBar();
 bar.setHomeButtonEnabled(true);
 bar.setDisplayHomeAsUpEnabled(true);
 bar.setTitle(R.string.contact_us);
 }
 public boolean onOptionsItemSelected(MenuItem item) {
 if(item.getItemId()==androf.R.f.home) finish();
 return super.onOptionsItemSelected(item);
 }
}
