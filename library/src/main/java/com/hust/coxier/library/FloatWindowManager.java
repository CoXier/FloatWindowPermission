package com.hust.coxier.library;

import android.content.Context;

import com.hust.coxier.library.rom.Rom;

/**
 * Created by lijianxin on 2017/8/16.
 * 管理悬浮窗权限
 */

public class FloatWindowManager {
    private static final String TAG = "FloatWindowManager";
    private static volatile FloatWindowManager sInstance;
    private Rom mRom;

    private FloatWindowManager(){
        mRom = Rom.getRom();
    }

    public static FloatWindowManager getInstance() {
        if (sInstance == null) {
            synchronized (FloatWindowManager.class) {
                if (sInstance == null) {
                    sInstance = new FloatWindowManager();
                }
            }
        }
        return sInstance;
    }

    public boolean checkPermission(Context context){
        return mRom.checkFloatWindowPermission(context);
    }

    public void requestPermission(Context context){
        if (checkPermission(context)){
            throw new IllegalStateException("Has granted permission");
        }
        mRom.requestFloatPermission(context);
    }
}
