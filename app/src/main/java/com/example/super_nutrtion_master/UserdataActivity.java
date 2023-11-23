package com.example.super_nutrtion_master;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//此為使用者輸入資料的頁面
public class UserdataActivity extends AppCompatActivity {

    private int gender;
    private int age;
    private int height;
    private int weight;
    private int exercise;
    private RadioGroup gender_choose;
    private RadioButton gender_value;
    private EditText age_value;
    private EditText height_value;
    private EditText weight_value;
    private Spinner exercise_spinner;
    private Button confirm_button;
    private Button cancel_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);
        BindById();
        setSpinnerDefault();
        confirm();
    }

    public void BindById(){
        gender_choose = findViewById(R.id.radioGroup1);
        age_value = findViewById(R.id.age_editText);
        height_value = findViewById(R.id.height_editText);
        weight_value = findViewById(R.id.weight_editText);
        exercise_spinner = findViewById(R.id.exercise_degree_spinner);
        confirm_button = findViewById(R.id.confirm_button);
        cancel_button = findViewById(R.id.cancel_button);
    }

    public void setSpinnerDefault(){ //預設運動量選單的選項為第一個
        exercise_spinner.setSelection(0);
    }

    public void confirm(){
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender_choose.getCheckedRadioButtonId() == -1 || age_value.getText().toString().length() < 1 || height_value.getText().toString().length() < 1 || weight_value.getText().toString().length() < 1){
                    Toast.makeText(UserdataActivity.this, "請確實填寫所有欄位", Toast.LENGTH_LONG).show();
                }
                else {
                    getData();
                    gender_value = findViewById(gender_choose.getCheckedRadioButtonId());
                    String gender_str = gender_value.getText().toString();
                    String[] exercise_list =getResources().getStringArray(R.array.exercise_degree);
                    Bundle confirm_bundle = new Bundle();
                    confirm_bundle.putString("gender str", gender_str);
                    confirm_bundle.putInt("gender", gender);
                    confirm_bundle.putInt("age", age);
                    confirm_bundle.putInt("height", height);
                    confirm_bundle.putInt("weight", weight);
                    confirm_bundle.putString("exercise str", exercise_list[exercise]);
                    confirm_bundle.putInt("exercise", exercise);
                    confirm_bundle.putString("fragmentToShow", "user_frag");

                    Intent confirm_intent = new Intent();
                    confirm_intent.setClass(UserdataActivity.this, MainActivity.class);
                    confirm_intent.putExtras(confirm_bundle);
                    startActivity(confirm_intent);
                }
            }
        });
    }

    public void getData(){
        gender_value = findViewById(gender_choose.getCheckedRadioButtonId());
        String gender_str = gender_value.getText().toString();
        if (gender_str.compareTo("男") == 0) {
            gender = 1;
        } else {
            gender = 0;
        }
        age = Integer.parseInt(age_value.getText().toString());
        height = Integer.parseInt(height_value.getText().toString());
        weight = Integer.parseInt(weight_value.getText().toString());
        exercise = exercise_spinner.getSelectedItemPosition();
    }

    @Override
    protected void onNewIntent(Intent intent) { //按下編輯回來此頁面後才開始執行的動作
        super.onNewIntent(intent);
        if (intent != null) {
            getData();
            cancel_button.setVisibility(View.VISIBLE);
            cancel();
        }
    }

    public void cancel(){
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle cancel_bundle = new Bundle();
                String gender_str;
                if(gender == 1){
                    gender_str = "男";
                }
                else{
                    gender_str = "女";
                }
                cancel_bundle.putString("gender str", gender_str);
                cancel_bundle.putInt("gender", gender);
                cancel_bundle.putInt("age", age);
                cancel_bundle.putInt("height", height);
                cancel_bundle.putInt("weight", weight);
                String[] exercise_list =getResources().getStringArray(R.array.exercise_degree);
                cancel_bundle.putString("exercise str", exercise_list[exercise]);
                cancel_bundle.putInt("exercise", exercise);
                cancel_bundle.putString("fragmentToShow", "user_frag");

                Intent cancel_intent = new Intent();
                cancel_intent.setClass(UserdataActivity.this, MainActivity.class);
                cancel_intent.putExtras(cancel_bundle);
                startActivity(cancel_intent);
            }
        });
    }

}