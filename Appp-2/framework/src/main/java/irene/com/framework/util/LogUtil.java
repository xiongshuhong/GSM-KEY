package irene.com.framework.util;

import android.util.Log;

import irene.com.framework.common.AppConfig;

/**
 * Created by Irene on 2015/8/18.
 */
public class LogUtil {

    public static void println(Object content){
        if(AppConfig.IS_DEBUG)
            System.out.println(content);
    }

    public static void e(String tag, String msg){
        if(AppConfig.IS_DEBUG)
            Log.e(tag,msg);
    }

    public static void d(String tag, String msg){
        if(AppConfig.IS_DEBUG)
            Log.d(tag,msg);
    }
}
