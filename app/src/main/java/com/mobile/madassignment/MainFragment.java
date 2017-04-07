package com.mobile.madassignment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.madassignment.models.Expense;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public TextView type;
        public ImageView typeImgView;
        public TextView cost;
       //public CircleImageView messengerImageView;

        public ExpenseViewHolder(View v) {
            super(v);
            type = (TextView) itemView.findViewById(R.id.tv_item_item_type);
            typeImgView = (ImageView) itemView.findViewById(R.id.iv_expense_type_img);
            cost = (TextView) itemView.findViewById(R.id.tv_item_item_cost);

        }
    }

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView myExpenseRecyclerView;
    private ListView main_lv;
    private FloatingActionButton add_expense;
    private String group_key;
    private DatabaseReference mDatabaseRef;
    private FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> mFirebaseAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Bundle bundle = this.getArguments();
        if(bundle!= null){
            group_key = bundle.getString("group_key");
            Log.v("get_arg ", group_key);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_main, container, false);

        add_expense = (FloatingActionButton) v.findViewById(R.id.bt_add_expense);
        myExpenseRecyclerView = (RecyclerView) v.findViewById(R.id.expenseRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mLinearLayoutManager.setStackFromEnd(false);
        myExpenseRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDatabaseRef  = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Expense, ExpenseViewHolder>(
                Expense.class,
                R.layout.expense_item,
                ExpenseViewHolder.class,
                mDatabaseRef.child("expenses").child(group_key)) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, Expense expense, int position) {
                Log.v("expense.type",expense.getType());
                Log.v("expense.cost",expense.getCost()+"");
                String type = expense.getType();
                viewHolder.type.setText(type);
                viewHolder.cost.setText("- " +expense.getCost());
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

                if(expense.getDescription()!=null){
                    //// TODO: 07/04/2017  
                }
            }
        };
        myExpenseRecyclerView.setLayoutManager(mLinearLayoutManager);
        myExpenseRecyclerView.setAdapter(mFirebaseAdapter);



        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AddExpenseFragment addExpenseFragment = AddExpenseFragment.newInstance("1", "2");
//                FragmentManager manager = getActivity().getSupportFragmentManager();
//                manager.beginTransaction().replace(R.id.main_content, addExpenseFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putString("group_key",group_key);
                Intent intent = new Intent(getActivity(),AddNewExpenseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
