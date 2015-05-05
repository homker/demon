package ecjtu.net.demon.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.ToastMsg;

/**
 * Created by homker on 2015/5/4.
 * 日新网新闻客户端
 */
public class CollageNificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private ArrayList<HashMap<String, String>> content = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public CollageNificationAdapter(Context context, ArrayList<HashMap<String, String>> content) {
        this.content = content;
        layoutInflater = LayoutInflater.from(context);
    }

    public ArrayList<HashMap<String, String>> getContent() {
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
            return new NormalTextViewHolder(layoutInflater.inflate(R.layout.collage_item, viewGroup, false));
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
            ((NormalTextViewHolder) normalTextViewHolder).title.setText(content.get(position).get("title"));
            ((NormalTextViewHolder) normalTextViewHolder).info.setText(content.get(position).get("info"));
            ((NormalTextViewHolder) normalTextViewHolder).click.setText(content.get(position).get("click"));
            ((NormalTextViewHolder) normalTextViewHolder).time.setText(content.get(position).get("time"));
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
        private TextView time;

        public NormalTextViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            info = (TextView) itemView.findViewById(R.id.info);
            click = (TextView) itemView.findViewById(R.id.click);
            time = (TextView) itemView.findViewById(R.id.time);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastMsg.builder.display("我被点击了啊~！", 300);
                }
            });
        }
    }
}
