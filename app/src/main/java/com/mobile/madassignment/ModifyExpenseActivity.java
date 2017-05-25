package com.mobile.madassignment;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mobile.madassignment.Adapter.ExpenseTypeAdapter;
import com.mobile.madassignment.Adapter.ExpenseTypeViewPagerAdapter;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.ExpenseType;
import com.mobile.madassignment.view.NoScrollGridView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ModifyExpenseActivity extends AppCompatActivity implements View.OnClickListener{
    private DatabaseReference mDatebaseRef;
    private DatabaseReference exDatebaseRef;

    private ImageView ivNewExpenseBacke;
    private ImageView ivNewExpenseStatee;//page
    private ImageView ivInputIv0e;
    private ImageView ivInputIv1e;
    private ImageView ivInputIv2e;
    private ImageView ivInputIv3e;
    private ImageView ivInputIv4e;
    private ImageView ivInputIv5e;
    private ImageView ivInputIv6e;
    private ImageView ivInputIv7e;
    private ImageView ivInputIv8e;
    private ImageView ivInputIv9e;
    private ImageView ivInputPointe;
    private ImageView ivInputDeletee;
    private String resulte = "0";
    private ImageView ivInputSave;
    private EditText  descriptione;
    private TextView tvInputResulte;

    private List<ExpenseType> gvList1e = new ArrayList<>();
    private List<View> liste;
    private ExpenseTypeAdapter gvAdapter1e;
    private ExpenseTypeViewPagerAdapter adaptere;
    private ViewPager vpNewExpensee;
    private ExpenseType selectedTypee =null;

    private String group_key;
    public String expense_key;

    String cost;
    //Firebase
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_modify_expense );

        mDatebaseRef = FirebaseDatabase.getInstance().getReference();
        //exDatebaseRef = FirebaseDatabase.getInstance().getReference();

        vpNewExpensee = (ViewPager) findViewById(R.id.vp_new_expensee);
        ivInputIv0e = (ImageView) findViewById(R.id.iv_input_iv0e);
        ivInputIv1e = (ImageView) findViewById(R.id.iv_input_iv1e);
        ivInputIv2e = (ImageView) findViewById(R.id.iv_input_iv2e);
        ivInputIv3e = (ImageView) findViewById(R.id.iv_input_iv3e);
        ivInputIv4e = (ImageView) findViewById(R.id.iv_input_iv4e);
        ivInputIv5e = (ImageView) findViewById(R.id.iv_input_iv5e);
        ivInputIv6e = (ImageView) findViewById(R.id.iv_input_iv6e);
        ivInputIv7e = (ImageView) findViewById(R.id.iv_input_iv7e);
        ivInputIv8e = (ImageView) findViewById(R.id.iv_input_iv8e);
        ivInputIv9e = (ImageView) findViewById(R.id.iv_input_iv9e);

        ivInputPointe = (ImageView) findViewById(R.id.iv_input_pointe);
        ivInputSave = (ImageView) findViewById(R.id.iv_input_save);
        ivInputDeletee = (ImageView) findViewById(R.id.iv_input_deletee);
        ivNewExpenseBacke = (ImageView) findViewById(R.id.iv_new_record_backe);
        tvInputResulte = (TextView) findViewById(R.id.tv_input_resulte);

        descriptione = (EditText) findViewById(R.id.ed_new_descriptione);
        ivNewExpenseStatee = (ImageView) findViewById(R.id.iv_new_record_statee);
        group_key = getIntent().getExtras().getString("group_key");
        expense_key= getIntent().getExtras().getString("expense_key");
        cost= getIntent().getExtras().getString("cost");

        initViewPagerData();
        initViewListener();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        tvInputResulte.setText(cost);
        // Log.d( "tag","result: "+tvInputResult.getText().toString());

    }


    @Override
    public void onClick(View view) {
        //tvInputResulte.setText(getIntent().getExtras().getString("cost"));
        switch (view.getId()) {
            case R.id.iv_input_iv0e:
                if (resulte.startsWith("0")) {
                    if (resulte.length() != 1)
                        resulte += "0";
                }else{
                    resulte += "0";
                }
                break;
            case R.id.iv_input_iv1e:
                if (resulte.equals("0")) {
                    resulte = "1";
                } else {
                    resulte += "1";
                }
                break;
            case R.id.iv_input_iv2e:
                if (resulte.equals("0")) {
                    resulte = "2";
                } else {
                    resulte +=  "2";
                }
                break;
            case R.id.iv_input_iv3e:
                if (resulte.equals("0")) {
                    resulte = "3";
                } else {
                    resulte += "3";
                }
                break;
            case R.id.iv_input_iv4e:
                if (resulte.equals("0")) {
                    resulte = "4";
                } else {
                    resulte += "4";
                }
                break;
            case R.id.iv_input_iv5e:
                if (resulte.equals("0")) {
                    resulte = "5";
                } else {
                    resulte += "5";
                }
                break;
            case R.id.iv_input_iv6e:
                if (resulte.equals("0")) {
                    resulte = "6";
                } else {
                    resulte += "6";
                }
                break;
            case R.id.iv_input_iv7e:
                if (resulte.equals("0")) {
                    resulte = "7";
                } else {
                    resulte += "7";
                }
                break;
            case R.id.iv_input_iv8e:
                if (resulte.equals("0")) {
                    resulte = "8";
                } else {
                    resulte += "8";
                }
                break;
            case R.id.iv_input_iv9e:
                if (resulte.equals("0")) {
                    resulte = "9";
                } else {
                    resulte += "9";
                }
                break;
            case R.id.iv_input_pointe:
                if (!resulte.contains(".")) {
                    resulte += ".";
                }
                break;
            case R.id.iv_input_deletee:
                if (resulte.length() == 1) {
                    resulte = "0";
                } else {
                    resulte = resulte.substring(0, resulte.length() - 1);
                }
                break;
            case R.id.iv_input_save:
                if(user==null){
                    Toast.makeText(ModifyExpenseActivity.this, "please login！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedTypee==null){
                    Toast.makeText(ModifyExpenseActivity.this, "please choose a type！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Float.parseFloat(resulte)==0){
                    Toast.makeText(ModifyExpenseActivity.this, "please enter cost！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(group_key.matches("")){
                    Toast.makeText(ModifyExpenseActivity.this, "service error", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Expense newExpense = new Expense();
                NumberFormat formatter = new DecimalFormat("#.##");
                float cost = Float.parseFloat(resulte);

                newExpense.setCost(cost);

                newExpense.setType(selectedTypee.getName());
                //
                newExpense.setPayer(user.getUid());

                //
                String descriotion = descriptione.getText().toString();
                if(!descriotion.matches("")){
                    newExpense.setDescription(descriptione.getText().toString());
                }else{
                    newExpense.setDescription("");
                }


                final Map<String,Object> exp= new HashMap<>();
                exp.put("cost",newExpense.getCost());
                exp.put("payer",newExpense.getPayer());
                exp.put("type",newExpense.getType());
                exp.put("description",newExpense.getDescription());

                expense_key= getIntent().getExtras().getString("expense_key");
                mDatebaseRef.child("expenses").child(group_key).child(expense_key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mDatebaseRef.child("expenses").child(group_key).child(expense_key).updateChildren(exp);
                        Toast.makeText(ModifyExpenseActivity.this,"Expense "+expense_key+" updated",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                       // Logger.error(TAG, ">>> Error:" + "find onCancelled:" + databaseError);
                    }
                });

                this.finish();
                break;
            case R.id.iv_new_record_backe:
                this.finish();
                break;
        }
        tvInputResulte.setText(resulte);
    }


    private void initViewPagerData() {
        liste = new ArrayList<>();
        //first page(only 1 page implemented)
        View view1 = getLayoutInflater().inflate(R.layout.layout_gridview_viewpager, null);
        final NoScrollGridView gridView1 = (NoScrollGridView) view1.findViewById(R.id.gridview_viewpager);
        gvList1e.add(new ExpenseType(getString(R.string.shopping), R.mipmap.ic_handcart));
        gvList1e.add(new ExpenseType(getString(R.string.trip), R.mipmap.ic_bus));
        gvList1e.add(new ExpenseType(getString(R.string.food), R.mipmap.ic_bowl));
        gvList1e.add(new ExpenseType(getString(R.string.home), R.mipmap.ic_home));
        gvList1e.add(new ExpenseType(getString(R.string.other), R.mipmap.ic_other));
        gvAdapter1e = new ExpenseTypeAdapter(this, gvList1e, R.layout.expense_type_gridview);
        gridView1.setAdapter(gvAdapter1e);
        liste.add(view1);

        adaptere = new ExpenseTypeViewPagerAdapter(liste);
        vpNewExpensee.setAdapter(adaptere);

        //listener
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //test
                setSelectedStyle(gvList1e, position);
            }
        });

    }


    private void setSelectedStyle(List<ExpenseType> gvList, int position) {
        for (ExpenseType model : gvList1e) {
            model.setSelected(false);
        }

        gvList.get(position).setSelected(true);
        gvAdapter1e.notifyDataSetChanged();

        selectedTypee = gvList.get(position);
    }


    private  void initViewListener(){

        ivInputIv0e.setOnClickListener(this);
        ivInputIv1e.setOnClickListener(this);
        ivInputIv2e.setOnClickListener(this);
        ivInputIv3e.setOnClickListener(this);
        ivInputIv4e.setOnClickListener(this);
        ivInputIv5e.setOnClickListener(this);
        ivInputIv6e.setOnClickListener(this);
        ivInputIv7e.setOnClickListener(this);
        ivInputIv8e.setOnClickListener(this);
        ivInputIv9e.setOnClickListener(this);
        //ivInputAdd.setOnClickListener(this);
        ivInputPointe.setOnClickListener(this);
        ivInputDeletee.setOnClickListener(this);
        ivInputSave.setOnClickListener(this);
        tvInputResulte.setText(cost);
        ivNewExpenseBacke.setOnClickListener(this);
    }


}




