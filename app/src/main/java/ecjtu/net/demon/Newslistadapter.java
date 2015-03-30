package ecjtu.net.demon;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.view.rxViewPager;

/**
 * Created by homker on 2015/1/19.
 */
public class Newslistadapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> listItem;
    private LayoutInflater listContainer;
    private View topView;
    private rxViewPager myViewPager;
    private LinearLayout myPointView;//pointView 的容器
    private ArrayList<ImageView> myTopView; //顶部ViewPager image list
    private ArrayList<ImageView> points;//标识点的list


    public Newslistadapter(Context context, ArrayList<HashMap<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        ArrayList<HashMap<String, Object>> listitem = new ArrayList<>();
        myTopView = new ArrayList<ImageView>();
        HashMap<String, Object> hashMap;
        for (HashMap<String, Object> item : listItems) {
            if (!item.get("flag").equals("h")) {
                hashMap = item;
                listitem.add(hashMap);
            }
        }
        this.listItem = listitem;
        Log.i("tag","the list.size()" + listitem.size());
    }

    private void getNewsList()
    {

    }



    public void onDateChange(ArrayList<HashMap<String, Object>> listItems) {
        Log.i("tag","onDAteChange has been work");
        ArrayList<HashMap<String, Object>> listitem = new ArrayList<>();
        HashMap<String, Object> hashMap;
        for (HashMap<String, Object> item : listItems) {
            if (!item.get("flag").equals("h")) {
                hashMap = item;
                listitem.add(hashMap);
            }
        }
        this.listItem = listitem;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItem.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position == 0 ){
            return 0;
        }else{
            return position -1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0){
            Log.i("tag","we do it works");
            return getTopView(convertView);
        }else{
            return getItemView(position,convertView);
        }
    }

    private View getItemView(int position, View convertView){
        ListItemView listItemView = null;

        if (convertView == null) {
            listItemView = new ListItemView();

            convertView = listContainer.inflate(R.layout.news, null);
            listItemView.image = (ImageView) convertView.findViewById(R.id.news_image);
            listItemView.title = (TextView) convertView.findViewById(R.id.news_title);
            listItemView.info = (TextView) convertView.findViewById(R.id.news_info);
            listItemView.articleID = (TextView) convertView.findViewById(R.id.articleID);
            convertView.setTag(listItemView);
        } else listItemView = (ListItemView) convertView.getTag();


        listItemView.image.setImageDrawable((android.graphics.drawable.Drawable) listItem.get(position - 1).get("imageDrawable"));
        listItemView.title.setText((String) listItem.get(position - 1).get("title"));
        listItemView.info.setText((String) listItem.get(position - 1).get("info"));
        listItemView.articleID.setText((String) listItem.get(position - 1).get("articleID"));

        return convertView;
    }

    private View getTopView(View convertView){
        if (topView == null){
            topView = LayoutInflater.from(context).inflate(R.layout.newsheadimage, null);
            int height = (int) context.getResources().getDimension(R.dimen.news_content_image_height);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            lp.gravity = Gravity.TOP;
            myViewPager = (rxViewPager) topView.findViewById(R.id.news_viewPager);
            myViewPager.setLayoutParams(lp);

            //得到pointView 的容器

            myPointView = (LinearLayout) topView.findViewById(R.id.point_view);


            ImageView leftimageView = new ImageView(context);
            leftimageView.setBackgroundColor(context.getResources().getColor(R.color.white));
            leftimageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            leftimageView.setImageResource(R.drawable.a);
            leftimageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"it has be click",Toast.LENGTH_SHORT).show();
                }
            });


            ImageView rightimageView = new ImageView(context);
            rightimageView.setBackgroundColor(context.getResources().getColor(R.color.white));
            rightimageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            rightimageView.setImageResource(R.drawable.b);
            myTopView.add(leftimageView);
            myTopView.add(rightimageView);
            ecjtu.net.demon.newsImageAdapter newsImageAdapter = new newsImageAdapter(myTopView);
            myViewPager.setAdapter(newsImageAdapter);
            myViewPager.setCurrentItem(0);
            myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    draw_point(position);
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            initPoint();//初始化pointView
        }
        return topView;
    }


    private void initPoint(){
        points = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0 ; i < myTopView.size();i++){
            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.indicator);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            myPointView.addView(imageView,layoutParams);
            points.add(imageView);
        }
        draw_point(0);
    }

    private void draw_point(int position){
        for (int i = 0; i<myTopView.size();i++){
            points.get(i).setImageResource(R.drawable.indicator);
        }
        points.get(position).setImageResource(R.drawable.indicator_focused);
    }

    /**
     * 新建一个viewhoder
     */
    public final class ListItemView {
        public ImageView image;
        public TextView title;
        public TextView info;
        public TextView articleID;
    }

}
