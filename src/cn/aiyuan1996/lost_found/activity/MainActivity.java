package cn.aiyuan1996.lost_found.activity;

import static cn.aiyuan1996.lost_found.R.id.tv_describe;
import static cn.aiyuan1996.lost_found.R.id.tv_photo;
import static cn.aiyuan1996.lost_found.R.id.tv_time;
import static cn.aiyuan1996.lost_found.R.id.tv_title;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import cn.aiyuan1996.lost_found.R;
import cn.aiyuan1996.lost_found.adapter.BaseAdapterHelper;
import cn.aiyuan1996.lost_found.adapter.QuickAdapter;
import cn.aiyuan1996.lost_found.base.EditPopupWindow;
import cn.aiyuan1996.lost_found.bean.Found;
import cn.aiyuan1996.lost_found.bean.Lost;
import cn.aiyuan1996.lost_found.config.Constants;
import cn.aiyuan1996.lost_found.i.IPopupItemClick;

/**
 * Lost/Found
 * 
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-21 上午11:12:36
 */
public class MainActivity extends BaseActivity implements OnClickListener,
		IPopupItemClick, OnItemLongClickListener {

	RelativeLayout layout_action;
	LinearLayout layout_all;
	TextView tv_lost;
	ListView listview;
	Button btn_add;

	protected QuickAdapter<Lost> LostAdapter;// 失物

	protected QuickAdapter<Found> FoundAdapter;// 招领

	private Button layout_found;
	private Button layout_lost;
	PopupWindow morePop;

	RelativeLayout progress;
	LinearLayout layout_no;
	TextView tv_no;

	@Override
	public void setContentView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		progress = (RelativeLayout) findViewById(R.id.progress);
		layout_no = (LinearLayout) findViewById(R.id.layout_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		layout_action = (RelativeLayout) findViewById(R.id.layout_action);
		layout_all = (LinearLayout) findViewById(R.id.layout_all);
		// 默认是失物界面
		tv_lost = (TextView) findViewById(R.id.tv_lost);
		tv_lost.setTag("Lost");
		listview = (ListView) findViewById(R.id.list_lost);
		btn_add = (Button) findViewById(R.id.btn_add);
		// 初始化长按弹窗
		initEditPop();
	}

	@Override
	public void initListeners() {
		// TODO Auto-generated method stub
		listview.setOnItemLongClickListener(this);
		btn_add.setOnClickListener(this);
		layout_all.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == layout_all) {
			showListPop();
		} else if (v == btn_add) {
			Intent intent = new Intent(this, AddActivity.class);
			intent.putExtra("from", tv_lost.getTag().toString());
			startActivityForResult(intent, Constants.REQUESTCODE_ADD);
		} else if (v == layout_found) {
			changeTextView(v);
			morePop.dismiss();
			queryFounds();
		} else if (v == layout_lost) {
			changeTextView(v);
			morePop.dismiss();
			queryLosts();
		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		if (LostAdapter == null) {
			LostAdapter = new QuickAdapter<Lost>(this, R.layout.item_list) {
				@Override
				protected void convert(BaseAdapterHelper helper, Lost lost) {
					helper.setText(tv_title, lost.getTitle())
							.setText(tv_describe, lost.getDescribe())
							.setText(tv_time, lost.getCreatedAt())
							.setText(tv_photo, lost.getPhone());
				}
			};
		}

		if (FoundAdapter == null) {
			FoundAdapter = new QuickAdapter<Found>(this, R.layout.item_list) {
				@Override
				protected void convert(BaseAdapterHelper helper, Found found) {
					helper.setText(tv_title, found.getTitle())
							.setText(tv_describe, found.getDescribe())
							.setText(tv_time, found.getCreatedAt())
							.setText(tv_photo, found.getPhone());
				}
			};
		}
		listview.setAdapter(LostAdapter);
		// 默认加载失物界面
		queryLosts();
	}

	private void changeTextView(View v) {
		if (v == layout_found) {
			tv_lost.setTag("Found");
			tv_lost.setText("Found");
		} else {
			tv_lost.setTag("Lost");
			tv_lost.setText("Lost");
		}
	}

	@SuppressWarnings("deprecation")
	private void showListPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_lost, null);
		// 注入
		layout_found = (Button) view.findViewById(R.id.layout_found);
		layout_lost = (Button) view.findViewById(R.id.layout_lost);
		layout_found.setOnClickListener(this);
		layout_lost.setOnClickListener(this);
		morePop = new PopupWindow(view, mScreenWidth, 600);

		morePop.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					morePop.dismiss();
					return true;
				}
				return false;
			}
		});

		morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		morePop.setTouchable(true);
		morePop.setFocusable(true);
		morePop.setOutsideTouchable(true);
		morePop.setBackgroundDrawable(new BitmapDrawable());
		// 动画效果 从顶部弹下
		morePop.setAnimationStyle(R.style.MenuPop);
		morePop.showAsDropDown(layout_action, 0, -dip2px(this, 2.0F));
	}

	private void initEditPop() {
		mPopupWindow = new EditPopupWindow(this, 200, 48);
		mPopupWindow.setOnPopupItemClickListner(this);
	}

	EditPopupWindow mPopupWindow;
	int position;
	String phone;

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		this.position = position;
		phone = ((TextView) (view.findViewById(R.id.tv_photo))).getText().toString();
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		mPopupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP,
				location[0], getStateBar() + location[1]);
		return false;
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Constants.REQUESTCODE_ADD:// 添加成功之后的回调
			String tag = tv_lost.getTag().toString();
			if (tag.equals("Lost")) {
				queryLosts();
			} else {
				queryFounds();
			}
			break;
		}
	}

	/**
	 * 查询全部失物信息 queryLosts
	 * 
	 * @return void
	 * @throws
	 */
	private void queryLosts() {
		showView();
		BmobQuery<Lost> query = new BmobQuery<Lost>();
		query.order("-createdAt");// 按照时间降序
		query.findObjects(this, new FindListener<Lost>() {

			@Override
			public void onSuccess(List<Lost> losts) {
				// TODO Auto-generated method stub
				LostAdapter.clear();
				FoundAdapter.clear();
				if (losts == null || losts.size() == 0) {
					showErrorView(0);
					LostAdapter.notifyDataSetChanged();
					return;
				}
				progress.setVisibility(View.GONE);
				LostAdapter.addAll(losts);
				listview.setAdapter(LostAdapter);
			}

			@Override
			public void onError(int code, String arg0) {
				// TODO Auto-generated method stub
				showErrorView(0);
			}
		});
	}

	public void queryFounds() {
		showView();
		BmobQuery<Found> query = new BmobQuery<Found>();
		query.order("-createdAt");// 按照时间降序
		query.findObjects(this, new FindListener<Found>() {

			@Override
			public void onSuccess(List<Found> arg0) {
				// TODO Auto-generated method stub
				LostAdapter.clear();
				FoundAdapter.clear();
				if (arg0 == null || arg0.size() == 0) {
					showErrorView(1);
					FoundAdapter.notifyDataSetChanged();
					return;
				}
				FoundAdapter.addAll(arg0);
				listview.setAdapter(FoundAdapter);
				progress.setVisibility(View.GONE);
			}

			@Override
			public void onError(int code, String arg0) {
				// TODO Auto-generated method stub
				showErrorView(1);
			}
		});
	}

	/**
	 * 请求出错或者无数据时候显示的界面 showErrorView
	 * 
	 * @return void
	 * @throws
	 */
	private void showErrorView(int tag) {
		progress.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		layout_no.setVisibility(View.VISIBLE);
		if (tag == 0) {
			tv_no.setText(getResources().getText(R.string.list_no_data_lost));
		} else {
			tv_no.setText(getResources().getText(R.string.list_no_data_found));
		}
	}

	private void showView() {
		listview.setVisibility(View.VISIBLE);
		layout_no.setVisibility(View.GONE);
	}

	@Override
	public void CallPhone(View v) {
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phone));
		startActivity(intent);
	}

	@Override
	public void SendMessage(View v) {
		Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phone));
		startActivity(intent);
	}

}
