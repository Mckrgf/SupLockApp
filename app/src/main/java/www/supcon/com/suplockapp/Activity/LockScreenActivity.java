package www.supcon.com.suplockapp.Activity;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import www.supcon.com.suplockapp.App;
import www.supcon.com.suplockapp.Eventbus.NotifyEvent;
import www.supcon.com.suplockapp.Utils.MyDateUtils;
import www.supcon.com.suplockapp.Service.MyNotificationListenerService;
import www.supcon.com.suplockapp.R;
import www.supcon.com.suplockapp.Utils.NotificationUtils;

public class LockScreenActivity extends AppCompatActivity {

    private int notify_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_lock_screen);

        //启动通知监听
        toggleNotificationListenerService(this);

        //解锁按钮
        Button btClose = findViewById(R.id.bt_close);
        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartApplicationWithPackageName("www.supcon.com.hsesystem");
            }
        });

        //时间显示
        TextView tv_time = findViewById(R.id.tv_time);
        tv_time.setText(MyDateUtils.getCurTimeFormat(MyDateUtils.date_format5));

        //箭头动画
        ImageView iv_anim = findViewById(R.id.iv_anim);
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -100);
        animation.setDuration(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        iv_anim.setAnimation(animation);
        animation.start();

        //设置全屏,并且控件在状态栏上也有显示,
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }

        //判断手机是否可以监控通知栏消息
        if (!isNotificationListenersEnabled()) {
            //如果手机不能被监控通知栏消息,就跳转到设置界面
            gotoNotificationAccessSetting(this);
        } else {
            Intent intent = new Intent(this, MyNotificationListenerService.class);
            startService(intent);
        }

        Button bt_add_notify = findViewById(R.id.bt_add_notify);
        bt_add_notify.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                notify_id++;
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(LockScreenActivity.this)
                        .setLargeIcon(BitmapFactory.decodeResource(LockScreenActivity.this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                        .setContentTitle("添加一个通知,id为: "+notify_id) // 设置下拉列表里的标题
                        .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                        .setContentText("测试的通知类") // 设置上下文内容
                        .setWhen(System.currentTimeMillis())
                        .build(); // 设置该通知发生的时间
                manager.notify(notify_id,notification);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotifyEvent event) {
        StatusBarNotification[] notifications = event.getNotifications();
        Toast.makeText(getBaseContext(), "获取到的通知栏条数为: " + notifications.length, Toast.LENGTH_SHORT).show();
    }

    private static final String TAG = "LockScreenActivity";

    public static void toggleNotificationListenerService(Context context) {
        Log.e(TAG, "toggleNotificationListenerService");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, MyNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(context, MyNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.screenIsLock = true;
    }


    public static boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;

        } catch (ActivityNotFoundException e) {//普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }


    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    public boolean isNotificationListenersEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //监听系统的物理按键
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Log.i("tag", "===BACK====");
//        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
//            Log.i("tag", "===HOME====");
//        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
//            Log.i("tag", "===KEYCODE_MENU====");
//        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Log.i("tag", "===KEYCODE_MENU====");
//            finish();
//        }
//        return false;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.screenIsLock = false;
        EventBus.getDefault().unregister(this);
    }

    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }
}
