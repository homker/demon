package ecjtu.net.demon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.xml.sax.helpers.LocatorImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by homker on 2015/3/19.
 */
public class newsImageAdapter extends PagerAdapter {

    private ArrayList<ImageView> newsHeadImageViewList;
    private ArrayList<HashMap<String,String>> newsHeadImageViewListS;
    private Context context;
    private DisplayImageOptions options;

    public newsImageAdapter(ArrayList<ImageView> newsHeadImageViewList){
        this.newsHeadImageViewList = newsHeadImageViewList;
    }
    public newsImageAdapter(ArrayList<HashMap<String,String>> newsHeadImageViewListS,Context context){
        newsHeadImageViewList = new ArrayList<>();
        this.newsHeadImageViewListS = newsHeadImageViewListS;
        this.context = context;
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(context);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.thumb_default)
                .showImageOnFail(R.drawable.thumb_default)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = newsHeadImageViewListS.get(position).get("url");
        String id = newsHeadImageViewListS.get(position).get("id");
        ImageView imageView = null;
        if (position<newsHeadImageViewList.size()){
            if (newsHeadImageViewList.get(position) != null){
                imageView = newsHeadImageViewList.get(position);
            }
        }
        if(imageView == null){
            imageView = new ImageView(context);
        }
        imageView.setImageResource(R.drawable.thumb_default);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new slidePageClickerListener(id));
        newsHeadImageViewList.add(imageView);
        container.addView(newsHeadImageViewList.get(position));
        ImageLoader.getInstance().displayImage(url, imageView, options);
        return newsHeadImageViewList.get(position);
    }
    private class slidePageClickerListener implements View.OnClickListener {

        private String articleId;

        public slidePageClickerListener(String articleId ){
            this.articleId = articleId;
        }

        @Override
        public void onClick(View v) {
            turn2contentActivity(articleId);
        }
    }
    private void turn2contentActivity(String ArticleID) {
        String articleUrl = "http://app.ecjtu.net/api/v1/article/"+ArticleID+"/view";
        Intent intent = new Intent();
        intent.setClass(context, webview.class);
        Bundle bundle = new Bundle();

        bundle.putString("url", articleUrl);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i("position","dele"+position);
        container.removeView(newsHeadImageViewList.get(position));
        Drawable drawable = newsHeadImageViewList.get(position).getDrawable();
        if(drawable != null) {
            if(drawable instanceof BitmapDrawable){
                BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if(bitmap != null) {
                    bitmap.recycle();
                }
            }
        }
    }




    @Override
    public int getCount() {
        return newsHeadImageViewListS.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
