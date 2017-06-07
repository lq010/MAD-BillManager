package com.mobile.madassignment.util;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.mobile.madassignment.R;


import java.text.DecimalFormat;

/**
 * Created by lq on 07/06/2017.
 */

public class BarChartItemMarker extends MarkerView {


    private TextView tvContent;
    private DayAxisValueFormatter xAxisValueFormatter;

    private DecimalFormat format;
    public BarChartItemMarker(Context context, DayAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = (TextView) findViewById(R.id.tvContent);
        format = new DecimalFormat("###.00");
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText(xAxisValueFormatter.getMonthName((int)e.getX()) + " Spending: " + format.format(e.getY()));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
