package irene.com.framework.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import irene.com.framework.R;

/**
 * Created by Irene on 2015/8/18.
 */
public class TitleBar extends LinearLayout {

	private LayoutInflater mInflater;
	private Resources mRes;
	private ImageButton ib_title_back;
	private TextView tv_title_content;
	private FrameLayout fl_title_right;
	private TextView tv_title_right;
	private ImageView iv_title_right;
	private LinearLayout mTitleView = null;
    private Context mContext;

	public TitleBar(Context context) {
		super(context);
		init(context);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mRes = getResources();
        mContext=context;
		getViewByInflater(context);
	}

	private void getViewByInflater(Context mContext) {

		mInflater = LayoutInflater.from(mContext);
		mTitleView = (LinearLayout) mInflater.inflate(R.layout.titlebar_default, this);
		ib_title_back = (ImageButton) mTitleView.findViewById(R.id.ib_title_back);
		tv_title_content = (TextView) mTitleView.findViewById(R.id.tv_title_content);
		fl_title_right = (FrameLayout) mTitleView.findViewById(R.id.fl_title_right);
		tv_title_right = (TextView) mTitleView.findViewById(R.id.tv_title_right);
		iv_title_right = (ImageView) mTitleView.findViewById(R.id.iv_title_right);
	}

	private void hideView(View view) {
		if (view.getVisibility() == View.VISIBLE)
			view.setVisibility(View.GONE);
	}

	private void showView(View view) {
		if (view.getVisibility() == View.GONE)
			view.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 * Method name: setTitle <BR>
	 * Description: 带有标题和返回键的Title <BR>
	 * Remark: <BR>
	 * 
	 * @param title
	 *            void<BR>
	 */
	public void setTitle(Object title) {
		
		showView(mTitleView);
		showView(tv_title_content);
		showView(ib_title_back);
		hideView(fl_title_right);
		setTitleContent(title);
	}
	/**
	 * 
	 * Method name: setTitleOnly <BR>
	 * Description: 仅带有标题的Title <BR>
	 * Remark: <BR>
	 * @param title  void<BR>
	 */
	public void setTitleOnly(Object title){
		
		showView(mTitleView);
		showView(tv_title_content);
		hideView(ib_title_back);
		hideView(fl_title_right);
		setTitleContent(title);
	}
	
	/**
	 * 
	 * Method name: setTitleAndRight <BR>
	 * Description: 不带左边返回键，右边可以设置的 <BR>
	 * Remark: <BR>
	 * @param title
	 * @param rightTitle
	 * @param rightDrawable  void<BR>
	 */
	
	public void setTitleAndRight(Object title,Object rightTitle,Integer rightDrawable){
		showView(mTitleView);
		showView(tv_title_content);
		hideView(ib_title_back);
		showView(fl_title_right);
		setTitleContent(title);
		setRightContent(rightTitle);
		setRightDrawable(rightDrawable);
	}

    public void setTitleAndRight(Object title,Integer rightDrawable){
        showView(mTitleView);
        showView(tv_title_content);
        hideView(ib_title_back);
        showView(fl_title_right);
        setTitleContent(title);
        setRightDrawable(rightDrawable);
    }


    public void setTitleContent(Object title) {
		if (title instanceof String) {
			tv_title_content.setText((String) title);
		} else {
			tv_title_content.setText(mRes.getString((Integer) title));
		}
	}
	
	public void setRightContent(Object rightTitle){
		showView(tv_title_right);
		showView(fl_title_right);
		if(rightTitle == null){
			hideView(fl_title_right);
			hideView(tv_title_right);
		}else if (rightTitle instanceof String) {
			tv_title_right.setText((String) rightTitle);
		} else {
			tv_title_right.setText(mRes.getString((Integer) rightTitle));
		}
	}

    public void setRightDrawable(Integer rightDrawable){
		showView(fl_title_right);
		showView(iv_title_right);
		if(rightDrawable == null){
			hideView(iv_title_right);
			hideView(fl_title_right);
		}else if (rightDrawable instanceof Integer) {
			iv_title_right.setBackgroundResource((Integer)rightDrawable);
		}
	}

    public void setLeftDrawable(Integer leftDrawable){
        showView(ib_title_back);
        if(leftDrawable == null){
            hideView(ib_title_back);
        }else if (leftDrawable instanceof Integer) {
            ib_title_back.setImageResource(leftDrawable);
        }
    }

	 public void setLeftBtnListener(View.OnClickListener l) {
		 ib_title_back.setOnClickListener(l);
	 }

	 public void setRightBtnListener(View.OnClickListener l) {
		 fl_title_right.setOnClickListener(l);
	 }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

}
