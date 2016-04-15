package com.seventh.personalfinance;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import com.github.mikephil.charting.charts.LineChart;
import com.seventh.db.AccountDBdao;
import com.seventh.db.PersonDBdao;
import com.seventh.view.CornerListView;
import com.seventh.view.DataRange;
import com.seventh.view.MainActivityService;
import com.seventh.view.PieChart;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener,
		OnTouchListener, OnGestureListener {
	private Intent intent = null;// 定义一个意图
	private String name;// 账号
	private String pwd1;
	private String pwd2;
	AccountDBdao accountDBdao;// 数据库
	PersonDBdao persondbdao;

	private EditText mEditTextName;// 账号
	private EditText mEditTextPwd1;// 密码
	private EditText mEditTextPwd2;// 密码
	private Button mButtonOK;
	private Button mButtonCancel;

	private TextView mTextViewTime;// 时间
	private Button mButtonAddNodes;// 记一笔按钮
	private Button mButtonPie;//饼图按钮
	private Button mButtonLine;//曲线图按钮

	private PieChart piechart;// 饼形图
	private LinearLayout piechar;
	private int varlue1;// 收入比例
	private int varlue2;// 支出比例

	private float totalOut;// 总支出
	private float totalInto;// 总收入

	private CornerListView cornerListView1 = null;// 自定义listview1
	private List<Map<String, String>> map_list1 = null;

	private CornerListView cornerListView2 = null;// 自定义listview2
	private List<DataRange> dataRanges;
	private LayoutInflater inflater;

	private LinearLayout ll_right;
	private LinearLayout ll_left;
	private GestureDetector mGestureDetector; // 手势检测器

	private int window_width; // 屏幕的宽度
	private static float SNAP_VELOCITY = 400; // x方向上滑动的距离
	private int SPEED = 30; // 滑动的速度
	private int MAX_WIDTH = 0; // 滑动的最大距离
	private int mScrollX;
	private boolean isScrolling = false;
	private boolean isFinish = true;
	private boolean isMenuOpen = false;
	private boolean hasMeasured = false; // 是否Measured.

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainactivity);

		intent = this.getIntent();
		name = intent.getStringExtra("name");// 接收登录界面的数据
		if (name == null) {
			intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
		} else {

			// 设置时间
			mTextViewTime = (TextView) this.findViewById(R.id.tv_main_time);
			mTextViewTime.setText(GetTime());// 设置时间

			accountDBdao = new AccountDBdao(getApplicationContext());
			totalOut = accountDBdao.fillTotalOut(name);// 总支出
			totalInto = accountDBdao.fillTotalInto(name);// 总收入

			// 设置饼形图
			SetVarlue(totalOut, totalInto);
			piechar = (LinearLayout) findViewById(R.id.barchart);
			piechart = new PieChart();
			piechart.paintingPieChart(getApplicationContext(), piechar,
					varlue1, varlue2);

			// 设置listview1 值
			cornerListView1 = (CornerListView) findViewById(R.id.lv_main_calculation);
			map_list1 = MainActivityService.getDataSource1(totalInto, totalOut);
			// listview1适配器
			SimpleAdapter adapter1 = new SimpleAdapter(getApplicationContext(),
					map_list1, R.layout.main_listview_calculation,
					new String[] { "txtCalculationName", "txtMoney" },
					new int[] { R.id.ls_tv_txtCalculationName,
							R.id.ls_tv_txtMoney });
			// 填充listview1的数据
			cornerListView1.setAdapter(adapter1);

			// 设置listview2 值
			inflater = LayoutInflater.from(this);
			cornerListView2 = (CornerListView) findViewById(R.id.lv_main_datareport);
			try {
				dataRanges = MainActivityService.getDataSource2(name,
						getApplicationContext());
			} catch (Exception e) {
				Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			// 填充listview2的数据
			cornerListView2.setAdapter(new MyAdapter());

			
			// listview1选项的点击事件 收入总额 支出总额 预算余额
			cornerListView1.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						TotalIntoData();
						break;
					case 1:
						TotalOutData();
						break;
					case 2:
						TotalAllData();
						break;
					}

				}
			});

			// listview2选项的点击事件      一览表
			cornerListView2.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						TodayData();
						break;
					case 1:
						MonthData();
						break;
					case 2:
						YearData();
						break;
					}
				}
			});

			mEditTextName = (EditText) this.findViewById(R.id.et_main_username);
			mEditTextName.setText(name);
			mEditTextPwd1 = (EditText) this.findViewById(R.id.et_main_new_pwd);
			mEditTextPwd2 = (EditText) this
					.findViewById(R.id.et_main_confirm_pwd);
			mButtonOK = (Button) this.findViewById(R.id.bt_main_ok);
			mButtonCancel = (Button) this.findViewById(R.id.bt_main_cancel);
			mButtonAddNodes = (Button) this.findViewById(R.id.bt_main_addnotes);
			mButtonPie = (Button) this.findViewById(R.id.bt_main_pie);
			mButtonLine = (Button) this.findViewById(R.id.bt_main_line);
			mButtonOK.setOnClickListener(this);
			mButtonCancel.setOnClickListener(this);
			mButtonAddNodes.setOnClickListener(this);

			mButtonPie.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					intent = new Intent();
					intent .setClass(MainActivity.this, PieChartActivity.class);
					intent.putExtra("name", name);
					// 传值 帐户名
					startActivity(intent);
				}
			});

			mButtonLine.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					intent = new Intent();
					intent .setClass(MainActivity.this, LineChartActivity.class);
					intent.putExtra("name", name);
					// 传值 帐户名
					startActivity(intent);

				}
			});

			ll_right = (LinearLayout) findViewById(R.id.layout_right);// 滑动的菜单
			ll_left = (LinearLayout) findViewById(R.id.layout_left);
			ll_right.setOnTouchListener(this);
			mGestureDetector = new GestureDetector(this);
			mGestureDetector.setIsLongpressEnabled(false);// 禁用长按监听
			getMaxX();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_main_addnotes:
			intent = new Intent(this, AddNodes.class);
			intent.putExtra("name", name);
			// 传值 帐户名
			startActivity(intent);
			break;
		case R.id.bt_main_ok:
			name = mEditTextName.getText().toString();
			pwd1 = mEditTextPwd1.getText().toString().trim();
			pwd2 = mEditTextPwd2.getText().toString().trim();

			if (name.equals("")) {
				Toast.makeText(getApplicationContext(), "账户名不能为空！",
						Toast.LENGTH_SHORT).show();
				break;
			}
			if (pwd1.equals("")) {
				Toast.makeText(getApplicationContext(), "密码不能为空！",
						Toast.LENGTH_SHORT).show();
				break;
			}
			if (!pwd1.equals(pwd2)) {
				Toast.makeText(getApplicationContext(), "确认密码不同！",
						Toast.LENGTH_SHORT).show();
				break;
			}
			persondbdao = new PersonDBdao(getApplicationContext());
			persondbdao.update(name, name, pwd2);
			Toast.makeText(getApplicationContext(), "修改成功,请重新登录。", Toast.LENGTH_SHORT)
					.show();
			intent = new Intent(MainActivity.this,Login.class);
			startActivity(intent);
//			finish();
			break;
		case R.id.bt_main_cancel:
			persondbdao = new PersonDBdao(getApplicationContext());
			persondbdao.updateLoginCancel(name);
			intent = new Intent(this, Login.class);
			startActivity(intent);
			finish();
			break;
		}
	}

	// 跳转到收入账单
	public void TotalIntoData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "收入账单");
		startActivity(intent);
	}

	// 跳转到支出账单
	public void TotalOutData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "支出账单");
		startActivity(intent);
	}

	// 跳转到详细账单
	public void TotalAllData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "详细账单");
		startActivity(intent);
	}

	// 跳转到今日账单
	public void TodayData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "今日账单");
		startActivity(intent);
	}

	// 跳转到本月账单
	public void MonthData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "本月账单");
		startActivity(intent);
	}

	// 跳转到本年账单
	public void YearData() {
		intent = new Intent(this, SpecificData.class);
		intent.putExtra("name", name);
		intent.putExtra("title", "本年账单");
		startActivity(intent);
	}

	// 设置时间
	public String GetTime() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
		int year = c.get(Calendar.YEAR); // 获取年
		int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		int day = c.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		String time = year + "/" + month + "/" + day;
		return time;
	}

	
	// 设置饼形图比例
	public void SetVarlue(float totalInto, float totalOut) {
		// 根据总支出与总收入变化饼形图
		if ((totalInto - totalOut) < 0) {
			varlue1 = 0;
			varlue2 = 1;
		} else if (totalInto == totalOut) {
			varlue1 = 1;
			varlue2 = 1;
		} else {
			varlue1 = (int) totalInto;
			varlue2 = (int) totalOut;
		}
	}

	// listview2适配器
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataRanges.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return dataRanges.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.main_listview_datareport,
					null);
			DataRange aboutBillData = dataRanges.get(position);

			TextView tv2_text1 = (TextView) view
					.findViewById(R.id.ls_tv2_txtDataRange);
			TextView tv2_text2 = (TextView) view
					.findViewById(R.id.ls_tv2_txtInto);
			TextView tv2_text3 = (TextView) view
					.findViewById(R.id.ls_tv2_txtOut);
			tv2_text1.setText(aboutBillData.getText1());
			tv2_text2.setText(aboutBillData.getText2());
			tv2_text3.setText(aboutBillData.getText3());

			return view;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTextViewTime.setText(GetTime());// 设置时间

		totalOut = accountDBdao.fillTotalOut(name);// 总支出
		totalInto = accountDBdao.fillTotalInto(name);// 总收入
		SetVarlue(totalInto, totalOut);
		piechart = new PieChart();
		piechart.paintingPieChart(getApplicationContext(), piechar, varlue1,
				varlue2);

		map_list1 = MainActivityService.getDataSource1(totalInto, totalOut);
		// listview1适配器
		SimpleAdapter adapter1 = new SimpleAdapter(getApplicationContext(),
				map_list1, R.layout.main_listview_calculation, new String[] {
						"txtCalculationName", "txtMoney" }, new int[] {
						R.id.ls_tv_txtCalculationName, R.id.ls_tv_txtMoney });
		// 填充listview1的数据
		cornerListView1.setAdapter(adapter1);

		try {
			dataRanges = MainActivityService.getDataSource2(name,
					getApplicationContext());
		} catch (Exception e) {
			Toast.makeText(this, "获取数据失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		// 填充listview2的数据
		cornerListView2.setAdapter(new MyAdapter());
	}

	void getMaxX() {// 得到滑动的最大宽度,即此layout的宽度

		ViewTreeObserver viewTreeObserver = ll_right.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_width = getWindowManager().getDefaultDisplay()
							.getWidth(); // 屏幕宽度
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
							.getLayoutParams(); // layout参数
					layoutParams.width = window_width;
					ll_right.setLayoutParams(layoutParams);
					hasMeasured = true;
					MAX_WIDTH = ll_left.getWidth();// 左边layout宽度
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mScrollX = 0;
		isScrolling = false;
		return true;// 将之改为true，不然事件不会向下传递.
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int currentX = (int) e2.getX();
		int lastX = (int) e1.getX();

		if (isMenuOpen) {

			if (!isScrolling && currentX - lastX >= 0) {

				return false;
			}
		} else {

			if (!isScrolling && currentX - lastX <= 0) {

				return false;

			}
		}

		boolean suduEnough = false;

		if (velocityX > MainActivity.SNAP_VELOCITY
				|| velocityX < -MainActivity.SNAP_VELOCITY) {

			suduEnough = true;

		} else {

			suduEnough = false;

		}

		doCloseScroll(suduEnough);

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (isFinish)
			doScrolling(distanceX);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if (isFinish) {

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
					.getLayoutParams();
			// 左移动
			if (layoutParams.leftMargin >= MAX_WIDTH) {
				new AsynMove().execute(-SPEED);
			} else {
				// 右移动
				new AsynMove().execute(SPEED);
			}
		}
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	public void doScrolling(float distanceX) {
		isScrolling = true;
		mScrollX += distanceX;// distanceX:向左为正，右为负
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
				.getLayoutParams();

		layoutParams.leftMargin -= mScrollX;
		layoutParams.rightMargin += mScrollX;

		if (layoutParams.leftMargin <= 0) {
			isScrolling = false;// 拖过头了不需要再执行AsynMove了
			layoutParams.leftMargin = 0;
			layoutParams.rightMargin = 0;

		} else if (layoutParams.leftMargin >= MAX_WIDTH) {
			// 拖过头了不需要再执行AsynMove了
			isScrolling = false;
			layoutParams.leftMargin = MAX_WIDTH;
		}
		ll_right.setLayoutParams(layoutParams);
		ll_left.invalidate();
	}

	public void doCloseScroll(boolean suduEnough) {
		if (isFinish) {

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
					.getLayoutParams();

			int tempSpeed = SPEED;

			if (isMenuOpen) {
				tempSpeed = -tempSpeed;
			}

			if (suduEnough
					|| (!isMenuOpen && (layoutParams.leftMargin > window_width / 2))
					|| (isMenuOpen && (layoutParams.leftMargin < window_width / 2))) {

				new AsynMove().execute(tempSpeed);

			} else {

				new AsynMove().execute(-tempSpeed);

			}

		}
	}

	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			isFinish = false;
			int times = 0;
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// 整除
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// 有余数

			for (int i = 0; i < times; i++) {
				publishProgress(params[0]);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			isFinish = true;
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
					.getLayoutParams();
			if (layoutParams.leftMargin >= MAX_WIDTH) {
				isMenuOpen = true;
			} else {
				isMenuOpen = false;
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_right
					.getLayoutParams();
			// 右移动
			if (values[0] > 0) {
				layoutParams.leftMargin = Math.min(layoutParams.leftMargin
						+ values[0], MAX_WIDTH);
				layoutParams.rightMargin = Math.max(layoutParams.rightMargin
						- values[0], -MAX_WIDTH);

			} else {
				// 左移动
				layoutParams.leftMargin = Math.max(layoutParams.leftMargin
						+ values[0], 0);

			}
			ll_right.setLayoutParams(layoutParams);
			ll_left.invalidate();
		}

	}
private long exitTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			long currentTime = new Date().getTime();
			if (currentTime - exitTime < 2000) {
				finish();
			} else {
				Toast.makeText(this, "再按一次退出大学生理财~", Toast.LENGTH_SHORT).show();
				exitTime = currentTime;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
