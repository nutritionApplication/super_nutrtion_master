package com.example.super_nutrtion_master;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// 此為diary選項顯示的畫面
public class diary_fragment extends Fragment {
    private Toolbar topBar;
    private Button datePicker_button, addFood_button;
    private ImageButton calendar_button;
    private Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener datePicker;
    private String myFormat = "yyyy-MM-dd", dateString;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.TAIWAN);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary_fragment, container, false);
        BindById(view);
        toolBarSetting();
        settingDate();
        selectDate();
        addFood();
        open_calendar();
        return view;
    }

    public void BindById(View view){
        topBar = view.findViewById(R.id.diaryFrag_top_toolbar);
        datePicker_button = view.findViewById(R.id.datePicker_button);
        calendar_button = view.findViewById(R.id.calendar_button);
        addFood_button = view.findViewById(R.id.addFood_button);
    }

    public void toolBarSetting(){ //設置toolbar
        if(getActivity() != null){
            ((AppCompatActivity) getActivity()).setSupportActionBar(topBar);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void settingDate(){ //設置日期按鈕上顯示的日期，以及inner_frag顯示的內容
        datePicker_button.setText(selectedDate.getInstance().getDateString());
        initial_child_frag();
    }

    public void selectDate(){ //取得日期選擇器所選的日期，將日期選擇器按鈕上的日期設為所選日期，並初始化inner_fragment

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH , i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                //dateFormat = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                dateString = dateFormat.format(calendar.getTime());
                selectedDate.getInstance().setDateString(dateString);
                settingDate();
            }
        };
        datePicker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //設置日期選擇器預設的日期
                Date date;
                try {
                    date = dateFormat.parse(selectedDate.getInstance().getDateString());
                } catch (ParseException e) {
                    date = new Date(); // 解析失敗的話，使用當前日期
                    e.printStackTrace();
                }
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        datePicker,
                        year,
                        month,
                        day);
                dialog.show();
            }
        });
    }

    public void open_calendar(){ //跳至行事曆頁面
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendar_intent = new Intent();
                calendar_intent.setClass(getActivity(), CalenderActivity.class);
                startActivity(calendar_intent);
            }
        });
    }

    public void addFood(){ //點選新增後跳至FoodSearchActivity
        addFood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle add_bundle = new Bundle();
                add_bundle.putString("source", "diary_frag_addFood");

                Intent add_intent = new Intent();
                add_intent.setClass(getActivity(), FoodSearchActivity.class);
                add_intent.putExtras(add_bundle);
                startActivity(add_intent);
            }
        });
    }

    public void initial_child_frag(){ //初始化inner_fragment顯示的內容
        inner_diary_fragment inner_diary_fragment = new inner_diary_fragment();
        setFragment(inner_diary_fragment);
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().replace(R.id.diary_child_frag,fragment).commit();
    }
}