package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//此為diary選項用來顯示特定日期的內容
public class inner_diary_fragment extends Fragment {
    private LinearLayout dynamicLayout;
    private String date_str;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inner_diary_fragment, container, false);
        BindById(view);
        getData();
        CheckDateExist(date_str);
        return view;
    }

    public void BindById(View view){
        dynamicLayout = view.findViewById(R.id.inner_diary_linearLayout);
    }

    public void getData(){ //取得日期參數
        Bundle args = getArguments();
        if (args != null) {
            date_str = args.getString("date str");
        }
    }


    public void CheckDateExist(String dateString){ //檢查資料庫中是否存在此日期
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateString).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dynamicLayout.removeAllViews();
                        getDataFromDataBase(dateString);

                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void getDataFromDataBase(String dateString){ //從資料庫取得特定日期儲存的所有食物
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(dateString).collection("foods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentID = document.getId();
                        String food_name = document.get("name").toString();
                        int calories = Integer.parseInt(document.get("calories").toString());
                        double carbohydrate = Double.parseDouble(document.get("carbohydrate").toString());
                        double protein = Double.parseDouble(document.get("protein").toString());
                        double fat = Double.parseDouble(document.get("fat").toString());
                        int sodium = Integer.parseInt(document.get("sodium").toString());
                        int quantity = Integer.parseInt(document.get("quantity").toString());
                        addItem(food_name, calories, carbohydrate, protein, fat, sodium, quantity, documentID);
                    }

                }
                else {
                    Log.d(MotionEffect.TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addItem(String food_name, int calories, double carbohydrate, double protein, double fat, int sodium, int quantity, String documentID){ //用來新增顯示的食物物件
        TextView textview = new TextView(getContext());
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String textPart1 = food_name + "\n";    //食物名稱
        builder.append(textPart1);
        int textSizePart1 = 30;
        AbsoluteSizeSpan sizeSpanPart1 = new AbsoluteSizeSpan(textSizePart1, true);
        builder.setSpan(sizeSpanPart1, 0, textPart1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart2 = "數量 : " + String.valueOf(quantity) + " 份\n";
        builder.append(textPart2);
        int textSizePart2 = 20;
        AbsoluteSizeSpan sizeSpanPart2 = new AbsoluteSizeSpan(textSizePart2, true);
        builder.setSpan(sizeSpanPart2, textPart1.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart3 = "熱量 : " + String.valueOf(quantity * calories) + " cal\n";
        builder.append(textPart3);
        int textSizePart3 = 15;
        AbsoluteSizeSpan sizeSpanPart3 = new AbsoluteSizeSpan(textSizePart3, true);
        builder.setSpan(sizeSpanPart3, textPart2.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart4 = "碳水 : " + String.valueOf(quantity * carbohydrate) + " g\t\t蛋白質 : " + String.valueOf(quantity * protein) + " g\n";
        builder.append(textPart4);
        int textSizePart4 = 15;
        AbsoluteSizeSpan sizeSpanPart4 = new AbsoluteSizeSpan(textSizePart4, true);
        builder.setSpan(sizeSpanPart4, textPart3.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String textPart5 = "脂肪 : " + String.valueOf(quantity * fat) + " g\t\t鈉 : " + String.valueOf(quantity * sodium) + " mg";
        builder.append(textPart5);
        int textSizePart5 = 15;
        AbsoluteSizeSpan sizeSpanPart5 = new AbsoluteSizeSpan(textSizePart5, true);
        builder.setSpan(sizeSpanPart5, textPart4.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textview.setText(builder);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( //設置textview之間的間距以及textview寬度
                getResources().getDimensionPixelSize(R.dimen.fixed_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0);
        textview.setLayoutParams(layoutParams);

        textview.setPadding(15,0,15,10); //設置textView和外框的間距

        int drawableResourceId = R.drawable.diary_fooditem_border; //設置邊框
        textview.setBackground(ContextCompat.getDrawable(requireContext(), drawableResourceId)); //設置背景

        textview.setOnClickListener(new View.OnClickListener() { //點選指定textView能跳至ShowFoodDataActivity顯示詳細資訊
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "diary_frag_editFood");
                bundle.putString("food_name", food_name);
                bundle.putInt("quantity", quantity);
                bundle.putString("documentID",documentID);
                bundle.putString("dateStr",date_str);

                Intent intent = new Intent();
                intent.setClass(getActivity(), ShowFoodDataActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        dynamicLayout.addView(textview);
    }

}