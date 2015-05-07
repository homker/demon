package ecjtu.net.demon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import ecjtu.net.demon.R;
import ecjtu.net.demon.adapter.CollageNificationAdapter;

/**
 * Created by homker on 2015/5/4.
 * 日新网新闻客户端
 */
public class CollageNificationFragment extends Fragment {

    private CollageNificationAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int lastVisibleItem;

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
        adapter = new CollageNificationAdapter(getActivity(), getcontent());
        recyclerView.setAdapter(adapter);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.collage_nification_fresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                adapter.getContent().clear();
                adapter.getContent().addAll(getcontent());
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == adapter.getItemCount() - 1) {
                    adapter.getContent().addAll(getcontent());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    private ArrayList<HashMap<String, Object>> getcontent() {
        ArrayList<HashMap<String, Object>> content = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("title", "这是测试内容标题" + i);
            hashMap.put("info", "这是测试内容标题");
            hashMap.put("click", "111");
            hashMap.put("time", "2015-05-04");
            content.add(hashMap);
        }
        return content;
    }

}
