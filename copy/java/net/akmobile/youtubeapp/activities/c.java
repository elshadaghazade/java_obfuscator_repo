package net.akmobile.youtubeapp.activities;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.adapters.FragmentsPagerAdapter;
import net.akmobile.youtubeapp.database.SQLDatabaseAdapter;
import net.akmobile.youtubeapp.database.UpdateRecords;
import net.akmobile.youtubeapp.interfaces.OnListItemsClickListener;
import net.akmobile.youtubeapp.media.MediaPlayerController;
import net.akmobile.youtubeapp.media.MusicController;
import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.network.DownloadHelper;
import net.akmobile.youtubeapp.utils.AlbumUtils;
import net.akmobile.youtubeapp.utils.DonutProgress;
import net.akmobile.youtubeapp.utils.FileManager;
import net.akmobile.youtubeapp.models.VideoStream;
import net.akmobile.youtubeapp.services.MusicService;
import net.akmobile.youtubeapp.utils.Converters;
import java.util.ArrayList;
/** * Activity containing two fragments: Search and Downloaded files lists * You can see in Manifest file, that activity has Search intent action, * so it receives an intent with extra value 'query' whenever the user clicks the search button */
public class c extends a implements OnListItemsClickListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener {
private static final int a= 1;
private static final int b= 10;
public static final String c= "StatusChanged";
public static final String d= "UpdateSearchFragment";
private TabLayout e;
private MenuItem f, g, h;
private DrawerLayout i;
public MusicService j;
private Intent k;
private boolean l= false;
private MusicController m;
private MediaPlayerController n;
private boolean o= false, p= false;
private SeekBar q;
private Handler mHandler = new Handler();
private BroadcastReceiver r;
private TextView s;
private LinearLayout t;
public BottomSheetBehavior u;
private boolean v= false;
private DownloadedFile w;
private ServiceConnection musicConnection = new ServiceConnection() {
@Override
public void onServiceConnected(ComponentName name, IBinder service) {
MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
j= binder.getService();
j.setList(getMusicFiles());
l= true;
}
@Override
public void onServiceDisconnected(ComponentName name) {
l= false;
}
}
;
/** * Register music service when activity is started */
@Override
protected void onStart() {
super.onStart();
if (k== null) {
k= new Intent(this, MusicService.class);
bindService(k, musicConnection, Context.BIND_AUTO_CREATE);
startService(k);
}
n= new MediaPlayerController(j, l);
}
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
setMusicController();
setUpUI();
r= new BroadcastReceiver() {
@Override
public void onReceive(Context context, Intent intent) {
updateProgressBar();
updateThumbnail();
togglePlayButton();
}
}
;
UpdateRecords task = new UpdateRecords(this);
task.execute();
}
@Override
protected void onPause() {
o= true;
unregisterReceiver(r);
super.onPause();
}
@Override
protected void onResume() {
super.onResume();
if (o) {
setMusicController();
o= false;
}
registerReceiver(r, new IntentFilter(MusicService.UPDATE_UI));
}
@Override
protected void onRestart() {
super.onRestart();
if (j!= null && j.isPlaybackNotEmpty()) {
togglePlayButton();
updateThumbnail();
}
}
@Override
protected void onStop() {
super.onStop();
m.hide();
}
@Override
protected void onDestroy() {
k= new Intent(this, MusicService.class);
unbindService(musicConnection);
stopService(k);
j= null;
mHandler.removeCallbacks(updateSeekBarRunnable);
super.onDestroy();
}
/** * Method which setups the menu in toolbar (SearchView and little search icon) * and allows to perform search in this activity from toolbar * * @param menu is the Application's toolbar menu, see options_menu.xml * @return true */
@Override
public boolean onCreateOptionsMenu(Menu menu) {
getMenuInflater().inflate(R.menu.options_menu, menu);
SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
f= menu.findItem(R.id.search);
g= menu.findItem(R.id.action_remove);
h= menu.findItem(R.id.action_share);
SearchView searchView = (SearchView) MenuItemCompat.getActionView(f);
searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
return true;
}
@Override
public boolean onOptionsItemSelected(MenuItem item) {
switch (item.getItemId()) {
case R.id.action_remove: if (w!= null) {
deleteDownloadedFile(w);
}
break;
case R.id.action_share: break;
}
return super.onOptionsItemSelected(item);
}
/** * The method which is called whenever user searches * anything from toolbar and clicks go on keyboard, * We should make sure that user in the search tab, that's why we select this tab * * @param intent contains the user's query which we retrieve and send a broadcast to the fragment * that user have typed something, make search */
@Override
protected void onNewIntent(Intent intent) {
super.onNewIntent(intent);
if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linear_main_activity);
mainLayout.requestFocus();
TabLayout.Tab search = e.getTabAt(0);
search.select();
String query = intent.getStringExtra(SearchManager.QUERY);
intent = new Intent(d);
intent.putExtra("query", query);
sendBroadcast(intent);
}
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
switch (requestCode) {
case a: if (resultCode == b) {
finish();
}
break;
}
}
@Override
public void onBackPressed() {
if (v) {
v= false;
toggleHighlightMode();
}
else if (i.isDrawerOpen(GravityCompat.START)) i.closeDrawer(GravityCompat.START);
else if (u.getState() == BottomSheetBehavior.STATE_EXPANDED) u.setState(BottomSheetBehavior.STATE_HIDDEN);
else if (f.isActionViewExpanded()) f.collapseActionView();
else moveTaskToBack(true);
}
/** * void method which setups the toolbar, initializes the * FragmentPagerAdapter (which in turn deals with the tabs and fragments * see {
@link FragmentsPagerAdapter}
*/
private void a() {
Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
setSupportActionBar(toolbar);
String[] tabs = getResources().getStringArray(R.array.tabs);
FragmentsPagerAdapter mSectionsPagerAdapter = new FragmentsPagerAdapter(getSupportFragmentManager(), tabs);
ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
mViewPager.setAdapter(mSectionsPagerAdapter);
mViewPager.addOnPageChangeListener(this);
e= (TabLayout) findViewById(R.id.tabs);
e.setupWithViewPager(mViewPager);
i= (DrawerLayout) findViewById(R.id.drawer_layout);
ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, i, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
i.addDrawerListener(toggle);
toggle.syncState();
NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
navigationView.setNavigationItemSelectedListener(this);
final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
u= BottomSheetBehavior.from(bottomSheet);
u.setHideable(true);
u.setState(BottomSheetBehavior.STATE_HIDDEN);
ImageView nextImage = (ImageView) findViewById(R.id.image_view_next);
ImageView previousImage = (ImageView) findViewById(R.id.image_view_previous);
ImageView playImage = (ImageView) findViewById(R.id.image_view_play_pause);
ImageView shuffle = (ImageView) findViewById(R.id.imageview_shuffle);
ImageView repeat = (ImageView) findViewById(R.id.imageview_repeat);
s= (TextView) findViewById(R.id.music_progress_text);
q= (SeekBar) findViewById(R.id.music_progress);
q.setMax(100);
q.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
}
@Override
public void onStartTrackingTouch(SeekBar seekBar) {
mHandler.removeCallbacks(updateSeekBarRunnable);
}
@Override
public void onStopTrackingTouch(SeekBar seekBar) {
mHandler.removeCallbacks(updateSeekBarRunnable);
int totalDuration = j.getDuration();
int currentPosition = Converters.progressToTimer(seekBar.getProgress(), totalDuration);
j.seek(currentPosition);
updateProgressBar();
}
}
);
nextImage.setOnClickListener(this);
previousImage.setOnClickListener(this);
playImage.setOnClickListener(this);
repeat.setOnClickListener(this);
shuffle.setOnClickListener(this);
if (j!= null) {
repeat.setActivated(j.isRepeating());
shuffle.setActivated(j.isShuffled());
}
}
public void b(View view) {
TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
animate.setDuration(800);
animate.setFillAfter(true);
view.startAnimation(animate);
view.setVisibility(View.VISIBLE);
}
public void c(View view) {
TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
animate.setDuration(800);
animate.setFillAfter(true);
view.startAnimation(animate);
view.setVisibility(View.INVISIBLE);
}
@Override
public void onClick(View v) {
switch (v.getId()) {
case R.id.image_view_next: j.playNext();
break;
case R.id.image_view_previous: j.playPrevious();
break;
case R.id.image_view_play_pause: if (j.isPlaying()) {
j.pausePlayer();
mHandler.removeCallbacks(updateSeekBarRunnable);
}
else {
j.go();
updateProgressBar();
}
togglePlayButton();
break;
case R.id.imageview_shuffle: j.toggleShuffle();
v.setActivated(j.isShuffled());
ImageView repeat = (ImageView) findViewById(R.id.imageview_repeat);
repeat.setActivated(false);
break;
case R.id.imageview_repeat: j.toggleRepeating();
v.setActivated(j.isRepeating());
ImageView shuffle = (ImageView) findViewById(R.id.imageview_shuffle);
shuffle.setActivated(false);
break;
}
}
/** * This interface method is triggered when the user clicks to the list * fin the downloads fragment * * @param downloadedFile is the File which was clicked */
@Override
public void onDownloadedFileClick(DownloadedFile downloadedFile, int position) {
if (v) {
v= false;
toggleHighlightMode();
return;
}
if (downloadedFile.isMP3()) {
LinearLayout player = (LinearLayout) findViewById(R.id.player_layout);
if (player.getVisibility() == View.INVISIBLE) {
slideToTop(player);
}
mHandler.removeCallbacks(updateSeekBarRunnable);
j.setSongPosition(position);
j.playSong();
if (p) {
setMusicController();
p= false;
}
m.show(0);
}
else {
Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadedFile.getFile().getAbsolutePath()));
intent.setDataAndType(Uri.parse(downloadedFile.getFile().getAbsolutePath()), "video/*");
startActivity(intent);
}
}
/** * This interface method is triggered when the user clicks the download button * in the recycler view of {
@link net.akmobile.youtubeapp.fragments.SearchFragment}
*/
@Override
public void onDownloadClick(SearchedItem item, DonutProgress progressBar, final int position) {
u.setState(BottomSheetBehavior.STATE_EXPANDED);
item.normalizeTitle();
final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
DownloadHelper helper = new DownloadHelper(this, item, manager, progressBar, new DownloadHelper.DownloadStatusListener() {
@Override
public void onDownloadStart() {
notifyFragments(position, true);
}
@Override
public void downloadFailed(String reason) {
Toast.makeText(MainActivity.this, reason, Toast.LENGTH_SHORT).show();
notifyFragments(position, false);
}
@Override
public void downloadSuccessful(SearchedItem downloadedItem, VideoStream downloadedStream) {
Toast.makeText(MainActivity.this, getString(R.string.download_successful), Toast.LENGTH_SHORT).show();
SQLDatabaseAdapter adapter = new SQLDatabaseAdapter(MainActivity.this);
adapter.insertFile(downloadedItem, downloadedStream);
j.setList(getMusicFiles());
notifyFragments(position, false);
}
}
);
helper.initiateVideoDownload();
}
@Override
public void onMediaTypeSwitch() {
v= false;
toggleHighlightMode();
}
@Override
public boolean onDownloadedLongClick(DownloadedFile downloadedFile, LinearLayout highlight) {
if (v) return true;
t= highlight;
w= downloadedFile;
v= true;
toggleHighlightMode();
return true;
}
private void toggleHighlightMode() {
if (t== null) return;
f.collapseActionView();
t.setActivated(v);
h.setVisible(v);
g.setVisible(v);
f.setVisible(!inHighlightMode);
}
private void notifyFragments(int position, boolean isDownloading) {
if (!isDownloading) {
Intent updateDownloadsFragment = new Intent("UpdateDownloadsFragment");
updateDownloadsFragment.putExtra("folder", FileManager.MUSIC_FOLDER);
sendBroadcast(updateDownloadsFragment);
}
Intent changeDownloadStatus = new Intent(c);
changeDownloadStatus.putExtra("isDownloading", isDownloading);
changeDownloadStatus.putExtra("position", position);
sendBroadcast(changeDownloadStatus);
}
private void notifyItemRemoved(DownloadedFile downloadedFile) {
Intent updateDownloadsFragment = new Intent("UpdateDownloadsFragment");
updateDownloadsFragment.putExtra("folder", downloadedFile.getFile().getName().endsWith("mp3") ? FileManager.MUSIC_FOLDER : FileManager.VIDEOS_FOLDER);
sendBroadcast(updateDownloadsFragment);
}
/** * Method which is triggered whenever user clicks the large search icon in fragment * see {
fragment_search.xml}
* * @param view is the View which was clicked */
public void onSearchClick(View view) {
if (!searchMenuItem.isActionViewExpanded()) f.expandActionView();
view.setVisibility(View.INVISIBLE);
}
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
int id = item.getItemId();
switch (id) {
case R.id.nav_settings: Intent startSettings = new Intent(MainActivity.this, d.class);
startActivityForResult(startSettings, a);
i.closeDrawer(GravityCompat.START);
break;
case R.id.nav_contact: Intent startContact = new Intent(MainActivity.this, b.class);
startActivity(startContact);
i.closeDrawer(GravityCompat.START);
break;
}
return true;
}
private void deleteDownloadedFile(final DownloadedFile downloadedFile) {
AlertDialog.Builder dialog = new AlertDialog.Builder(this);
dialog.setMessage(getString(R.string.delete_checkup));
dialog.setCancelable(false);
dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface paramDialogInterface, int paramInt) {
String message;
if (downloadedFile.getFile().delete()) {
message = "File deleted";
UpdateRecords updateRecords = new UpdateRecords(MainActivity.this);
updateRecords.execute();
notifyItemRemoved(downloadedFile);
v= false;
toggleHighlightMode();
if (getMusicFiles().size() < 1 || downloadedFile.getFile().equals(j.getPlayingSong())) {
mHandler.removeCallbacks(updateSeekBarRunnable);
j.stop();
j.setList(getMusicFiles());
if (downloadedFile.isMP3()) slideToBottom(findViewById(R.id.player_layout));
}
else j.setList(getMusicFiles());
}
else message = getResources().getString(R.string.error_deleting_file);
Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
}
}
);
dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
}
}
);
dialog.show();
}
public void updateThumbnail() {
TextView title = (TextView) findViewById(R.id.texview_song_title);
title.setText(j.getPlayingSong().getName());
title.setSelected(true);
AlbumUtils utils = new AlbumUtils();
Bitmap album = utils.parseAlbum(j.getPlayingSong());
ImageView thumbnail = (ImageView) findViewById(R.id.image_view_thumbnail);
if (album == null) {
thumbnail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_music_file));
}
else {
thumbnail.setImageDrawable(new BitmapDrawable(getResources(), album));
}
}
private void togglePlayButton() {
ImageView view = (ImageView) findViewById(R.id.image_view_play_pause);
view.setActivated(!musicService.isPlaying());
}
/** * Update timer on seekbar */
public void updateProgressBar() {
mHandler.postDelayed(updateSeekBarRunnable, 200);
}
public void setMusicController() {
this.musicController = new MusicController(this);
m.setPrevNextListeners(new View.OnClickListener() {
@Override
public void onClick(View v) {
playNext();
}
}
, new View.OnClickListener() {
@Override
public void onClick(View v) {
playPrev();
}
}
);
m.setMediaPlayer(n);
m.setEnabled(true);
}
private void playNext() {
j.playNext();
if (p) {
setMusicController();
p= false;
}
m.show(0);
}
private void playPrev() {
j.playPrevious();
if (p) {
setMusicController();
p= false;
}
m.show(0);
}
public ArrayList<DownloadedFile> getMusicFiles() {
FileManager fileManager = new FileManager();
return fileManager.getDownloadedFiles(Environment.getExternalStoragePublicDirectory(FileManager.MUSIC_FOLDER));
}
/** * Background Runnable thread */
private Runnable updateSeekBarRunnable = new Runnable() {
public void run() {
long totalDuration = j.getDuration();
long currentDuration = j.getPosition();
String text = Converters.milliSecondsToTimer(currentDuration) + "/" + Converters.milliSecondsToTimer(totalDuration);
int progress = (Converters.getProgressPercentage(currentDuration, totalDuration));
s.setText(text);
q.setProgress(progress);
mHandler.postDelayed(this, 200);
}
}
;
@Override
public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
if (position == 0 && v) {
v= false;
toggleHighlightMode();
}
}
@Override
public void onPageSelected(int position) {
if (position == 0 && v) {
v= false;
toggleHighlightMode();
}
}
@Override
public void onPageScrollStateChanged(int state) {
}
}
