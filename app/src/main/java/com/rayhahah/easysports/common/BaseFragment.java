package com.rayhahah.easysports.common;

import android.content.res.TypedArray;
import android.databinding.ViewDataBinding;

import com.rayhahah.easysports.R;
import com.rayhahah.rbase.base.IRBasePresenter;
import com.rayhahah.rbase.base.RBaseFragment;

import java.util.HashMap;

/**
 * Created by a on 2017/5/27.
 */

public abstract class BaseFragment<T extends IRBasePresenter, V extends ViewDataBinding>
        extends RBaseFragment<T, V> {

    @Override
    protected void initThemeAttrs() {
        mThemeColorMap = new HashMap<>();
        TypedArray array = getActivity().getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorPrimary,
                android.R.attr.colorPrimaryDark,
                android.R.attr.colorAccent,
                R.attr.colorTextLight,
                R.attr.colorTextDark,
                R.attr.colorBg,
                R.attr.colorBgDark
        });
        int colorPrimary = array.getColor(0, 0xC01E2F);
        int colorPrimaryDark = array.getColor(1, 0xA82828);
        int colorAccent = array.getColor(2, 0xF65663);
        int colorTextLight = array.getColor(3, 0xB6B6BE);
        int colorTextDark = array.getColor(4, 0x444242);
        int colorBg = array.getColor(5, 0xFFFFFF);
        int colorBgDark = array.getColor(6, 0xF6F5F4);
        array.recycle();

        mThemeColorMap.put(C.ATTRS.COLOR_PRIMARY, colorPrimary);
        mThemeColorMap.put(C.ATTRS.COLOR_PRIMARY_DARK, colorPrimaryDark);
        mThemeColorMap.put(C.ATTRS.COLOR_ACCENT, colorAccent);
        mThemeColorMap.put(C.ATTRS.COLOR_TEXT_LIGHT, colorTextLight);
        mThemeColorMap.put(C.ATTRS.COLOR_TEXT_DARK, colorTextDark);
        mThemeColorMap.put(C.ATTRS.COLOR_BG, colorBg);
        mThemeColorMap.put(C.ATTRS.COLOR_BG_DARK, colorBgDark);
    }
}
