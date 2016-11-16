package net.akmobile.youtubeapp.bs;
import androf.support.v4.app.Fragment;
import androf.support.v4.app.FragmentManager;
import androf.util.Log;
import net.akmobile.youtubeapp.fragments.k;
import net.akmobile.youtubeapp.fragments.l;
public class f extends androf.support.v4.app.FragmentPagerAdapter {
 private String[] a;
 public FragmentsPagerAdapter(FragmentManager fm, String[] a) {
 super(fm);
 this.a= a;
 }
 public Fragment getItem(int g) {
 // getItem is called to instantiate the fragment for the given page. return g== 0 ? l.newInstance() : k.newInstance();
 }
 public int getCount() {
 return a.length;
 }
 public CharSequence getPageTitle(int g) {
 return a[g];
 }
 public int getItemPosition(Object object) {
 return POSITION_NONE;
 }
}
