package www.supcon.com.suplockapp.Eventbus;

import android.service.notification.StatusBarNotification;

/**
 * Created by yaobing on 2018/6/22.
 * Description xxx
 */

public class NotifyEvent {
    public StatusBarNotification[] getNotifications() {
        return notifications;
    }

    public void setNotifications(StatusBarNotification[] notifications) {
        this.notifications = notifications;
    }

    public StatusBarNotification[] notifications;
}
