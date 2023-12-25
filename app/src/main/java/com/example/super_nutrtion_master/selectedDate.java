package com.example.super_nutrtion_master;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//用來保存使用者選取的日期，讓所有的activity與fragment可以獲取日期字串，預設日期為當天日期
public class selectedDate {
    private static selectedDate instance;
    private String DateString, MonthString;

    private selectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
        DateString = sdf.format(new Date());
    }

    public static synchronized selectedDate getInstance() {
        if (instance == null) {
            instance = new selectedDate();
        }
        return instance;
    }

    public String getDateString() {
        return DateString;
    }

    public void setDateString(String userInput) {
        this.DateString = userInput;
    }

    public String getMonthString() {
        return MonthString;
    }

    public void setMonthString(String userInput) {
        this.MonthString = userInput;
    }
}
