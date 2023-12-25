package com.example.super_nutrtion_master;

import static android.content.Intent.getIntent;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

import android.util.Log;

//此為food選項用來瀏覽食物的頁面
public class food_fragment extends Fragment {
    private Button search_button;

    private ListView foodList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_fragment, container, false);
        BindById(view);

        return view;
    }

    public void BindById(View view){
        search_button = view.findViewById(R.id.search);

        foodList = view.findViewById(R.id.foodlist2);

        TabLayout tabLayout = view.findViewById(R.id.food_type_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() { //選擇食物類別
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String tabText = (String) tab.getText();
                if ("飯".equals(tabText)) {
                    getDataFromDataBase(tabText);
                } else if ("麵".equals(tabText)) {
                    getDataFromDataBase(tabText);
                }else if ("便當".equals(tabText)) {
                    getDataFromDataBase(tabText);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                    // 如果需要，處理選擇取消
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                 // 如果需要，處理重新選擇
            }
        });
    }

    public void getDataFromDataBase(String keyword){ //從資料庫取得指定食物的資訊
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

    public void setAdapter(ArrayList<String> food_name_list){ //設置搜尋選單
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, food_name_list);
        foodList.setAdapter(adapter);
        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String food_name = food_name_list.get(position);
                GotoFoodDataPage(food_name);
            }
        });
    }

    public void GotoFoodDataPage(String food_name){ //跳至ShowFoodDataActivity顯示食物資訊
        Bundle fdc_bundle = new Bundle();
        fdc_bundle.putString("food_name", food_name);

        Intent fdc_intent = new Intent();
        fdc_intent.setClass(getActivity(), ShowFoodDataActivity.class);
        fdc_intent.putExtras(fdc_bundle);
        startActivity(fdc_intent);
    }

}