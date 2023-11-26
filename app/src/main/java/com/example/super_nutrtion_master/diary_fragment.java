package com.example.super_nutrtion_master;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class diary_fragment extends Fragment {
    Toolbar topBar;
    Button datePicker_button;
    ImageButton calendar_button;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener datePicker;
    String myFormat = "yyyy年MM月dd日";
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
        return view;
    }

    public void BindById(View view){
        topBar = view.findViewById(R.id.diaryFrag_top_toolbar);
        datePicker_button = view.findViewById(R.id.datePicker_button);
        calendar_button = view.findViewById(R.id.calendar_button);
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
        String formattedDate = dateFormat.format(currentDate);
        datePicker_button.setText(formattedDate);
    }

    public void selectDate(){

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH , i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                datePicker_button.setText(dateFormat.format(calendar.getTime()));
                Toast.makeText(getActivity(), dateFormat.format(calendar.getTime()), Toast.LENGTH_LONG).show(); //test用
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

}
