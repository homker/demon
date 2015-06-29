package ecjtu.net.demon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.adapter.CollageNificationAdapter;
import ecjtu.net.demon.utils.ACache;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.ToastMsg;

/**
 * Created by homker on 2015/5/4.
 * 日新网新闻客户端
 */

public class CollageNificationFragment extends Fragment {
    private final static String url = "http://app.ecjtu.net/api/v1/schoolnews";
    private static final int duration = 100;
    private CollageNificationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;
    private static String lastId;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<HashMap<String, Object>> content = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.collage_nification, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.collage_nification);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.collage_nification_fresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        adapter = new CollageNificationAdapter(getActivity(),content);
        recyclerView.setAdapter(adapter);
        getcontent(url, null, true, false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getcontent(url, null, false, true);
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == adapter.getItemCount() - 1) {
                    getcontent(url, lastId, false, false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    //    private ArrayList<HashMap<String, Object>> getcontent() {
//        ArrayList<HashMap<String, Object>> content = new ArrayList<>();
//        for (int i = 0; i < 6; i++) {
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("title", "这是测试内容标题" + i);
//            hashMap.put("info", "这是测试内容标题");
//            hashMap.put("click", "111");
//            hashMap.put("time", "2015-05-04");
//            content.add(hashMap);
//        }
//        return content;
//    }
    private ArrayList<HashMap<String,Object>> getcontent(String url , final String lastId , boolean isInit, final boolean isRefresh) {

        if (lastId != null) {
            url = url + "?before=" + lastId;
        }
        final ACache tushuoListCache = ACache.get(getActivity());
        if (isInit) {
            final JSONObject cache = tushuoListCache.getAsJSONObject("CNList");
            if (cache != null) {//判断缓存是否为空
                Log.i("tag", "我们使用了缓存~！collage");
                try {
                    JSONArray array = cache.getJSONArray("articles");
                    content = jsonArray2Arraylist(array);
                    adapter.getContent().addAll(content);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(cache == null) {
                HttpAsync.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (lastId == null) {//只缓存最新的内容列表
                            tushuoListCache.remove("CNList");
                            tushuoListCache.put("CNList", response, 7 * ACache.TIME_DAY);
                        }
                        try {
                            JSONArray list = response.getJSONArray("articles");
                            content = jsonArray2Arraylist(list);
                            adapter.getContent().addAll(content);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        else {
            HttpAsync.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (lastId == null) {//只缓存最新的内容列表
                        tushuoListCache.remove("CNList");
                        tushuoListCache.put("CNList", response, 7 * ACache.TIME_DAY);
                    }
                    try {
                        JSONArray list = response.getJSONArray("articles");
                        content = jsonArray2Arraylist(list);
                        if(isRefresh) {
                            adapter.getContent().clear();
                        }
                        adapter.getContent().addAll(content);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isRefresh)
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    ToastMsg.builder.display("网络环境好像不是很好呀~！", duration);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFinish() {

                }
            });
        }
        return content;
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
                item.put("title", jsonObject.getString("title"));
                item.put("info", jsonObject.getString("info"));
                item.put("click", jsonObject.getString("click"));
                item.put("time", jsonObject.getString("created_at"));
                item.put("id",jsonObject.getString("id"));
                lastId = jsonObject.getString("created_at");
//                item.put("article_id",jsonObject.getString("article_id"));
                arrayList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

}
