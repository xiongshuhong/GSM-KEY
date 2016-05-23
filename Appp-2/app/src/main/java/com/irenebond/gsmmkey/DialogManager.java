package com.irenebond.gsmmkey;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Irene on 2016/1/25.
 */
public class DialogManager {

    /**
     * Method name: showAlert <BR>
     * Remark: <BR>
     * @param contentView
     * @return Dialog<BR>
     */
    public static Dialog showCenterAlert(Activity mActivity, View contentView) {

        DisplayMetrics metric = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = (int)(metric.widthPixels - mActivity.getResources().getDimension(R.dimen.margin_step_24)*2);
        Dialog dlg = new Dialog(mActivity, R.style.alertTheme);
        int cFullFillWidth = width;
        contentView.setMinimumWidth(cFullFillWidth);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.gravity = Gravity.CENTER;
        dlg.onWindowAttributesChanged(lp);
        dlg.setContentView(contentView);
        dlg.setCanceledOnTouchOutside(true);
        dlg.show();
        return dlg;
    }
}
