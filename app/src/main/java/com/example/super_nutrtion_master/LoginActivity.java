package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//此為登入畫面的頁面
public class LoginActivity extends AppCompatActivity {

    private EditText username_editText, password_editText;
    private Button login_button, register_button;
    private String username, password;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BindById();
        login();
        register();
    }

    public void BindById(){
        username_editText = findViewById(R.id.username_input);
        password_editText = findViewById(R.id.password_input);
        login_button = findViewById(R.id.login);
        register_button = findViewById(R.id.register);
    }

    public void login(){
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = username_editText.getText().toString();
                password = password_editText.getText().toString();
                check_account(username, password);
            }
        });
    }

    public void register(){ //跳至註冊頁面
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent();
                register_intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
            }
        });

    }

    public void check_account(String username, String password){
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { //帳號存在
                        String DB_password = document.get("password").toString();
                        if(DB_password.equals(password)){ //密碼正確
                            login_username.getInstance().setUsername(username); //將使用者名稱設定至login_username，讓所有頁面皆可使用username
                            if(document.contains("TDEE")){ //非首次登入，跳轉到MainActivity開始使用APP
                                Bundle bundle = new Bundle();
                                bundle.putString("fragmentToShow", "diary_frag");
                                Intent login_intent = new Intent();
                                login_intent.setClass(LoginActivity.this, MainActivity.class);
                                login_intent.putExtras(bundle);
                                startActivity(login_intent);
                                finish();
                            }
                            else{ //首次登入，跳轉到UserdataActivity輸入資料
                                Bundle bundle = new Bundle();
                                bundle.putString("source", "LoginActivity");
                                Intent login_intent = new Intent();
                                login_intent.setClass(LoginActivity.this, UserdataActivity.class);
                                login_intent.putExtras(bundle);
                                startActivity(login_intent);
                                finish();
                            }
                        }
                        else{ //帳號存在但密碼錯誤的情況
                            Toast.makeText(LoginActivity.this, "使用者密碼錯誤", Toast.LENGTH_SHORT).show();
                            password_editText.setText("");
                            password_editText.requestFocus();
                        }
                    } else { //帳號不存在的情況
                        Toast.makeText(LoginActivity.this, "使用者帳號不存在", Toast.LENGTH_SHORT).show();
                        username_editText.setText("");
                        password_editText.setText("");
                        username_editText.requestFocus();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}