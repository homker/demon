package ecjtu.net.demon.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.activitys.Show_image_Activity;

/**
 * Created by homker on 2015/5/21.
 * 日新网新闻客户端
 */
public class tushuShowCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<HashMap<String, Object>> content = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private DisplayImageOptions options;

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
        return this.content;
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
        if (viewType == TYPE_ITEM) {
            return new NormalTextViewHolder(layoutInflater.inflate(R.layout.show_card_item, viewGroup, false));
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_footer, null);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder normalTextViewHolder, int position) {
        if (normalTextViewHolder instanceof NormalTextViewHolder) {
            ((NormalTextViewHolder) normalTextViewHolder).title.setText((String) content.get(position).get("title"));
            ((NormalTextViewHolder) normalTextViewHolder).info.setText((String) content.get(position).get("info"));
            ((NormalTextViewHolder) normalTextViewHolder).click.setText((String) content.get(position).get("click"));
            ((NormalTextViewHolder) normalTextViewHolder).image.setImageResource(R.drawable.thumb_default);
            ImageLoader.getInstance().displayImage((String) content.get(position).get("image"), ((NormalTextViewHolder) normalTextViewHolder).image, options);

        }
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
        private TextView info;
        private TextView click;
        private ImageView image;

        public NormalTextViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            info = (TextView) itemView.findViewById(R.id.info);
            click = (TextView) itemView.findViewById(R.id.click);
            image = (ImageView) itemView.findViewById(R.id.tushuo_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(itemView.getContext(), Show_image_Activity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
