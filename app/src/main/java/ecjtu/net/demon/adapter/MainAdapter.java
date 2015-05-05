package ecjtu.net.demon.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * Created by homker on 2015/5/3.
 * 日新网新闻客户端
 */
public class MainAdapter extends FragmentPagerAdapter {

    private String[] titles = {"日新新闻", "学院专题", "社团动态"};
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private FragmentTransaction mCurTransaction = null;
    private FragmentManager mFragmentManager;

    public MainAdapter(FragmentManager fragmentManager, ArrayList<Fragment> fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
