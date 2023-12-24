package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

//此為diary選項用來顯示特定日期的內容
public class inner_diary_fragment extends Fragment {
    private LinearLayout dynamicLayout;
    private String date_str = selectedDate.getInstance().getDateString();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int total_calories = 0, total_sodium = 0, user_sodium;
    private double total_carbohydrate = 0, total_protein = 0, total_fat = 0, user_TDEE, user_protein, user_fat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inner_diary_fragment, container, false);
        BindById(view);
        getUserHealthIndicator();
        CheckDateExist(date_str);
        return view;
    }

    public void BindById(View view){
        dynamicLayout = view.findViewById(R.id.inner_diary_linearLayout);
    }

    public void getUserHealthIndicator(){ //從資料庫取得使用者的健康指標
        db.collection("users").document(login_username.getInstance().getUsername()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_TDEE = Double.parseDouble(document.get("TDEE").toString());
                        user_protein = Double.parseDouble(document.get("protein").toString());
                        user_fat = Double.parseDouble(document.get("fat").toString());
                        user_sodium = Integer.parseInt(document.get("sodium").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void CheckDateExist(String dateString){ //檢查資料庫中是否存在此日期
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateString).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dynamicLayout.removeAllViews();
                        getDataFromDataBase(dateString);
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getDataFromDataBase(String dateString){ //從資料庫取得特定日期儲存的所有食物
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateString).collection("foods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentID = document.getId();
                        String food_name = document.get("name").toString();
                        int calories = Integer.parseInt(document.get("calories").toString());
                        double carbohydrate = Double.parseDouble(document.get("carbohydrate").toString());
                        double protein = Double.parseDouble(document.get("protein").toString());
                        double fat = Double.parseDouble(document.get("fat").toString());
                        int sodium = Integer.parseInt(document.get("sodium").toString());
                        int quantity = Integer.parseInt(document.get("quantity").toString());

                        //------------計算各項營養含量 * 數量的值
                        int food_calories = calories * quantity;
                        double food_carbohydrate = Math.round((carbohydrate * quantity) * 100.0) / 100.0;
                        double food_protein = Math.round((protein * quantity) * 100.0) / 100.0;
                        double food_fat = Math.round((fat * quantity) * 100.0) / 100.0;
                        int food_sodium = sodium * quantity;

                        total_calories += food_calories;
                        total_carbohydrate += food_carbohydrate;
                        total_protein += food_protein;
                        total_fat += food_fat;
                        total_sodium += food_sodium;

                        addItem(food_name, food_calories, food_carbohydrate, food_protein, food_fat, food_sodium, quantity, documentID);
                    }
                    add_H_I(); //等待前面的動作完成再開始計算，避免發生同步錯誤
                    updateEatenFood();
                }
                else {
                    Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addItem(String food_name, int calories, double carbohydrate, double protein, double fat, int sodium, int quantity, String documentID){ //用來新增顯示的食物物件
        TextView textview = new TextView(getContext());
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String textPart1 = food_name + "\n";    //食物物件中的第一行顯示食物名稱
        builder.append(textPart1);
        int textSizePart1 = 30;
        AbsoluteSizeSpan sizeSpanPart1 = new AbsoluteSizeSpan(textSizePart1, true);
        builder.setSpan(sizeSpanPart1, 0, textPart1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart2 = "數量 : " + String.valueOf(quantity) + " 份\n";  //食物物件中的第二行顯示食物數量
        builder.append(textPart2);
        int textSizePart2 = 20;
        AbsoluteSizeSpan sizeSpanPart2 = new AbsoluteSizeSpan(textSizePart2, true);
        builder.setSpan(sizeSpanPart2, textPart1.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart3 = "熱量 : " + String.valueOf(calories) + " cal\n";  //食物物件中的第三行顯示食物熱量(單份熱量 * 數量)
        builder.append(textPart3);
        int textSizePart3 = 15;
        AbsoluteSizeSpan sizeSpanPart3 = new AbsoluteSizeSpan(textSizePart3, true);
        builder.setSpan(sizeSpanPart3, textPart2.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart4 = "碳水 : " + String.valueOf(carbohydrate) + " g\t\t蛋白質 : " + String.valueOf(protein) + " g\n";  //食物物件中的第四行顯示食物碳水含量以及蛋白質含量(單份 * 數量)
        builder.append(textPart4);
        int textSizePart4 = 15;
        AbsoluteSizeSpan sizeSpanPart4 = new AbsoluteSizeSpan(textSizePart4, true);
        builder.setSpan(sizeSpanPart4, textPart3.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart5 = "脂肪 : " + String.valueOf(fat) + " g\t\t鈉 : " + String.valueOf(sodium) + " mg";  //食物物件中的第五行顯示食物脂肪含量以及鈉含量(單份 * 數量)
        builder.append(textPart5);
        int textSizePart5 = 15;
        AbsoluteSizeSpan sizeSpanPart5 = new AbsoluteSizeSpan(textSizePart5, true);
        builder.setSpan(sizeSpanPart5, textPart4.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textview.setText(builder);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( //設置textview之間的間距以及textview寬度
                getResources().getDimensionPixelSize(R.dimen.fixed_width),  //設置textview寬度
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0);  //設置textview之間的間距
        textview.setLayoutParams(layoutParams);

        textview.setPadding(15,0,15,10); //設置textView內文字和外框的間距

        int drawableResourceId = R.drawable.diary_fooditem_border; //設置邊框
        textview.setBackground(ContextCompat.getDrawable(requireContext(), drawableResourceId));

        textview.setOnClickListener(new View.OnClickListener() { //點選指定textView能跳至ShowFoodDataActivity顯示詳細資訊
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "diary_frag_editFood");
                bundle.putString("food_name", food_name);
                bundle.putInt("quantity", quantity);
                bundle.putString("documentID",documentID);

                Intent intent = new Intent();
                intent.setClass(getActivity(), ShowFoodDataActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        dynamicLayout.addView(textview);
    }


    public void add_H_I(){  //將營養指標加入到畫面最下方
        TextView title = new TextView(getContext()); //標題
        title.setText("今日攝取量");
        title.setTextSize(25);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams Title_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        Title_layoutParams.setMargins(0, 40, 0, 0);
        title.setLayoutParams(Title_layoutParams);
        dynamicLayout.addView(title);
        //------------------------------------------------------------------------------------
        View divider1 = new View(getContext()); //分隔線1
        divider1.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 分隔線的高度，可以根據需要調整
        ));
        divider1.setBackgroundColor(Color.BLACK);
        dynamicLayout.addView(divider1);
        //------------------------------------------------------------------------------------
        TextView calories_textview = new TextView(getContext()); //熱量
        double calories_diff = Math.round(Math.abs(total_calories - user_TDEE) * 100.0) / 100.0; //熱量差值
        String calories_hint = "";
        if(total_calories > user_TDEE){
            calories_hint = "(超過建議熱量 " + String.valueOf(calories_diff) + " 大卡)";
        }
        else if(user_TDEE > total_calories){
            calories_hint = "(少於建議熱量 " + String.valueOf(calories_diff) + " 大卡)";
        }
        calories_textview.setText("總熱量攝取 : " + String.valueOf(total_calories) + " 大卡\n" + calories_hint + "\n(建議攝取量 : " + String.valueOf(user_TDEE) + " 大卡)");
        calories_textview.setTextSize(20);
        LinearLayout.LayoutParams calories_textview_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        calories_textview_layoutParams.setMargins(0, 20, 0, 20);
        calories_textview.setLayoutParams(calories_textview_layoutParams);
        dynamicLayout.addView(calories_textview);
        //------------------------------------------------------------------------------------
        View divider2 = new View(getContext()); //分隔線2
        divider2.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 分隔線的高度，可以根據需要調整
        ));
        divider2.setBackgroundColor(Color.BLACK);
        dynamicLayout.addView(divider2);
        //------------------------------------------------------------------------------------
        TextView carbohydrate_textview = new TextView(getContext()); //碳水
        carbohydrate_textview.setText("碳水攝取量 : " + String.valueOf(Math.round(total_carbohydrate * 100.0) / 100.0) + " g");
        carbohydrate_textview.setTextSize(20);
        LinearLayout.LayoutParams carbohydrate_textview_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        carbohydrate_textview_layoutParams.setMargins(0, 20, 0, 20);
        carbohydrate_textview.setLayoutParams(carbohydrate_textview_layoutParams);
        dynamicLayout.addView(carbohydrate_textview);
        //------------------------------------------------------------------------------------
        View divider3 = new View(getContext()); //分隔線3
        divider3.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 分隔線的高度，可以根據需要調整
        ));
        divider3.setBackgroundColor(Color.BLACK);
        dynamicLayout.addView(divider3);
        //------------------------------------------------------------------------------------
        TextView protein_textview = new TextView(getContext()); //蛋白質
        double protein_diff = Math.round(Math.abs(total_protein - user_protein) * 100.0) / 100.0; //蛋白質差值
        String protein_hint = "";
        if(total_protein > user_protein){
            protein_hint = "(超過建議攝取量 " + String.valueOf(protein_diff) + " g)";
        }
        else if(user_protein > total_protein){
            protein_hint = "(少於建議攝取量 " + String.valueOf(protein_diff) + " g)";
        }
        protein_textview.setText("蛋白質攝取量 : " + String.valueOf(Math.round(total_protein * 100.0) / 100.0) + " g\n" + protein_hint + "\n(建議攝取量 : " + String.valueOf(user_protein) + " g)");
        protein_textview.setTextSize(20);
        LinearLayout.LayoutParams protein_textview_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        protein_textview_layoutParams.setMargins(0, 20, 0, 20);
        protein_textview.setLayoutParams(protein_textview_layoutParams);
        dynamicLayout.addView(protein_textview);
        //------------------------------------------------------------------------------------
        View divider4 = new View(getContext()); //分隔線4
        divider4.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 分隔線的高度，可以根據需要調整
        ));
        divider4.setBackgroundColor(Color.BLACK);
        dynamicLayout.addView(divider4);
        //------------------------------------------------------------------------------------
        TextView fat_textview = new TextView(getContext()); //脂肪
        double fat_diff = Math.round(Math.abs(total_fat - user_fat) * 100.0) / 100.0; //脂肪差值
        String fat_hint = "";
        if(total_fat > user_fat){
            fat_hint = "(超過建議攝取量 " + String.valueOf(fat_diff) + " g)";
        }
        else if(user_fat > total_fat){
            fat_hint = "(少於建議攝取量 " + String.valueOf(fat_diff) + " g)";
        }
        fat_textview.setText("脂肪攝取量 : " + String.valueOf(Math.round(total_fat * 100.0) / 100.0) + " g\n" + fat_hint + "\n(建議攝取量 : " + String.valueOf(user_fat) + " g)");
        fat_textview.setTextSize(20);
        LinearLayout.LayoutParams fat_textview_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        fat_textview_layoutParams.setMargins(0, 20, 0, 20);
        fat_textview.setLayoutParams(fat_textview_layoutParams);
        dynamicLayout.addView(fat_textview);
        //------------------------------------------------------------------------------------
        View divider5 = new View(getContext()); //分隔線5
        divider5.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                2 // 分隔線的高度，可以根據需要調整
        ));
        divider5.setBackgroundColor(Color.BLACK);
        dynamicLayout.addView(divider5);
        //------------------------------------------------------------------------------------
        TextView sodium_textview = new TextView(getContext()); //鈉
        int sodium_diff = Math.abs(total_sodium - user_sodium); //鈉差值
        String sodium_hint = "";
        if(total_sodium > user_sodium){
            sodium_hint = "(超過建議攝取量 " + String.valueOf(sodium_diff) + " mg)";
        }
        else if(user_fat > total_fat){
            sodium_hint = "(少於建議攝取量 " + String.valueOf(sodium_diff) + " mg)";
        }
        sodium_textview.setText("鈉攝取量 : " + String.valueOf(total_sodium) + " mg\n" + sodium_hint + "\n(建議攝取量 : " + String.valueOf(user_sodium) + " mg)");
        sodium_textview.setTextSize(20);
        LinearLayout.LayoutParams sodium_textview_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sodium_textview_layoutParams.setMargins(0, 20, 0, 20);
        sodium_textview.setLayoutParams(sodium_textview_layoutParams);
        dynamicLayout.addView(sodium_textview);
    }

    public void updateEatenFood(){
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("consumedCalories", total_calories);
        updateData.put("consumedCarbohydrate", Math.round(total_carbohydrate * 100.0) / 100.0);
        updateData.put("consumedProtein", Math.round(total_protein * 100.0) / 100.0);
        updateData.put("consumedFat", Math.round(total_fat * 100.0) / 100.0);
        updateData.put("consumedSodium", total_sodium);
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(selectedDate.getInstance().getDateString()).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}