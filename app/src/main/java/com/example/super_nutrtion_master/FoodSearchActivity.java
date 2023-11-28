package com.example.super_nutrtion_master;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FoodSearchActivity extends AppCompatActivity {

    EditText keywordText;
    Button foodSearch_button, back_button;
    String keyword;
    ListView foodList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);
        BindById();
        search();
        Back();
    }

    public void BindById(){
        foodSearch_button = findViewById(R.id.FoodSearch);
        back_button = findViewById(R.id.back_button);
        keywordText = findViewById(R.id.KeywordEditText);
        foodList = findViewById(R.id.food_list);
    }

    public void getKeyword(){
        keyword = keywordText.getText().toString();
    }

    public void search(){
        foodSearch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getKeyword();
                getDatabaseData(keyword);

            }
        });
    }

    public void Back(){
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle back_bundle = new Bundle();
                back_bundle.putString("fragmentToShow", "food_frag");

                Intent back_intent = new Intent();
                back_intent.setClass(FoodSearchActivity.this, MainActivity.class);
                back_intent.putExtras(back_bundle);
                back_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(back_intent);
            }
        });
    }

    public void setAdapter(ArrayList<String> food_name_list){
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,food_name_list);
        foodList.setAdapter(adapter);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String msg = food_name_list.get(position);
                Toast.makeText(FoodSearchActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getDatabaseData(String keyword){

        CollectionReference collectRef = db.collection("foods");


        ArrayList<String> food_list = new ArrayList<String>();

        collectRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getId().contains(keyword)){
                            food_list.add(document.getId());
                        }
                    }
                    setAdapter(food_list);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
}