package ecjtu.net.demon;

import android.content.Context;
import android.support.v4.view.ViewPager;
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
    private ArrayList<HashMap<String, Object>> listItem;// 列表正文的的arraylist
    private ArrayList<HashMap<String,Object>> slide_articles;// 轮转图的arralist
    private LayoutInflater listContainer;
    private View topView;
    private rxViewPager myViewPager;
    private LinearLayout myPointView;//pointView 的容器
    private ArrayList<ImageView> myTopView; //顶部ViewPager image list
    private ArrayList<ImageView> points;//标识点的list
    private TextView info;


    public Newslistadapter(Context context, HashMap<String, Object> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        //ArrayList<HashMap<String, Object>> listitem = new ArrayList<>();
        myTopView = new ArrayList<ImageView>();
        /*HashMap<String, Object> hashMap;
        for (HashMap<String, Object> item : listItems) {
            if (!item.get("flag").equals("h")) {
                hashMap = item;
                listitem.add(hashMap);
            }
        }*/
        this.listItem = (ArrayList<HashMap<String, Object>>) listItems.get("normal_articles");
        this.slide_articles = (ArrayList<HashMap<String, Object>>) listItems.get("slide_articles");
    }

    private void getNewsList()
    {

    }



    public void onDateChange(HashMap<String, Object> listItems) {
        this.listItem.addAll((ArrayList<HashMap<String, Object>>) listItems.get("normal_articles"));
        this.slide_articles =  (ArrayList<HashMap<String, Object>>) listItems.get("slide_articles");
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItem.size() + 1;
    }


    @Override
    public Object getItem(int position) {
        return listItem.get(position);
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


        listItemView.image.setImageDrawable((android.graphics.drawable.Drawable) listItem.get(position - 1).get("thumb"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150 ,100);
        listItemView.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        listItemView.image.setLayoutParams(layoutParams);
        listItemView.title.setText((String) listItem.get(position - 1).get("title"));
        listItemView.title.setTextSize(20);
        listItemView.title.setPadding(0,5,0,0);
        listItemView.info.setText((String) listItem.get(position - 1).get("info"));
         listItemView.articleID.setText((String) listItem.get(position - 1).get("id"));

        return convertView;
    }

    private View getTopView(View convertView){
        if (topView == null){
            topView = LayoutInflater.from(context).inflate(R.layout.newsheadimage, null);
            int height = (int) context.getResources().getDimension(R.dimen.news_content_image_height);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            lp.gravity = Gravity.TOP;
            myViewPager = (rxViewPager) topView.findViewById(R.id.news_viewPager);
            info = (TextView) topView.findViewById(R.id.news_info);
            myViewPager.setLayoutParams(lp);

            //得到pointView 的容器

            myPointView = (LinearLayout) topView.findViewById(R.id.point_view);
            ImageView imageView;
            for(int i = 0; i<slide_articles.size();i++){
                imageView = new ImageView(context);
                imageView.setImageDrawable((android.graphics.drawable.Drawable) slide_articles.get(i).get("thumb"));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                String id = (String) slide_articles.get(i).get("id");
                imageView.setOnClickListener(new slidePageClickerListener(id));
                myTopView.add(imageView);
            }

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

    private class slidePageClickerListener implements View.OnClickListener {

        private String articleId;

        public slidePageClickerListener(String articleId ){
            this.articleId = articleId;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context,"this is been click on"+ articleId,Toast.LENGTH_SHORT).show();
        }
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
        HashMap<String,Object> hashMap = slide_articles.get(position);
        info.setText((String)hashMap.get("title"));
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
