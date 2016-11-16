package net.akmobile.youtubeapp.interfaces;

/**
 * Created by Rahimli Rahim on 28/09/2016.
 * ragim95@gmail.com
 * https://github.com/ragim/
 */


import android.widget.LinearLayout;

import net.akmobile.youtubeapp.models.DownloadedFile;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.utils.DonutProgress;

/**
 * This interface is implemented by the main activity
 * it contains the methods which are triggered when user clicks some view inside recycler
 * the name of functions suggest what was called
 */
public interface OnListItemsClickListener {
    void onDownloadedFileClick(DownloadedFile file,int position);
   // void onDownloadedFileLongClick(DownloadedFile file,int position);
    void onDownloadClick(SearchedItem video, DonutProgress progress,int position);
    void onMediaTypeSwitch();
    boolean onDownloadedLongClick(DownloadedFile downloadedFile, LinearLayout highlight);
}
