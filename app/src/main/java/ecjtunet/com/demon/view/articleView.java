package ecjtunet.com.demon.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ecjtunet.com.demon.R;

/**
 * Created by homker on 2015/1/23.
 */
public class articleView extends LinearLayout {

    private Context context;
    private TypedArray typedArray;
    private LinearLayout.LayoutParams params;
    private AnimationDrawable animationDrawable;
    private Handler handler = new Handler() {

        public void handleMessage(Message message) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) message.obj;
            ImageView imageView = (ImageView) hashMap.get("imageView");
            LayoutParams params1 = new LayoutParams(message.arg1, message.arg2);
            params1.gravity = Gravity.CENTER_HORIZONTAL;
            imageView.setLayoutParams(params1);
            Drawable drawable = (Drawable) hashMap.get("drawable");
            Log.i("tag", "nimei" + String.valueOf(drawable));
            imageView.setImageDrawable(drawable);
            animationDrawable.stop();
        }
    };

    public articleView(Context context) {
        super(context);
    }

    public articleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.articleView);
    }

    public static int dip2px(articleView context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setText(ArrayList<HashMap<String, String>> datas, int windows_width) {
        Log.i("tag", "w1withd" + String.valueOf(windows_width));
        for (HashMap<String, String> hashMap : datas) {
            String type = hashMap.get("type");
            switch (type) {
                case "image":
                    int imageWidth = typedArray.getDimensionPixelOffset(R.styleable.articleView_image_width, 100);
                    int imageHight = typedArray.getDimensionPixelOffset(R.styleable.articleView_image_height, 100);
                    int margin = typedArray.getDimensionPixelOffset(R.styleable.articleView_image_margin, 100);
                    ImageView imageView = new ImageView(context);
                    params = new LinearLayout.LayoutParams(imageWidth, imageHight);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    params.setMargins(0, margin, 0, margin);
                    imageView.setScaleType(ImageView.ScaleType.FIT_START);
                    imageView.setLayoutParams(params);

                    imageView.setBackgroundResource(R.drawable.loding);
                    Log.i("tag", String.valueOf(imageView));
                    animationDrawable = (AnimationDrawable) imageView.getBackground();
                    if (animationDrawable != null) {
                        animationDrawable.start();
                    }
                    addView(imageView);
                    new DownLoadPicThread(imageView, hashMap.get("value"), windows_width).start();
                    break;
                default:
                    float textSize = typedArray.getDimension(R.styleable.articleView_textSize, 16);
                    int textColor = typedArray.getColor(R.styleable.articleView_textColor, 0xFF0000FF);
                    TextView textView = new TextView(context);
                    textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    textView.setText(Html.fromHtml(hashMap.get("value")));
                    textView.setTextSize(textSize);     //设置字体大小
                    textView.setTextColor(textColor);   //设置字体颜色
                    addView(textView);
            }
        }
        typedArray.recycle();//回收typedarray
    }

    private class DownLoadPicThread extends Thread {

        private ImageView imageView;
        private String url;
        private int windowswidth;

        public DownLoadPicThread(ImageView imageView, String url, int widows_width) {

            super();
            this.imageView = imageView;
            this.url = url;
            this.windowswidth = widows_width;
            Log.i("tag", "w2withd" + String.valueOf(widows_width));
        }

        @Override
        public void run() {
            Log.i("tag", "i do");
            Drawable drawable = null;
            int newImageWidth = 0;
            int newImageHeight = 0;

            try {
                drawable = Drawable.createFromStream(new URL(url).openStream(), "image");
                Log.i("tag", "dsaf" + String.valueOf(drawable));
            } catch (Exception e) {
                e.printStackTrace();
            }

            SystemClock.sleep(2000);

            Message msg = handler.obtainMessage();
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("imageView", imageView);
            Bitmap bitmap = getImage(drawable, windowswidth);
            Drawable bitmapDrawable = new BitmapDrawable(bitmap);
            newImageWidth = bitmapDrawable.getIntrinsicWidth();
            newImageHeight = bitmapDrawable.getIntrinsicHeight();
            hashMap.put("drawable", bitmapDrawable);
            msg.obj = hashMap;
            msg.arg1 = newImageWidth;
            msg.arg2 = newImageHeight;
            handler.sendMessage(msg);
        }

        public Bitmap getImage(Drawable drawable, int windows_width) {
            Log.i("tag", "wwidth" + String.valueOf(windows_width));
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scale = (float) windows_width / w;
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
    }


}
