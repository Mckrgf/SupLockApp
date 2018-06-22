package www.supcon.com.suplockapp.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import www.supcon.com.suplockapp.Service.LockService;
import www.supcon.com.suplockapp.Service.MyNotificationListenerService;
import www.supcon.com.suplockapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(MainActivity.this,LockService.class);
//        startService(intent);
        Button start_lock = findViewById(R.id.bt_start_lock);
        Button stop_lock = findViewById(R.id.bt_stop_lock);
        start_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LockService.class);
                startService(intent);
            }
        });
        stop_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LockService.class);
                stopService(intent);
            }
        });

        //判断手机是否可以监控通知栏消息
        if (!isNotificationListenersEnabled()) {
            //如果手机不能被监控通知栏消息,就跳转到设置界面
            LockScreenActivity.gotoNotificationAccessSetting(this);
        }else {
            //启动通知栏监听五福
            Intent intent = new Intent(this,MyNotificationListenerService.class);
            startService(intent);
        }
    }

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public boolean isNotificationListenersEnabled () {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),   ENABLED_NOTIFICATION_LISTENERS);
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
}
