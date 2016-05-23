package irene.com.framework.observer;

import android.view.View;

/**
 * Created by Irene on 2015/8/18.
 */

public interface BaseFragmentObserver {
	/**
	 * 
	 * Method name: installViews <BR>
	 * Description: 控件初始化 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void installViews();

	/**
	 * 
	 * Method name: installViews <BR>
	 * Description: 控件监听注册 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void registerEvents();

	/**
	 * 
	 * Method name: reload <BR>
	 * Description: 请求失败后的重新回调 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void reload();

	/**
	 * 
	 * Method name: showEmptyView <BR>
	 * Description: 无数据时显示的布局 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void showEmptyView();

	/**
	 * 
	 * Method name: showReloadView <BR>
	 * Description: 请求失败时候重新请求页面 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void showReloadView(String failStr);

	/**
	 * 
	 * Method name: showNormalView <BR>
	 * Description: 请求成功时显示正常数据页面 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void showNormalView();

	/**
	 * 
	 * Method name: showLoadingView <BR>
	 * Description: 请求中加载数据页面 <BR>
	 * Remark: <BR>
	 * void<BR>
	 */
	public void showLoadingView();

	/**
	 * 
	 * Method name: setContentView <BR>
	 * Description: 设置layid <BR>
	 * Remark: <BR>
	 * 
	 * @param resourceId
	 *            void<BR>
	 */
	public void setContentView(int resourceId);
}
