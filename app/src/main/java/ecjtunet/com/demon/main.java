package ecjtunet.com.demon;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ecjtunet.com.demon.view.CycleImageView;
import ecjtunet.com.demon.view.RefreshLayout;
import ecjtunet.com.demon.view.newListView;


public class main extends ActionBarActivity  {

    private static boolean isExit = false;
    private ListView newslist;
    private ViewFlipper flipper;
    private int windows_width; //屏幕的宽度
    private Newslistadapter newslistadapter;
    RefreshLayout refreshLayout = null;
    /**
     * 更新新闻内容的的句柄
     */
    private Handler getNewsData = new Handler() {
        public void handleMessage(Message message) {
            ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) message.obj;
            if (newslistadapter == null) {
                newslistadapter = new Newslistadapter(main.this, data);
                newslist.setAdapter(newslistadapter);
            } else {
                newslistadapter.onDateChange(data);
            }
            for (HashMap<String, Object> item : data) {
                if (item.get("flag").equals("h")) {
//                    newslist.setInfos((String) item.get("title"), (String) item.get("articleID"));
//                    newslist.updateHeadImageViews((Drawable) item.get("imageDrawable"));
                }
            }

        }
    };
    private SlidingMenu sm;
    private TextView StudentID;
    private String studentID;
    private TextView userNameView;
    private CycleImageView headIamgeView;
    private String userName;
    private String headImage;
    private String url = "http://homker.sinaapp.com/app.php";
    /**
     * 更新头像的线程句柄
     */
    private Handler updateHeadImage = new Handler() {
        public void handleMessage(Message message) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) message.obj;
            Drawable drawable = (Drawable) hashMap.get("drawable");
            ImageView imageView = (ImageView) hashMap.get("imageView");
            imageView.setImageDrawable(drawable);
            refreshLayout.setLoading(false);
        }
    };

    /**
     * init view
     * 点击事件监听
     */
    private void initView() {
        newslist = (ListView) findViewById(R.id.newslist);
//        newslist.setContext(this);
//        newslist.setWindows_width(windows_width);
//        newslist.initHeadImage(this);

        flipper = (ViewFlipper) findViewById(R.id.viewflipper);
        newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id > 0) {
                    TextView articleIDText = (TextView) view.findViewById(R.id.articleID);
                    String articleID = (String) articleIDText.getText();
                    turn2contentActivity(articleID);
//                    Toast.makeText(main.this,"听说你被戳了"+articleID,Toast.LENGTH_SHORT).show();
                } else {
                    int index = newListView.getPageIndex(flipper.getCurrentView());
                    String articleID = newListView.getArticleID(index);
                    turn2contentActivity(articleID);
//                    Toast.makeText(main.this,"image"+articleID,Toast.LENGTH_SHORT).show();
                }
            }
        });
        //初始化listView
        new getNewsList(url).start();
    }

    /**
     * 初始化silidingmenu
     */



    private void initSildingmenu() {
        sm = new SlidingMenu(main.this.getBaseContext());
        sm.setBehindOffsetRes(R.dimen.sling_margin_main);
        sm.setFadeEnabled(false);
        sm.setMode(SlidingMenu.LEFT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        sm.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        sm.setMenu(R.layout.left_menu);
        StudentID = (TextView) findViewById(R.id.UserID);
        StudentID.setText(studentID);
        userNameView = (TextView) findViewById(R.id.UserName);
        userNameView.setText(userName);
        headIamgeView = (CycleImageView) findViewById(R.id.UserImage);
        new updateImageThread(headIamgeView, headImage).start();
        sm.showContent();
    }

    /**
     * 下拉刷新的回调函数
     */

    private void turn2contentActivity(String ArticleID) {
        Intent intent = new Intent();
        intent.setClass(main.this, newscontent.class);
        Bundle bundle = new Bundle();
        bundle.putString("articleID", ArticleID);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreUtil.initSharedPreference(getApplicationContext());
        setContentView(R.layout.activity_main);
        UserEntity userEntity = SharedPreUtil.getInstance().getUser();
        if (!TextUtils.isEmpty(userEntity.getUserName())) {
            studentID = userEntity.getStudentID();
            userName = userEntity.getUserName();
            headImage = userEntity.getHeadImage();
        }

        refreshLayout = (RefreshLayout) findViewById(R.id.fresh_layout);
        newslist = (ListView) findViewById(R.id.newslist);
        setActionBarLayout(R.layout.action_bar);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windows_width = displayMetrics.widthPixels;
        initView();
        initSildingmenu();
        initReflash(refreshLayout);
    }

    private void initReflash(final RefreshLayout refreshLayout){
        refreshLayout.setColorSchemeColors(R.color.link_text_material_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(main.this,"加载中...",Toast.LENGTH_SHORT).show();
                new getNewsList(url).start();
                refreshLayout.setLoading(false);
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                new getNewsList(url).start();
                refreshLayout.setLoading(false);
            }
        });
    }

    public void toggleMenu(View view) {
        sm.toggle();
    }

    public void slidingMenuClickListen(View view) {
        Intent intent = new Intent();
        intent.setClass(main.this, webview.class);
        Bundle bundle = new Bundle();
        String url = null;
        Log.i("tag", String.valueOf(view.getId()));
        switch (view.getId()) {
            case R.id.score:
                url = "http://score.ecjtu.net/";
                break;
            case R.id.classquery:
                url = "http://class.ecjtu.net/wClass.php?class=" + studentID;
                break;
            case R.id.scran:
                turn2capturActivity();
                break;
            default:
                Toast.makeText(main.this, "什么鬼还在开发", Toast.LENGTH_SHORT).show();
            /*case R.id.yktquery:;break;
            case R.id.bookquery:;break;
            case R.id.moonModel:;break;
            case R.id.setting:;break;
            */
        }
        if (url != null) {
            bundle.putString("url", url);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    private void turn2capturActivity() {
        Intent intent = new Intent();
        intent.setClass(main.this, CaptureActivity.class);
        startActivity(intent);
    }

    public void rightClick(View view) {
        Toast.makeText(main.this, "我还不知道这是什么鬼", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("touch", "touched");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i("touch", "nidi");
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化actionbar的布局
     *
     * @param layoutID
     */
    public void setActionBarLayout(int layoutID) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        Log.i("tag", "2" + String.valueOf(actionBar));
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layoutID, null);
            android.support.v7.app.ActionBar.LayoutParams layoutParams = new android.support.v7.app.ActionBar.LayoutParams(
                    android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                    android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(view, layoutParams);
        }

    }

/*    */

    /**
     * 测试用
     *
     * @return
     *//*dsa
    private ArrayList<HashMap<String, Object>> getDate(){
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object>map = new HashMap<String, Object>();
        for (int i = 0; i <10 ; i++ ){
        map.put("title","first title");
        map.put("info","it's a simple info show");
        map.put("image",R.drawable.least_image);
        list.add(map);
        }
        return list;
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitInBack2();
        }
        return false;
    }

    private void exitInBack2() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Toast.makeText(main.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 更新图片的句柄
     */
    private class updateImageThread extends Thread {

        private ImageView imageView;
        private String imageUrl;

        public updateImageThread(ImageView imageView, String url) {
            this.imageView = imageView;
            this.imageUrl = url;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(imageUrl);
                Drawable ImageDrawable = Drawable.createFromStream(url.openStream(), "image");
                Message message = updateHeadImage.obtainMessage();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("imageView", imageView);
                hashMap.put("drawable", ImageDrawable);
                message.obj = hashMap;
                updateHeadImage.sendMessage(message);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class getNewsList extends Thread {

        private String url;

        public getNewsList(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            Log.i("tag", "getnews thread works");
            Message message = getNewsData.obtainMessage();
            HttpHelper httpHelper = new HttpHelper(url);
            message.obj = httpHelper.getNewsList();
            getNewsData.sendMessage(message);
        }
    }
}
