package com.hust.coxier.library.rom;

import android.content.Context;
import android.os.Build;

/**
 * Created by lijianxin on 2017/8/16.
 */

public class FlymeRom extends Rom {

    @Override
    public boolean checkFloatWindowPermission(Context context) {
        // 因为魅族的 6.0 及之后版本需要特殊适配，所以重写
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
        }
        return true;
    }

    @Override
    public void requestFloatPermission(Context context) {

    }
}
