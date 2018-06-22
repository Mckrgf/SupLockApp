package www.supcon.com.suplockapp;

import android.app.Application;


/**
 * Created by tfhr on 2018/2/1.
 */

public class App extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = true;


    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static boolean screenIsLock = false;
}
