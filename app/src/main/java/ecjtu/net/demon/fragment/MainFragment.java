package ecjtu.net.demon.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.activitys.webview;
import ecjtu.net.demon.adapter.Newslistadapter;
import ecjtu.net.demon.utils.ACache;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.ToastMsg;
import ecjtu.net.demon.view.RefreshLayout;

public class MainFragment extends Fragment {

    private static final int duration = 100;
    private final static String url = "http://app.ecjtu.net/api/v1/index";
    private TextView upToLoad;
    private Newslistadapter newslistadapter;
    private ListView newslist;
    private RefreshLayout refreshLayout = null;
    private HashMap<String, Object> content;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        newslist = (ListView) getView().findViewById(R.id.newslist);
        //初始化listView
        refreshLayout = (RefreshLayout) getView().findViewById(R.id.fresh_layout);

        upToLoad = new TextView(getActivity());
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
                turn2Activity(webview.class, articleUrl);
            }
        });
        setNewslist(url, null, true);
        initReflash(refreshLayout);
    }

    private void initReflash(final RefreshLayout refreshLayout) {
        refreshLayout.setmListView(newslist);
        refreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newslistadapter.getListItem().clear();
                newslistadapter.getSlide_articles().clear();
                setNewslist(url, null, false);
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                newslist.removeFooterView(upToLoad);
                Log.i("tag", "the count is" + newslist.getCount());
                HashMap<String, Object> hashMap = (HashMap<String, Object>) newslist.getAdapter().getItem((newslist.getCount() - 3));
                String articleId = String.valueOf(hashMap.get("id"));
                Log.i("tag", "the articleId is " + articleId);
                setNewslist(url, articleId, false);
            }
        });
    }

    private void turn2Activity(Class activity, String url) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), activity);
        if (url != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private void setNewslist(String url, final String lastId, Boolean isInit) {
        final HashMap<String, Object> list = new HashMap<>();
        if (lastId != null) {
            url = url + "?until=" + lastId;
        }
        Log.i("tag", "请求链接：" + url);
        final ACache newsListCache = ACache.get(getActivity());
        if (isInit) {
            final JSONObject cache = newsListCache.getAsJSONObject("newsList");
            if (cache != null) {//判断缓存是否为空
                Log.i("tag", "我们使用了缓存~！");
                try {
                    JSONObject slide_article = cache.getJSONObject("slide_article");
                    JSONArray slide_articles = slide_article.getJSONArray("articles");
                    JSONObject normal_article = cache.getJSONObject("normal_article");
                    JSONArray normal_articles = normal_article.getJSONArray("articles");
                    list.put("slide_articles", jsonArray2Arraylist(slide_articles));
                    list.put("normal_articles", jsonArray2Arraylist(normal_articles));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                newslistadapter = new Newslistadapter(getActivity(), list);
                newslist.setAdapter(newslistadapter);
            }
            if (cache == null) {
                HttpAsync.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        ToastMsg.builder.display("正在加载...", duration);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (lastId == null) {//只缓存最新的内容列表
                            newsListCache.remove("newsList");
                            newsListCache.put("newsList", response, 7 * ACache.TIME_DAY);
                        }
                        try {
                            JSONObject slide_article = response.getJSONObject("slide_article");
                            JSONArray slide_articles = slide_article.getJSONArray("articles");
                            JSONObject normal_article = response.getJSONObject("normal_article");
                            JSONArray normal_articles = normal_article.getJSONArray("articles");
                            list.put("slide_articles", jsonArray2Arraylist(slide_articles));
                            list.put("normal_articles", jsonArray2Arraylist(normal_articles));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("tag", "更新线程执行成功");
                        if (newslistadapter != null) {
                            Log.i("tag", "list is " + String.valueOf(list));
                            newslistadapter.onDateChange(list);
                        } else {
                            newslistadapter = new Newslistadapter(getActivity(), list);
                            newslist.setAdapter(newslistadapter);
                        }
                        refreshLayout.setLoading(false);
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        ToastMsg.builder.display("网络环境好像不是很好呀~！", duration);
                    }

                    @Override
                    public void onFinish() {

                    }

                });
            }
        } else {
        HttpAsync.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                ToastMsg.builder.display("正在加载...", duration);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (lastId == null) {//只缓存最新的内容列表
                    newsListCache.remove("newsList");
                    newsListCache.put("newsList", response, 7 * ACache.TIME_DAY);
                }
                try {
                    JSONObject slide_article = response.getJSONObject("slide_article");
                    JSONArray slide_articles = slide_article.getJSONArray("articles");
                    JSONObject normal_article = response.getJSONObject("normal_article");
                    JSONArray normal_articles = normal_article.getJSONArray("articles");
                    list.put("slide_articles", jsonArray2Arraylist(slide_articles));
                    list.put("normal_articles", jsonArray2Arraylist(normal_articles));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("tag", "更新线程执行成功");
                if (newslistadapter != null) {
                    Log.i("tag", "list is " + String.valueOf(list));
                    newslistadapter.onDateChange(list);
                } else {
                    newslistadapter = new Newslistadapter(getActivity(), list);
                    newslist.setAdapter(newslistadapter);
                }
                refreshLayout.setLoading(false);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastMsg.builder.display("网络环境好像不是很好呀~！", duration);
            }

            @Override
            public void onFinish() {

            }

        });
    }
    }

    /**
     * 将json数组变成arraylist
     *
     * @param jsonArray 输入你转换的jsonArray
     * @return 返回arraylist
     */
    private ArrayList<HashMap<String, Object>> jsonArray2Arraylist(JSONArray jsonArray) {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, Object> item = new HashMap<>();
                item.put("id", jsonObject.getInt("id"));
                item.put("title", jsonObject.getString("title"));
                item.put("updated_at", jsonObject.getString("updated_at"));
                item.put("info", jsonObject.getString("info"));
                item.put("click", jsonObject.getString("click"));
                String imageUrl = "http://app.ecjtu.net" + jsonObject.getString("thumb");
                item.put("thumb", imageUrl);
                arrayList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

}
