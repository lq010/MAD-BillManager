package com.mobile.madassignment;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.madassignment.Adapter.ExpenseTypeAdapter;
import com.mobile.madassignment.Adapter.ExpenseTypeViewPagerAdapter;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.ExpenseType;
import com.mobile.madassignment.util.DataFormat;
import com.mobile.madassignment.view.NoScrollGridView;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddNewExpenseActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView ivNewExpenseBack;
    private ViewPager vpNewExpense;
    private ImageView ivNewExpenseState;//page
    private EditText  description;
    private TextView tvInputResult;

    private DatabaseReference mDatebaseRef;

    private ImageView ivInputIv0;
    private ImageView ivInputIv1;
    private ImageView ivInputIv2;
    private ImageView ivInputIv3;
    private ImageView ivInputIv4;
    private ImageView ivInputIv5;
    private ImageView ivInputIv6;
    private ImageView ivInputIv7;
    private ImageView ivInputIv8;
    private ImageView ivInputIv9;
    private ImageView ivInputPoint;
    private ImageView ivInputDelete;

    private ImageView ivInputOk;

    private ImageView ivInputAdd;

    private String group_key;
    private HashMap<String, String> participants;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private String result = "0";

    private List<View> list;

    private List<ExpenseType> gvList1 = new ArrayList<>();
    private ExpenseTypeAdapter gvAdapter1;
    private ExpenseTypeViewPagerAdapter adapter;
    private ExpenseType selectedType =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_new_expense);

        mDatebaseRef = FirebaseDatabase.getInstance().getReference();
        vpNewExpense = (ViewPager) findViewById(R.id.vp_new_expense);
        ivInputIv0 = (ImageView) findViewById(R.id.iv_input_iv0);
        ivInputIv1 = (ImageView) findViewById(R.id.iv_input_iv1);
        ivInputIv2 = (ImageView) findViewById(R.id.iv_input_iv2);
        ivInputIv3 = (ImageView) findViewById(R.id.iv_input_iv3);
        ivInputIv4 = (ImageView) findViewById(R.id.iv_input_iv4);
        ivInputIv5 = (ImageView) findViewById(R.id.iv_input_iv5);
        ivInputIv6 = (ImageView) findViewById(R.id.iv_input_iv6);
        ivInputIv7 = (ImageView) findViewById(R.id.iv_input_iv7);
        ivInputIv8 = (ImageView) findViewById(R.id.iv_input_iv8);
        ivInputIv9 = (ImageView) findViewById(R.id.iv_input_iv9);
        ivInputPoint = (ImageView) findViewById(R.id.iv_input_point);
        ivInputOk = (ImageView) findViewById(R.id.iv_input_ok);
        ivInputDelete = (ImageView) findViewById(R.id.iv_input_delete);
        ivNewExpenseBack = (ImageView) findViewById(R.id.iv_new_record_back);
        tvInputResult = (TextView) findViewById(R.id.tv_input_result);

        description = (EditText) findViewById(R.id.ed_new_description);

        ivNewExpenseState = (ImageView) findViewById(R.id.iv_new_record_state);
        group_key = getIntent().getExtras().getString("group_key");
        participants = (HashMap<String,String>)getIntent().getExtras().getSerializable("group_members");
        initViewPagerData();
        initViewListener();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:

                switch (view.getId()) {

                    case R.id.iv_input_iv0:
                        ivInputIv0.setImageResource(R.mipmap.c_c35);
                        if (result.startsWith("0")) {
                            if (result.length() != 1)
                                result += "0";
                        }else{
                            result += "0";
                        }

                        break;
                    case R.id.iv_input_iv1:
                        ivInputIv1.setImageResource(R.mipmap.c_c20);
                        if (result.equals("0")) {
                            result = "1";
                        } else {
                            result += "1";
                        }

                        break;
                    case R.id.iv_input_iv2:
                        ivInputIv2.setImageResource(R.mipmap.c_c21);
                        if (result.equals("0")) {
                            result = "2";
                        } else {
                            result += "2";
                        }

                        break;
                    case R.id.iv_input_iv3:
                        ivInputIv3.setImageResource(R.mipmap.c_c22);
                        if (result.equals("0")) {
                            result = "3";
                        } else {
                            result += "3";
                        }

                        break;
                    case R.id.iv_input_iv4:
                        ivInputIv4.setImageResource(R.mipmap.c_c23);
                        if (result.equals("0")) {
                            result = "4";
                        } else {
                            result += "4";
                        }

                        break;
                    case R.id.iv_input_iv5:
                        ivInputIv5.setImageResource(R.mipmap.c_c24);
                        if (result.equals("0")) {
                            result = "5";
                        } else {
                            result += "5";
                        }

                        break;
                    case R.id.iv_input_iv6:
                        ivInputIv6.setImageResource(R.mipmap.c_c25);
                        if (result.equals("0")) {
                            result = "6";
                        } else {
                            result += "6";
                        }

                        break;
                    case R.id.iv_input_iv7:
                        ivInputIv7.setImageResource(R.mipmap.c_c26);
                        if (result.equals("0")) {
                            result = "7";
                        } else {
                            result += "7";
                        }

                        break;
                    case R.id.iv_input_iv8:
                        ivInputIv8.setImageResource(R.mipmap.c_c27);
                        if (result.equals("0")) {
                            result = "8";
                        } else {
                            result += "8";
                        }

                        break;
                    case R.id.iv_input_iv9:
                        ivInputIv9.setImageResource(R.mipmap.c_c28);
                        if (result.equals("0")) {
                            result = "9";
                        } else {
                            result += "9";
                        }
                        break;
                    case R.id.iv_input_point:
                        ivInputPoint.setImageResource(R.mipmap.c_c36);
                        if (!result.contains(".")) {
                            result += ".";
                        }
                        break;
                    case R.id.iv_input_delete:
                        ivInputDelete.setImageResource(R.mipmap.c_c03);
                        if (result.length() == 1) {
                            result = "0";
                        } else {
                            result = result.substring(0, result.length() - 1);
                        }
                        break;
                    case R.id.iv_input_ok:
                        ivInputOk.setImageResource(R.mipmap.c_c37);
                        if(user==null){
                            Toast.makeText(AddNewExpenseActivity.this, "please login！", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if(selectedType==null){
                            Toast.makeText(AddNewExpenseActivity.this, "please chose a type！", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if(Float.parseFloat(result)==0){
                            Toast.makeText(AddNewExpenseActivity.this, "please enter cost！", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if(group_key.matches("")){
                            Toast.makeText(AddNewExpenseActivity.this, "service error", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        String expense_key = mDatebaseRef.push().getKey();
                        Expense newExpense = new Expense();
                        float cost = Float.parseFloat(DataFormat.myDFloatFormat(Float.parseFloat(result)));

                        newExpense.setCost(cost);

                        newExpense.setType(selectedType.getName());
                        //
                        newExpense.setPayer(user.getUid());
                        newExpense.setParticipants(participants);
                        newExpense.setId(expense_key);
                        //
                        String descriotion = description.getText().toString();
                        if(!descriotion.matches("")){
                            newExpense.setDescription(description.getText().toString());
                        }else{
                            newExpense.setDescription("");
                        }

                        mDatebaseRef.child("expenses").child(group_key).child(expense_key).setValue(newExpense);

                        this.finish();
                        break;
                    case R.id.iv_new_record_back:
                        this.finish();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (view.getId()){
                    case R.id.iv_input_iv0:
                        ivInputIv0.setImageResource(R.mipmap.c_c31);
                        break;
                    case R.id.iv_input_iv1:
                        ivInputIv1.setImageResource(R.mipmap.c_c04);
                        break;
                    case R.id.iv_input_iv2:
                        ivInputIv2.setImageResource(R.mipmap.c_c05);
                        break;
                    case R.id.iv_input_iv3:
                        ivInputIv3.setImageResource(R.mipmap.c_c06);
                        break;
                    case R.id.iv_input_iv4:
                        ivInputIv4.setImageResource(R.mipmap.c_c07);
                        break;
                    case R.id.iv_input_iv5:
                        ivInputIv5.setImageResource(R.mipmap.c_c08);
                        break;
                    case R.id.iv_input_iv6:
                        ivInputIv6.setImageResource(R.mipmap.c_c09);
                        break;
                    case R.id.iv_input_iv7:
                        ivInputIv7.setImageResource(R.mipmap.c_c10);
                        break;
                    case R.id.iv_input_iv8:
                        ivInputIv8.setImageResource(R.mipmap.c_c11);
                        break;
                    case R.id.iv_input_iv9:
                        ivInputIv9.setImageResource(R.mipmap.c_c12);
                        break;
                    case R.id.iv_input_point:
                        ivInputPoint.setImageResource(R.mipmap.c_c32);
                        break;
                    case R.id.iv_input_delete:
                        ivInputDelete.setImageResource(R.mipmap.c_c02);
                        break;
                    case R.id.iv_input_ok:
                        ivInputOk.setImageResource(R.mipmap.c_c33);
                        break;
                }
                break;
        }

        tvInputResult.setText(result);
        return true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    private void initViewPagerData() {
        list = new ArrayList<>();
        //first page(only 1 page implemented)
        View view1 = getLayoutInflater().inflate(R.layout.layout_gridview_viewpager, null);
        final NoScrollGridView gridView1 = (NoScrollGridView) view1.findViewById(R.id.gridview_viewpager);
        gvList1.add(new ExpenseType(getString(R.string.shopping), R.mipmap.ic_handcart));
        gvList1.add(new ExpenseType(getString(R.string.trip), R.mipmap.ic_bus));
        gvList1.add(new ExpenseType(getString(R.string.food), R.mipmap.ic_bowl));
        gvList1.add(new ExpenseType(getString(R.string.home), R.mipmap.ic_home));
        gvList1.add(new ExpenseType(getString(R.string.other), R.mipmap.ic_other));
        gvAdapter1 = new ExpenseTypeAdapter(this, gvList1, R.layout.expense_type_gridview);
        gridView1.setAdapter(gvAdapter1);
        list.add(view1);

        adapter = new ExpenseTypeViewPagerAdapter(list);
        vpNewExpense.setAdapter(adapter);

        //listener
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setSelectedStyle(gvList1, position);
            }
        });

    }


    private void setSelectedStyle(List<ExpenseType> gvList, int position) {
        for (ExpenseType model : gvList1) {
            model.setSelected(false);
        }

        gvList.get(position).setSelected(true);
        gvAdapter1.notifyDataSetChanged();

        selectedType = gvList.get(position);
    }
    private void initViewListener() {

        tvInputResult.setText(result);
//        ivNewExpenseBack.setOnClickListener(this);

        ivInputIv0.setOnTouchListener(this);
        ivInputIv1.setOnTouchListener(this);
        ivInputIv2.setOnTouchListener(this);
        ivInputIv3.setOnTouchListener(this);
        ivInputIv4.setOnTouchListener(this);
        ivInputIv5.setOnTouchListener(this);
        ivInputIv6.setOnTouchListener(this);
        ivInputIv7.setOnTouchListener(this);
        ivInputIv8.setOnTouchListener(this);
        ivInputIv9.setOnTouchListener(this);
        //ivInputAdd.setOnTouchListener(this);
        ivInputPoint.setOnTouchListener(this);
        ivInputDelete.setOnTouchListener(this);
        ivInputOk.setOnTouchListener(this);
        ivNewExpenseBack.setOnTouchListener(this);
    }


//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        switch (motionEvent.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                switch (view.getId()){
//                    case R.id.iv_input_iv0:
//                        ivInputIv0.setImageResource(R.mipmap.c_c35);
//                        break;
//                    case R.id.iv_input_iv1:
//                        ivInputIv1.setImageResource(R.mipmap.c_c20);
//                        break;
//                    case R.id.iv_input_iv2:
//                        ivInputIv2.setImageResource(R.mipmap.c_c21);
//                        break;
//                    case R.id.iv_input_iv3:
//                        ivInputIv3.setImageResource(R.mipmap.c_c22);
//                        break;
//                    case R.id.iv_input_iv4:
//                        ivInputIv4.setImageResource(R.mipmap.c_c23);
//                        break;
//                    case R.id.iv_input_iv5:
//                        ivInputIv5.setImageResource(R.mipmap.c_c24);
//                        break;
//                    case R.id.iv_input_iv6:
//                        ivInputIv6.setImageResource(R.mipmap.c_c25);
//                        break;
//                    case R.id.iv_input_iv7:
//                        ivInputIv7.setImageResource(R.mipmap.c_c26);
//                        break;
//                    case R.id.iv_input_iv8:
//                        ivInputIv8.setImageResource(R.mipmap.c_c27);
//                        break;
//                    case R.id.iv_input_iv9:
//                        ivInputIv9.setImageResource(R.mipmap.c_c28);
//                        break;
//                    case R.id.iv_input_point:
//                        ivInputPoint.setImageResource(R.mipmap.c_c36);
//                        break;
//                    case R.id.iv_input_delete:
//                        ivInputDelete.setImageResource(R.mipmap.c_c03);
//                        break;
//                    case R.id.iv_input_ok:
//                        ivInputOk.setImageResource(R.mipmap.c_c37);
//                        break;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                switch (view.getId()){
//                    case R.id.iv_input_iv0:
//                        ivInputIv0.setImageResource(R.mipmap.c_c31);
//                        break;
//                    case R.id.iv_input_iv1:
//                        ivInputIv1.setImageResource(R.mipmap.c_c04);
//                        break;
//                    case R.id.iv_input_iv2:
//                        ivInputIv2.setImageResource(R.mipmap.c_c05);
//                        break;
//                    case R.id.iv_input_iv3:
//                        ivInputIv3.setImageResource(R.mipmap.c_c06);
//                        break;
//                    case R.id.iv_input_iv4:
//                        ivInputIv4.setImageResource(R.mipmap.c_c07);
//                        break;
//                    case R.id.iv_input_iv5:
//                        ivInputIv5.setImageResource(R.mipmap.c_c08);
//                        break;
//                    case R.id.iv_input_iv6:
//                        ivInputIv6.setImageResource(R.mipmap.c_c09);
//                        break;
//                    case R.id.iv_input_iv7:
//                        ivInputIv7.setImageResource(R.mipmap.c_c10);
//                        break;
//                    case R.id.iv_input_iv8:
//                        ivInputIv8.setImageResource(R.mipmap.c_c11);
//                        break;
//                    case R.id.iv_input_iv9:
//                        ivInputIv9.setImageResource(R.mipmap.c_c12);
//                        break;
//                    case R.id.iv_input_point:
//                        ivInputPoint.setImageResource(R.mipmap.c_c32);
//                        break;
//                    case R.id.iv_input_delete:
//                        ivInputDelete.setImageResource(R.mipmap.c_c02);
//                        break;
//                    case R.id.iv_input_ok:
//                        ivInputOk.setImageResource(R.mipmap.c_c33);
//                        break;
//                }
//                break;
//        }
//        return true;
//
//
//    }
}
