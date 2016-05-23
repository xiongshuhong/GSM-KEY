package com.irenebond.gsmmkey;

import android.content.pm.PackageManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.irenebond.gsmmkey.base.NetBaseActivity;

/**
 * Created by Irene on 2016/3/24.
 */
public class ReviewUrlActivity extends NetBaseActivity{

    WebView wv_url;
    private String url = "http://www.waferstar.com/en/GSM-KEY.html";
    @Override
    public void installViews() {

        super.installViews();
        setContentView(R.layout.activity_photo_review_standard);
        wv_url = (WebView) findViewById(R.id.wv_url);
        wv_url.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        wv_url.getSettings().setSupportZoom(true);

        // 设置默认缩放方式尺寸是far

        wv_url.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        // 设置出现缩放工具

        wv_url.getSettings().setBuiltInZoomControls(true);

        // 让网页自适应屏幕宽度

        WebSettings webSettings = wv_url.getSettings(); // webView: 类WebView的实例
		webSettings.setUseWideViewPort(true);      //设置加载进来的页面自适应手机屏幕（可缩放）
		webSettings.setLoadWithOverviewMode(true);

		wv_url.setScrollbarFadingEnabled(false);
		wv_url.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		wv_url.setMapTrackballToArrowKeys(true); // use trackball directly
		// Enable the built-in zoom
		wv_url.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
		wv_url.getSettings().setGeolocationEnabled(true);
		wv_url.getSettings().setJavaScriptEnabled(true);
		wv_url.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		wv_url.getSettings().setDomStorageEnabled(true);// 不可用的话有可能导致网页内有的元素无法加载
		wv_url.getSettings().setNeedInitialFocus(true);
		wv_url.getSettings().setBuiltInZoomControls(true);
		wv_url.getSettings().setSupportMultipleWindows(true);
		wv_url.getSettings().setLoadWithOverviewMode(true);


		final PackageManager pm = getPackageManager();
		wv_url.setFocusable(true);
		wv_url.setFocusableInTouchMode(true);
		wv_url.setClickable(true);
		wv_url.setLongClickable(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_url.loadUrl(url);
    }

}
