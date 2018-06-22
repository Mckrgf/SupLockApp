package www.supcon.com.suplockapp.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import www.supcon.com.suplockapp.R;


/**
 * Created by TFHR02 on 2016/12/16.
 */
public  class NotificationUtils {
    private static NotificationUtils notificationUtils = null;
    private Context context;
    private String content_title;
    private int notificationnum;
    private String content_text;
    private Intent resultintent;
    private Class<?> backtaskclass;

    private NotificationUtils(Context context) {
        this.context = context;
    }

    public static NotificationUtils getInstance(Context context) {
        if (notificationUtils == null) {
            notificationUtils = new NotificationUtils(context);
        }
        return notificationUtils;
    }

    public NotificationUtils setContenttitle(String contenttitle) {
        this.content_title = contenttitle;
        return this;
    }
    public static void setcancel(){
        if (notificationUtils != null) {
            notificationUtils = null;
        }
    }

    public NotificationUtils setNotificationnum(int notificationnum) {
        this.notificationnum = notificationnum;
        return this;
    }

    public NotificationUtils setContenttext(String content_text) {
        this.content_text = content_text;
        return this;
    }

    public NotificationUtils setResultintent(Intent resultintent) {
        this.resultintent = resultintent;
        return this;
    }

    public void SendNotification() {

        Bitmap largebitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        int large_w=largebitmap.getWidth();
        //裁剪为圆图
        largebitmap=ImageCompressUtil.toRoundCorner(largebitmap,large_w/2);
        Bitmap biglargeicon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        Bitmap bigpicture = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        //宽视图样式（只有4.1版本以上才支持）
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle
                .setBigContentTitle("下面显示6行-------------------------消息：")
                .addLine("M.Twain (Google+) Haiku is more than a cert...")
                .addLine("M.Twain Reminder")
                .addLine("M.Twain Lunch?")
                .addLine("M.Twain Revised Specs")
                .addLine("M.Twain ")
                .addLine("Google Play Celebrate 25 billion apps with Goo..")
                .addLine("Stack Exchange StackOverflow weekly Newsl...")
                .setSummaryText("mtwain@android.com");
        //宽视图图片样式
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle("宽视图图片样式标题");
        bigPictureStyle.bigLargeIcon(biglargeicon);
        bigPictureStyle.bigPicture(bigpicture);
        bigPictureStyle.setSummaryText("宽视图图片样式总结概要");
        //宽视图文字样式
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("宽视图文字样式标题");
        bigTextStyle.bigText("月落乌啼霜满天，\n江枫渔火对愁眠。\n姑苏城外寒山寺，\n夜半钟声到客船。");
        bigTextStyle.setSummaryText("宽视图文字样式总结概要");

        //通知栏的实体内容
        String content = "有新的通知！";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(largebitmap)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(content_title)//设置标题
                        .setContentText(content_text)//设置内容
//                                        .setSubText("我是第三行文本内容")//第三行文本
                        .setAutoCancel(true)//点击后自动消失
                                        .setContentInfo(" ")//如果设置number则将其覆盖
                        .setTicker(content) //第一次提示消息的时候显示在通知栏上（ 5.0以上版本好像没有用了）
                        .setNumber(notificationnum)//设置同消息的条目数
                        .setColor(Color.parseColor("#0074a7"))//小图标外接圆圈颜色
                        .setPriority(NotificationCompat.PRIORITY_MAX)//设置广播的优先级
//                                        .setStyle(bigTextStyle)//自定义通知样式
//                                        .setDefaults(Notification.DEFAULT_ALL)//设置通知的行为,例如声音,震动等
                ;

        //跳转压面
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //返回按键回退页面(与这个activity的manifest文件中的parentActivityName的属性相关联，及回退intent的起点)
        try {
            backtaskclass= Class.forName(resultintent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        stackBuilder.addParentStack(backtaskclass);
        //设置跳转页面
//        resultintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (resultintent!=null){
            stackBuilder.addNextIntent(resultintent);
        }
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
//        //通过通知处理点击之后的操作
//        PendingIntent.getBroadcast(context,0,resultintent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationnum, mBuilder.build());

        //回收bigmap
        largebitmap.recycle();
        biglargeicon.recycle();
        bigpicture.recycle();
    }
}
