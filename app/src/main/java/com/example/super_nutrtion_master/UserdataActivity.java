package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

//此為使用者輸入資料的頁面
public class UserdataActivity extends AppCompatActivity {

    private int gender, age, height, weight, exercise;
    private RadioGroup gender_choose;
    private RadioButton gender_value;
    private EditText age_editText, height_editText, weight_editText;
    private Spinner exercise_spinner;
    private Button confirm_button, cancel_button;
    private String source, gender_string, exercise_string;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int BMR, LFEM, Vegetables, OFNS, oils_and_fats, nuts_and_seeds = 1, sodium = 2000;
    private double BMI, TDEE, TDEE_Mag, protein, protein_Mag, fat, Whole_grains, Dairy, Fruits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdata);
        BindById();
        IntentSourceJudgment();
        confirm();
        cancel();
    }

    public void BindById(){
        gender_choose = findViewById(R.id.radioGroup1);
        age_editText = findViewById(R.id.age_editText);
        height_editText = findViewById(R.id.height_editText);
        weight_editText = findViewById(R.id.weight_editText);
        exercise_spinner = findViewById(R.id.exercise_degree_spinner);
        confirm_button = findViewById(R.id.confirm_button);
        cancel_button = findViewById(R.id.cancel_button);
    }

    public void IntentSourceJudgment(){ //用來判斷是從哪裡跳到此頁面
        if(getIntent().hasExtra("source")) {
            source = getIntent().getStringExtra("source");
            if (source.equals("LoginActivity")) { //第一次登入
                cancel_button.setVisibility(View.INVISIBLE); //將取消按鈕隱藏
                exercise_spinner.setSelection(0);  //預設運動量選單的選項為第一個
            }
            else if(source.equals("user_fragment")){ //按下編輯按鈕來到此頁面
                getUserDataFromDataBase();
            }
        }
    }

    public void getUserDataFromDataBase(){ //從資料庫取得該帳號的資料
        db.collection("users").document(login_username.getInstance().getUsername()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { //將資料庫取得的值設置到各個輸入欄位中
                        if(Integer.parseInt(document.get("gender").toString()) == 0){
                            gender_choose.check(R.id.female_radioButton);
                        }
                        else{
                            gender_choose.check(R.id.male_radioButton);
                        }
                        age_editText.setText(document.get("age").toString());
                        height_editText.setText(document.get("height").toString());
                        weight_editText.setText(document.get("weight").toString());
                        exercise_spinner.setSelection(Integer.parseInt(document.get("exercise").toString()));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void confirm(){
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender_choose.getCheckedRadioButtonId() == -1 || age_editText.getText().toString().length() < 1 || height_editText.getText().toString().length() < 1 || weight_editText.getText().toString().length() < 1){
                    Toast.makeText(UserdataActivity.this, "請確實填寫所有欄位", Toast.LENGTH_LONG).show();
                }
                else {
                    getDataFromInputField();
                    calculateHealthIndicator();
                    Bundle confirm_bundle = new Bundle();
                    if (source.equals("LoginActivity")) {
                        writeDataToDataBase();
                        confirm_bundle.putString("fragmentToShow", "diary_frag");
                    }
                    else if(source.equals("user_fragment")){
                        updateDataToDataBase();
                        confirm_bundle.putString("fragmentToShow", "user_frag");
                    }
                    Intent confirm_intent = new Intent();
                    confirm_intent.setClass(UserdataActivity.this, MainActivity.class);
                    confirm_intent.putExtras(confirm_bundle);
                    startActivity(confirm_intent);
                    finish();
                }
            }
        });
    }

    public void getDataFromInputField(){ //從各個輸入欄位取得資料
        gender_value = findViewById(gender_choose.getCheckedRadioButtonId());
        gender_string = gender_value.getText().toString();
        if (gender_string.compareTo("女") == 0) {
            gender = 0;
        } else {
            gender = 1;
        }
        age = Integer.parseInt(age_editText.getText().toString());
        height = Integer.parseInt(height_editText.getText().toString());
        weight = Integer.parseInt(weight_editText.getText().toString());
        exercise = exercise_spinner.getSelectedItemPosition();
        String[] exercise_list = getResources().getStringArray(R.array.exercise_degree);
        exercise_string = exercise_list[exercise];
    }

    public void calculateHealthIndicator(){ //計算各項健康指標
        BMI = Math.round(weight / Math.pow(height / 100.0, 2) * 100.0) / 100.0;  //四捨五入到小數第二位
        BMR = (int)Math.round(10 * weight + 6.25 * height - 4.92 * age + (166 * gender - 161));
        switch(exercise){
            case 0:{
                TDEE_Mag = 1.2;
                protein_Mag = 0.8;
                break;
            }
            case 1:{
                TDEE_Mag = 1.375;
                protein_Mag = 1.1;
                break;
            }
            case 2:{
                TDEE_Mag = 1.55;
                protein_Mag = 1.2;
                break;
            }
            case 3:{
                TDEE_Mag = 1.72;
                protein_Mag = 1.6;
                break;
            }
            case 4:{
                TDEE_Mag = 1.9;
                protein_Mag = 2;
                break;
            }
        }
        TDEE = Math.round((BMR * TDEE_Mag) * 100.0) / 100.0;
        protein = Math.round((weight * protein_Mag) * 10.0) / 10.0;
        fat = Math.round((TDEE / 5 / 9) * 100.0) / 100.0;

        //------------------------------六大類食物已廢棄，以下為廢棄範圍----------------------------//
        //if(TDEE < 1350){
        //    Whole_grains = 1.5;
        //    LFEM = 3;
        //    Dairy = 1.5;
        //    Vegetables = 3;
        //    Fruits = 2;
        //    oils_and_fats = 3;
        //}
        //else if(TDEE >= 1350 && TDEE < 1650){
        //    Whole_grains = 2.5;
        //    LFEM = 4;
        //    Dairy = 1.5;
        //    Vegetables = 3;
        //    Fruits = 2;
        //    oils_and_fats = 3;
        //}
        //else if (TDEE >= 1650 && TDEE < 1900) {
        //    Whole_grains = 3;
        //   LFEM = 5;
        //    Dairy = 1.5;
        //    Vegetables = 3;
        //    Fruits = 2;
        //    oils_and_fats = 4;
        //}
        //else if(TDEE >= 1900 && TDEE < 2100){
        //    Whole_grains = 3;
        //    LFEM = 6;
        //    Dairy = 1.5;
        //    Vegetables = 4;
        //    Fruits = 3;
        //    oils_and_fats = 5;
        //}
        //else if(TDEE >= 2100 && TDEE < 2350){
        //    Whole_grains = 3.5;
        //    LFEM = 6;
        //    Dairy = 1.5;
        //    Vegetables = 4;
        //    Fruits = 3.5;
        //    oils_and_fats = 5;
        //}
        //else if(TDEE >= 2350 && TDEE < 2600){
        //    Whole_grains = 4;
        //    LFEM = 7;
        //    Dairy = 1.5;
        //    Vegetables = 5;
        //    Fruits = 4;
        //    oils_and_fats = 6;
        //}
        //else if(TDEE >= 2600){
        //    Whole_grains = 4;
        //    LFEM = 8;
        //    Dairy = 2;
        //    Vegetables = 5;
        //    Fruits = 4;
        //    oils_and_fats = 7;
        //}
        //OFNS = oils_and_fats + nuts_and_seeds;
        //-------------------------------------------------------------//
    }

    public void writeDataToDataBase(){ //將資料寫入資料庫
        Map<String, Object> userData = new HashMap<>();
        //基本資料
        userData.put("gender", gender);
        userData.put("gender_string", gender_string);
        userData.put("age", age);
        userData.put("height", height);
        userData.put("weight", weight);
        userData.put("exercise", exercise);
        userData.put("exercise_string", exercise_string);
        //營養指標值
        userData.put("BMI", BMI);
        userData.put("BMR", BMR);
        userData.put("TDEE", TDEE);
        userData.put("protein", protein);
        userData.put("fat", fat);
        userData.put("sodium", sodium);
        //----------------------------六大類食物已廢棄--------------------------------//
        //userData.put("Whole_grains", Whole_grains); //全榖雜糧類
        //userData.put("LFEM", LFEM); //豆蛋肉魚類
        //userData.put("Dairy", Dairy); //乳製品類
        //userData.put("Vegetables", Vegetables); //蔬菜類
        //userData.put("Fruits", Fruits); //水果類
        //userData.put("oils_and_fats", oils_and_fats); //油脂與脂肪類
        //userData.put("nuts_and_seeds", nuts_and_seeds); //堅果與種子類
        //userData.put("OFNS", OFNS); //油脂與脂肪類 + 堅果與種子類
        //-------------------------------------------------------------------------//

        db.collection("users").document(login_username.getInstance().getUsername())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void updateDataToDataBase(){ //將資料更新到資料庫
        Map<String, Object> updateData = new HashMap<>();
        //基本資料
        updateData.put("gender", gender);
        updateData.put("gender_string", gender_string);
        updateData.put("age", age);
        updateData.put("height", height);
        updateData.put("weight", weight);
        updateData.put("exercise", exercise);
        updateData.put("exercise_string", exercise_string);
        //營養指標值
        updateData.put("BMI", BMI);
        updateData.put("BMR", BMR);
        updateData.put("TDEE", TDEE);
        updateData.put("protein", protein);
        updateData.put("fat", fat);
        updateData.put("sodium", sodium);
        //----------------------------六大類食物已廢棄--------------------------------//
        //updateData.put("Whole_grains", Whole_grains); //全榖雜糧類
        //updateData.put("LFEM", LFEM); //豆蛋肉魚類
        //updateData.put("Dairy", Dairy); //乳製品類
        //updateData.put("Vegetables", Vegetables); //蔬菜類
        //updateData.put("Fruits", Fruits); //水果類
        //updateData.put("oils_and_fats", oils_and_fats); //油脂與脂肪類
        //updateData.put("nuts_and_seeds", nuts_and_seeds); //堅果與種子類
        //updateData.put("OFNS", OFNS); //油脂與脂肪類 + 堅果與種子類
        //-------------------------------------------------------------------------//
        db.collection("users").document(login_username.getInstance().getUsername()).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
    }

    public void cancel(){ //按下取消後返回主畫面
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle cancel_bundle = new Bundle();
                cancel_bundle.putString("fragmentToShow", "user_frag");
                Intent cancel_intent = new Intent();
                cancel_intent.setClass(UserdataActivity.this, MainActivity.class);
                cancel_intent.putExtras(cancel_bundle);
                startActivity(cancel_intent);
                finish();
            }
        });
    }

}