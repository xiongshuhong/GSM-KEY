package irene.com.framework.util;

import android.app.Activity;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Irene on 2015/8/31.
 */
public class ImageUtil {

    private ImageLoader mImageLoader;

    public ImageUtil(Activity mActivity){
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));
        }
    }

    public void showImage(String url,ImageView iv){
        mImageLoader.displayImage(url, iv);
    }


}
