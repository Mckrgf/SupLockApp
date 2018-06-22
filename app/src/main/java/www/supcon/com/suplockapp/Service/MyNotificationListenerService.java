package www.supcon.com.suplockapp.Service;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import www.supcon.com.suplockapp.Eventbus.NotifyEvent;

/**
 * Created by yaobing on 2018/6/22.
 * Description xxx
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyNotificationListenerService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "Notification posted");
        StatusBarNotification[] notifications = getActiveNotifications();
        NotifyEvent event = new NotifyEvent();
        event.setNotifications(notifications);
        EventBus.getDefault().postSticky(event);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Notification removed");
        StatusBarNotification[] notifications = getActiveNotifications();
        NotifyEvent event = new NotifyEvent();
        event.setNotifications(notifications);
        EventBus.getDefault().postSticky(event);
    }

    private static final String TAG = "MyNotificationListenerS";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "通知监听服务启动");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "通知监听服务启动");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StatusBarNotification[] notifications = getActiveNotifications();
                Log.d(TAG, " NotificationListenerService onCreate, notification count ：" + notifications.length + "onStartCommand");

                NotifyEvent event = new NotifyEvent();
                event.setNotifications(notifications);
                EventBus.getDefault().postSticky(event);
            }
        }, 1);
        return super.onStartCommand(intent, flags, startId);
    }
}
