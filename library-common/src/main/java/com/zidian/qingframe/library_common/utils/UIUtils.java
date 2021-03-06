package com.zidian.qingframe.library_common.utils;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zidian.library_common.R;

import java.lang.reflect.Method;

public class UIUtils {
    /**
     * 获取顶部的bar
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取底部的bar的高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int height = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId);
            }
        }
        return height;
    }

    /**
     * 判断设备是否有底部的bar
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            //do something
        }
        return hasNavigationBar;
    }


    /**
     * 底部bar是否显示
     *
     * @param mActivity
     * @return
     */
    public static boolean isNavigationBarShow(Activity mActivity) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    }

    /**
     * 打开软键盘
     *
     * @param mEditText 输入框
     */
    public static void openKeyboard(EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) mEditText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditText.requestFocus();
        mEditText.setFocusable(true);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param activity
     */
    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 根据listview内容设置高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 根据手机分辨率从DP转成PX
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率PX(像素)转成DP
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 下载apk通知栏
     * @param notificationManager
     * @return
     */
    public static final int notifyID = 99;
    public static final String CHANNEL_ID = "111";

    public static Notification.Builder creatNotification(NotificationManager notificationManager) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) AppUtils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //8.0新增通知渠道
            CharSequence name = "QingFrame";
            int importance = NotificationManager.IMPORTANCE_LOW;   //优先级
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableLights(false);            //闪灯开关
            mChannel.enableVibration(false);         //振动开关
            mChannel.setShowBadge(false);         //通知圆点开关
            notificationManager.createNotificationChannel(mChannel);
            builder = new Notification.Builder(AppUtils.getApp(), CHANNEL_ID);
        } else {
            builder = new Notification.Builder(AppUtils.getApp());
        }
        builder.setSmallIcon(R.drawable.ic_app_ntfc)
                .setLargeIcon(BitmapFactory.decodeResource(AppUtils.getApp().getResources(), R.mipmap.share_qq))
                .setContentText("0%")
                .setContentTitle("青结构")
                .setProgress(100, 0, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //小图标背景
            builder.setColor(AppUtils.getApp().getResources().getColor(R.color.colorPrimary));
        }

        notificationManager.notify(notifyID, builder.build());
        return builder;
    }

    public static void cancleNotification(NotificationManager notificationManager) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) AppUtils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.cancel(notifyID);
    }
}
