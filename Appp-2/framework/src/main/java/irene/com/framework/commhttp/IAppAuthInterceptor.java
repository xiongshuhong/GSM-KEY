package irene.com.framework.commhttp;

import java.util.HashMap;

/**
 * Created by Irene on 2015/8/18.
 */

public interface IAppAuthInterceptor {
    HashMap<String, String> intercept(HashMap<String, Object> paramsMap);
}
