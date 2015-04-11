package ecjtu.net.demon;

import android.app.Application;

/**
 * Created by homker on 2015/4/11.
 */
public class RxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToastMsg.builder.init(getApplicationContext());
    }
}
