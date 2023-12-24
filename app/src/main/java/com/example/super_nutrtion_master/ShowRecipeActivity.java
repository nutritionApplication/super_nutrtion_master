package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//用來顯示食物詳細資訊的頁面
public class ShowRecipeActivity extends AppCompatActivity {

    private Button Back_button;
    private Button check_food_info_button;

    private TextView food_name_view;
    private ImageView food_picture;
    private String source, food_name, documentID, dateStr = selectedDate.getInstance().getDateString();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private View food_data_topDivider, food_data_bottomDivider;

    private TextView food_ingredient;
    private TextView food_recipe;
    private ProgressBar loading_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);
        BindById();
        IntentJudgment();
        getFoodData();
        Back();
        checkFoodInfo();
    }

    public void BindById(){
        Back_button = findViewById(R.id.foodDataPageBackButton);
        food_name_view = findViewById(R.id.food_name_view);
        food_picture = findViewById(R.id.foodImage);
        food_data_topDivider = findViewById(R.id.food_data_topDivider);
        food_data_bottomDivider = findViewById(R.id.food_data_bottomDivider);
        food_ingredient = findViewById(R.id.show_food_ingredient);
        food_recipe = findViewById(R.id.show_food_recipe);
        check_food_info_button = findViewById(R.id.check_food_info_button);
        loading_bar = findViewById(R.id.loading_bar);
    }

    public void IntentJudgment(){ //根據開啟此頁面的來源來判斷要顯示的內容以及擷取對應的參數
        if (getIntent().hasExtra("food_name")) {
            food_name = getIntent().getStringExtra("food_name");
        }
        if (getIntent().hasExtra("source")){
            source = getIntent().getStringExtra("source");
        }
    }

    public void getFoodData(){ // 從資料庫取得食物的資訊
        loading_bar.setVisibility(View.VISIBLE);
        db.collection("recipes").document(food_name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> ingredient = (List<Object>) document.get("ingredient");
                        List<Object> recipe = (List<Object>) document.get("recipe");
                        food_name_view.setText(food_name);

                        // 處理材料陣列 逐行印出
                        StringBuilder ingredientList = new StringBuilder();
                        for (Object i : ingredient) {
                            ingredientList.append(i.toString()).append("\n");
                        }
                        food_ingredient.setText(ingredientList.toString());

                        // 處理食譜陣列 逐行印出
                        StringBuilder recipeList = new StringBuilder();
                        for (Object i : recipe) {
                            recipeList.append(i.toString()).append("\n");
                        }
                        food_recipe.setText(recipeList.toString());
                    }
                    else {
                        food_ingredient.setText("還沒收錄製作" + food_name + "的材料...");
                        food_recipe.setText("還沒收錄製作" + food_name + "的食譜...");
                        food_recipe.setGravity(Gravity.CENTER);
                        food_ingredient.setGravity(Gravity.CENTER);
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
                    loading_bar.setVisibility(View.GONE);
                }
                else{
                    Log.e("Storage", "Error getting image", task.getException());
                }
            }
        });
    }


    public void Back() { //點選返回按鈕會回到前一個頁面
        Back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle back_bundle = new Bundle();
                Intent back_intent = new Intent();

                // 來源頁面有兩種: 一種是直接點擊食譜，一種是透過搜尋進入
                if (getIntent().hasExtra("source")) {

                    source = getIntent().getStringExtra("source");
                    if (source.equals("recipe_search")) {
                        back_bundle.putString("source", "ShowRecipeActivity");
                        back_intent.setClass(ShowRecipeActivity.this, RecipeSearchActivity.class);

                    } else {
                        back_bundle.putString("source", "recipe_frag");
                        back_intent.setClass(ShowRecipeActivity.this, MainActivity.class);
                    }

                    back_intent.putExtras(back_bundle);
                    back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(back_intent);
                    finish();  // 關閉當前 Activity
                } else {
                    Log.d("recipe", "no source");
                }

            }
        });
    }
    public void checkFoodInfo () {
        check_food_info_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle back_bundle = new Bundle();
                Intent back_intent = new Intent();

                if (getIntent().hasExtra("source")) {

                    back_bundle.putString("source", "receipt_show");
                    back_bundle.putString("source", "receipt_show");
                    back_bundle.putString("food_name", food_name);

                    back_intent.setClass(ShowRecipeActivity.this, ShowFoodDataActivity.class);

                    back_intent.putExtras(back_bundle);
                    back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(back_intent);
                    finish();  // 關閉當前 Activity
                } else {
                    Log.d("recipe", "no source");
                }

            }
        });
    }
}