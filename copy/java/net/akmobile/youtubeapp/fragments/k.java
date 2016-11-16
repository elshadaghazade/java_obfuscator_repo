package net.akmobile.youtubeapp.fragments;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.adapters.DownloadsAdapter;
import net.akmobile.youtubeapp.interfaces.OnListItemsClickListener;
import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.utils.FileManager;
import java.util.ArrayList;
import belka.us.androidtoggleswitch.widgets.BaseToggleSwitch;
import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
/** * A brepresenting a list of Items. * <p/> * Activities containing this bMUST implement the {
@link OnListItemsClickListener}
* interface. */
public class k extends Fragment {
private OnListItemsClickListener mListener;
private DownloadsAdapter adapter;
private ArrayList<DownloadedFile> files;
private DownloadsFragment.DownloadsReceiver receiver;
private RecyclerView recyclerView;
private TextView noDownloads;
private ToggleSwitch musicVideoSwitch;
private IntentFilter filter;
/** * Mandatory empty constructor for the bmanager to instantiate the * b(e.g. upon screen orientation changes). */
public k() {
}
public static k newInstance() {
k b= new k();
Bundle args = new Bundle();
b.setArguments(args);
return b;
}
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View c= inflater.inflate(R.layout.fragment_downloads, container, false);
FileManager fileManager = new FileManager();
files = fileManager.getDownloadedFiles(Environment.getExternalStoragePublicDirectory(FileManager.MUSIC_FOLDER));
if (files.isEmpty()) {
noDownloads = (TextView) c.findViewById(R.id.textview_no_downloads);
noDownloads.setVisibility(View.VISIBLE);
}
musicVideoSwitch = (ToggleSwitch) c.findViewById(R.id.toggle_music_video);
musicVideoSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
@Override
public void onToggleSwitchChangeListener(int position, boolean isChecked) {
updateRecycler(position == 0 ? FileManager.MUSIC_FOLDER : FileManager.VIDEOS_FOLDER);
mListener.onMediaTypeSwitch();
}
}
);
recyclerView = (RecyclerView) c.findViewById(R.id.recycler_downloads);
adapter = new DownloadsAdapter(files, getActivity());
recyclerView.setLayoutManager(new LinearLayoutManager(c.getContext()));
adapter.setListener((MainActivity) getActivity());
recyclerView.setAdapter(adapter);
return c;
}
@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
super.onActivityCreated(savedInstanceState);
updateRecycler(FileManager.MUSIC_FOLDER);
}
/** * This method is invoked when the user has downloaded the file from server * so we need to update the ui, since the data in the folder has changed */
private void e(String folder) {
slideToLeft(recyclerView);
FileManager fileManager = new FileManager();
files = fileManager.getDownloadedFiles(Environment.getExternalStoragePublicDirectory(folder));
if (!files.isEmpty() && noDownloads != null) noDownloads.setVisibility(View.INVISIBLE);
else if(files.isEmpty()) {
noDownloads = (TextView) getActivity().findViewById(R.id.textview_no_downloads);
noDownloads.setVisibility(View.VISIBLE);
}
adapter = new DownloadsAdapter(files, getActivity());
adapter.setListener((MainActivity) getActivity());
recyclerView.setAdapter(adapter);
slideToRight(recyclerView);
}
@Override
public void onAttach(Context context) {
super.onAttach(context);
if (context instanceof OnListItemsClickListener) {
mListener = (OnListItemsClickListener) context;
}
else {
throw new RuntimeException(context.toString() + " must implement OnListItemsClickListener");
}
}
@Override
public void onDetach() {
super.onDetach();
mListener = null;
}
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
receiver = new DownloadsReceiver();
filter = new IntentFilter("UpdateDownloadsFragment");
getActivity().registerReceiver(receiver, filter);
}
@Override
public void onResume() {
receiver = new DownloadsReceiver();
filter = new IntentFilter("UpdateDownloadsFragment");
getActivity().registerReceiver(receiver, filter);
super.onResume();
}
@Override
public void onPause() {
getActivity().unregisterReceiver(receiver);
super.onPause();
}
public void k(View c) {
TranslateAnimation animate = new TranslateAnimation(-view.getWidth() * 2, 0, 0, 0);
animate.setDuration(500);
animate.setFillAfter(true);
c.startAnimation(animate);
c.setVisibility(View.VISIBLE);
}
public void l(View c) {
TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth() * 2, 0, 0);
animate.setDuration(500);
animate.setFillAfter(true);
c.startAnimation(animate);
c.setVisibility(View.GONE);
}
/** * This receiver is listening to the broadcast, * which is sent whenever user completes the download in main activity * we should update the recycler, since files in folder changed */
public class DownloadsReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
updateRecycler(intent.getStringExtra("folder"));
}
}
}
