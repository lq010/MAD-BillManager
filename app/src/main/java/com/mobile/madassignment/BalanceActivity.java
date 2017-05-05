package com.mobile.madassignment;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Calendar;
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
    //chart
    private BarChart barChart;
    private ArrayList<BarEntry> barEntries;
    private Map<Integer,Map<Integer,Float>> monthlyExpenses;

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

        barChart = (BarChart)view2.findViewById(R.id.balance_bar);

        bt_settle.setOnClickListener(this);
        bt_back.setOnClickListener(this);
        balanceList.setOnClickListener(this);
        balanceChart.setOnClickListener(this);
        pageview =new ArrayList<View>();
        memberMap = new ConcurrentHashMap<>();
        monthlyExpenses= new HashMap<>();
        pageview.add(view1);
        pageview.add(view2);

        barEntries = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view1.getContext());
        rv_balance_list.setLayoutManager(layoutManager);

        //
        group_key = getIntent().getExtras().getString("group_key");

        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override

            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override

            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
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
                    if(init==0)
                    initBarPage();
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
        final List<GroupMember> members = new ArrayList<>();
        balanceListViewAdapter = new BalanceListViewAdapter(members,BalanceActivity.this);

        mFirebaseRef.child("balances").child(group_key).orderByChild("settledUp").equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                       Log.d("balances",dataSnapshot.getChildren().toString());
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Log.d("balances",data.getKey().toString());
                            newestBalanceKey = data.getKey().toString();
                            for(DataSnapshot balance : data.child("balance").getChildren()){

                                if(memberMap.containsKey(balance.getKey())){
                                    Log.v("balanceyyy", balance.getValue().toString());
                                    memberMap.get(balance.getKey()).setBalance(Float.parseFloat(balance.getValue().toString()));
                                }else{
                                    //TODO
                                }
                            }

                        }
                        for(GroupMember member: memberMap.values()){
//                            member.setSpending(spending);
                            if(!members.contains(member))
                                 members.add(member);
                        }

                        balanceListViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mFirebaseRef.child("expenses").child(group_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Expense expense = data.getValue(Expense.class);
                            total_spending += expense.getCost();


                            //expense per month
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(expense.getCreateTime());


                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH)+1;

                            if(monthlyExpenses.containsKey(year)){
                                if(monthlyExpenses.get(year).containsKey(month)){
                                    float temp = monthlyExpenses.get(year).get(month) + expense.getCost();
                                    monthlyExpenses.get(year).put(month,temp);
                                }else {
                                    monthlyExpenses.get(year).put(month,expense.getCost());
                                }

                            }else{
                                Map<Integer,Float> temp2 = new HashMap();
                                temp2.put(month,expense.getCost());
                                monthlyExpenses.put(year,temp2);
                            }
                            Log.v("year month",  year +" "+month+ " "+ monthlyExpenses.get(year).get(month));


                        }
//
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        rv_balance_list.setAdapter(balanceListViewAdapter);

    }

    private void initBarPage(){
//        for(int i : monthlyExpenses.get(2017).keySet()){
//            barEntries.add(new BarEntry( monthlyExpenses.get(2017).get(i),i));
//        }
        ArrayList<String> month = new ArrayList<>();
        for(int j=1 ;j<13;++j){
            if(monthlyExpenses.containsKey(2017)){
                if(monthlyExpenses.get(2017).containsKey(j)){
                    barEntries.add(new BarEntry( monthlyExpenses.get(2017).get(j),j-1));
                }else{
                    barEntries.add(new BarEntry( 0,j-1));
                }
            }else{
                barEntries.add(new BarEntry( 0,j-1));
            }

            month.add(j+"");
        }


        BarDataSet barDataSet = new BarDataSet(barEntries,"year 2017");
        BarData barData = new BarData(month,barDataSet);
        barChart.setData(barData);
        init = 1;
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

                settleThedebt();
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

    private void settleThedebt() {
        new AlertDialog.Builder(this)
                .setTitle("Settle the debt")
                .setMessage("Balance will be reset to 0, are you want settle the debt?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("settleThedebt", "key "+newestBalanceKey);

                        if(newestBalanceKey!=null && isBalanceNotZero()){

                            mFirebaseRef.child("balances").child(group_key).child(newestBalanceKey).child("settledUp").setValue(true);
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
