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

// TODO: 14/10/2016 Update player if music is switched from service

/**
 * Activity containing two fragments: Search and Downloaded files lists
 * You can see in Manifest file, that activity has Search intent action,
 * so it receives an intent with extra value 'query' whenever the user clicks the search button
 */
public class MainActivity extends BaseActivity implements OnListItemsClickListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener {

    ///////////////////////////////////////////////////////////////////////////
// Declaration of variables
///////////////////////////////////////////////////////////////////////////
    private static final int SETTINGS_INTENT = 1;
    private static final int LANGUAGE_CHANGED = 10;
    public static final String DOWNLOAD_STATUS_CHANGED = "StatusChanged";
    public static final String MAKE_SEARCH_INTENT = "UpdateSearchFragment";
    private TabLayout tabLayout;
    private MenuItem searchMenuItem, deleteMenuItem, shareMenuItem;
    private DrawerLayout drawer;
    public MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicController musicController;
    private MediaPlayerController mediaPlayerController;
    private boolean paused = false, playbackPaused = false;
    private SeekBar musicProgress;
    private Handler mHandler = new Handler();
    private BroadcastReceiver receiver;
    private TextView progressText;
    private LinearLayout highlightedItem;
    public BottomSheetBehavior bottomSheetBehavior;
    private boolean inHighlightMode = false;
    private DownloadedFile longClickedFile;
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setList(getMusicFiles());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

///////////////////////////////////////////////////////////////////////////
// Activity lifecycle and other methods
///////////////////////////////////////////////////////////////////////////

    /**
     * Register music service when activity is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        mediaPlayerController = new MediaPlayerController(musicService, musicBound);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMusicController();
        setUpUI();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateProgressBar();
                updateThumbnail();
                togglePlayButton();
            }
        };
        UpdateRecords task = new UpdateRecords(this);
        task.execute();
    }

    @Override
    protected void onPause() {
        paused = true;
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setMusicController();
            paused = false;
        }
        registerReceiver(receiver, new IntentFilter(MusicService.UPDATE_UI));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (musicService != null && musicService.isPlaybackNotEmpty()) {
            togglePlayButton();
            updateThumbnail();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        musicController.hide();
    }

    @Override
    protected void onDestroy() {
        playIntent = new Intent(this, MusicService.class);
        unbindService(musicConnection);
        stopService(playIntent);
        musicService = null;
        mHandler.removeCallbacks(updateSeekBarRunnable);
        super.onDestroy();
    }

    /**
     * Method which setups the menu in toolbar (SearchView and little search icon)
     * and allows to perform search in this activity from toolbar
     *
     * @param menu is the Application's toolbar menu, see options_menu.xml
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        deleteMenuItem = menu.findItem(R.id.action_remove);
        shareMenuItem = menu.findItem(R.id.action_share);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                if (longClickedFile != null) {
                    deleteDownloadedFile(longClickedFile);
                }
                break;
            case R.id.action_share:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The method which is called whenever user searches
     * anything from toolbar and clicks go on keyboard,
     * We should make sure that user in the search tab, that's why we select this tab
     *
     * @param intent contains the user's query which we retrieve and send a broadcast to the fragment
     *               that user have typed something, make search
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linear_main_activity);
            mainLayout.requestFocus();
            TabLayout.Tab search = tabLayout.getTabAt(0);
            search.select();
            String query = intent.getStringExtra(SearchManager.QUERY);
            intent = new Intent(MAKE_SEARCH_INTENT);
            intent.putExtra("query", query);
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETTINGS_INTENT:
                if (resultCode == LANGUAGE_CHANGED) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (inHighlightMode) {
            inHighlightMode = false;
            toggleHighlightMode();
        } else if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else if (searchMenuItem.isActionViewExpanded())
            searchMenuItem.collapseActionView();
        else
            moveTaskToBack(true);
        //super.onBackPressed();
    }
///////////////////////////////////////////////////////////////////////////
// UI functions
///////////////////////////////////////////////////////////////////////////

    /**
     * void method which setups the toolbar, initializes the
     * FragmentPagerAdapter (which in turn deals with the tabs and fragments
     * see {@link FragmentsPagerAdapter}
     */
    private void setUpUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] tabs = getResources().getStringArray(R.array.tabs);
        FragmentsPagerAdapter mSectionsPagerAdapter = new FragmentsPagerAdapter(getSupportFragmentManager(), tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final LinearLayout bottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        //PlayerControl
        ImageView nextImage = (ImageView) findViewById(R.id.image_view_next);
        ImageView previousImage = (ImageView) findViewById(R.id.image_view_previous);
        ImageView playImage = (ImageView) findViewById(R.id.image_view_play_pause);
        ImageView shuffle = (ImageView) findViewById(R.id.imageview_shuffle);
        ImageView repeat = (ImageView) findViewById(R.id.imageview_repeat);
        progressText = (TextView) findViewById(R.id.music_progress_text);
        musicProgress = (SeekBar) findViewById(R.id.music_progress);
        musicProgress.setMax(100);
        musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                int totalDuration = musicService.getDuration();
                int currentPosition = Converters.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                musicService.seek(currentPosition);
                // update timer progressBar again
                updateProgressBar();
            }
        });
        nextImage.setOnClickListener(this);
        previousImage.setOnClickListener(this);
        playImage.setOnClickListener(this);
        repeat.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        if (musicService != null) {
            repeat.setActivated(musicService.isRepeating());
            shuffle.setActivated(musicService.isShuffled());
        }
    }

    // To animate view slide out from bottom to top
    public void slideToTop(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(800);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }

    public void slideToBottom(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(800);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.INVISIBLE);
    }

    ///////////////////////////////////////////////////////////////////////////
    // On click handlers
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_next:
                musicService.playNext();
                break;
            case R.id.image_view_previous:
                musicService.playPrevious();
                break;

            case R.id.image_view_play_pause:
                if (musicService.isPlaying()) {
                    musicService.pausePlayer();
                    mHandler.removeCallbacks(updateSeekBarRunnable);
                } else {
                    musicService.go();
                    updateProgressBar();
                }
                togglePlayButton();
                break;

            case R.id.imageview_shuffle:
                musicService.toggleShuffle();
                v.setActivated(musicService.isShuffled());
                ImageView repeat = (ImageView) findViewById(R.id.imageview_repeat);
                repeat.setActivated(false);
                break;

            case R.id.imageview_repeat:
                musicService.toggleRepeating();
                v.setActivated(musicService.isRepeating());
                ImageView shuffle = (ImageView) findViewById(R.id.imageview_shuffle);
                shuffle.setActivated(false);
                break;

        }
    }

    /**
     * This interface method is triggered when the user clicks to the list
     * searchMenuItem in the downloads fragment
     *
     * @param downloadedFile is the File which was clicked
     */
    @Override
    public void onDownloadedFileClick(DownloadedFile downloadedFile, int position) {
        if (inHighlightMode) {
            inHighlightMode = false;
            toggleHighlightMode();
            return;
        }
        if (downloadedFile.isMP3()) {
            LinearLayout player = (LinearLayout) findViewById(R.id.player_layout);
            if (player.getVisibility() == View.INVISIBLE) {
                slideToTop(player);
            }
            mHandler.removeCallbacks(updateSeekBarRunnable);
            musicService.setSongPosition(position);
            musicService.playSong();
            if (playbackPaused) {
                setMusicController();
                playbackPaused = false;
            }
            musicController.show(0);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadedFile.getFile().getAbsolutePath()));
            intent.setDataAndType(Uri.parse(downloadedFile.getFile().getAbsolutePath()), "video/*");
            startActivity(intent);
        }
    }

    /**
     * This interface method is triggered when the user clicks the download button
     * in the recycler view of {@link net.akmobile.youtubeapp.fragments.SearchFragment}
     */
    @Override
    public void onDownloadClick(SearchedItem item, DonutProgress progressBar, final int position) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
                //update playback of service
                musicService.setList(getMusicFiles());
                notifyFragments(position, false);
            }
        });

        helper.initiateVideoDownload();
    }

    @Override
    public void onMediaTypeSwitch() {
        inHighlightMode = false;
        toggleHighlightMode();
    }

    @Override
    public boolean onDownloadedLongClick(DownloadedFile downloadedFile, LinearLayout highlight) {
        if (inHighlightMode)
            return true;
        highlightedItem = highlight;
        longClickedFile = downloadedFile;
        inHighlightMode = true;
        toggleHighlightMode();
        return true;
    }

    private void toggleHighlightMode() {
        if (highlightedItem == null)
            return;

        searchMenuItem.collapseActionView();
        highlightedItem.setActivated(inHighlightMode);
        shareMenuItem.setVisible(inHighlightMode);
        deleteMenuItem.setVisible(inHighlightMode);
        searchMenuItem.setVisible(!inHighlightMode);
    }


    private void notifyFragments(int position, boolean isDownloading) {
        if (!isDownloading) {
            Intent updateDownloadsFragment = new Intent("UpdateDownloadsFragment");
            updateDownloadsFragment.putExtra("folder", FileManager.MUSIC_FOLDER);
            sendBroadcast(updateDownloadsFragment);
        }
        Intent changeDownloadStatus = new Intent(DOWNLOAD_STATUS_CHANGED);
        changeDownloadStatus.putExtra("isDownloading", isDownloading);
        changeDownloadStatus.putExtra("position", position);
        sendBroadcast(changeDownloadStatus);
    }

    private void notifyItemRemoved(DownloadedFile downloadedFile) {
        Intent updateDownloadsFragment = new Intent("UpdateDownloadsFragment");
        updateDownloadsFragment.putExtra("folder",
                downloadedFile.getFile().getName().endsWith("mp3") ? FileManager.MUSIC_FOLDER : FileManager.VIDEOS_FOLDER);
        sendBroadcast(updateDownloadsFragment);
    }

    /**
     * Method which is triggered whenever user clicks the large search icon in fragment
     * see { fragment_search.xml}
     *
     * @param view is the View which was clicked
     */
    public void onSearchClick(View view) {
        if (!searchMenuItem.isActionViewExpanded())
            searchMenuItem.expandActionView();
        view.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_settings:
                Intent startSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(startSettings, SETTINGS_INTENT);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_contact:
                Intent startContact = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(startContact);
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
// Helper functions
///////////////////////////////////////////////////////////////////////////
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
                    inHighlightMode = false;
                    toggleHighlightMode();
                    if (getMusicFiles().size() < 1 || downloadedFile.getFile().equals(musicService.getPlayingSong())) {
                        mHandler.removeCallbacks(updateSeekBarRunnable);
                        musicService.stop();
                        musicService.setList(getMusicFiles());
                        if (downloadedFile.isMP3())
                            slideToBottom(findViewById(R.id.player_layout));
                    } else
                        musicService.setList(getMusicFiles());
                } else
                    message = getResources().getString(R.string.error_deleting_file);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();

    }

    public void updateThumbnail() {
        TextView title = (TextView) findViewById(R.id.texview_song_title);
        title.setText(musicService.getPlayingSong().getName());
        title.setSelected(true);
        AlbumUtils utils = new AlbumUtils();
        Bitmap album = utils.parseAlbum(musicService.getPlayingSong());
        ImageView thumbnail = (ImageView) findViewById(R.id.image_view_thumbnail);
        if (album == null) {
            thumbnail.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_music_file));
        } else {
            thumbnail.setImageDrawable(new BitmapDrawable(getResources(), album));
        }
    }

    private void togglePlayButton() {
        ImageView view = (ImageView) findViewById(R.id.image_view_play_pause);
        view.setActivated(!musicService.isPlaying());
    }

    /**
     * Update timer on seekbar
     */
    public void updateProgressBar() {
        mHandler.postDelayed(updateSeekBarRunnable, 200);
    }

    public void setMusicController() {
        this.musicController = new MusicController(this);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        musicController.setMediaPlayer(mediaPlayerController);
        musicController.setEnabled(true);
    }

    //play next
    private void playNext() {
        musicService.playNext();
        if (playbackPaused) {
            setMusicController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    //play previous
    private void playPrev() {
        musicService.playPrevious();
        if (playbackPaused) {
            setMusicController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    public ArrayList<DownloadedFile> getMusicFiles() {
        FileManager fileManager = new FileManager();
        return fileManager.getDownloadedFiles(Environment.getExternalStoragePublicDirectory(FileManager.MUSIC_FOLDER));
    }


///////////////////////////////////////////////////////////////////////////
// Other classes and variables
///////////////////////////////////////////////////////////////////////////

    /**
     * Background Runnable thread
     */
    private Runnable updateSeekBarRunnable = new Runnable() {
        public void run() {
            long totalDuration = musicService.getDuration();
            long currentDuration = musicService.getPosition();
            // Updating progressBar bar
            String text = Converters.milliSecondsToTimer(currentDuration) + "/" + Converters.milliSecondsToTimer(totalDuration);
            int progress = (Converters.getProgressPercentage(currentDuration, totalDuration));
            progressText.setText(text);
            musicProgress.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 200);
        }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0 && inHighlightMode) {
            inHighlightMode = false;
            toggleHighlightMode();
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0 && inHighlightMode) {
            inHighlightMode = false;
            toggleHighlightMode();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
