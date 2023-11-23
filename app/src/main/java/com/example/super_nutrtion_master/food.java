package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class food {

    public String food_name;
    public int calories;
    public int carbohydrate;
    public int protein;
    public int fat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    public food(){

    }

    public food(String food_name){
        this.food_name = food_name;
    }

    public food(String food_name, int calories, int carbohydrate, int protein, int fat){
        this.food_name = food_name;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
    }

    public void writeData(){
        Map<String, Object> food = new HashMap<>();
        //food.put("food name", food_name);
        food.put("calories", calories);
        food.put("carbohydrate", carbohydrate);
        food.put("protein", protein);
        food.put("fat", fat);

        db.collection("foods").document(food_name)
                .set(food)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void readData(){
        docRef = db.collection("foods").document(food_name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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
