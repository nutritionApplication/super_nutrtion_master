package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// 此為H.I選項顯示的畫面
public class HI_fragment extends Fragment {
    private TextView BMI_view, BMR_view, TDEE_view, Whole_grains_view, LFEM_view, Dairy_view, Vegetables_view, Fruits_view, OFNS_view, protein_view, fat_view, sodium_view;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_h_i_fragment, container, false);
        BindById(view);
        getUserDataFromDataBase();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void BindById(View view){
        BMI_view = view.findViewById(R.id.BMI_textView);
        BMR_view = view.findViewById(R.id.BMR_textView);
        TDEE_view = view.findViewById(R.id.TDEE_textView);
        protein_view = view.findViewById(R.id.protein_textView);
        fat_view = view.findViewById(R.id.fat_textView);
        sodium_view = view.findViewById(R.id.sodium_textView);

        //----------------------六大類食物已廢棄-----------------------------//
        //Whole_grains_view = view.findViewById(R.id.Whole_grains_textView);
        //LFEM_view = view.findViewById(R.id.LFEM_textView);
        //Dairy_view = view.findViewById(R.id.Dairy_textView);
        //Vegetables_view = view.findViewById(R.id.Vegetables_textView);
        //Fruits_view = view.findViewById(R.id.Fruits_textView);
        //OFNS_view = view.findViewById(R.id.OFNS_textView);
        //----------------------------------------------------------------//
    }

    public void getUserDataFromDataBase(){ //從資料庫取得使用者的營養指標值並設置到textView上
        db.collection("users").document(login_username.getInstance().getUsername()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        BMI_view.setText("BMI : " + document.get("BMI").toString());
                        BMR_view.setText("BMR(基礎代謝率) : " + document.get("BMR").toString());
                        TDEE_view.setText("TDEE(每日總熱量消耗) : " + document.get("TDEE").toString());
                        protein_view.setText("蛋白質 : " + document.get("protein").toString() + "g");
                        fat_view.setText("脂肪 : " + document.get("fat").toString() + "g");
                        sodium_view.setText("鈉 : " + document.get("sodium").toString() + "mg");

                        //----------------------六大類食物已廢棄-----------------------------//
                        //Whole_grains_view.setText("全榖雜糧類 : " + document.get("Whole_grains").toString() + "份");
                        //LFEM_view.setText("豆魚蛋肉類 : " + document.get("LFEM").toString() + "份");
                        //Dairy_view.setText("乳品類 : " + document.get("Dairy").toString() + "份");
                        //Vegetables_view.setText("蔬菜類 : " + document.get("Vegetables").toString() + "份");
                        //Fruits_view.setText("水果類 : " + document.get("Fruits").toString() + "份");
                        //OFNS_view.setText("油脂與堅果種子類 : " + document.get("OFNS").toString() + "份  (油脂類 : " + document.get("oils_and_fats").toString() + "份 + 堅果種子類 : " + document.get("nuts_and_seeds").toString() + "份)");
                        //----------------------------------------------------------------//
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