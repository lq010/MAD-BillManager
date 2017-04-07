package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.madassignment.R;
import com.mobile.madassignment.models.ExpenseType;

import java.util.List;

/**
 * Created by lq on 05/04/2017.
 */

public class ExpenseTypeAdapter extends CommonAdapter<ExpenseType>{
    public ExpenseTypeAdapter(Context context, List<ExpenseType> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void convert(ViewHolders holder, ExpenseType item) {
        ((ImageView)holder.getView(R.id.iv_item_new_record_gridview_icon)).setImageResource(item.getResId());
        ((TextView)holder.getView(R.id.tv_item_new_record_gridview_des)).setText(item.getName());
        ImageView ivFlag = holder.getView(R.id.iv_flag_new_record);
        if(item.isSelected()){
            ivFlag.setVisibility(View.VISIBLE);
        }else{
            ivFlag.setVisibility(View.GONE);
        }
    }
}