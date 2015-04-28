package ecjtu.net.demon;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by homker on 2015/4/28.
 */
public class mainFragment extends Fragment {

    private Context context;
    private TextView upToLoad;

    public mainFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView newslist = (ListView) getView().findViewById(R.id.newslist);

        upToLoad = new TextView(context);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        upToLoad.setLayoutParams(layoutParams);
        upToLoad.setGravity(Gravity.CENTER);
        upToLoad.setText("向上滑动加载更多");
        newslist.addFooterView(upToLoad);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView articleIDText = (TextView) view.findViewById(R.id.articleID);
                String articleID = (String) articleIDText.getText();
                String articleUrl = "http://app.ecjtu.net/api/v1/article/" + articleID + "/view";
                //       main.this.turn2Activity(webview.class, articleUrl);
            }
        });
        //初始化listView
        // setNewslist(url, null, true);
    }
}
