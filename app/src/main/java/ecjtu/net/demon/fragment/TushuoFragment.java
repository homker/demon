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

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.adapter.Newslistadapter;
import ecjtu.net.demon.adapter.TushuoAdapter;
import ecjtu.net.demon.utils.ACache;
import ecjtu.net.demon.utils.HttpAsync;
import ecjtu.net.demon.utils.ToastMsg;
import ecjtu.net.demon.view.RefreshLayout;

/**
 * Created by homker on 2015/5/5.
 * 日新网新闻客户端
 */
public class TushuoFragment extends Fragment {


    private static final int duration = 100;
    private ArrayList<HashMap<String, Object>> content = new ArrayList<>();
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private final static String url = "http://pic.ecjtu.net/api.php/list";
    private static String lastId;
    private TushuoAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.tushuo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.tushuo);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new TushuoAdapter(getActivity(),content);
        recyclerView.setAdapter(adapter);
        getcontent(url, null, true,false);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.tushuo_fresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getcontent(url, null, false,true);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    private ArrayList<HashMap<String,Object>> getcontent(String url , final String lastId , boolean isInit, final boolean isRefresh) {

        if (lastId != null) {
            url = url + "?before=" + lastId;
        }
        final ACache tushuoListCache = ACache.get(getActivity());
        if (isInit) {
            final JSONObject cache = tushuoListCache.getAsJSONObject("tushuoList");
            if (cache != null) {//判断缓存是否为空
                Log.i("tag", "我们使用了缓存~！tushuo");
                try {
                    JSONArray array = cache.getJSONArray("list");
                    content = jsonArray2Arraylist(array);
                    adapter.getContent().addAll(content);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(cache == null) {
                Log.i("tag", "初始化tushuo");
                HttpAsync.get(url, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        if (lastId == null) {//只缓存最新的内容列表
                            tushuoListCache.remove("tushuoList");
                            tushuoListCache.put("tushuoList", response, 7 * ACache.TIME_DAY);
                        }
                        try {
                            JSONArray list = response.getJSONArray("list");
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
                public void onStart() {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (lastId == null) {//只缓存最新的内容列表
                        tushuoListCache.remove("tushuoList");
                        tushuoListCache.put("tushuoList", response, 7 * ACache.TIME_DAY);
                    }
                    try {
                        JSONArray list = response.getJSONArray("list");
                        content = jsonArray2Arraylist(list);
                        if(isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.getContent().clear();
                        }
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
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFinish() {

                }
            });
        }

/**

            for(int i = 0;i<3;i++)

            {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("image", "http://pic.ecjtu.net/pic/201504/15/c28ace179cff6ba9196f6b734903e627.jpg");
                hashMap.put("title", "这是测试内容标题" + i);
                hashMap.put("info", "这是测试内容标题");
                hashMap.put("click", "111");
                hashMap.put("time", "2015-05-04");
                content.add(hashMap);
            }
 *
 */
            Log.i("tag", "初始化wancengtushuo");
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
                String imageUrl = "http://" + jsonObject.getString("thumb");
                item.put("image", imageUrl);
                item.put("title", jsonObject.getString("title"));
                item.put("info", jsonObject.getString("count"));
                item.put("click", jsonObject.getString("click"));
                item.put("time", TimeStamp2Date(jsonObject.getString("pubdate"),"yyyy-MM-dd"));
                lastId = jsonObject.getString("pubdate");
                item.put("pid",jsonObject.getString("pid"));
                arrayList.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
            return arrayList;
        }

    public String TimeStamp2Date(String timestampString, String formats){
        Long timestamp = Long.parseLong(timestampString)*1000;
        String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
        return date;
    }
}
