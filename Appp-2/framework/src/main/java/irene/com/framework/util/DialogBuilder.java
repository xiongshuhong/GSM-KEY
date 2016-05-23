package irene.com.framework.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Toast;

import java.util.HashMap;
/**
 * Created by Irene on 2015/8/18.
 */

public abstract class DialogBuilder {

    public static void showSimpleDialog(String message, Activity mActivity) {
        showSimpleDialog(message, mActivity, null);
    }

    public static void showSimpleDialog(String message, Activity mActivity, OnClickListener listener) {
        AlertDialog mDialog = getExitDialog(mActivity);
        if(mDialog != null){
            mDialog.setMessage(message);
        }else {
            AlertDialog dialog = new AlertDialog.Builder(mActivity).setPositiveButton("我知道了", listener).setMessage(message).setCancelable(false).show();
            cancelDialog(mActivity);
            addDialog(mActivity, dialog);
        }
    }

    public static void customSimpleDialog(String message, Activity mActivity, OnClickListener listener, String positive) {
        new AlertDialog.Builder(mActivity).setPositiveButton(positive, listener).setMessage(message).setCancelable(false).show();
    }

    public static void showSimpleDialog(String message, String posMessage, Activity mActivity, OnClickListener listener) {
        new AlertDialog.Builder(mActivity).setMessage(message).setPositiveButton(posMessage, listener).show();
    }

    public static void showSimpleDialog(String message, String posMessage, String negMessage, Activity mActivity, OnClickListener listener) {
        new AlertDialog.Builder(mActivity).setMessage(message).setPositiveButton(posMessage, listener).setNegativeButton(negMessage, null).show();
    }

    /**
     * 否定当确定用
     * @param message
     * @param posMessage
     * @param negMessage
     * @param mActivity
     * @param listener
     */
    public static void showNegtiveSureSimpleDialog(String message, String posMessage, String negMessage, Activity mActivity, OnClickListener listener) {
        new AlertDialog.Builder(mActivity).setMessage(message).setPositiveButton(posMessage, null).setNegativeButton(negMessage, listener).show();
    }

    private static Toast toast = null;

    /**
     * 统一弹Toast
     *
     * @param context
     * @param message
     */
    public static void showSimpleToast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void cancelSimpleToast(){
        if (toast != null){
            toast.cancel();
        }
    }

    public static void showCancelableToast(Context context, String message) {
        // TextView tv = new TextView(context);
        // tv.setText(message);
        // mCancelableDialog = new AlertDialog.Builder(context).setView(tv).setCancelable(true).create();
        mCancelableDialog = new AlertDialog.Builder(context).setMessage(message).setCancelable(true).create();
        mCancelableDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mTimer != null) {
                    mTimer.cancel();
                }
            }
        });
        mCancelableDialog.show();

        if (mTimer != null) {
            mTimer.start();
        }

    }

    private static Dialog mCancelableDialog = null;
    private static CountDownTimer mTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long arg0) {
        }

        @Override
        public void onFinish() {
            if (mCancelableDialog != null && mCancelableDialog.isShowing()) {
                mCancelableDialog.dismiss();
                mCancelableDialog = null;
            }
        }
    };

    private static HashMap<Activity,AlertDialog> dialogLists;

    private static void addDialog(Activity mAcitivty,AlertDialog dialog){
        if(dialogLists == null)
            dialogLists = new HashMap<Activity,AlertDialog>();
        dialogLists.put(mAcitivty,dialog);
    }

    private static AlertDialog getExitDialog(Activity mActivity){
        if(dialogLists == null)
            return null;
        AlertDialog mDialog = dialogLists.get(mActivity);
        if(mDialog != null && mDialog.isShowing())
            return mDialog;
        else
            return null;
    }

    public static void cancelDialog(Activity mActivity){
        if(dialogLists == null)
            return;
        AlertDialog mDialog = dialogLists.get(mActivity);
        if(mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        dialogLists.remove(mActivity);
    }
}
