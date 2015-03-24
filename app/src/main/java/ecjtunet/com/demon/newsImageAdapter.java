package ecjtunet.com.demon;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by homker on 2015/3/19.
 */
public class newsImageAdapter extends PagerAdapter {

    private ArrayList<ImageView> newsHeadImageViewList;

    public newsImageAdapter(ArrayList<ImageView> newsHeadImageViewList){
        this.newsHeadImageViewList = newsHeadImageViewList;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(newsHeadImageViewList.get(position));
        return newsHeadImageViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(newsHeadImageViewList.get(position));
    }

    @Override
    public int getCount() {
        return newsHeadImageViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
