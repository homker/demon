/*
package ecjtunet.com.demon.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ecjtunet.com.demon.R;

*/
/**
 * Created by homker on 2015/3/17.
 *//*

public class newsListView extends ListView{

    private View myHeadView;
    private rxViewPager viewPager;
    private LinearLayout point;
    private TextView newsImageInfo;
    private FrameLayout frameLayout;
    private List<ImageView> newsHeadImageList = new ArrayList<ImageView>();
    private int[] myIds = new int[]{R.drawable.a,R.drawable.b,R.drawable.c};
    private Context context;


    public newsListView(Context context) {
        this(context,null);
    }

    public newsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public newsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void initHeadView(final Context context){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myHeadView = layoutInflater.inflate(R.layout.newsheadimage, null);
        point = (LinearLayout) myHeadView.findViewById(R.id.ll_point);
        viewPager = (rxViewPager) myHeadView.findViewById(R.id.news_viewPager);
        newsImageInfo = (TextView) myHeadView.findViewById(R.id.news_head_info);
        frameLayout = (FrameLayout) myHeadView.findViewById(R.id.news_head_fl_main);
        Log.i("tag","---------------------------->"+String.valueOf(viewPager));

        ViewGroup.LayoutParams layoutParams = frameLayout.getLayoutParams();
        layoutParams.height = R.dimen.news_content_image_height;
        frameLayout.setLayoutParams(layoutParams);

        initViewPager(viewPager);
        measureHeadView(myHeadView);
        this.addHeaderView(viewPager,null,true);
        this.setHeaderDividersEnabled(true);
    }

    private void initViewPager(ViewPager viewPager){
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(myIds[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                newsHeadImageList.add(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(newsHeadImageList.get(position));
            }

            @Override
            public int getCount() {
                return myIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }

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
}
*/
