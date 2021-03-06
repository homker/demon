/*
package ecjtunet.com.demon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import ecjtunet.com.demon.R;

*/
/**
 * Created by homker on 2015/1/23.
 *//*

public class newListView extends ListView {


    private static ArrayList<String> infos = new ArrayList<String>();
    private static ArrayList<String> articleIDs = new ArrayList<String>();
    private static ArrayList<ImageView> imageViews; //point 的集合
    private static ArrayList<View> views; // flipper 的孩子
    private static TextView newsHeadImageInfo;
    private int image_id[] = {R.drawable.a, R.drawable.b, R.drawable.c}; //初始化的头部图片
    private ViewFlipper flipper;
    private LinearLayout ll_point;
    private FrameLayout frameLayout;
    private Context context;
    private int windows_width = 480;
    private int headerHeight;
    private View headView;
    private int frameheight;
    private GestureDetector mGestureDetector;
    private int verticalMinDistance = 20;
    private int minVelocity         = 0;
    View.OnTouchListener mGestureListener;

    public newListView(Context context) {
        this(context, null);
    }

    public newListView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        */
/*mGestureDetector = new GestureDetector(context,new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                flipper.setClickable(true);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                flipper.setClickable(false);
                int pageIndex = newListView.getPageIndex(flipper.getCurrentView());
                //左滑
                if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.push_left_out));
                    flipper.showNext();
                    if (pageIndex == flipper.getChildCount() - 1)
                        draw_point(0);
                    else
                        draw_point(++pageIndex);
                    return true;
                } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.push_right_out));
                    flipper.showPrevious();
                    if (pageIndex == 0)
                        draw_point(flipper.getChildCount() - 1);
                    else
                        draw_point(--pageIndex);
                    return true;
                }
                return true;
            }
        });*//*

    }

    */
/**
     * 修改前面的那几个点。。。。还有就是修改同一条的info
     *
     * @param index
     *//*

    public static void draw_point(int index) {
        String info;
        for (int i = 0; i < imageViews.size(); i++) {
            imageViews.get(i).setImageResource(R.drawable.indicator);
        }
        imageViews.get(index).setImageResource(R.drawable.indicator_focused);
        if (infos.size() == 0 || infos.get(index) == null) {
            info = "test";
        } else {
            info = infos.get(index);
        }
        newsHeadImageInfo.setText(info);
    }

    */
/**
     * 得到当前图片的标记
     *
     * @param view
     * @return *
     *//*

    public static int getPageIndex(View view) {
        for (int i = 0; i < views.size(); i++) {
            if (view == views.get(i)) return i;
        }
        return 0;
    }

    public static String getArticleID(int index) {
        return articleIDs.get(index);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setWindows_width(int windows_width) {
        this.windows_width = windows_width;
    }

    public void setInfos(String info, String articleID) {
        this.infos.add(info);
        this.articleIDs.add(articleID);
    }

    */
/**
     * 通知父标签测量的宽高
     *
     * @param view
     *//*

    private void measureHeadView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = View.MeasureSpec.makeMeasureSpec(tempHeight, View.MeasureSpec.EXACTLY);
        } else {
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    */
/**
     * init point
     *//*

    private void initPoint() {
        imageViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < 3; i++) {
            imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.indicator);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            ll_point.addView(imageView, layoutParams);
            imageViews.add(imageView);
        }
    }

    */
/**
     * childview
     * 初始化轮播图片
     *//*

    private void initChildView(ViewFlipper flipper) {
        views = new ArrayList<View>();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), image_id[i]);
            Bitmap bitmap2 = getBitmap(bitmap, windows_width);
            //拿到图片显示的高度
            frameheight = bitmap2.getHeight();
            imageView.setImageBitmap(bitmap2);
            flipper.addView(imageView, layoutParams);
            views.add(imageView);
        }
        initPoint();
    }

    */
/**
     * 更新新闻头部图片,以及提示info
     *
     * @param drawable
     *//*

    public void updateHeadImageViews(Drawable drawable) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView imageView1 = new ImageView(context);
        imageView1.setScaleType(ImageView.ScaleType.FIT_START);
        imageView1.setImageDrawable(drawable);
        draw_point(0);
        frameheight = imageView1.getHeight();
        flipper.removeViewAt(0);
        flipper.addView(imageView1, layoutParams);
        views.remove(0);
        views.add(imageView1);
    }

    */
/**
     * 图片处理，保证图片不会变形
     *
     * @param bitmap
     * @param width
     * @return
     *//*


    public Bitmap getBitmap(Bitmap bitmap, int width) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) width / w;
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

    }



    */
/**
     * 初始化headimg,headimage包括 一个 framelayout 一个viewflipper 一个linerlayout,其中framelayout用来布局
     * viewflipper用来承载图片，用linerlayout来布局半透明标题。还有那几个点。。
     *//*


    public void initHeadImage(final Context context) {

        LayoutInflater newsheadimage = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        headView = newsheadimage.inflate(R.layout.newsheadimage, null);

        flipper = (ViewFlipper) headView.findViewById(R.id.viewflipper);
        ll_point = (LinearLayout) headView.findViewById(R.id.ll_point);
        frameLayout = (FrameLayout) headView.findViewById(R.id.news_head_fl_main);
        newsHeadImageInfo = (TextView) headView.findViewById(R.id.news_head_info);
        initChildView(flipper);
        measureHeadView(headView);
        ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();

        layoutParams.height = frameheight;
        frameLayout.setLayoutParams(layoutParams);
        draw_point(0);
        this.addHeaderView(headView, null, true);
        this.setHeaderDividersEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int position = pointToPosition(x, y);
        // 只有headview才进行手势操作.
        if (position == 0) {
            // 注入手势
            mGestureDetector.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        super.dispatchTouchEvent(ev);
        return true;
    }
    //    int state;// 当前的状态；
//    final int NONE = 0;// 正常状态；
//    final int PULL = 1;// 提示下拉状态；
//    final int RELESE = 2;// 提示释放状态；
//    final int REFLASHING = 3;// 刷新状态；

 */
/*   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("tag", "_+_+_+_+_+_+_+__+_+_+_+" + String.valueOf(isMove));
        Log.i("tag",String.valueOf(headView.getPaddingTop()));
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int position = pointToPosition(x, y);
        this.setOnScrollListener(new listener());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startX = (int) ev.getX();
                    startY = (int) ev.getY();
                }
                isMove = false;
                break;

            case MotionEvent.ACTION_MOVE:
                onMove(ev);
//                    isMove = true;
                break;
            case MotionEvent.ACTION_UP:
                endX = (int) ev.getX();
                endY = (int) ev.getY();
                if (position == 0 && horizontalMove(startX, startX, endX, endY)) {
                    headImageChange(startX, endX);
                }else{
                    if (state == RELESE) {
                        state = REFLASHING;
                        // 加载最新数据；
                        reflashViewByState();
                        iReflashListener.onReflash();
                    } else if (state == PULL) {
                        state = NONE;
                        isRemark = false;
                        reflashViewByState();
                    }
                    if (isMove) return true;
                    isMove = false;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }*//*








}
*/
