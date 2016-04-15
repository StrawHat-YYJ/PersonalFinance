package com.seventh.personalfinance;

import com.seventh.db.Account;
import com.seventh.db.AccountDBdao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MoreAction extends Activity implements OnClickListener {
	private Intent intent = null;// 定义一个意图
	AccountDBdao accountDBdao;// 数据库
	private String name;// 账号
	private String title;// 标题
	private String id;// id

	private EditText mEditTextMoney;// 金额
	private EditText mEditTextRemark;// 备注
	private EditText mEditTextTime; // 时间
	private Spinner mSpinnerType; // 类型
	private Spinner mSpinnerEarnings; // 收益
	private Button mButtonUpdate;
	private Button mButtonDelete;
	private Button mButtonCancel;

	private String time;
	private float money;
	private String type;
	private boolean earning;
	private String remark;

	private Account account;

	private static final String[] types = { "餐饮", "衣饰", "教育", "医疗",
		"通讯", "交通", "娱乐", "人情", "投资", "生活费","奖学金","勤工俭学","兼职","补贴","其他"  };
	private static final String[] earnings = { "支出", "收入" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_action);

		intent = this.getIntent();
		name = intent.getStringExtra("name");// 接收具体数据界面的数据
		title = intent.getStringExtra("title");// 接收具体数据界面的数据
		id = intent.getStringExtra("id");// 接收具体数据界面面的数据

		mEditTextMoney = (EditText) this.findViewById(R.id.et_moreaction_money);// 金额
		mEditTextRemark = (EditText) this
				.findViewById(R.id.et_moreaction_remark);// 备注
		mEditTextTime = (EditText) this.findViewById(R.id.et_moreaction_time);// 时间

		// 类型
		mSpinnerType = (Spinner) this.findViewById(R.id.sp_moreaction_type);
		// 将可选内容与mSpinnerType连接起来
		ArrayAdapter<String> adapterType = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, types);
		// 设置下拉列表的风格
		adapterType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapterType 添加到mSpinnerType中
		mSpinnerType.setAdapter(adapterType);

		// 收益
		mSpinnerEarnings = (Spinner) this
				.findViewById(R.id.sp_moreaction_earnings);
		ArrayAdapter<String> adapterEarnings = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, earnings);
		adapterEarnings
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerEarnings.setAdapter(adapterEarnings);

		mButtonUpdate = (Button) this.findViewById(R.id.bt_moreaction_update);
		mButtonDelete = (Button) this.findViewById(R.id.bt_moreaction_delete);
		mButtonCancel = (Button) this.findViewById(R.id.bt_moreaction_cancel);
		mButtonUpdate.setOnClickListener(this);
		mButtonDelete.setOnClickListener(this);
		mButtonCancel.setOnClickListener(this);

		// 设置初始显示的值
		accountDBdao = new AccountDBdao(getApplicationContext());
		account = accountDBdao.findInfoById(id);
		mEditTextMoney.setText(account.getMoney() + "");// 金额
		mEditTextRemark.setText(account.getRemark());// 备注
		mEditTextTime.setText(account.getTime()); // 时间
		if (account.getType().equals("餐饮")) {
			mSpinnerType.setSelection(0);
		} else if (account.getType().equals("衣饰")) {
			mSpinnerType.setSelection(1);
		} else if (account.getType().equals("教育")) {
			mSpinnerType.setSelection(2);
		} else if (account.getType().equals("医疗")) {
			mSpinnerType.setSelection(3);
		} else if (account.getType().equals("通讯")) {
			mSpinnerType.setSelection(4);
		} else if (account.getType().equals("交通")) {
			mSpinnerType.setSelection(5);
		} else if (account.getType().equals("娱乐")) {
			mSpinnerType.setSelection(6);
		} else if (account.getType().equals("人情")) {
			mSpinnerType.setSelection(7);
		} else if (account.getType().equals("投资")) {
			mSpinnerType.setSelection(8);
		} else if (account.getType().equals("生活费")) {
			mSpinnerType.setSelection(9);
		} else if (account.getType().equals("奖学金")) {
			mSpinnerType.setSelection(10);
		} else if (account.getType().equals("勤工俭学")) {
			mSpinnerType.setSelection(11);
		} else if (account.getType().equals("兼职")) {
			mSpinnerType.setSelection(12);
		} else if (account.getType().equals("补贴")) {
			mSpinnerType.setSelection(13);
		} else if (account.getType().equals("其他")) {
			mSpinnerType.setSelection(14);
		} 	
		else {
			mSpinnerType.setSelection(9);
		}
		// 收益
		if (account.isEarnings()) {
			mSpinnerEarnings.setSelection(1);
		} else {
			mSpinnerEarnings.setSelection(0);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_moreaction_update:// 修改按钮
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
			accountDBdao.update(id, time, money, type, earning, remark);
			Toast.makeText(getApplicationContext(), "修 改 成  功 ！", 0).show();
			break;
		case R.id.bt_moreaction_delete:// 删除按钮
			accountDBdao = new AccountDBdao(getApplicationContext());
			accountDBdao.delete(id);
			Toast.makeText(getApplicationContext(), "删 除  成 功 ！", 0).show();
			finish();
			break;
		case R.id.bt_moreaction_cancel:// 取消按钮
			Intent intent = new Intent(this, SpecificData.class);
			intent.putExtra("name", name);
			intent.putExtra("title", title);
			// 传值 帐户名
			startActivity(intent);
			finish();
			break;
		}

	}

}
