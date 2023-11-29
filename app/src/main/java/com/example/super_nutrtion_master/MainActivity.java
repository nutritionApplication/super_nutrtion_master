package com.example.super_nutrtion_master;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottom_menu;
    private user_fragment user_fragment = new user_fragment();
    private HI_fragment HI_fragment = new HI_fragment();
    private diary_fragment diary_fragment = new diary_fragment();
    private food_fragment food_fragment = new food_fragment();
    private String gender_str, exercise_str;
    private int gender, age, height, weight, exercise;
    private int BMR, LFEM, Vegetables, OFNS, oils_and_fats, nuts_and_seeds = 1, sodium = 2000;
    private double BMI, TDEE, TDEE_Mag, protein, protein_Mag, fat, Whole_grains, Dairy, Fruits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindById();
        fragToShow();
        change_page();
    }


    public void BindById(){
        bottom_menu = findViewById(R.id.bottomNavigationView);
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentPage,fragment).commit();
    }

    public void fragToShow(){
        if(getIntent().hasExtra("fragmentToShow")){
            String fragTag = getIntent().getStringExtra("fragmentToShow");
            if(fragTag.equals("user_frag")){
                getUserData();
                calculate_health_indicator(); //取得使用者資料後馬上計算營養指標
                deliver_data_to_user_frag();
                setFragment(user_fragment);
            }
            else if(fragTag.equals("diary_frag")){
                getUserData();
                calculate_health_indicator();
                setFragment(diary_fragment);
            }
            else if(fragTag.equals("food_frag")){
                getUserData();
                calculate_health_indicator();
                setFragment(food_fragment);
            }
        }
    }

    public void getUserData(){
        gender_str = getIntent().getExtras().getString("gender str");
        gender = getIntent().getExtras().getInt("gender");
        age = getIntent().getExtras().getInt("age");
        height = getIntent().getExtras().getInt("height");
        weight = getIntent().getExtras().getInt("weight");
        exercise_str = getIntent().getExtras().getString("exercise str");
        exercise = getIntent().getExtras().getInt("exercise");
    }

    public void deliver_data_to_user_frag(){
        Bundle user_bundle = new Bundle();
        user_bundle.putString("gender str", gender_str);
        user_bundle.putInt("gender", gender);
        user_bundle.putInt("age", age);
        user_bundle.putInt("height", height);
        user_bundle.putInt("weight", weight);
        user_bundle.putString("exercise str", exercise_str);
        user_bundle.putInt("exercise", exercise);
        user_fragment.setArguments(user_bundle);
    }

    public void change_page(){ //按下按鈕時切換到對應的fragment
        bottom_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user:
                        deliver_data_to_user_frag();
                        setFragment(user_fragment);
                        return true;
                    case R.id.HI:
                        deliver_data_to_HI_frag();
                        setFragment(HI_fragment);
                        return true;
                    case R.id.diary:
                        setFragment(diary_fragment);
                        return true;
                    case R.id.food:
                        setFragment(food_fragment);
                        return true;
                }
                return false;
            }
        });

    }

    public void calculate_health_indicator(){
        BMI = Math.round(weight / (Math.sqrt(height / 100.0)) * 100.0) / 100.0;  //四捨五入到小數第二位
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

        if(TDEE < 1350){
            Whole_grains = 1.5;
            LFEM = 3;
            Dairy = 1.5;
            Vegetables = 3;
            Fruits = 2;
            oils_and_fats = 3;
        }
        else if(TDEE >= 1350 && TDEE < 1650){
            Whole_grains = 2.5;
            LFEM = 4;
            Dairy = 1.5;
            Vegetables = 3;
            Fruits = 2;
            oils_and_fats = 3;
        }
        else if (TDEE >= 1650 && TDEE < 1900) {
            Whole_grains = 3;
            LFEM = 5;
            Dairy = 1.5;
            Vegetables = 3;
            Fruits = 2;
            oils_and_fats = 4;
        }
        else if(TDEE >= 1900 && TDEE < 2100){
            Whole_grains = 3;
            LFEM = 6;
            Dairy = 1.5;
            Vegetables = 4;
            Fruits = 3;
            oils_and_fats = 5;
        }
        else if(TDEE >= 2100 && TDEE < 2350){
            Whole_grains = 3.5;
            LFEM = 6;
            Dairy = 1.5;
            Vegetables = 4;
            Fruits = 3.5;
            oils_and_fats = 5;
        }
        else if(TDEE >= 2350 && TDEE < 2600){
            Whole_grains = 4;
            LFEM = 7;
            Dairy = 1.5;
            Vegetables = 5;
            Fruits = 4;
            oils_and_fats = 6;
        }
        else if(TDEE >= 2600){
            Whole_grains = 4;
            LFEM = 8;
            Dairy = 2;
            Vegetables = 5;
            Fruits = 4;
            oils_and_fats = 7;
        }
        OFNS = oils_and_fats + nuts_and_seeds;
    }

    public void deliver_data_to_HI_frag(){
        Bundle HI_bundle = new Bundle();
        HI_bundle.putDouble("BMI", BMI);
        HI_bundle.putInt("BMR", BMR);
        HI_bundle.putDouble("TDEE", TDEE);
        HI_bundle.putDouble("Whole_grains", Whole_grains);
        HI_bundle.putInt("LFEM", LFEM);
        HI_bundle.putDouble("Dairy", Dairy);
        HI_bundle.putInt("Vegetables", Vegetables);
        HI_bundle.putDouble("Fruits", Fruits);
        HI_bundle.putInt("OFNS", OFNS);
        HI_bundle.putInt("oils_and_fats", oils_and_fats);
        HI_bundle.putInt("nuts_and_seeds", nuts_and_seeds);
        HI_bundle.putDouble("protein", protein);
        HI_bundle.putDouble("fat", fat);
        HI_bundle.putInt("Sodium", sodium);
        HI_fragment.setArguments(HI_bundle);
    }

    public void test(){
        food Food = new food("bento",750,35,20,40);
        Food.writeData();
        //food Food = new food("bento");
        //Food.readData();
    }
}