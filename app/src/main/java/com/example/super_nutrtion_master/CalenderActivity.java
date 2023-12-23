package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//此為行事曆頁面的主畫面
public class CalenderActivity extends AppCompatActivity {
    private Button monthPicker, back_button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        BindById();
        initial_month();
        back();
        choose_month();
    }

    public void BindById(){
        monthPicker = findViewById(R.id.MonthPicker);
        back_button = findViewById(R.id.calendar_back);
    }

    public void initial_month(){ //將月份選擇器顯示的字串以及fragment顯示的內容預設為當下月份
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            // 解析日期字串
            Date date = dateFormat.parse(selectedDate.getInstance().getDateString());
            // 將日期拆分為年、月、日
            int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date));
            int month = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(date));
            //int day = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(date));
            //selectDate中儲存的月份要-1才會變為index
            selectedDate.getInstance().setMonthString(String.valueOf(year)+"-"+String.valueOf(month-1));

            String PickerYearAndMonthString = String.valueOf(year)+"-"+String.valueOf(month);
            monthPicker.setText(PickerYearAndMonthString);
            initial_child_frag();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void back(){ //按下返回後回到diary頁面
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle back_bundle = new Bundle();
                back_bundle.putString("fragmentToShow", "diary_frag");
                Intent back_intent = new Intent();
                back_intent.setClass(CalenderActivity.this, MainActivity.class);
                back_intent.putExtras(back_bundle);
                back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(back_intent);
                finish();
            }
        });

    }

    public void choose_month(){ //按下月份選擇器按鈕後可以選擇年和月
        monthPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthPickerDialog(v);
            }
        });
    }

    public void showMonthPickerDialog(View v) {
        //設置月份選擇器預設的年分與月份
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.TAIWAN);
        Date date;
        try {
            date = dateFormat.parse(selectedDate.getInstance().getMonthString());
        } catch (ParseException e) {
            date = new Date(); // 解析失敗的話，使用當前日期
            e.printStackTrace();
        }
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        String selectedYearAndMonth = String.format("%d-%d", selectedYear, selectedMonth);
                        //因為1月的index為0，所以月份選擇器上面顯示的月份要+1才會正常
                        String PickerYearAndMonthString = String.format("%d-%d", selectedYear, selectedMonth + 1);
                        selectedDate.getInstance().setMonthString(selectedYearAndMonth);
                        monthPicker.setText(PickerYearAndMonthString);
                        initial_child_frag();
                    }
                },
                year, month, 1); // 将日曆的日期設置为固定的值

        // 隱藏日期選擇器中日期的部分
        datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.show();
    }

    public void initial_child_frag(){ //初始化inner_fragment顯示的內容
        inner_calendar_fragment inner_calendar_fragment = new inner_calendar_fragment();
        setFragment(inner_calendar_fragment);
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.calendar_frag_container,fragment).commit();
    }

}