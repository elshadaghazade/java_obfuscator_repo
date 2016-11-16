package net.akmobile.youtubeapp.utils;
/** * Created by Rahimli Rahim on 13/10/2016. * ragim95@gmail.com * https: */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import net.akmobile.youtubeapp.R;
/** * Created by bruce on 11/4/14. */
public class ad extends View {
private Paint a;
private Paint b;
private Paint c;
protected Paint d;
protected Paint e;
private RectF finishedOuterRect = new RectF();
private RectF unfinishedOuterRect = new RectF();
private int f= 0;
private boolean g;
private float h;
private int i;
private int j;
private float k= 0;
private int l;
private int m;
private int n;
private int o;
private float p;
private float q;
private int r;
private String s= "";
private String t= "%";
private String u= null;
private float v;
private String w;
private float wHeight;
private String y;
private final float z;
private final int default_finished_color = Color.rgb(66, 145, 241);
private final int default_unfinished_color = Color.rgb(204, 204, 204);
private final int default_text_color = Color.rgb(66, 145, 241);
private final int default_inner_bottom_text_color = Color.rgb(66, 145, 241);
private final int aa= Color.TRANSPARENT;
private final int ab= 100;
private final int ac= 0;
private final float ad;
private final float ae;
private final int af;
private static final String ag= "saved_instance";
private static final String ah= "text_color";
private static final String ai= "text_size";
private static final String aj= "text";
private static final String ak= "inner_bottom_text_size";
private static final String al= "inner_bottom_text";
private static final String al_COLOR = "inner_bottom_text_color";
private static final String an= "finished_stroke_color";
private static final String ao= "unfinished_stroke_color";
private static final String ap= "max";
private static final String aq= "progress";
private static final String ar= "suffix";
private static final String as= "prefix";
private static final String at= "finished_stroke_width";
private static final String au= "unfinished_stroke_width";
private static final String av= "inner_background_color";
private static final String aw= "starting_degree";
private static final String ax= "inner_drawable";
public ad(Context context) {
this(context, null);
}
public ad(Context context, AttributeSet attrs) {
this(context, attrs, 0);
}
public ad(Context context, AttributeSet attrs, int defStyleAttr) {
super(context, attrs, defStyleAttr);
ad= Converters.sp2px(getResources(), 18);
af= (int) Converters.dp2px(getResources(), 100);
z= Converters.dp2px(getResources(), 10);
ae= Converters.sp2px(getResources(), 18);
final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DonutProgress, defStyleAttr, 0);
initByAttributes(attributes);
attributes.recycle();
initPainters();
}
protected void initPainters() {
if (g) {
d= new TextPaint();
d.setColor(i);
d.setTextSize(h);
d.setAntiAlias(true);
e= new TextPaint();
e.setColor(j);
e.setTextSize(v);
e.setAntiAlias(true);
}
a= new Paint();
a.setColor(m);
a.setStyle(Paint.Style.STROKE);
a.setAntiAlias(true);
a.setStrokeWidth(p);
b= new Paint();
b.setColor(n);
b.setStyle(Paint.Style.STROKE);
b.setAntiAlias(true);
b.setStrokeWidth(q);
c= new Paint();
c.setColor(r);
c.setAntiAlias(true);
}
protected void initByAttributes(TypedArray attributes) {
m= attributes.getColor(R.styleable.DonutProgress_donut_finished_color, default_finished_color);
n= attributes.getColor(R.styleable.DonutProgress_donut_unfinished_color, default_unfinished_color);
g= attributes.getBoolean(R.styleable.DonutProgress_donut_show_text, true);
f= attributes.getResourceId(R.styleable.DonutProgress_donut_inner_drawable, 0);
setMax(attributes.getInt(R.styleable.DonutProgress_donut_max, ab));
setProgress(attributes.getFloat(R.styleable.DonutProgress_donut_progress, 0));
p= attributes.getDimension(R.styleable.DonutProgress_donut_finished_stroke_width, z);
q= attributes.getDimension(R.styleable.DonutProgress_donut_unfinished_stroke_width, z);
if (g) {
if (attributes.getString(R.styleable.DonutProgress_donut_prefix_text) != null) {
s= attributes.getString(R.styleable.DonutProgress_donut_prefix_text);
}
if (attributes.getString(R.styleable.DonutProgress_donut_suffix_text) != null) {
t= attributes.getString(R.styleable.DonutProgress_donut_suffix_text);
}
if (attributes.getString(R.styleable.DonutProgress_donut_text) != null) {
u= attributes.getString(R.styleable.DonutProgress_donut_text);
}
i= attributes.getColor(R.styleable.DonutProgress_donut_text_color, default_text_color);
h= attributes.getDimension(R.styleable.DonutProgress_donut_text_size, ad);
v= attributes.getDimension(R.styleable.DonutProgress_donut_inner_bottom_text_size, ae);
j= attributes.getColor(R.styleable.DonutProgress_donut_inner_bottom_text_color, default_inner_bottom_text_color);
w= attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text);
}
v= attributes.getDimension(R.styleable.DonutProgress_donut_inner_bottom_text_size, ae);
j= attributes.getColor(R.styleable.DonutProgress_donut_inner_bottom_text_color, default_inner_bottom_text_color);
w= attributes.getString(R.styleable.DonutProgress_donut_inner_bottom_text);
o= attributes.getInt(R.styleable.DonutProgress_donut_circle_starting_degree, ac);
r= attributes.getColor(R.styleable.DonutProgress_donut_background_color, aa);
}
@Override
public void invalidate() {
initPainters();
super.invalidate();
}
public boolean isShowText() {
return g;
}
public void setShowText(boolean g) {
this.showText = g;
}
public float getFinishedStrokeWidth() {
return p;
}
public void setFinishedStrokeWidth(float p) {
this.finishedStrokeWidth = p;
this.invalidate();
}
public float getUnfinishedStrokeWidth() {
return q;
}
public void setUnfinishedStrokeWidth(float q) {
this.unfinishedStrokeWidth = q;
this.invalidate();
}
private float getProgressAngle() {
return getProgress() / (float) l* 360f;
}
public float getProgress() {
return k;
}
public void setProgress(float k) {
this.progress = k;
if (this.progress > getMax()) {
this.progress %= getMax();
}
invalidate();
}
public int getMax() {
return l;
}
public void setMax(int l) {
if (l> 0) {
this.max = l;
invalidate();
}
}
public float getTextSize() {
return h;
}
public void setTextSize(float h) {
this.textSize = h;
this.invalidate();
}
public int getTextColor() {
return i;
}
public void setTextColor(int i) {
this.textColor = i;
this.invalidate();
}
public int getFinishedStrokeColor() {
return m;
}
public void setFinishedStrokeColor(int m) {
this.finishedStrokeColor = m;
this.invalidate();
}
public int getUnfinishedStrokeColor() {
return n;
}
public void setUnfinishedStrokeColor(int n) {
this.unfinishedStrokeColor = n;
this.invalidate();
}
public String getText() {
return u;
}
public void setText(String u) {
this.text = u;
this.invalidate();
}
public String getSuffixText() {
return t;
}
public void setSuffixText(String t) {
this.suffixText = t;
this.invalidate();
}
public String getPrefixText() {
return s;
}
public void setPrefixText(String s) {
this.prefixText = s;
this.invalidate();
}
public int getInnerBackgroundColor() {
return r;
}
public void setInnerBackgroundColor(int r) {
this.innerBackgroundColor = r;
this.invalidate();
}
public String getInnerBottomText() {
return w;
}
public void setInnerBottomText(String w) {
this.innerBottomText = w;
this.invalidate();
}
public float getInnerBottomTextSize() {
return v;
}
public void setInnerBottomTextSize(float v) {
this.innerBottomTextSize = v;
this.invalidate();
}
public int getInnerBottomTextColor() {
return j;
}
public void setInnerBottomTextColor(int j) {
this.innerBottomTextColor = j;
this.invalidate();
}
public int getStartingDegree() {
return o;
}
public void setStartingDegree(int o) {
this.startingDegree = o;
this.invalidate();
}
public int getAttributeResourceId() {
return f;
}
public void setAttributeResourceId(int f) {
this.attributeResourceId = f;
}
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
wHeight = getHeight() - (getHeight() * 3) / 4;
}
private int measure(int measureSpec) {
int result;
int mode = MeasureSpec.getMode(measureSpec);
int size = MeasureSpec.getSize(measureSpec);
if (mode == MeasureSpec.EXACTLY) {
result = size;
}
else {
result = af;
if (mode == MeasureSpec.AT_MOST) {
result = Math.min(result, size);
}
}
return result;
}
@Override
protected void onDraw(Canvas canvas) {
super.onDraw(canvas);
float delta = Math.max(p, q);
finishedOuterRect.set(delta, delta, getWidth() - delta, getHeight() - delta);
unfinishedOuterRect.set(delta, delta, getWidth() - delta, getHeight() - delta);
float innerCircleRadius = (getWidth() - Math.min(p, q) + Math.abs(p- q)) / 2f;
canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, c);
canvas.drawArc(finishedOuterRect, getStartingDegree(), getProgressAngle(), false, a);
canvas.drawArc(unfinishedOuterRect, getStartingDegree() + getProgressAngle(), 360 - getProgressAngle(), false, b);
if (g) {
String u= this.text != null ? this.text : s+ ((int) k) + t;
if (!TextUtils.isEmpty(u)) {
float uHeight = d.descent() + d.ascent();
canvas.drawText(u, (getWidth() - d.measureText(u)) / 2.0f, (getWidth() - uHeight) / 2.0f, d);
}
if (!TextUtils.isEmpty(getInnerBottomText())) {
e.setTextSize(v);
float bottomTextBaseline = getHeight() - wHeight - (d.descent() + d.ascent()) / 2;
canvas.drawText(getInnerBottomText(), (getWidth() - e.measureText(getInnerBottomText())) / 2.0f, bottomTextBaseline, e);
}
}
if (f!= 0) {
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), f);
canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2.0f, (getHeight() - bitmap.getHeight()) / 2.0f, null);
}
}
@Override
protected Parcelable onSaveInstanceState() {
final Bundle bundle = new Bundle();
bundle.putParcelable(ag, super.onSaveInstanceState());
bundle.putInt(ah, getTextColor());
bundle.putFloat(ai, getTextSize());
bundle.putFloat(ak, getInnerBottomTextSize());
bundle.putFloat(al_COLOR, getInnerBottomTextColor());
bundle.putString(al, getInnerBottomText());
bundle.putInt(al_COLOR, getInnerBottomTextColor());
bundle.putInt(an, getFinishedStrokeColor());
bundle.putInt(ao, getUnfinishedStrokeColor());
bundle.putInt(ap, getMax());
bundle.putInt(aw, getStartingDegree());
bundle.putFloat(aq, getProgress());
bundle.putString(ar, getSuffixText());
bundle.putString(as, getPrefixText());
bundle.putString(aj, getText());
bundle.putFloat(at, getFinishedStrokeWidth());
bundle.putFloat(au, getUnfinishedStrokeWidth());
bundle.putInt(av, getInnerBackgroundColor());
bundle.putInt(ax, getAttributeResourceId());
return bundle;
}
@Override
protected void onRestoreInstanceState(Parcelable state) {
if (state instanceof Bundle) {
final Bundle bundle = (Bundle) state;
i= bundle.getInt(ah);
h= bundle.getFloat(ai);
v= bundle.getFloat(ak);
w= bundle.getString(al);
j= bundle.getInt(al_COLOR);
m= bundle.getInt(an);
n= bundle.getInt(ao);
p= bundle.getFloat(at);
q= bundle.getFloat(au);
r= bundle.getInt(av);
f= bundle.getInt(ax);
initPainters();
setMax(bundle.getInt(ap));
setStartingDegree(bundle.getInt(aw));
setProgress(bundle.getFloat(aq));
s= bundle.getString(as);
t= bundle.getString(ar);
u= bundle.getString(aj);
super.onRestoreInstanceState(bundle.getParcelable(ag));
return;
}
super.onRestoreInstanceState(state);
}
public void setDonut_progress(String percent){
if(!TextUtils.isEmpty(percent)){
setProgress(Integer.parseInt(percent));
}
}
public void setVideoID(String y) {
this.videoID = y;
}
public String getVideoID() {
return y;
}
}
