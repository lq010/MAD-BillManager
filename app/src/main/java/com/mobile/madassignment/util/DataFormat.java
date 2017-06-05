package com.mobile.madassignment.util;

import java.text.DecimalFormat;

/**
 * Created by lq on 14/04/2017.
 */

public  class DataFormat {
    public static String myDFloatFormat(float i){
        DecimalFormat df = new DecimalFormat("0.00");
        return  df.format(i);
    }



}

