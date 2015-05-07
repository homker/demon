package ecjtu.net.demon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;

import ecjtu.net.demon.utils.HttpAsync;

/*
* download Service
* */
public class DownloadService extends Service {

    private static final int duration = 300;
    private static final String apkUrl = "http://app.ecjtu.net/download";
    private static final String rxApk = "rixin.apk";
    private final static String VersionUrl = "http://app.ecjtu.net/api/v1/version";
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private String md5;
    private String mSavePath = null;

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        md5 = intent.getStringExtra("md5");
        return new MyBinder();
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.i("tag", "the download service is work");
        super.onCreate();
        initNotification();
        DownLoadApk();
    }

    private void initNotification() {
        Log.i("tag", "更新开始");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("更新中")//设置通知栏标题
                .setContentText("正在下载。。。")//设置通知栏显示内容
                .setTicker("开始更新") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_LIGHTS)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.logo_cycle)
                .setProgress(100, 0, false);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotificationManager.notify(1, notification);
        //版本更新

    }

    private void DownLoadApk() {
        HttpAsync.get(apkUrl, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onStart() {
                Log.i("tag", "下载开始");
            }

            @Override
            public void onFailure(int i, Header[] headers, Throwable throwable, File file) {
                mBuilder.setContentText("更新失败");
                mNotificationManager.notify(1, mBuilder.build());
                sendDownLoadBroadcast(false);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                int progress = (int) (((float) bytesWritten / totalSize) * 100);
                mBuilder.setProgress(100, progress, false);
                mNotificationManager.notify(1, mBuilder.build());
            }

            @Override
            public void onSuccess(int i, Header[] headers, File file) {
                String sdpath = Environment.getExternalStorageDirectory() + "/";
                mSavePath = sdpath + "download";
                File apkfile = new File(mSavePath, rxApk);
                file.renameTo(apkfile);
                //安装文件
                mBuilder.setContentText("更新成功~！");
                mBuilder.setProgress(0, 0, false);
                mNotificationManager.notify(1, mBuilder.build());
                sendDownLoadBroadcast(true);
            }
        });
    }


    protected void sendDownLoadBroadcast(Boolean isDownLoad) {
        Intent intent = new Intent();
        intent.setAction("ecjtu.net.demon.DownLoadService.isDownLoad");
        intent.putExtra("isDownLoad", isDownLoad);
        intent.putExtra("md5", md5);
        sendBroadcast(intent);
    }



    public class MyBinder extends Binder {

        public DownloadService getDownLoadSercice() {
            return DownloadService.this;
        }

    }


}
