package ecjtu.net.demon;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class webview extends Activity {

    private WebView webView;
    public String title;
    private String url;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        webView = (WebView) findViewById(R.id.webView);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Log.i("tag", url);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                webview.this.title = title;
                actionBar.setTitle(title);
                //super.onReceivedTitle(view, title);
            }
        });
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        ws.setDomStorageEnabled(true);
        ws.setDatabaseEnabled(true);
        String cachepath = getFilesDir().getAbsolutePath()+"/ws/";
        ws.setAppCachePath(cachepath);
        ws.setAppCacheMaxSize(8*1024*1024);
        ws.setDatabasePath(cachepath);
        ws.setAppCacheEnabled(true);
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

        webView.setDownloadListener(new myDownLoad());


//        title = "你妹妹的点点";
//
//        actionBar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
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
        if (id == R.id.action_refresh) {
            webView.reload();
            ToastMsg.builder.display("正在刷新",300);
            return true;
        }
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
}
