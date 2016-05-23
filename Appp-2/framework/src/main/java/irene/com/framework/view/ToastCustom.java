package irene.com.framework.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Irene on 2015/8/18.
 */
import irene.com.framework.R;

public class ToastCustom {

	public static Toast toast = null;

	public static void showShortToast(Object info){
		
		String showContent;
        Context context = null;
//		Context context = MyApplication.getInstance();
		if(info instanceof String){
			showContent = (String)info;
		}else{
			showContent = context.getString((Integer) info);
		}
		showToastShort(showContent);
	}
	
	public static void showLongToast(Object info){
		
		String showContent;
        Context context = null;
//		Context context = MyApplication.getInstance();
		if(info instanceof String){
			showContent = (String)info;
		}else{
			showContent = context.getString((Integer) info);
		}
		showToastLong(showContent);
	}
	
	private static void showToastShort(String infoString) {
        Context context = null;
//		Context context = MyApplication.getInstance();
		if (context == null) {
			return;
		}
		showToastText(infoString, Toast.LENGTH_SHORT);
	}

	private static void showToastLong(String infoString) {
        Context context = null;
//		Context context = MyApplication.getInstance();
		if (context == null) {
			return;
		}
		showToastText(infoString, Toast.LENGTH_LONG);
	}
	
	public static void showToastNetError() {
        Context context = null;
//		Context context = MyApplication.getInstance();
		if (context == null) {
			return;
		}
		showToastText("网络异常,请检查您的网络", Toast.LENGTH_SHORT);
	}

	public static void cancel() {
		if (toast == null) {
			return;
		}
		toast.cancel();
	}

	private static void setToast(Toast toast2Set) {
		toast = toast2Set;
	}

	private static Toast getToast(Context context) {
		if (toast == null) {
			toast = new Toast(context.getApplicationContext());
			LayoutInflater inflate = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflate.inflate(R.layout.toast_main, null);
			toast.setView(v);
			setToast(toast);
		}
		toast.setDuration(Toast.LENGTH_SHORT);
		return toast;
	}

	private static void showToastText(String infoStr, int TOAST_LENGTH) {
//		Context context = MyApplication.getInstance();
        Context context = null;
		toast = getToast(context);
		TextView textView = (TextView) toast.getView().findViewById(R.id.tv_toast);
		textView.setText(infoStr);
        textView.setTypeface(Typeface.SANS_SERIF);
		toast.setDuration(TOAST_LENGTH);
		toast.show();
	}
}
