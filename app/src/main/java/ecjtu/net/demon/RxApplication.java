package ecjtu.net.demon;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;
import ecjtu.net.demon.utils.SharedPreUtil;
import ecjtu.net.demon.utils.ToastMsg;

/**
 * Created by homker on 2015/4/11.
 */
public class RxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreUtil.initSharedPreference(getApplicationContext());
        ToastMsg.builder.init(getApplicationContext());
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }
}
