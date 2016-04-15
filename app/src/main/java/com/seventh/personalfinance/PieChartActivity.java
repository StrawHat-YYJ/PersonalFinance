package com.seventh.personalfinance;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.seventh.db.Account;
import com.seventh.db.AccountDBdao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PieChartActivity  extends Activity implements SeekBar.OnSeekBarChangeListener {
    private Intent intent = null;
    AccountDBdao accountDBdao;// 数据库
    private List<Account> accounts;// 账单数据
    private String name;// 账号
    private PieChart mChartExpend;
    private PieChart mChartIncome;
    private PieChart chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_piechart);
        intent = this.getIntent();
        name = intent.getStringExtra("name");// 接收主界面的数据

        accountDBdao = new AccountDBdao(getApplicationContext());
        // 时间
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
        int year = c.get(Calendar.YEAR); // 获取年
        int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
        int day = c.get(Calendar.DAY_OF_MONTH); // 获取当前天数
        String time = year + "/" + month + "%";

        accounts = accountDBdao.findSomeTimeByName(name, time);

        mChartExpend = (PieChart) findViewById(R.id.chartExpend);
        mChartExpend.setDescription("当月支出");
        mChartIncome = (PieChart) findViewById(R.id.chartIncome);
        mChartIncome.setDescription("当月收入");
        setChart(mChartExpend);
        setChart(mChartIncome);

        PieData expendData = setData(true);
        PieData incomeData = setData(false);

        mChartExpend.setData(expendData);
        mChartExpend.highlightValues(null);
        mChartExpend.invalidate();

        mChartIncome.setData(incomeData);
        mChartIncome.highlightValues(null);
        mChartIncome.invalidate();

        mChartExpend.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChartIncome.animateY(1400, Easing.EasingOption.EaseInOutQuad);

    }


    private PieData setData(boolean isEarnings) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        HashMap<String ,Float> accountsMap = new HashMap<String,Float>();

        for(Account a :accounts){
            if(a.isEarnings()==isEarnings){
                if(accountsMap.containsKey(a.getType())){
                    Float money= accountsMap.get(a.getType())+a.getMoney();
                    accountsMap.put(a.getType(),money);
                }else{
                    accountsMap.put(a.getType(),a.getMoney());
                }
            }

        }

        Iterator iter =accountsMap.entrySet().iterator();
        int j=0;
        while (iter.hasNext()){

            Map.Entry entry =(Map.Entry)iter.next();
            xVals.add(String.valueOf(entry.getKey()));
            yVals1.add(new Entry((Float)entry.getValue(),j));
            j++;
        }


        PieDataSet dataSet = new PieDataSet(yVals1, "类型");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
        //        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

        if(isEarnings){
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
        }else {
            for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);


        return data;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);

        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);


        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);
    }
}
