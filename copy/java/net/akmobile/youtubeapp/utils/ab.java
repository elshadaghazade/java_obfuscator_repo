package net.akmobile.youtubeapp.utils;
/** * Created by Rahimli Rahim on 11/10/2016. * ragim95@gmail.com * https: */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.InputMismatchException;
/** * A drawable that draws the target view as busing fast blur * <p/> * <p/> * TODO:we might use setBounds() to draw only part a of the target view * <p/> * Created by 10uR on 24.5.2014. */
public class ab extends Drawable {
private WeakReference<View> a;
private Bitmap b;
private Paint c;
private int d;
public ab(View target) {
this(target, 10);
}
public ab(View target, int d) {
this.targetRef = new WeakReference<View>(target);
setRadius(d);
target.setDrawingCacheEnabled(true);
target.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
c= new Paint();
c.setAntiAlias(true);
c.setFilterBitmap(true);
}
@Override
public void draw(Canvas canvas) {
if (b== null) {
View target = a.get();
if (target != null) {
Bitmap bitmap = target.getDrawingCache(true);
if (bitmap == null) return;
b= fastBlur(bitmap, d);
}
}
if (b!= null && !blurred.isRecycled()) canvas.drawBitmap(b, 0, 0, c);
}
/** * Set the bluring dthat will be applied to target view's bitmap * * @param dshould be 0-100 */
public void setRadius(int d) {
if (d< 0 || d> 100) throw new InputMismatchException("Radius must be 0 <= d<= 100 !");
this.radius = d;
if (b!= null) {
b.recycle();
b= null;
}
invalidateSelf();
}
public int getRadius() {
return d;
}
@Override
public void setAlpha(int alpha) {
}
@Override
public void setColorFilter(ColorFilter cf) {
}
@Override
public int getOpacity() {
return PixelFormat.TRANSLUCENT;
}
/** * from http: * <p/> * <p/> * <p/> * Stack Blur v1.0 from * http: * <p/> * Java Author: Mario Klingemann <mario at quasimondo.com> * http: * created Feburary 29, 2004 * Android port : Yahel Bouaziz <yahel at kayenko.com> * http: * ported april 5th, 2012 * <p/> * This is a compromise between Gaussian Blur and Box blur * It creates much better looking blurs than Box Blur, but is * 7x faster than my Gaussian Blur implementation. * <p/> * I called it Stack Blur because this describes best how this * filter works internally: it creates a kind of moving stack * of colors whilst scanning through the image. Thereby it * just has to add one new block of color to the right side * of the stack and remove the leftmost color. The remaining * colors on the topmost layer of the stack are either added on * or reduced by one, depending on if they are on the right or * on the left side of the stack. * <p/> * If you are using this algorithm in your code please add * the following line: * <p/> * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com> */
private static Bitmap fastBlur(Bitmap sentBitmap, int d) {
Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
if (d< 1) {
return (null);
}
int w = bitmap.getWidth();
int h = bitmap.getHeight();
int[] pix = new int[w * h];
Log.e("pix", w + " " + h + " " + pix.length);
bitmap.getPixels(pix, 0, w, 0, 0, w, h);
int wm = w - 1;
int hm = h - 1;
int wh = w * h;
int div = d+ d+ 1;
int r[] = new int[wh];
int g[] = new int[wh];
int b[] = new int[wh];
int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
int vmin[] = new int[Math.max(w, h)];
int divsum = (div + 1) >> 1;
divsum *= divsum;
int dv[] = new int[256 * divsum];
for (i = 0;
i < 256 * divsum;
i++) {
dv[i] = (i / divsum);
}
yw = yi = 0;
int[][] stack = new int[div][3];
int stackpointer;
int stackstart;
int[] sir;
int rbs;
int r1 = d+ 1;
int routsum, goutsum, boutsum;
int rinsum, ginsum, binsum;
for (y = 0;
y < h;
y++) {
rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
for (i = -radius;
i <= d;
i++) {
p = pix[yi + Math.min(wm, Math.max(i, 0))];
sir = stack[i + d];
sir[0] = (p & 0xff0000) >> 16;
sir[1] = (p & 0x00ff00) >> 8;
sir[2] = (p & 0x0000ff);
rbs = r1 - Math.abs(i);
rsum += sir[0] * rbs;
gsum += sir[1] * rbs;
bsum += sir[2] * rbs;
if (i > 0) {
rinsum += sir[0];
ginsum += sir[1];
binsum += sir[2];
}
else {
routsum += sir[0];
goutsum += sir[1];
boutsum += sir[2];
}
}
stackpointer = d;
for (x = 0;
x < w;
x++) {
r[yi] = dv[rsum];
g[yi] = dv[gsum];
b[yi] = dv[bsum];
rsum -= routsum;
gsum -= goutsum;
bsum -= boutsum;
stackstart = stackpointer - d+ div;
sir = stack[stackstart % div];
routsum -= sir[0];
goutsum -= sir[1];
boutsum -= sir[2];
if (y == 0) {
vmin[x] = Math.min(x + d+ 1, wm);
}
p = pix[yw + vmin[x]];
sir[0] = (p & 0xff0000) >> 16;
sir[1] = (p & 0x00ff00) >> 8;
sir[2] = (p & 0x0000ff);
rinsum += sir[0];
ginsum += sir[1];
binsum += sir[2];
rsum += rinsum;
gsum += ginsum;
bsum += binsum;
stackpointer = (stackpointer + 1) % div;
sir = stack[(stackpointer) % div];
routsum += sir[0];
goutsum += sir[1];
boutsum += sir[2];
rinsum -= sir[0];
ginsum -= sir[1];
binsum -= sir[2];
yi++;
}
yw += w;
}
for (x = 0;
x < w;
x++) {
rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
yp = -radius * w;
for (i = -radius;
i <= d;
i++) {
yi = Math.max(0, yp) + x;
sir = stack[i + d];
sir[0] = r[yi];
sir[1] = g[yi];
sir[2] = b[yi];
rbs = r1 - Math.abs(i);
rsum += r[yi] * rbs;
gsum += g[yi] * rbs;
bsum += b[yi] * rbs;
if (i > 0) {
rinsum += sir[0];
ginsum += sir[1];
binsum += sir[2];
}
else {
routsum += sir[0];
goutsum += sir[1];
boutsum += sir[2];
}
if (i < hm) {
yp += w;
}
}
yi = x;
stackpointer = d;
for (y = 0;
y < h;
y++) {
pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
rsum -= routsum;
gsum -= goutsum;
bsum -= boutsum;
stackstart = stackpointer - d+ div;
sir = stack[stackstart % div];
routsum -= sir[0];
goutsum -= sir[1];
boutsum -= sir[2];
if (x == 0) {
vmin[y] = Math.min(y + r1, hm) * w;
}
p = x + vmin[y];
sir[0] = r[p];
sir[1] = g[p];
sir[2] = b[p];
rinsum += sir[0];
ginsum += sir[1];
binsum += sir[2];
rsum += rinsum;
gsum += ginsum;
bsum += binsum;
stackpointer = (stackpointer + 1) % div;
sir = stack[stackpointer];
routsum += sir[0];
goutsum += sir[1];
boutsum += sir[2];
rinsum -= sir[0];
ginsum -= sir[1];
binsum -= sir[2];
yi += w;
}
}
bitmap.setPixels(pix, 0, w, 0, 0, w, h);
return (bitmap);
}
}
