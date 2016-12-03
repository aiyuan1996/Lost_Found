package cn.aiyuan1996.lost_found.activity;

import cn.aiyuan1996.lost_found.config.Constants;
import cn.bmob.v3.Bmob;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

/**
 * @author aiyuan
 *
 */
public abstract class BaseActivity extends Activity{
	protected int mScreenWidth;
	protected int mScreenHeight;
	public static final String TAG = "BaseActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ��ʼ�� Bmob SDK
        // ʹ��ʱ�뽫�ڶ�������Application ID�滻������Bmob�������˴�����Application ID
		Bmob.initialize(this, Constants.Bmob_APPID);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//��ȡ��ǰ��Ļ�߶�
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenHeight = metric.heightPixels;
		mScreenWidth = metric.widthPixels;
		setContentView();
		initViews();
		initListeners();
		initData();
	}

	public abstract void initData();

	public abstract void initListeners() ;

	public abstract void initViews();

	public abstract void setContentView() ;
	
	Toast mToast;

	public void ShowToast(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (mToast == null) {
				mToast = Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT);
			} else {
				mToast.setText(text);
			}
			mToast.show();
		}
	}
	
	

	/**
	 * ��ȡ��ǰ״̬���ĸ߶�
	 * @return
	 */
	public int getStateBar(){
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
	public static int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}

}
