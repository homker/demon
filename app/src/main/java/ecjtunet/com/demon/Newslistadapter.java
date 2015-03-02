package ecjtunet.com.demon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by homker on 2015/1/19.
 */
public class Newslistadapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> listItem;
    private LayoutInflater listContainer;


    public Newslistadapter(Context context, ArrayList<HashMap<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        ArrayList<HashMap<String, Object>> listitem = new ArrayList<>();
        HashMap<String, Object> hashMap;
//        if(!listItem.get(position).get("flag").equals("h")) {
        for (HashMap<String, Object> item : listItems) {
            if (!item.get("flag").equals("h")) {
                hashMap = item;
                listitem.add(hashMap);
            }
        }
        this.listItem = listitem;

    }

    public void onDateChange(ArrayList<HashMap<String, Object>> listItems) {
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
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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


        listItemView.image.setImageDrawable((android.graphics.drawable.Drawable) listItem.get(position).get("imageDrawable"));
        listItemView.title.setText((String) listItem.get(position).get("title"));
        listItemView.info.setText((String) listItem.get(position).get("info"));
        listItemView.articleID.setText((String) listItem.get(position).get("articleID"));

        Log.i("tag", "ok4");
        return convertView;
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
