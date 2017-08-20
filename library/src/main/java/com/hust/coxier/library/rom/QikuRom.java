package com.hust.coxier.library.rom;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lijianxin on 2017/8/16.
 */

public class QikuRom extends Rom {
    private static final String TAG = "QikuRom";

    @Override
    public void requestFloatPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            try{
                context.startActivity(intent);
            }catch (Throwable e){
                Log.e(TAG, e.toString());
            }
        }
    }
}
