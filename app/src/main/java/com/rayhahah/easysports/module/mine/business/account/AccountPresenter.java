package com.rayhahah.easysports.module.mine.business.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import com.rayhahah.easysports.R;
import com.rayhahah.easysports.app.MyApp;
import com.rayhahah.easysports.bean.db.LocalUser;
import com.rayhahah.easysports.common.C;
import com.rayhahah.easysports.module.mine.bean.BmobUsers;
import com.rayhahah.easysports.module.mine.bean.MineListBean;
import com.rayhahah.rbase.base.RBasePresenter;
import com.rayhahah.rbase.utils.base.DialogUtil;
import com.rayhahah.rbase.utils.useful.RLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * ┌───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐
 * │Esc│ │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│ ┌┐    ┌┐    ┌┐
 * └───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘ └┘    └┘    └┘
 * ┌──┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐┌───┬───┬───┐┌───┬───┬───┬───┐
 * │~`│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp ││Ins│Hom│PUp││N L│ / │ * │ - │
 * ├──┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤├───┼───┼───┤├───┼───┼───┼───┤
 * │Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ ││Del│End│PDn││ 7 │ 8 │ 9 │   │
 * ├────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤└───┴───┴───┘├───┼───┼───┤ + │
 * │Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │             │ 4 │ 5 │ 6 │   │
 * ├─────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤    ┌───┐    ├───┼───┼───┼───┤
 * │Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │    │ ↑ │    │ 1 │ 2 │ 3 │   │
 * ├────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤┌───┼───┼───┐├───┴───┼───┤ E││
 * │Ctrl│Ray │Alt │         Space         │ Alt│code│fuck│Ctrl││ ← │ ↓ │ → ││   0   │ . │←─┘│
 * └────┴────┴────┴───────────────────────┴────┴────┴────┴────┘└───┴───┴───┘└───────┴───┴───┘
 *
 * @author Rayhahah
 * @time 2017/7/21
 * @tips 这个类是Object的子类
 * @fuction
 */
public class AccountPresenter extends RBasePresenter<AccountContract.IAccountView>
        implements AccountContract.IAccountPresenter {

    private Uri mUri;

    public AccountPresenter(AccountContract.IAccountView view) {
        super(view);
    }

    @Override
    public List<MineListBean> getListData(Context context) {
        List<MineListBean> mData = new ArrayList<>();

        MineListBean scrreenName = new MineListBean();
        scrreenName.setCoverRes(R.drawable.ic_svg_screenname_colorful_24);
        scrreenName.setTitle("设置昵称");
        scrreenName.setSectionData(context.getResources().getString(R.string.account_setting));
        scrreenName.setType(MineListBean.TYPE_NULL);
        scrreenName.setId(C.ACCOUNT.ID_SCREENNAME);
        mData.add(scrreenName);

        MineListBean reset = new MineListBean();
        reset.setCoverRes(R.drawable.ic_svg_reset_blue_24);
        reset.setTitle("重置密码");
        reset.setSectionData(context.getResources().getString(R.string.account_setting));
        reset.setType(MineListBean.TYPE_NULL);
        reset.setId(C.ACCOUNT.ID_RESET_PASSWORD);
        mData.add(reset);

        MineListBean hupu = new MineListBean();
        hupu.setCoverRes(R.drawable.ic_svg_hupu_red_24);
        hupu.setTitle("虎扑账号设置");
        hupu.setSectionData(context.getResources().getString(R.string.account_setting));
        hupu.setType(MineListBean.TYPE_NULL);
        hupu.setId(C.ACCOUNT.ID_HUPU);
        mData.add(hupu);

        MineListBean tel = new MineListBean();
        tel.setCoverRes(R.drawable.ic_svg_telephone_orange_24);
        tel.setTitle("电话号码设置");
        tel.setSectionData(context.getResources().getString(R.string.account_setting));
        tel.setType(MineListBean.TYPE_NULL);
        tel.setId(C.ACCOUNT.ID_TEL);
        mData.add(tel);

        return mData;
    }

    @Override
    public void updateUser(final LocalUser localUser) {
        BmobUsers bmobUsers = new BmobUsers(localUser.getUser_name()
                , localUser.getPassword(), localUser.getScreen_name()
                , localUser.getTel(), localUser.getCover()
                , localUser.getHupu_user_name(), localUser.getPassword());
        bmobUsers.update(localUser.getBmobId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyApp.getDaoSession().getLocalUserDao().insertOrReplace(localUser);
                    MyApp.setCurrentUser(localUser);
                    mView.updateUserSuccess();
                } else {
                    mView.updateUserFailed();
                }
            }
        });
    }

    @Override
    public void choosePhoto(Activity context) {
        Intent choose_intent = new Intent(Intent.ACTION_GET_CONTENT);
        choose_intent.setType("image/*");
        context.startActivityForResult(choose_intent, C.ACCOUNT.CODE_CHOOSE_PHOTO);
    }

    @Override
    public void takePhoto(Activity context) {
        File file = new File(C.DIR.PIC_DIR);
        String name = DateFormat.format("yyyyMMdd_hhmmss",
                Calendar.getInstance(Locale.CHINA))
                + ".png";
        if (!file.exists()) {
            file.mkdirs();// 创建文件夹
        }
        // 调用摄像头程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//								图片文件
        File picture = new File(file, name);
        mUri = Uri.fromFile(picture);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        context.startActivityForResult(intent, C.ACCOUNT.CODE_TAKE_PHOTO);
    }

    @Override
    public void uploadCover(String path) {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                String url = bmobFile.getUrl();
                RLog.e("coverUrl=" + url);
                mView.uploadCoverSuccess(url);
            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                DialogUtil.setProgress(value);
            }

            @Override
            public void doneError(int code, String msg) {
                super.doneError(code, msg);
                mView.uploadCoverFailed(code, msg);
            }
        });
    }

    public Uri getUri() {
        return mUri;
    }
}
