package irene.com.framework.commhttp;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import irene.com.framework.exception.BaseUncaughtExceptionHandler;

/**
 * Created by Irene on 2015/8/18.
 */

public class IBNetworkError {

    private static IBNetworkError mMCNetworkError;

    public static synchronized IBNetworkError getInstance() {

        if (mMCNetworkError == null) {
            mMCNetworkError = new IBNetworkError();
        }
        return mMCNetworkError;
    }

    public void  onErrorResponse(VolleyError volleyError){
        if (volleyError instanceof com.android.volley.NoConnectionError) {
            BaseUncaughtExceptionHandler.getInstance().showNetWorkError();
        }else if(volleyError instanceof com.android.volley.TimeoutError){
            BaseUncaughtExceptionHandler.getInstance().showNetWorkTimeout();
        }else if(volleyError instanceof com.android.volley.ServerError
            || volleyError instanceof com.android.volley.NetworkError
            || volleyError instanceof AuthFailureError){
            BaseUncaughtExceptionHandler.getInstance().showServerError();
        }
    }

}
