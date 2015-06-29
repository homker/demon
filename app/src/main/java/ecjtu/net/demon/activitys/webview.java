package ecjtu.net.demon.activitys;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import ecjtu.net.demon.R;
import ecjtu.net.demon.fragment.comment_btn;
import ecjtu.net.demon.fragment.comment_text;
import ecjtu.net.demon.utils.ToastMsg;


public class webview extends BaseActivity {

    public String title;
    private WebView webView;
    private String url;
    private android.support.v7.app.ActionBar actionBar;
    private LinearLayout linearLayout;
    private InputMethodManager imm;
    private FragmentTransaction transaction;
    public static comment_text commentText;
    public static comment_btn commentBtn;
    public static boolean isComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        isComment = false;
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        linearLayout = (LinearLayout) findViewById(R.id.webview_layout);
//        linearLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.i("tag", "---------Touch!!!----------");
//                return false;
//            }
//        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("日新新闻");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Log.i("tag", url);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.i("tag", "webview是真的拿到了title");
                webview.this.title = title;
                //actionBar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isComment) {
                    transaction = getSupportFragmentManager().beginTransaction();
                    imm.hideSoftInputFromWindow(comment_text.commentText.getWindowToken(), 0);
                    transaction.replace(R.id.comment_layout, webview.commentBtn).commit();
                }
                return false;
            }
        });
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setDomStorageEnabled(true);
        String cachepath = getFilesDir().getAbsolutePath()+"/ws/";
        ws.setAppCachePath(cachepath);
        ws.setAppCacheMaxSize(8 * 1024 * 1024);
        ws.setDatabasePath(cachepath);
        ws.setAppCacheEnabled(true);
        ws.setDatabaseEnabled(true);
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
//                if(errorCode == 404){
                view.loadUrl("file:///android_asset/404/404.htm");
//                }
            }
        });
        transaction = getSupportFragmentManager().beginTransaction();
        webView.setDownloadListener(new myDownLoad());
        commentBtn = new comment_btn();
        String commentUrl = url.substring(0,url.length()- 5);
        commentUrl = commentUrl + "/comment";
        commentText = new comment_text(this,commentUrl,webView);
        transaction.add(R.id.comment_layout, commentBtn);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    public void reloads(){
        webView.reload();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

//        if (id == R.id.action_refresh) {
//            webView.reload();
//            ToastMsg.builder.display("正在刷新",300);
//            return true;
//        }
        if (id == R.id.share){
            share(url,title);
        }
        if (id == R.id.homeAsUp) {
            finish();
            return true;
        }
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(webview.this);
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

    private void share(String url, String title){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享到"));
    }


    class myDownLoad implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            // new httpThread(url);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("ActionBar", "OnKey事件");
        if(isComment){
            comment_text.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
