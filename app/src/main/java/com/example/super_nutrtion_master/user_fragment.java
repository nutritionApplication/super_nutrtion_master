package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

// 此為user選項顯示的畫面
public class user_fragment extends Fragment {
    private TextView genderView, ageView, heightView, weightView, exerciseView;
    private Button edit_button, logout_button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_fragment, container, false);
        BindById(view);
        getUserDataFromDataBase();
        edit();
        logout();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void BindById(View view){
        genderView = view.findViewById(R.id.user_frag_gender_value);
        ageView = view.findViewById(R.id.user_frag_age_value);
        heightView = view.findViewById(R.id.user_frag_height_value);
        weightView = view.findViewById(R.id.user_frag_weight_value);
        exerciseView = view.findViewById(R.id.user_frag_exercise_value);
        edit_button = view.findViewById(R.id.edit_button);
        logout_button = view.findViewById(R.id.logout_button);
    }

    public void getUserDataFromDataBase(){ //從資料庫取得使用者資料並設置到textView上
        db.collection("users").document(login_username.getInstance().getUsername()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        genderView.setText(document.get("gender_string").toString());
                        ageView.setText(document.get("age").toString());
                        heightView.setText(document.get("height").toString());
                        weightView.setText(document.get("weight").toString());
                        exerciseView.setText(document.get("exercise_string").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void edit(){ //點選編輯按鈕後跳至UserdataActivity
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "user_fragment");
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserdataActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void logout(){ //返回登入帳號的頁面
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}