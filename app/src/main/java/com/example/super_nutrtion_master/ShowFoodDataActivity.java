package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShowFoodDataActivity extends AppCompatActivity {

    Button Back_button, Delete_button, Confirm_button;
    TextView food_name_view, calories_view, carbohydrate_view, protein_view, fat_view, sodium_view, food_quantity_view;
    ImageView food_picture;
    EditText food_quantity_value;
    String source, food_name, dateStr;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    double carbohydrate, protein, fat;
    int calories, sodium, food_quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food_data);

        BindById();
        IntentJudgment();
        getFoodData();
        Back();
        Delete();
        Confirm();
    }

    public void BindById(){
        Back_button = findViewById(R.id.foodDataPageBackButton);
        Delete_button = findViewById(R.id.foodDataPageDeleteButton);
        Confirm_button = findViewById(R.id.foodDataPageConfirmButton);
        food_name_view = findViewById(R.id.food_name_view);
        food_quantity_view = findViewById(R.id.food_quantity_textview);
        calories_view = findViewById(R.id.foodDataPage_calories_view);
        carbohydrate_view = findViewById(R.id.foodDataPage_carbohydrate_view);
        protein_view = findViewById(R.id.foodDataPage_protein_view);
        fat_view = findViewById(R.id.foodDataPage_fat_view);
        sodium_view = findViewById(R.id.foodDataPage_sodium_view);
        food_picture = findViewById(R.id.foodImage);
        food_quantity_value = findViewById(R.id.food_quantity_value);
    }

    public void IntentJudgment(){ //根據開啟此頁面的來源來判斷要顯示的內容以及擷取對應的參數
        if(getIntent().hasExtra("source")){
            source = getIntent().getStringExtra("source");
            if(source.equals("diary_frag_addFood")){
                Delete_button.setVisibility(View.INVISIBLE);
                if(getIntent().hasExtra("dateStr")){
                    dateStr = getIntent().getStringExtra("dateStr");
                }

            }
            else if(source.equals("diary_frag_editFood")){
                if(getIntent().hasExtra("dateStr")){
                    dateStr = getIntent().getStringExtra("dateStr");
                }
            }
            else if(source.equals("food_frag")){
                food_quantity_view.setVisibility(View.INVISIBLE);
                food_quantity_value.setVisibility(View.INVISIBLE);
                Delete_button.setVisibility(View.INVISIBLE);
                Confirm_button.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getFoodData(){
        if(getIntent().hasExtra("food_name")) {
            food_name = getIntent().getStringExtra("food_name");
            db.collection("foods").document(food_name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            calories = Integer.parseInt(document.get("calories").toString());
                            carbohydrate = Double.parseDouble(document.get("carbohydrate").toString());
                            protein = Double.parseDouble(document.get("protein").toString());
                            fat = Double.parseDouble(document.get("fat").toString());
                            sodium = Integer.parseInt(document.get("sodium").toString());
                            food_name_view.setText(food_name);
                            calories_view.setText("熱量 : " + String.valueOf(calories) + "cal");
                            carbohydrate_view.setText("碳水化合物 : " + String.valueOf(carbohydrate) + "g");
                            protein_view.setText("蛋白質 : " + String.valueOf(protein) + "g");
                            fat_view.setText("脂肪 : " + String.valueOf(fat) + "g");
                            sodium_view.setText("鈉 : " + String.valueOf(sodium) + "mg");
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            storage.getReference().child(food_name + ".jpg").getBytes(Long.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful()) {
                        byte[] imageData = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, Objects.requireNonNull(imageData).length);
                        food_picture.setImageBitmap(bitmap);
                    } else {
                        Log.e("Storage", "Error getting image", task.getException());
                    }
                }
            });
        }
    }


    public void Back(){
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle back_bundle = new Bundle();
                Intent back_intent = new Intent();
                if(source.equals("diary_frag_addFood")){
                    back_bundle.putString("source", "diary_frag_addFood");
                    back_intent.setClass(ShowFoodDataActivity.this, FoodSearchActivity.class);
                }
                else if(source.equals("diary_frag_editFood")){
                    back_bundle.putString("source", "diary_frag_editFood");
                    back_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                }
                else if(source.equals("food_frag")){
                    back_bundle.putString("source", "food_frag");
                    back_intent.setClass(ShowFoodDataActivity.this, FoodSearchActivity.class);
                }
                back_intent.putExtras(back_bundle);
                back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(back_intent);
            }
        });
    }

    public void Delete(){

    }

    public void Confirm(){
        Confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(food_quantity_value.getText().toString().length() < 1){
                    Toast.makeText(ShowFoodDataActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(food_quantity_value.getText().toString()) < 1){
                    Toast.makeText(ShowFoodDataActivity.this, "請輸入有效數量", Toast.LENGTH_SHORT).show();
                }
                else{
                    food_quantity = Integer.parseInt(food_quantity_value.getText().toString());
                    Map<String, Object> food = new HashMap<>();
                    food.put("name", food_name);
                    food.put("calories", calories);
                    food.put("carbohydrate", carbohydrate);
                    food.put("protein", protein);
                    food.put("fat", fat);
                    food.put("sodium", sodium);
                    food.put("quantity", food_quantity);

                    db.collection("date").document(dateStr).collection("foods")
                            .add(food)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                    //-----------------校正開始-----------------------------

                    //先新增校正欄位到對應的日期文件
                    Map<String, Object> correction = new HashMap<>();
                    correction.put("correction", "correction");
                    db.collection("date").document(dateStr)
                            .set(correction)
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

                    //接著把校正欄位刪除，藉此將document的日期格式去斜體化，避免找不到對應字串的問題
                    Map<String,Object> updates = new HashMap<>();
                    updates.put("correction", FieldValue.delete());
                    db.collection("date").document(dateStr).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                    //-----------------校正結束-----------------------------


                    Bundle confirm_bundle = new Bundle();
                    Intent confirm_intent = new Intent();
                    if(source.equals("diary_frag_addFood")){
                        confirm_bundle.putString("fragmentToShow", "diary_frag");
                        confirm_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                        confirm_intent.putExtras(confirm_bundle);
                        startActivity(confirm_intent);
                    }
                }
            }
        });
    }



}