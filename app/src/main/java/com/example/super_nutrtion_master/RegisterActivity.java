package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//此為註冊畫面的頁面
public class RegisterActivity extends AppCompatActivity {

    private EditText username_editText, password_editText;
    private Button back_button, confirm_button;
    private String username, password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        BindById();
        back();
        confirm();
    }

    public void BindById(){
        username_editText = findViewById(R.id.register_username);
        password_editText = findViewById(R.id.register_password);
        back_button = findViewById(R.id.register_back_button);
        confirm_button = findViewById(R.id.register_confirm_button);
    }

    public void back(){ //返回按鈕
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent();
                back_intent.setClass(RegisterActivity.this, LoginActivity.class);
                startActivity(back_intent);
                finish();
            }
        });
    }

    public void confirm(){ //確認按鈕
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_editText.getText().toString();
                password = password_editText.getText().toString();
                check_account(username, password);
            }
        });
    }

    public void check_account(String username, String password){ //確認帳號的有效性
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { //帳號名稱已被註冊過
                        Toast.makeText(RegisterActivity.this, "使用者帳號已被使用", Toast.LENGTH_SHORT).show();
                        username_editText.setText("");
                        password_editText.setText("");
                        username_editText.requestFocus();
                    } else { //沒有問題的話就註冊帳號，將該使用者新增置資料庫
                        create_account(username, password);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void create_account(String username, String password){
        Map<String, Object> account = new HashMap<>();
        account.put("password", password);

        db.collection("users").document(username)
                .set(account)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "註冊成功!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() { //三秒後跳轉回登入頁面
                            @Override
                            public void run() {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 3000);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }
}