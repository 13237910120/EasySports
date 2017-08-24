package com.rayhahah.easysports.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.rayhahah.easysports.bean.CrashMessage;
import com.rayhahah.easysports.bean.db.LocalUser;
import com.rayhahah.easysports.net.ApiClient;
import com.rayhahah.easysports.sonic.SonicRuntimeImpl;
import com.rayhahah.greendao.gen.DaoMaster;
import com.rayhahah.greendao.gen.DaoSession;
import com.rayhahah.rbase.BaseApplication;
import com.rayhahah.rbase.net.OkHttpManager;
import com.rayhahah.rbase.utils.RCrashHandler;
import com.rayhahah.rbase.utils.base.DateTimeUitl;
import com.rayhahah.rbase.utils.useful.RLog;
import com.rayhahah.rbase.utils.useful.SPManager;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;

import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.OkHttpClient;

public class MyApp extends BaseApplication {


    private static DaoSession daoSession;
    private RCrashHandler.CrashUploader mCrashUploader;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onFastInit() {
        super.onFastInit();
        initSophix();
    }

    /**
     * 初始化Sophix阿里云热修复
     */
    private void initSophix() {
        String appVersion;

        try {
            appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersion = "1.0.0";
            e.printStackTrace();
        }

        // initialize最好放在attachBaseContext最前面
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Bmob.initialize(mAppContext, C.AppKey.BMOB_APPID);

//        initCrashHandler();
        initRetrofit();

        initSonic();

        initGreenDao();
    }

    /**
     * 初始化Sonic
     */
    private void initSonic() {
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(mAppContext), new SonicConfig.Builder().build());
        }
    }

    public static boolean isNightTheme() {
        String isNight = SPManager.get().getStringValue(C.SP.THEME, C.FALSE);
        if (C.FALSE.equals(isNight)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 初始化崩溃处理器
     */
    private void initCrashHandler() {

        mCrashUploader = new RCrashHandler.CrashUploader() {
            @Override
            public void uploadCrashMessage(ConcurrentHashMap<String, Object> infos) {
                CrashMessage cm = new CrashMessage();
                ConcurrentHashMap<String, String> packageInfos = (ConcurrentHashMap<String, String>) infos.get(RCrashHandler.PACKAGE_INFOS_MAP);
                cm.setDate(DateTimeUitl.getCurrentWithFormate(DateTimeUitl.sysDateFormate));
                cm.setVersionName(packageInfos.get(RCrashHandler.VERSION_NAME));
                cm.setVersionCode(packageInfos.get(RCrashHandler.VERSION_CODE));
                cm.setExceptionInfos(((String) infos.get(RCrashHandler.EXCEPETION_INFOS_STRING)));
                cm.setMemoryInfos((String) infos.get(RCrashHandler.MEMORY_INFOS_STRING));
                cm.setDeviceInfos(RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos
                        .get(RCrashHandler.BUILD_INFOS_MAP)).toString());
                cm.setSystemInfoss(RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos
                        .get(RCrashHandler.SYSTEM_INFOS_MAP)).toString());
                cm.setSecureInfos(RCrashHandler.getInfosStr((ConcurrentHashMap<String, String>) infos
                        .get(RCrashHandler.SECURE_INFOS_MAP)).toString());
                cm.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            RLog.e("上传成功！");

                        } else {
                            RLog.e("上传Bmob失败 错误码：" + e.getErrorCode());
                        }
                    }
                });
            }
        };
        RCrashHandler.getInstance(C.DIR.CRASH)
                .init(mAppContext, mCrashUploader);
    }

    /**
     * 初始化RetrofitClient
     */
    private void initRetrofit() {
        OkHttpClient okHttpClient = OkHttpManager.create();
        ApiClient.create(C.BaseURL.TECENT_VIDEO_SERVER, okHttpClient);
        ApiClient.create(C.BaseURL.TECENT_VIDEO_SERVER_H5, okHttpClient);
        ApiClient.create(C.BaseURL.TECENT_SERVER, okHttpClient);
        ApiClient.create(C.BaseURL.TMIAAO_SERVER, okHttpClient);
        ApiClient.create(C.BaseURL.HUPU_FORUM_SERVER, okHttpClient);
        ApiClient.create(C.BaseURL.HUPU_GAMES_SERVER, okHttpClient);
        ApiClient.create(C.BaseURL.HUPU_LOGIN_SERVER, okHttpClient);
    }

    /**
     * 初始化数据库信息
     */
    private void initGreenDao() {

//        @Property：使用该属性定义一个非默认的列名，该变量和该列进行映射。
// 如果缺少greendao会使用这个变量名来命名数据库的列名(全部大写，并且使用下划线来代替驼峰命名法，eg:customName会变成CUSTOM_NAME)。

        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, C.DB_EASYSPORTS, null);
        //创建数据库db"
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, C.DB_EASYSPORTS, null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取表操作类 DAO
     *
     * @return
     */
    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static LocalUser mCurrentUser;

    public static LocalUser getCurrentUser() {
        return mCurrentUser;
    }

    public static void setCurrentUser(LocalUser mCurrentUser) {
        MyApp.mCurrentUser = mCurrentUser;
    }
}
