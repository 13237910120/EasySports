package com.rayhahah.rbase;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.rayhahah.rbase.utils.useful.RLog;
import com.rayhahah.rbase.utils.useful.SPManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by a on 2017/5/27.
 */

public class BaseApplication extends Application {

    protected static BaseApplication mAppContext;


    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        SPManager.init(mAppContext);

        initLog();

        LeakCanary.install(mAppContext);
        Stetho.initializeWithDefaults(mAppContext);
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    /**
     * 初始化Log打印配置
     */
    private void initLog() {
        RLog.Builder builder = new RLog.Builder(mAppContext)
                .isLog(true) //是否开启打印
                .isLogBorder(true) //是否开启边框
                .setLogType(RLog.TYPE.E) //设置默认打印级别
                .setTag("lzh"); //设置默认打印Tag
        RLog.init(builder);
    }


}
