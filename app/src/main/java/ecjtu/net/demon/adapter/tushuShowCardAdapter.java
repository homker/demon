package ecjtu.net.demon.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.activitys.Show_image_Activity;
import ecjtu.net.demon.activitys.Tusho_show_card_activity;

/**
 * Created by homker on 2015/5/21.
 * 日新网新闻客户端
 */
public class tushuShowCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<HashMap<String, Object>> content = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;

    public static int position = 0;

    public tushuShowCardAdapter(Context context, ArrayList<HashMap<String, Object>> content) {
        this.content = content;
        layoutInflater = LayoutInflater.from(context);
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

    public ArrayList<HashMap<String, Object>> getContent() {
        return content;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new NormalTextViewHolder(layoutInflater.inflate(R.layout.show_card_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder normalTextViewHolder, int position) {
        ((NormalTextViewHolder)normalTextViewHolder).position = position;
        ((NormalTextViewHolder) normalTextViewHolder).detail.setText((String) content.get(position).get("detail"));
        ((NormalTextViewHolder) normalTextViewHolder).image.setImageResource(R.drawable.thumb_default);
        ImageLoader.getInstance().displayImage((String) content.get(position).get("url"), ((NormalTextViewHolder) normalTextViewHolder).image, options);
    }


    @Override
    public int getItemCount() {
        return content == null ? 0 : content.size();
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    public class NormalTextViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView click;
        private TextView detail;
        private ImageView image;
        private int position;

        public NormalTextViewHolder(final View itemView) {
            super(itemView);
            detail = (TextView) itemView.findViewById(R.id.info);
            image = (ImageView) itemView.findViewById(R.id.tushuo_image);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            click = (TextView) itemView.findViewById(R.id.click);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tushuShowCardAdapter.position = position;
                    Intent intent = new Intent();
                    intent.setClass(itemView.getContext(), Show_image_Activity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
