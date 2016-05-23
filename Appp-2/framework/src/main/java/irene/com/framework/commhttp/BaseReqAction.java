package irene.com.framework.commhttp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Irene on 2015/8/18.
 */

public class BaseReqAction {

    private static RequestQueue mQueue;

    /**
     * Get获取Str请求函数
     *
     * @param mContext
     * @param url
     * @param paramsMap
     * @param iStrResListener
     * @return
     */
    public static Request<?> createGetReqToQu(Context mContext, String url, HashMap<String, Object> paramsMap, MCListener.IStrResListener iStrResListener, final HashMap<String, String> headerInfo) {
        return createReqToQue(mContext, getCommStrReq(Request.Method.GET, url, paramsMap, iStrResListener, headerInfo));
    }

    /**
     * Post获取Str请求函数
     *
     * @param mContext
     * @param url
     * @param paramsMap
     * @param iStrResListener
     * @return
     */
    public static Request<?> createPostReqToQu(Context mContext, String url, HashMap<String, Object> paramsMap, MCListener.IStrResListener iStrResListener, final HashMap<String, String> headerInfo) {
        return createReqToQue(mContext, getCommStrReq(Request.Method.POST, url, paramsMap, iStrResListener, headerInfo));
    }

    public static CommonReq getCommStrReq(int method, String urlStr, HashMap<String, Object> paramsMap, MCListener.IStrResListener iStrResListener, final HashMap<String, String> headerInfo) {
        return new CommonReq(method, urlStr, paramsMap, new MCListener(iStrResListener, urlStr)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerInfo;
            }
        };
    }

    /**
     * Get请求获取对象的函数
     *
     * @param mContext
     * @param url
     * @param paramsMap
     * @param iStrResListener
     * @param responseClass
     * @param <T>
     * @return
     */
    public static <T> Request<?> createGetReqToQu(Context mContext, String url, HashMap<String, Object> paramsMap, MCListenerObj.IObjResListener<T> iStrResListener, Class<T> responseClass, final HashMap<String, String> headerInfo) {
        return createReqToQue(mContext, getCommObjReq(Request.Method.GET, url, paramsMap, iStrResListener, responseClass, headerInfo));
    }

    /**
     * Post请求获取对象的接口
     *
     * @param mContext
     * @param url
     * @param paramsMap
     * @param iStrResListener
     * @param responseClass
     * @param <T>
     * @return
     */
    public static <T> Request<?> createPostReqToQu(Context mContext, String url, HashMap<String, Object> paramsMap, MCListenerObj.IObjResListener<T> iStrResListener, Class<T> responseClass, final HashMap<String, String> headerInfo) {
        return createReqToQue(mContext, getCommObjReq(Request.Method.POST, url, paramsMap, iStrResListener, responseClass, headerInfo));
    }

    public static <T> CommonObjReq getCommObjReq(int method, String urlStr, HashMap<String, Object> paramsMap, MCListenerObj.IObjResListener<T> iStrResListener, Class<T> responseClass, final HashMap<String, String> headerInfo) {

        return new CommonObjReq(method, urlStr, paramsMap, new MCListenerObj(iStrResListener, urlStr), responseClass) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headerInfo;
            }
        };
    }

    public static <T> CommonObjFileReq getFileReq(int method, String urlStr,MCListenerObj.IObjResListener<T> iStrResListener,HashMap<String, String> params) {
        return new CommonObjFileReq(method, urlStr,new MCListenerObj(iStrResListener, urlStr),params);
    }

    public static <T> ManyiObjReq getObjReq(int method, String urlStr, Object object, MCListenerObj.IObjResListener<T> iStrResListener, Class<T> responseClass, IAppAuthInterceptor iAppAuthInterceptor) {
        return new ManyiObjReq(method, urlStr, responseClass,
                new MCListenerObj(iStrResListener, urlStr), object, iAppAuthInterceptor);
    }

    public static Request<?> createReqToQue(Context mContext, Request commonReq) {
        commonReq.setTag(mContext);
    return getQueue(mContext).add(commonReq);
}

    private static RequestQueue getQueue(Context mContext) {
        mQueue = mQueue == null ? Volley.newRequestQueue(mContext) : mQueue;
        return mQueue;
    }

    public static void cancelAllRequest(Context mContext){

        if(getQueue(mContext) != null){
            getQueue(mContext).cancelAll(mContext);
        }
    }
}
