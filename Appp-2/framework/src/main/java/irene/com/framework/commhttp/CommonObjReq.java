package irene.com.framework.commhttp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2015/8/18.
 */

public class CommonObjReq<T> extends Request<T> {

    private static final String TAG = "CommonObjReq";

    private final Response.Listener<T> mListener;
    private Class<T> targetClass;
    private Object mObj;
    private HashMap<String, String> paramsMap = new HashMap<String, String>();
    private HashMap<String, String> headers = new HashMap<String, String>();

    public void putHeadersInfo(HashMap<String, String> headers) {
        headers.putAll(headers);
    }

    // 如何传参数
    public CommonObjReq(int method, String urlStr, HashMap<String, String> paramsMap, MCListenerObj<T> mcListener, Class<T> t) {
        super(method, urlStr, mcListener);
        targetClass = t;
        mListener = mcListener;
        this.paramsMap = paramsMap;
    }
    public CommonObjReq(int method, String urlStr, Object obj, MCListenerObj<T> mcListener, Class<T> t) {
        super(method, urlStr, mcListener);
        targetClass = t;
        mListener = mcListener;
        mObj = obj;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        String str;
        if (null == mObj) {
            str = JSON.toJSONString(paramsMap, SerializerFeature.WriteMapNullValue);
        } else {
            str = JSON.toJSONString(mObj, SerializerFeature.WriteMapNullValue);
        }
        return str.getBytes();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }

    @Override
    protected void deliverResponse(T response) {
        if (response != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        LogUtil.d(TAG, "url:" + this.getUrl() + " parseNetworkResponse:" + jsonString);
        T flickrResponse = JSON.parseObject(jsonString, targetClass);
        return Response.success(flickrResponse, getCacheEntry());
    }

}
