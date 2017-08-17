package com.hust.coxier.library.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lijianxin on 2017/8/16.
 */

public class Rom {
    private static final String TAG = "Rom";

    /** Value used for when a build property is unknown. */
    private static final String UNKNOWN = "unknown";

    protected static final int OP_SYSTEM_ALERT_WINDOW = 24;

    protected static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    protected static final String KEY_VERSION_EMUI = "ro.build.version.emui";

    /**
     * 申请悬浮窗权限,对于大多数的 rom 来说，只有6.0之后才需要申请权限
     */
    public void requestFloatPermission(Context context){
        if (Build.VERSION.SDK_INT >= 23){
            try {
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }
        }
    }

    /**
     * 检测是否有悬浮窗权限
     * @param context
     * @return
     */
    public boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19 && version < 23) {
            // Android 4.4 ~ 5.1 使用反射检查权限
            return checkOp(context, OP_SYSTEM_ALERT_WINDOW);
        }else if (version >= 23){
            try {
                // Android 6.0 之后 google 进行了统一
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                return (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return false;
            }
        } else {
            // Android 4.3（API 18 以前，直接返回 true）
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 获取系统属性
     * @param property
     * @return
     */
    static String getSystemProperty(String property) {
        try {
            Method m = Build.class.getDeclaredMethod("getString", String.class);
            m.setAccessible(true);
            return (String) m.invoke(null, property);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return UNKNOWN;
    }

    /**
     * 获取 rom 信息，http://www.jianshu.com/p/ba9347a5a05a
     * @return
     */
    public static Rom getRom(){
        if (isMiui()){
            return new MiuiRoom();
        }else if (isEmui()){
            return new EmuiRom();
        }else if (isFleme()){
            return new FlymeRom();
        }else if (isQiku()){
            return new QikuRom();
        }
        return new Rom();
    }

    private static boolean isMiui(){
        return !TextUtils.isEmpty(getSystemProperty(KEY_VERSION_MIUI));
    }

    private static boolean isEmui(){
        return !TextUtils.isEmpty(getSystemProperty(KEY_VERSION_EMUI));
    }

    private static boolean isFleme(){
        String flymeOSFlag = Build.DISPLAY;
        return !TextUtils.isEmpty(flymeOSFlag) && (flymeOSFlag.contains("flyme") ||
                flymeOSFlag.toLowerCase().contains("flyme"));
    }

    private static boolean isQiku(){
        return Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360");
    }
}
