package com.mobile.madassignment;


import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobile.madassignment.Adapter.ExpenseTypeAdapter;
import com.mobile.madassignment.Adapter.ExpenseTypeViewPagerAdapter;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.ExpenseType;
import com.mobile.madassignment.util.DataFormat;
import com.mobile.madassignment.util.SelectPicPopupWindow;
import com.mobile.madassignment.view.NoScrollGridView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mobile.madassignment.util.Constants.UNKNOWN_TYPE;
import static com.mobile.madassignment.util.FirebaseUtil.square_image;

public class AddNewExpenseActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int RC_OCR_CAPTURE = 7012;
    private ImageView ivNewExpenseBack;
    private ViewPager vpNewExpense;
    private ImageView ivNewExpenseState;//page
    private EditText  description;
    private TextView tvInputResult;
    private Uri outputFileUri;
    private ImageView addExpensePhoto;
    private Uri imageUrl;
    private Boolean ImageC=false;
    private Boolean cam;
    private boolean hasPic = false;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatebaseRef;
    private static final int REQUESTCODE_PICK = 0;
    private static final int REQUESTCODE_TAKE = 1;
    //    private static final int REQUESTCODE_CUTTING = 2;
    private SelectPicPopupWindow menuWindow;

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

    private ImageView ivOCR;
    private static final String IMAGE_FILE_NAME = "expense.jpg";
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
        addExpensePhoto = (ImageView)findViewById(R.id.add_expense_photo);
        description = (EditText) findViewById(R.id.ed_new_description);
        ivOCR = (ImageView) findViewById(R.id.iv_input_add);

        ivNewExpenseState = (ImageView) findViewById(R.id.iv_new_record_state);
        group_key = getIntent().getExtras().getString("group_key");
        participants = (HashMap<String,String>)getIntent().getExtras().getSerializable("group_members");
        initViewPagerData();
        initViewListener();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        addExpensePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(AddNewExpenseActivity.this, itemsOnClick);
                menuWindow.showAtLocation(findViewById(R.id.main_layout),
                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        ivOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewExpenseActivity.this, OcrCaptureActivity.class);


                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });


    }


    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                // take photos
                case R.id.takePhotoBtn:
                    Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //
                    takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                    startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                    break;
                // choose photos
                case R.id.pickPhotoBtn:
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                    // image/jpeg 、 image/png...
                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(pickIntent, REQUESTCODE_PICK);
                    break;
                default:
                    break;
            }
        }
    };

    //    public static List<Intent> require_image(Uri outputFileUri,PackageManager p) {
//
//        final List<Intent> cameraIntents = new ArrayList<Intent>();
//        Intent pickIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        final List<ResolveInfo> Im =p.queryIntentActivities(pickIntent, 0);
//        for(ResolveInfo res : Im) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(pickIntent);
//            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        final List<ResolveInfo> Cam =p.queryIntentActivities(captureIntent, 0);
//        for(ResolveInfo res : Cam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
//            cameraIntents.add(intent);
//        }
//
//        return cameraIntents;
//    }
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

                        if(hasPic){


                            InputStream image_stream = null;
                            try {
                                image_stream = getContentResolver().openInputStream(imageUrl);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmap= BitmapFactory.decodeStream(image_stream );
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            newExpense.setPicture(expense_key+".jpg");
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
                            StorageReference imagesRef = storageRef.child(group_key).child(newExpense.getPicture());

                            UploadTask uploadTask = imagesRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("img","upload img fail :"+ e.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("img","upload sucess");
                                }
                            });



                        }
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

        ivInputPoint.setOnTouchListener(this);
        ivInputDelete.setOnTouchListener(this);
        ivInputOk.setOnTouchListener(this);
        ivNewExpenseBack.setOnTouchListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            hasPic = true;
            switch (requestCode) {
                case REQUESTCODE_PICK:// get pics from album
                    imageUrl = data.getData();
                    square_image(getApplicationContext(), addExpensePhoto, imageUrl);
                    break;
                case REQUESTCODE_TAKE:// take photos
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    imageUrl = Uri.fromFile(temp);
                    square_image(getApplicationContext(), addExpensePhoto, imageUrl);
                    break;

            }
        }else if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    Log.d("debug", "Text read: " + text);
                    text = text.replace(',', '.');
                    text = text.replaceAll("\\s+","");
                    try{
                        float cost = Float.parseFloat(DataFormat.myDFloatFormat(Float.parseFloat(text)));
                        result = text;
                        tvInputResult.setText(text);

                    }catch (NumberFormatException e){
                        Log.d("debug","errror :"+ e.getMessage());
                        Toast.makeText(AddNewExpenseActivity.this,"get a invalid cost ( "+ text+ " ), please try again", Toast.LENGTH_LONG).show();
                    }
                    String type = data.getStringExtra("type");
                    if(!type.matches(UNKNOWN_TYPE)){
                        String desc = data.getStringExtra("description");
                        description.setText(desc);
                        if(type.matches("home")){
                            setSelectedStyle(gvList1, 3);
                        }
                    }

                } else {

                    Log.d("debug", "No Text captured, intent data is null");
                }
            } else {
//                statusMessage.setText(String.format(getString(R.string.ocr_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
                Log.d("debug", "OCR error");
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }




    //    public String getRealPathFromURI(Uri contentUri)
//    {
//        String[] proj = { MediaStore.Audio.Media.DATA };
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ) {
                Log.v("Permission","Permission is granted");
                return true;
            } else {

                Log.v("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission","Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Permission","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
}
