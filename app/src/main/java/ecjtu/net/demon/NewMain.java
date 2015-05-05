package ecjtu.net.demon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import ecjtu.net.demon.adapter.MainAdapter;
import ecjtu.net.demon.fragment.CollageNificationFragment;
import ecjtu.net.demon.fragment.MainFragment;
import ecjtu.net.demon.fragment.TushuoFragment;
import ecjtu.net.demon.view.SlidingTabLayout;


public class NewMain extends ActionBarActivity {

    private static final int duration = 300;
    private String studentID;
    private String userName;
    private String headImage;
    private SlidingTabLayout tab;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        View main_view = LayoutInflater.from(this).inflate(R.layout.drawlayout, null);
        main_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        setContentView(main_view);
        //出事化用户信息
        initUserInfo();
        //使用toolbar代替actionbar
        initActionBar();
        //initMainFrament();
        initViewPager();

//        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,test);
//        listView.setAdapter(arrayAdapter);

    }


    private void initViewPager() {
        tab = (SlidingTabLayout) findViewById(R.id.tab);
        pager = (ViewPager) findViewById(R.id.pager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new CollageNificationFragment());
        fragments.add(new TushuoFragment());
        MainAdapter mainAdapter = new MainAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(mainAdapter);
        pager.setOffscreenPageLimit(fragments.size());
        tab.setCustomTabView(R.layout.tab_style, 0);
       /* tab.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
            }

            @Override
            public int getDividerColor(int position) {
                return 0;
            }
        });*/
        tab.setViewPager(pager);
    }


    private void initUserInfo() {
        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(userEntity.getStudentID())) {
            studentID = userEntity.getStudentID();
            userName = userEntity.getUserName();
            headImage = userEntity.getHeadImage();
        }
    }

    public void slidingMenuClickListen(View view) {
        String url = null;
        if (studentID != null) {
            switch (view.getId()) {
                case R.id.score:
                    url = "http://score.ecjtu.net/";
                    break;
                case R.id.classquery:
                    url = "http://class.ecjtu.net/wClass.php?class=" + studentID;
                    break;
                case R.id.scran:
                    turn2ActivityWithUrl(CaptureActivity.class, null);
                    break;
                case R.id.setting:
                    turn2ActivityWithUrl(Setting.class, null);
                    break;
                default:
                    ToastMsg.builder.display("开发中...", duration);
                    //Toast.makeText(main.this, "开发中。。。", Toast.LENGTH_SHORT).show();
                /*case R.id.yktquery:;break;
                case R.id.bookquery:;break;
                case R.id.moonModel:;break;
                case R.id.setting:;break;
                */
            }
            if (url != null) {
                turn2ActivityWithUrl(webview.class, url);
            }
        } else {
            turn2ActivityWithUrl(LoginActivity.class, null);
            ToastMsg.builder.display("请先行登入", duration);
            //Toast.makeText(main.this, "请先行登入", Toast.LENGTH_SHORT).show();
        }
    }

    private void turn2ActivityWithUrl(Class activity, String url) {
        Intent intent = new Intent();
        intent.setClass(NewMain.this, activity);
        if (url != null) {
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    private void initActionBar() {
        setContentView(R.layout.activity_new_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.DrawLayout);
        toolbar.setTitle("首页");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.tool_bar_open, R.string.tool_bar_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_main, menu);
        return true;
    }

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

        return super.onOptionsItemSelected(item);
    }
}
