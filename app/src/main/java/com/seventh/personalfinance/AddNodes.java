package com.seventh.personalfinance;

import java.util.Calendar;
import java.util.TimeZone;

import com.seventh.db.AccountDBdao;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddNodes extends Activity implements OnClickListener {
	private String name;// 账号
	AccountDBdao accountDBdao;

	private EditText mEditTextMoney;// 金额
	private EditText mEditTextRemark;// 备注
	private EditText mEditTextTime; // 时间
	private Spinner mSpinnerType; // 类型
	private Spinner mSpinnerEarnings; // 收益
	private Button mButtonAdd;
	private Button mButtonCancel;

	private String time;
	private float money;
	private String type;
	private boolean earning;
	private String remark;

//	private static final String[] types = { "餐饮", "衣饰", "教育", "医疗","通讯", "交通", "娱乐", "人情", "投资", "生活费","奖学金","勤工俭学","兼职","补贴","其他" };
	private static final String[] incomeTypes = { "亲人给予","奖学金","勤工俭学","兼职","补助","借贷款","其他" };
	private static final String[] expendTypes = { "餐饮","衣饰","教育","医疗","通讯","交通","娱乐","人情","投资","其他" };

	private static final String[] earnings = { "支出", "收入" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addnodes);

		Intent intent = this.getIntent();
		name = intent.getStringExtra("name");// 接收登录界面的数据

		mEditTextMoney = (EditText) this.findViewById(R.id.et_addnodes_money);// 金额
		mEditTextRemark = (EditText) this.findViewById(R.id.et_addnodes_remark);// 备注
		mEditTextTime = (EditText) this.findViewById(R.id.et_addnodes_time);// 时间

		final  Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
		int year = c.get(Calendar.YEAR); // 获取年
		int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		int day = c.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		time = year + "/" + month + "/" + day;// 获取系统当前时间
		mEditTextTime.setText(time);// 设置时间

		mEditTextTime.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘
		mEditTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			int focusCount=0;
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus&&focusCount!=0){
					DatePickerDialog dialog = new DatePickerDialog(AddNodes.this, new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

							mEditTextTime.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
						}

					},
							c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
					dialog.show();
				}
				focusCount++;
			}
		});

		mEditTextTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(AddNodes.this, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mEditTextTime.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);

					}

				},
						c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				dialog.show();
			}
		});

		// 类型
		mSpinnerType = (Spinner) this.findViewById(R.id.sp_addnodes_type);
		// 将可选内容与mSpinnerType连接起来
		final ArrayAdapter<String> expendAdapterType = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, expendTypes);
		final ArrayAdapter<String> incomeAdapterType = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, incomeTypes);

		// 设置下拉列表的风格
//		adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expendAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		incomeAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapterType 添加到mSpinnerType中
		mSpinnerType.setAdapter(expendAdapterType);

		// 收益
		mSpinnerEarnings = (Spinner) this
				.findViewById(R.id.sp_addnodes_earnings);
		ArrayAdapter<String> adapterEarnings = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, earnings);
		adapterEarnings
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerEarnings.setAdapter(adapterEarnings);

		mButtonAdd = (Button) this.findViewById(R.id.bt_addnodes_add);
		mButtonCancel = (Button) this.findViewById(R.id.bt_addnodes_cancel);
		mButtonAdd.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);

		mSpinnerEarnings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if(id ==0){
					mSpinnerType.setAdapter(expendAdapterType);
				}else{
					mSpinnerType.setAdapter(incomeAdapterType);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addnodes_add:// 添加按钮
			if (mEditTextMoney.getText().toString().trim().equals("")) {
				Toast.makeText(getApplicationContext(), "金额不能为空！", 0).show();
				break;
			} else {
				money = Float.parseFloat(mEditTextMoney.getText().toString()
						.trim());
			}
			time = mEditTextTime.getText().toString().trim();
			type = mSpinnerType.getSelectedItem().toString();
			if (mSpinnerEarnings.getSelectedItem().toString().equals("收入")) {
				earning = true;
			} else {
				earning = false;
			}
			remark = mEditTextRemark.getText().toString().trim();

			accountDBdao = new AccountDBdao(getApplicationContext());
			accountDBdao.add(time, money, type, earning, remark, name);
			Toast.makeText(getApplicationContext(), "添 加 账 单 条 目 成 功 ！", 0)
					.show();
			break;
		case R.id.bt_addnodes_cancel:// 取消按钮
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("name", name);
			// 传值 帐户名
			startActivity(intent);
			break;

		}

	}

}
