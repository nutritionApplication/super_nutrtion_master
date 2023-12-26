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

//用來顯示食物詳細資訊的頁面
public class ShowFoodDataActivity extends AppCompatActivity {

    private Button Back_button, Delete_button, Confirm_button, Add_button;
    private TextView food_name_view, calories_view, carbohydrate_view, protein_view, fat_view, sodium_view, food_quantity_view;
    private ImageView food_picture;
    private EditText food_quantity_value;
    private String source, food_name, documentID, dateStr = selectedDate.getInstance().getDateString();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private double carbohydrate, protein, fat;
    private int calories, sodium, food_quantity, quantity_para;
    private View food_data_topDivider, food_data_bottomDivider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food_data);
        BindById();
        IntentJudgment();
        getFoodData();
        Back();
        Add();
        Confirm();
        Delete();
    }

    public void BindById(){
        Back_button = findViewById(R.id.foodDataPageBackButton);
        Delete_button = findViewById(R.id.foodDataPageDeleteButton);
        Confirm_button = findViewById(R.id.foodDataPageConfirmButton);
        Add_button = findViewById(R.id.foodDataPageAddButton);
        food_name_view = findViewById(R.id.food_name_view);
        food_quantity_view = findViewById(R.id.food_quantity_textview);
        calories_view = findViewById(R.id.foodDataPage_calories_view);
        carbohydrate_view = findViewById(R.id.foodDataPage_carbohydrate_view);
        protein_view = findViewById(R.id.foodDataPage_protein_view);
        fat_view = findViewById(R.id.foodDataPage_fat_view);
        sodium_view = findViewById(R.id.foodDataPage_sodium_view);
        food_picture = findViewById(R.id.foodImage);
        food_quantity_value = findViewById(R.id.food_quantity_value);
        food_data_topDivider = findViewById(R.id.food_data_topDivider);
        food_data_bottomDivider = findViewById(R.id.food_data_bottomDivider);
    }

    public void IntentJudgment(){ //根據開啟此頁面的來源來判斷要顯示的內容以及擷取對應的參數
        if(getIntent().hasExtra("food_name")) {
            food_name = getIntent().getStringExtra("food_name");
        }
        if(getIntent().hasExtra("source")){
            source = getIntent().getStringExtra("source");
            if(source.equals("diary_frag_addFood")){
                Delete_button.setVisibility(View.INVISIBLE);
                Confirm_button.setVisibility(View.INVISIBLE);
            }
            else if(source.equals("diary_frag_editFood")){
                Add_button.setVisibility(View.INVISIBLE);
                if(getIntent().hasExtra("quantity")){
                    quantity_para = getIntent().getIntExtra("quantity",1);
                    food_quantity_value.setText(String.valueOf(quantity_para));
                }
                if(getIntent().hasExtra("documentID")){
                    documentID = getIntent().getStringExtra("documentID");
                }
            }
            else if(source.equals("food_frag") || source.equals("food_frag_item") || source.equals("receipt_show")){
                food_quantity_view.setVisibility(View.INVISIBLE);
                food_quantity_value.setVisibility(View.INVISIBLE);
                Delete_button.setVisibility(View.INVISIBLE);
                Confirm_button.setVisibility(View.INVISIBLE);
                Add_button.setVisibility(View.INVISIBLE);
                food_data_topDivider.setVisibility(View.INVISIBLE);
                food_data_bottomDivider.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getFoodData(){ //從資料庫取得食物的資訊
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
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                    }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        storage.getReference().child(food_name + ".jpg").getBytes(Long.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() { //設置食物圖片
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.isSuccessful()) {
                    byte[] imageData = task.getResult();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, Objects.requireNonNull(imageData).length);
                    food_picture.setImageBitmap(bitmap);
                }
                else{
                    Log.e("Storage", "Error getting image", task.getException());
                }
            }
        });
    }


    public void Back(){ //點選返回按鈕會回到前一個頁面
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
                else if(source.equals("food_frag_item")){
                    back_bundle.putString("fragmentToShow", "food_frag");
                    back_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                }
                else if(source.equals("receipt_show")){
                    back_bundle.putString("source", "recipe_frag");
                    back_bundle.putString("fragmentToShow", "recipe_frag");
                    back_bundle.putString("food_name", food_name);
                    back_intent.setClass(ShowFoodDataActivity.this, ShowRecipeActivity.class);
                }
                back_intent.putExtras(back_bundle);
                back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(back_intent);
                finish();  // 關閉當前 Activity
            }
        });
    }

    public void Delete(){ //點選刪除按鈕能將該項食物從指定日期儲存的食物中刪除
        Delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateStr).collection("foods").document(documentID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                Bundle delete_bundle = new Bundle();
                Intent delete_intent = new Intent();
                delete_bundle.putString("fragmentToShow", "diary_frag");
                delete_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                delete_intent.putExtras(delete_bundle);
                startActivity(delete_intent);
                finish();
            }
        });
    }

    public void Add(){ //點選新增能將食物新增至指定日期儲存的食物中
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (food_quantity_value.getText().toString().length() < 1) {
                    Toast.makeText(ShowFoodDataActivity.this, "請輸入數量", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(food_quantity_value.getText().toString()) < 1) {
                    Toast.makeText(ShowFoodDataActivity.this, "請輸入有效數量", Toast.LENGTH_SHORT).show();
                }
                else {
                    food_quantity = Integer.parseInt(food_quantity_value.getText().toString());
                    Map<String, Object> food = new HashMap<>();
                    food.put("name", food_name);
                    food.put("calories", calories);
                    food.put("carbohydrate", carbohydrate);
                    food.put("protein", protein);
                    food.put("fat", fat);
                    food.put("sodium", sodium);
                    food.put("quantity", food_quantity);

                    db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateStr).collection("foods").add(food).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

                    //-----------------校正開始-----------------------------

                    //先新增校正欄位到對應的日期文件
                    Map<String, Object> correction = new HashMap<>();
                    correction.put("correction", "correction");
                    db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateStr).set(correction).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

                    //接著把校正欄位刪除，藉此將document的日期格式去斜體化，避免找不到對應字串的問題
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("correction", FieldValue.delete());
                    db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateStr).update(updates);

                    //-----------------校正結束-----------------------------

                    Bundle Add_bundle = new Bundle();
                    Intent Add_intent = new Intent();
                    Add_bundle.putString("fragmentToShow", "diary_frag");
                    Add_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                    Add_intent.putExtras(Add_bundle);
                    startActivity(Add_intent);
                    finish();
                }
            }
        });
    }

    public void Confirm(){ //點選確認(編輯)後能更新指定日期儲存的食物
        Confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                food_quantity = Integer.parseInt(food_quantity_value.getText().toString());

                Map<String, Object> updates = new HashMap<>();
                updates.put("quantity", food_quantity);
                db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateStr).collection("foods").document(documentID).update(updates);

                Bundle Confrim_bundle = new Bundle();
                Intent Confrim_intent = new Intent();
                Confrim_bundle.putString("fragmentToShow", "diary_frag");
                Confrim_intent.setClass(ShowFoodDataActivity.this, MainActivity.class);
                Confrim_intent.putExtras(Confrim_bundle);
                startActivity(Confrim_intent);
                finish();
            }
        });
    }

}