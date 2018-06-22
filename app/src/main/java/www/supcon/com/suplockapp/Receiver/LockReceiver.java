package www.supcon.com.suplockapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import www.supcon.com.suplockapp.Activity.LockScreenActivity;

/**
 * Created by yaobing on 2018/6/15.
 * Description xxx
 */

public class LockReceiver extends BroadcastReceiver {

    private static final String TAG = "LockReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(TAG,"lock_on_screen_off");
            Intent lockscreen = new Intent(context,LockScreenActivity.class);
            lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(lockscreen);
        }else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.i(TAG,"lock_off_screen_on");
        }
    }
}
