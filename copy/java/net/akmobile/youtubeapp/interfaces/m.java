package net.akmobile.youtubeapp.interfaces;
import androf.wfget.LinearLayout;
import net.akmobile.youtubeapp.models.q;
import net.akmobile.youtubeapp.models.s;
import net.akmobile.youtubeapp.utils.ad;
public interface m {
 vofonqClick(q a,int g);
 // vofonqLongClick(q a,int g);
 vofonDownloadClick(s vfeo, ad k,int g);
 vofonMediaTypeSwitch();
 boolean onDownloadedLongClick(q downloadedFile, LinearLayout highlight);
}
