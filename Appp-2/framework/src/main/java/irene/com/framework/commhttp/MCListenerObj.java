package irene.com.framework.commhttp;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import irene.com.framework.exception.ClientException;

/**
 * Created by Irene on 2015/8/18.
 */

public class MCListenerObj<T> implements Response.ErrorListener, Response.Listener<T> {

    IObjResListener<T> mIObjResListener;

    public MCListenerObj(IObjResListener<T> mIObjResListener) {
        this.mIObjResListener = mIObjResListener;
    }

    public String urlStr;

    public MCListenerObj(IObjResListener<T> mIObjResListener, String url) {
        this.mIObjResListener = mIObjResListener;
        this.urlStr = url;
    }

    private MCListenerObj() {
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //MCNetworkError.getInstance().onErrorResponse(volleyError);
        mIObjResListener.onFail(convertException(volleyError), urlStr);
    }

    private ClientException convertException(VolleyError volleyError) {
        if (volleyError instanceof com.android.volley.NoConnectionError) {
            return new ClientException("当前网络不可用", ClientException.REQUEST_NETWORK, volleyError);
        } else if(volleyError instanceof com.android.volley.TimeoutError){
            return new ClientException("请求超时，请稍后重试！", ClientException.REQUEST_TIMEOUT, volleyError);
        } else if(volleyError instanceof com.android.volley.ServerError
                || volleyError instanceof com.android.volley.NetworkError
                || volleyError instanceof AuthFailureError){
            return new ClientException("请求异常，请稍后再试！", ClientException.REQUEST_EXCEPTION, volleyError);
        } else {
            return new ClientException("请求异常，请稍后再试！", ClientException.REQUEST_EXCEPTION, volleyError);
        }
    }

    @Override
    public void onResponse(T s) {
        mIObjResListener.onSuccess(s, urlStr);
    }

    public static interface IObjResListener<T> {

        public void onSuccess(T t, String url);

        public void onFail(Exception exception, String url);

    }

}
