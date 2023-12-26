package com.example.super_nutrtion_master;

import static android.content.Intent.getIntent;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import android.util.Log;
import android.widget.TextView;

//此為food選項用來瀏覽食物的頁面
public class food_fragment extends Fragment {
    private Button search_button;
    private TabLayout tablayout;
    private ListView foodList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_fragment, container, false);
        BindById(view);
        search();
        setDefaultFoodType();
        selectFoodType();
        return view;
    }

    public void BindById(View view){
        search_button = view.findViewById(R.id.food_frag_search);
        tablayout = view.findViewById(R.id.food_type_tab);
        foodList = view.findViewById(R.id.foodFragList);
    }

    public void search(){   //按下搜尋後跳至FoodSearchActivity
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle search_bundle = new Bundle();
                search_bundle.putString("source", "food_frag");

                Intent search_intent = new Intent();
                search_intent.setClass(getActivity(), FoodSearchActivity.class);
                search_intent.putExtras(search_bundle);
                startActivity(search_intent);
            }
        });
    }

    public void setDefaultFoodType(){ //設置tablayout的預設選項
        TabLayout.Tab defaultItem = tablayout.getTabAt(0);
        if (defaultItem != null) {
            defaultItem.select();
            String keyword = String.valueOf(defaultItem.getText());
            getDataFromDataBase(keyword);
        }
    }

    public void selectFoodType(){ //選擇tabItem時做出對應的動作
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String keyword = String.valueOf(tab.getText());
                getDataFromDataBase(keyword);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void getDataFromDataBase(String keyword){ //從資料庫取得指定食物的資訊
        CollectionReference collectRef = db.collection("foods");
        ArrayList<String> food_list = new ArrayList<String>();
        ArrayList<String> food_calories = new ArrayList<String>();

        //取得該項類別的所有食物名稱
        collectRef.whereArrayContains("type", keyword).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        food_list.add(document.getId());
                        food_calories.add(document.get("calories").toString());
                    }
                    setAdapter(food_list, food_calories);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void setAdapter(ArrayList<String> food_name_list, ArrayList<String> food_calories_list){
        MyAdapter adapter = new MyAdapter(getContext(), food_name_list, food_calories_list);
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
        fdc_bundle.putString("source", "food_frag_item");

        Intent fdc_intent = new Intent();
        fdc_intent.setClass(getActivity(), ShowFoodDataActivity.class);
        fdc_intent.putExtras(fdc_bundle);
        startActivity(fdc_intent);
    }

    public class MyAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> foodNameList;
        ArrayList<String> foodCaloriesList;

        MyAdapter(Context context,ArrayList<String> food_list, ArrayList<String> food_calories){
            this.context = context;
            this.foodNameList = food_list;
            this.foodCaloriesList = food_calories;
        }

        @Override
        public int getCount() {
            return foodNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return foodNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.food_frag_list_item_layout, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.food_frag_list_item_image);
            TextView textView1 = convertView.findViewById(R.id.food_frag_list_item_foodName);
            TextView textView2 = convertView.findViewById(R.id.food_frag_list_item_foodCalories);

            // 设置数据
            String foodName = foodNameList.get(position);
            String foodCalories = foodCaloriesList.get(position);

            loadAndDisplayImage(foodName, imageView);
            textView1.setText(foodName);
            textView2.setText("熱量 : " + foodCalories + " 大卡");

            return convertView;
        }

        private void loadAndDisplayImage(String imageName, ImageView imageView) {
            storage.getReference().child(imageName + ".jpg").getBytes(Long.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() { //設置食物圖片
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful()) {
                        byte[] imageData = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, Objects.requireNonNull(imageData).length);
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Log.e("Storage", "Error getting image", task.getException());
                    }
                }
            });
        }
    }

}