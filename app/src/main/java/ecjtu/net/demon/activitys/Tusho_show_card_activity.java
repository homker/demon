package ecjtu.net.demon.activitys;

import android.annotation.TargetApi;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.LayoutManager.FullyLinearLayoutManager;
import ecjtu.net.demon.R;
import ecjtu.net.demon.adapter.TushuoAdapter;
import ecjtu.net.demon.adapter.TushuoShowCardAdapter;
import ecjtu.net.demon.adapter.tushuShowCardAdapter;
import ecjtu.net.demon.fragment.Show_image_ActivityFragment;
import ecjtu.net.demon.fragment.TushuoFragment;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.ToastMsg;
import ecjtu.net.demon.view.pulltozoomview.PullToZoomScrollViewEx;

public class Tusho_show_card_activity extends BaseActivity {

    private static final String url = "http://pic.ecjtu.net/api.php/post";
    private static final int duration = 100;
    private PullToZoomScrollViewEx scrollView;
    private RecyclerView recyclerView;
    private tushuShowCardAdapter adapeter;
    private FullyLinearLayoutManager linearLayoutManager;
    ArrayList<HashMap<String, Object>> content = new ArrayList<>();
    private static String pid;
    public static ArrayList<String> urlList = new ArrayList<>();

    public static void setPid(String pid) {
        Tusho_show_card_activity.pid = pid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tusho_show_card_activity);
        initActionBar();
        loadViewForCode();
        recyclerView = (RecyclerView) scrollView.getPullRootView().findViewById(R.id.profile_show_card_recyclerview);
        linearLayoutManager = new FullyLinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        getContent(url);
        adapeter =  new tushuShowCardAdapter(Tusho_show_card_activity.this,content);
        recyclerView.setAdapter(adapeter);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

    }


    private ArrayList<HashMap<String, Object>> getContent(String url) {
        urlList.clear();
            url = url + "/" + pid;
            HttpAsync.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        ((TextView) headView.findViewById(R.id.title)).setText(response.get("title").toString());
                        ((TextView) headView.findViewById(R.id.author)).setText(response.get("author").toString());
                        ((TextView) headView.findViewById(R.id.count)).setText(response.get("count").toString());
                        ((TextView) headView.findViewById(R.id.click)).setText(response.get("click").toString());
                        JSONArray jsonArray = response.getJSONArray("pictures");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, Object> item = new HashMap<>();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String url = "http://pic.ecjtu.net/" + jsonObject.getString("url");
                            urlList.add(i, url);
                            item.put("url", url);
                            item.put("detail", jsonObject.getString("detail"));
                            content.add(item);
                        }
                        adapeter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    ToastMsg.builder.display("加载失败，请稍后重试。", duration);
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                }
            });
        return content;
    }

    private void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("图集");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tusho_show_card_activity, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                Log.i("tag", "nihao" + String.valueOf(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private View headView;
    private View zoomView;
    private View contentView;

    private void loadViewForCode() {
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);
        headView = LayoutInflater.from(this).inflate(R.layout.profile_head_view, null, false);
        zoomView = LayoutInflater.from(this).inflate(R.layout.profile_zoom_view, null, false);
        contentView = LayoutInflater.from(this).inflate(R.layout.profile_content_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
    }

}
