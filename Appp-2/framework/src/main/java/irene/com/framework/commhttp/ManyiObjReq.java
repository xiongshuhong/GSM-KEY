package irene.com.framework.commhttp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import irene.com.framework.util.LogUtil;

/**
 * Created by Irene on 2015/8/18.
 */

public class ManyiObjReq<T> extends Request<T> {
    private final Class<T> clazz;
    private HashMap<String, String> mInterceptorHeaders;
    private final Object mRequestBody;
    private byte[] mRequestBytes;
    private Priority mPriority;
    private MCListenerObj mListener;
    private IAppAuthInterceptor mAppAuthInterceptor;

    /**
     * Make a GET request and return a parsed object from JSON.
     * @param method  Request.Method
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for jackson's reflection
     */
    public ManyiObjReq(int method, String url, Class<T> clazz,
                       MCListenerObj<T> mcListener, Object requestBody, IAppAuthInterceptor iAppAuthInterceptor) {
        super(method, url, mcListener);
        //不要retry. 这个2500是指等待服务器连接的时间，也是读取数据的时间，都设置为2500
        //volley暂时没有提供接口给我们来分别设置这两个数据.
        this.setRetryPolicy(new DefaultRetryPolicy(2500, 0, 1f));
        this.clazz = clazz;
        this.mListener = mcListener;
        this.mRequestBody = requestBody;
        this.mPriority = Priority.NORMAL;
        this.mAppAuthInterceptor = iAppAuthInterceptor;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (null == mInterceptorHeaders) {
            if (null != mAppAuthInterceptor) {
                byte[] body = mRequestBytes != null ? mRequestBytes : getBody();
                    HashMap<String, Object> map;
                    if (body.length > 0) {
                        String jsonString = new String(body);
                        map = JSON.parseObject(jsonString, HashMap.class);
                    } else {
                        map = new HashMap(0);
                    }
                    mInterceptorHeaders = mAppAuthInterceptor.intercept(map);
            } else {
                return super.getHeaders();
            }
        }
        return mInterceptorHeaders;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (null != this.mRequestBytes) {
            return mRequestBytes;
        }
        try {
            mRequestBytes = convertRequestToBytes();
            return mRequestBytes;
        } catch (IOException e) {
            throw new AuthFailureError("getBody", e);
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=" + getParamsEncoding();
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String jsonString = new String(response.data);
        LogUtil.d("ManyiObjReq", "url:" + this.getUrl() + " parseNetworkResponse:" + jsonString);
        T flickrResponse = JSON.parseObject(jsonString, clazz);
        return Response.success(flickrResponse, getCacheEntry());
    }

    private byte[] convertRequestToBytes() throws IOException {
        if(null == this.mRequestBody) {
            return "".getBytes();
        }
        String str = JSON.toJSONString(mRequestBody, SerializerFeature.WriteMapNullValue);
        return str.getBytes();
    }
    @Override
    public Priority getPriority() {
        return this.mPriority;
    }

    public void setPriority(Priority priority) {
        this.mPriority = priority;
    }
}
