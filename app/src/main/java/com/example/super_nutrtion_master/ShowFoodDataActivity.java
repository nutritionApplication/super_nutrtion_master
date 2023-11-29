package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class ShowFoodDataActivity extends AppCompatActivity {

    Button Back_button, Delete_button, Confirm_button;
    TextView food_name_view, calories_view, carbohydrate_view, protein_view, fat_view, sodium_view, food_quantity_view;
    ImageView food_picture;
    EditText food_quantity;
    String source, food_name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    double calories, carbohydrate, protein, fat, sodium;


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
        food_quantity = findViewById(R.id.food_quantity_value);
    }

    public void IntentJudgment(){ //根據開啟此頁面的來源來判斷要顯示的內容
        if(getIntent().hasExtra("source")){
            source = getIntent().getStringExtra("source");
            if(source.equals("diary_frag_addFood")){
                Delete_button.setVisibility(View.INVISIBLE);

            }
            else if(source.equals("diary_frag_editFood")){

            }
            else if(source.equals("food_frag")){
                food_quantity_view.setVisibility(View.INVISIBLE);
                food_quantity.setVisibility(View.INVISIBLE);
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
                            calories = (Double) document.getDouble("calories");
                            carbohydrate = document.getDouble("carbohydrate");
                            protein = document.getDouble("protein");
                            fat = document.getDouble("fat");
                            sodium = document.getDouble("sodium");
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
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

    }



}