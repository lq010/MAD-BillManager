package com.mobile.madassignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.mobile.madassignment.models.Group;
import com.mobile.madassignment.models.GroupMember;

import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import static java.util.Currency.getAvailableCurrencies;
import static java.util.Currency.getInstance;


public class AddGroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button done;
    private ImageView back;
    private EditText et_groupName;
    private Spinner s_currency;
    private DatabaseReference mFirebaseDatabaseReference;

    private Currency group_currency;


    private int eur_position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        done = (Button)findViewById(R.id.bt_add_group);
        back = (ImageView)findViewById(R.id.iv_new_group_back);
        et_groupName = (EditText)findViewById(R.id.et_group_name);
        s_currency = (Spinner) findViewById(R.id.currency_spinner);


        Set<Currency> myset = getAvailableCurrencies();
        List<Currency> myarray = new ArrayList<>(myset);
        ArrayAdapter<Currency> currencyAdapter = new ArrayAdapter<Currency>(this,
                android.R.layout.simple_spinner_dropdown_item,
                myarray
                );

        // currency spinner
        s_currency.setAdapter(currencyAdapter);
        s_currency.setOnItemSelectedListener(this);
        for(Currency i: myarray){

            if(i.equals(getInstance("EUR"))){
                break;
            }
            eur_position++;
        }
        s_currency.setSelection(eur_position);


        //cancel button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGroupActivity.this.finish();
            }
        });

        // submit button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = et_groupName.getText().toString();
                if(name.trim().matches("")){
                   Toast.makeText(getApplicationContext(),"please enter a group name",Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calendar = new GregorianCalendar();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
                Group newGroup = new Group(name,
                        s_currency.getSelectedItem().toString(),null);
                ///TODO get the usrid
                Map<String,String> group_member = new HashMap<String, String>();
                group_member.put("userid1","User1");
                newGroup.setMembers(group_member);
                ////
                String groudId =  mFirebaseDatabaseReference.push().getKey();
                mFirebaseDatabaseReference.child("groups").child(groudId).setValue(newGroup);


                AddGroupActivity.this.finish();

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
