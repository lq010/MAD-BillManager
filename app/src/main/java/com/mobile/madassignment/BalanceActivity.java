package com.mobile.madassignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.IntegerRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.madassignment.Adapter.BalanceListViewAdapter;
import com.mobile.madassignment.Adapter.BalenceListViewHolder;
import com.mobile.madassignment.Adapter.ExpenseViewHolder;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.Group;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.models.UpdateExpenseListEvent;
import com.mobile.madassignment.util.BarChartItemMarker;
import com.mobile.madassignment.util.DayAxisValueFormatter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BalanceActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView balanceList;
    private TextView balanceChart;
    private Spinner yearSpinner;

    private ImageView scrollbar;

    private int offset = 0;

    private int currIndex = 0;
    private int init=0;

    private int bmpW;

    private int one;
    private ImageView bt_back;
    private Button bt_settle;
    private RecyclerView rv_balance_list;
    private DatabaseReference mFirebaseRef;

    private BalanceListViewAdapter balanceListViewAdapter ;

    private String group_key;
    private float total_spending;
    private int total_members;
    private Map<String, GroupMember> memberMap;
    private String newestBalanceKey;
    //BarChart
    private BarChart barChart;

    //PieChart
    private PieChart pieChart;


    private boolean settled =false;//activity result, if true, refresh the mainfragmeng
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.balance_list, null);
        View view2 = inflater.inflate(R.layout.balance_chart, null);

        balanceList = (TextView)findViewById(R.id.balance_list);
        balanceChart = (TextView)findViewById(R.id.balance_chart);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        bt_back =(ImageView) findViewById(R.id.iv_balance_back);
        bt_settle = (Button) view1.findViewById(R.id.bt_settleUp);
        rv_balance_list = (RecyclerView)view1.findViewById(R.id.rv_balance_list);
        //chart
        pieChart = (PieChart) view2.findViewById(R.id.balance_pieChart);
        barChart = (BarChart)view2.findViewById(R.id.balance_barChart);
        yearSpinner = (Spinner) view2.findViewById(R.id.barChart_year_spinner);

        bt_settle.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        balanceList.setOnClickListener(this);
        balanceChart.setOnClickListener(this);
        pageview =new ArrayList<View>();
        memberMap = new ConcurrentHashMap<>();

        pageview.add(view1);
        pageview.add(view2);


        LinearLayoutManager layoutManager = new LinearLayoutManager(view1.getContext());
        rv_balance_list.setLayoutManager(layoutManager);

        //
        group_key = getIntent().getExtras().getString("group_key");

        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override

            public int getCount() {

                return pageview.size();
            }

            @Override

            public boolean isViewFromObject(View arg0, Object arg1) {

                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };

        viewPager.setAdapter(mPagerAdapter);

        viewPager.setCurrentItem(0);
        initListpage();
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //get the width of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenW = displayMetrics.widthPixels;


        offset = (screenW / 2 - bmpW) / 2;

        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);

        scrollbar.setImageMatrix(matrix);



    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:

                    animation = new TranslateAnimation(one, 0, 0, 0);
                    if(group_key!=null)


                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                 //   if(init==0)
//                    initBarPage();
                    pieChart.animateXY(1500,600);
                    barChart.animateXY(1500,1500);
                    break;
            }

            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(200);
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    private void initListpage() {
        /*[balance list]*/
        //get group names
        mFirebaseRef  = FirebaseDatabase.getInstance().getReference();
        DatabaseReference groupMembersRef = mFirebaseRef.child("groups").child(group_key).child("members");
        //get group members
        groupMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                total_members = (int)dataSnapshot.getChildrenCount();

                for(DataSnapshot data :dataSnapshot.getChildren()){
                    GroupMember member = new GroupMember();
                    member.setName(data.getValue().toString());
                    memberMap.put(data.getKey(),member);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO fail
            }
        });

        //balance list
        final List<GroupMember> members = new ArrayList<>();
        balanceListViewAdapter = new BalanceListViewAdapter(members,BalanceActivity.this);
        rv_balance_list.setAdapter(balanceListViewAdapter);

        mFirebaseRef.child("balances").child(group_key).orderByChild("settledUp").equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.d("balances",dataSnapshot.getChildren().toString());
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Log.d("balances",data.getKey().toString());
                            newestBalanceKey = data.getKey().toString();
                            for(DataSnapshot balance : data.child("balance").getChildren()){

                                if(memberMap.containsKey(balance.getKey())){
                                    Log.v("balanceyyy", balance.child("balance").getValue().toString());
                                    memberMap.get(balance.getKey()).setBalance(Float.parseFloat(balance.child("balance").getValue().toString()));
                                }else{
                                    //TODO
                                }
                            }

                        }
                        for(GroupMember member: memberMap.values()){
                            if(!members.contains(member))
                                 members.add(member);
                        }

                        balanceListViewAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        /*[balance list end]*/
        /*[chart data]*/
        //Pie cahrt
        final List<PieEntry> pieEntries = new ArrayList<PieEntry>();
        final Map<String, Float> categoriesPieChartMap = new HashMap<>();
        //bar chart
        final List<Integer> years = new ArrayList<>();
        final ArrayAdapter<Integer> yearListAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_dropdown_item,
                years
        );
        yearSpinner.setAdapter(yearListAdapter);
        final Map<Integer, ArrayList<BarEntry>> year_barEntryMap = new HashMap<>();
        final Map<Integer,Map<Integer,Float>> year_monthExpenseMap= new HashMap<>();
        DayAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
        BarChartItemMarker mv = new BarChartItemMarker(this, xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart

        final int CurrentYear = Calendar.getInstance().get(Calendar.YEAR);
//        years.add(2015);//test value
//        year_barEntryMap.put(2015,new ArrayList<BarEntry>());
        mFirebaseRef.child("expenses").child(group_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Expense expense = data.getValue(Expense.class);
                            //if expense is valid
                            if(!expense.getStatus().matches("deleted")){
                                total_spending += expense.getCost();
                                //
                                if(categoriesPieChartMap.containsKey(expense.getType())){
                                    float old = categoriesPieChartMap.get(expense.getType());
                                    categoriesPieChartMap.put(expense.getType(), expense.getCost()+old);
                                }else{
                                    categoriesPieChartMap.put(expense.getType(), expense.getCost());
                                }

                                //expense per month
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(expense.getCreateTime());


                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH)+1;

                                if(year_monthExpenseMap.containsKey(year)){
                                    if(year_monthExpenseMap.get(year).containsKey(month)){
                                        float temp = year_monthExpenseMap.get(year).get(month) + expense.getCost();
                                        year_monthExpenseMap.get(year).put(month,temp);
                                    }else {
                                        year_monthExpenseMap.get(year).put(month,expense.getCost());
                                    }

                                }else{
                                    Map<Integer,Float> newYear = new HashMap();
                                    newYear.put(month,expense.getCost());
                                    year_monthExpenseMap.put(year,newYear);
                                }
                                Log.v("year month",  year +" "+month+ " "+ year_monthExpenseMap.get(year).get(month));

                            }

                        }
                        //all expenses loaded
                        //init Pie Chart
                        for(String label : categoriesPieChartMap.keySet()){
                            pieEntries.add(new PieEntry(categoriesPieChartMap.get(label), label));
                        }
                        PieDataSet dataSet = new PieDataSet(pieEntries, "(categories)");
                        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                        dataSet.setValueLinePart1OffsetPercentage(80.f);
                        dataSet.setValueLinePart1Length(0.2f);
                        dataSet.setValueLinePart2Length(0.4f);
                        dataSet.setValueTextSize(13);
                        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                        PieData data = new PieData(dataSet);
                        Description description = new Description();
                        description.setText("total spending: " + total_spending);
                        pieChart.setDescription(description);

                        pieChart.setData(data);
                        pieChart.invalidate();
                        //init Bar chart

                        for (int year: year_monthExpenseMap.keySet()){
                            Log.v("year",year+"");
                            years.add(year);
                            final ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
                            for(int month = 1; month <12;month++){
                                if(year_monthExpenseMap.get(year).containsKey(month)){
                                    yVals.add(new BarEntry(month,year_monthExpenseMap.get(year).get(month)));
                                }else{
                                    yVals.add(new BarEntry(month,0f));
                                }
                            }

                            year_barEntryMap.put(year,yVals);

                        }


                        yearListAdapter.notifyDataSetChanged();
                        yearSpinner.setSelection(yearListAdapter.getCount());
                        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                drawBarChart(year_barEntryMap.get(yearSpinner.getSelectedItem()),(int)yearSpinner.getSelectedItem());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        drawBarChart(year_barEntryMap.get(CurrentYear),CurrentYear);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        final RectF mOnValueSelectedRectF = new RectF();
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;

                RectF bounds = mOnValueSelectedRectF;
                barChart.getBarBounds((BarEntry) e, bounds);
                MPPointF position = barChart.getPosition(e, YAxis.AxisDependency.LEFT);


                MPPointF.recycleInstance(position);
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void drawBarChart(ArrayList<BarEntry> yVals, int year) {
        BarDataSet set1;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals, "The year "+year);

            set1.setDrawIcons(true);
            //set1.setStackLabels(mMonths);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData barData = new BarData(dataSets);

            barData.setValueTextSize(10f);

            barData.setBarWidth(0.9f);

            Description description1 = new Description();
            description1.setText("spending per month");
            barChart.setDescription(description1);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            barChart.setData(barData);
        }
        barChart.invalidate();
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.balance_list:
                viewPager.setCurrentItem(0);
                break;
            case R.id.balance_chart:
                viewPager.setCurrentItem(1);
                break;
            case R.id.bt_settleUp:

                settleTheDebt();
                break;
            case R.id.iv_balance_back:
                Intent result = new Intent();
                result.putExtra("refresh",settled);//
                if(settled){
                    setResult(Activity.RESULT_OK,result);
                }else{
                    setResult(Activity.RESULT_CANCELED,result);
                }

               BalanceActivity.this.finish();
                break;
        }
    }

    private void settleTheDebt() {
        new AlertDialog.Builder(this)
                .setTitle("Settle the debt")
                .setMessage("Balance will be reset to 0, are you want settle the debt?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("settleThedebt", "key "+newestBalanceKey);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user==null){
                            Toast.makeText(BalanceActivity.this,"please login",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(newestBalanceKey!=null && isBalanceNotZero()){

                            mFirebaseRef.child("balances").child(group_key).child(newestBalanceKey).child("settledUp").setValue(true);
                            mFirebaseRef.child("balances").child(group_key).child(newestBalanceKey).child("settledBy").setValue(user.getUid());
                            //for activity result
                            settled = true;

                        }else{
                            Toast.makeText(BalanceActivity.this,"Don't need to settle up with others",Toast.LENGTH_SHORT).show();
                        }
                    }


                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    private boolean isBalanceNotZero() {
        for(GroupMember member: memberMap.values()){
            float balance = member.getBalance();
            if(balance!=0){
                return true;
            }
        }
        return false;
    }
}
