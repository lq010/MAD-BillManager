package com.mobile.madassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.madassignment.Adapter.ExpenseViewHolder;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.util.SimpleDividerItemDecoration;
import com.mobile.madassignment.util.UserNameMap;

import java.util.HashMap;

public class AllExpensesActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> mFirebaseAdapter;
    private DatabaseReference mDatabaseRef;
    private String group_key;
    private FirebaseUser user;
    private HashMap<String, String> userId_nameMap = UserNameMap.getUserId_nameMap();
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView myExpenseRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllExpensesActivity.this.finish();
            }
        });

        myExpenseRecyclerView = (RecyclerView) findViewById(R.id.expenseRecyclerView);
        setSupportActionBar(toolbar);
        if(getIntent().getExtras()!=null)
            group_key = getIntent().getExtras().getString("group_key");






        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabaseRef  = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Expense, ExpenseViewHolder>(
                Expense.class,
                R.layout.expense_item,
                ExpenseViewHolder.class,
                mDatabaseRef.child("expenses").child(group_key)) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, final Expense expense, final int position) {
                Log.v("expense.type",expense.getType());
                Log.v("expense.cost",expense.getCost()+"");
//                viewHolder.view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Expense clickedExpense = mFirebaseAdapter.getItem(position);
//                        Intent intent = new Intent(AllExpensesActivity.this,ExpenseDetailActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("group_key",group_key);
//                        bundle.putString("group_name",groupName);
//                        bundle.putString("currency",currency);
//                        bundle.putSerializable("expense",expense);
//                        intent.putExtras(bundle);
//                        startActivity(intent);
//                    }
//                });

                String type = expense.getType();
                if(expense.getDescription().matches("")||expense.getDescription()==null){
                    viewHolder.type.setText(type);

                }else {
                    viewHolder.type.setText(expense.getDescription());
                }
                viewHolder.cost.setText(expense.getCost()+"" );
                if(expense.getPayer().matches(user.getUid())){  //TODO
                    viewHolder.payer.setText("you payed ");
                    viewHolder.payer.setTextColor(Color.GREEN);
                }else {

                    viewHolder.payer.setText( userId_nameMap.get(expense.getPayer())+ " payed ");
                    viewHolder.payer.setTextColor(Color.RED);
                }
                switch (type){
                    case "home":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_home);
                        break;
                    case "trip":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_bus);
                        break;
                    case "shopping" :
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_handcart);
                        break;
                    case "food":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_bowl);
                        break;
                    case "other":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_other);
                        break;
                }
                viewHolder.createTime.setText(expense.getFormatDate());

                if( expense.getStatus().matches("deleted")){
                    viewHolder.deletedMark.setVisibility(View.VISIBLE);
                    viewHolder.deleteMark_line.setVisibility(View.VISIBLE);
                }



            }
        };

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        myExpenseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        myExpenseRecyclerView.setLayoutManager(mLinearLayoutManager);
        myExpenseRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
