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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class diary_fragment extends Fragment {
    Toolbar topBar;
    Button datePicker_button, addFood_button;
    ImageButton calendar_button;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener datePicker;
    String myFormat = "yyyy-MM-dd", dateString;
    SimpleDateFormat dateFormat;


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
        getCurrentDate();
        selectDate();
        addFood();
        return view;
    }

    public void BindById(View view){
        topBar = view.findViewById(R.id.diaryFrag_top_toolbar);
        datePicker_button = view.findViewById(R.id.datePicker_button);
        calendar_button = view.findViewById(R.id.calendar_button);
        addFood_button = view.findViewById(R.id.addFood_button);
    }

    public void toolBarSetting(){
        if(getActivity() != null){
            ((AppCompatActivity) getActivity()).setSupportActionBar(topBar);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void getCurrentDate(){ //將日期選擇器按鈕上的文字預設為當前日期
        calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        dateFormat = new SimpleDateFormat(myFormat, Locale.TAIWAN);
        dateString = dateFormat.format(currentDate);
        datePicker_button.setText(dateString);
        initial_child_frag();
    }

    public void selectDate(){

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH , i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                dateString = dateFormat.format(calendar.getTime());
                datePicker_button.setText(dateString);
                //Toast.makeText(getActivity(), dateString, Toast.LENGTH_SHORT).show(); //test用
                initial_child_frag();
            }
        };
        datePicker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        datePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }

    public void open_calendar(){
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void addFood(){
        addFood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle add_bundle = new Bundle();
                add_bundle.putString("source", "diary_frag_addFood");
                add_bundle.putString("dateStr", dateString);

                Intent add_intent = new Intent();
                add_intent.setClass(getActivity(), FoodSearchActivity.class);
                add_intent.putExtras(add_bundle);
                startActivity(add_intent);
            }
        });
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().replace(R.id.diary_child_frag,fragment).commit();

    }

    public void deliver_data_to_child_frag(Fragment fragment){
        Bundle user_bundle = new Bundle();
        user_bundle.putString("date str", dateString);
        fragment.setArguments(user_bundle);
    }

    public void initial_child_frag(){
        inner_diary_fragment inner_diary_fragment = new inner_diary_fragment();
        deliver_data_to_child_frag(inner_diary_fragment);
        setFragment(inner_diary_fragment);
    }


}