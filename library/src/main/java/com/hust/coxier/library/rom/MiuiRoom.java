package com.hust.coxier.library.rom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by lijianxin on 2017/8/16.
 */

public class MiuiRoom extends Rom {
    private static final String TAG = "MiuiRoom";

    @Override
    public void requestFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23){
            super.requestFloatPermission(context);
            return;
        }
        // 适配 6.0 以下
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            startPermissionActivityV5(context);
        } else if (versionCode == 6 || versionCode == 7) {
            startPermissionActivityV67(context);
        } else if (versionCode == 8) {
            startPermissionActivityV8(context);
        } else {
            Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
        }
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty(KEY_VERSION_MIUI);
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return -1;
    }

    /**
     * 小米 V5 版本 ROM权限申请
     */
    public void startPermissionActivityV5(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "intent is not available!");
        }
    }

    /**
     * 小米 V6 V7 版本 ROM权限申请
     */
    public void startPermissionActivityV67(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }

    /**
     * 小米 V8 版本 ROM权限申请
     */
    public void startPermissionActivityV8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (isIntentAvailable(intent, context)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "Intent is not available!");
            }
        }
    }

    private boolean isIntentAvailable(Intent intent, Context context) {
        if (intent == null || context == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        return packageManager != null &&
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;

    }
}
