package com.example.super_nutrtion_master;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//用來顯示使用者在指定月份中每天攝取的營養
public class inner_calendar_fragment extends Fragment {
    private LinearLayout dynamicLayout;
    private String YearAndMonth_str = selectedDate.getInstance().getMonthString();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int year, month;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inner_calendar_fragment, container, false);
        BindById(view);
        showDateItem();
        return view;
    }

    public void BindById(View view){
        dynamicLayout = view.findViewById(R.id.inner_calendar_layout);
    }

    public void showDateItem(){
        //取得年和月的整數數值
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        try {
            // 解析日期字串
            Date date = dateFormat.parse(YearAndMonth_str);
            // 將日期拆分為年、月
            year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date));
            month = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //清空fragment內容
        dynamicLayout.removeAllViews();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 动态添加TextView
        for (int day = 1; day <= daysInMonth; day++) {
            addDateItem(year, month, day);
        }
    }

    public void addDateItem(int year, int month, int day){
        LinearLayout RowContainer = new LinearLayout(getContext());
        RowContainer.setOrientation(LinearLayout.HORIZONTAL);

        //設置RowContainer的高度、寬度與margin
        LinearLayout.LayoutParams RowContainer_layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // 寬度填滿
                LinearLayout.LayoutParams.WRAP_CONTENT  // 高度包裹內容
        );
        RowContainer_layoutParams.setMargins(10, 10, 10, 0);
        RowContainer.setLayoutParams(RowContainer_layoutParams);

        //日期放在該列的左邊
        TextView dateTextView = new TextView(getContext());
        dateTextView.setText(String.valueOf(day));
        dateTextView.setTextSize(30);
        dateTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dateTextView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dateTextView_layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.calendar_date_width),
                getResources().getDimensionPixelSize(R.dimen.calendar_item_height)
        );
        dateTextView_layoutParams.gravity = Gravity.CENTER;
        dateTextView.setLayoutParams(dateTextView_layoutParams);
        RowContainer.addView(dateTextView);

        //對應的日期資訊放在該列的右邊
        TextView InformationTextView = new TextView(getContext());
        SpannableStringBuilder builder = new SpannableStringBuilder();
        //從資料庫取得指定日期攝取的各類營養數量
        db.collection("users").document(login_username.getInstance().getUsername()).collection("date").document(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int consumedCalories = Integer.parseInt(document.get("consumedCalories").toString());
                        double consumedCarbohydrate = Double.parseDouble(document.get("consumedCarbohydrate").toString());
                        double consumedProtein = Double.parseDouble(document.get("consumedProtein").toString());
                        double consumedFat = Double.parseDouble(document.get("consumedFat").toString());
                        int consumedSodium = Integer.parseInt(document.get("consumedSodium").toString());

                        //將資訊填入到textview中
                        String textPart1 = "熱量 : " + String.valueOf(consumedCalories) + " cal\n";    //食物物件中的第一行顯示食物名稱
                        builder.append(textPart1);
                        int textSizePart1 = 15;
                        AbsoluteSizeSpan sizeSpanPart1 = new AbsoluteSizeSpan(textSizePart1, true);
                        builder.setSpan(sizeSpanPart1, 0, textPart1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        String textPart2 = "碳水 : " + String.valueOf(consumedCarbohydrate) + " g\t\t蛋白質 : " + String.valueOf(consumedProtein) + " g\n";  //食物物件中的第二行顯示食物數量
                        builder.append(textPart2);
                        int textSizePart2 = 15;
                        AbsoluteSizeSpan sizeSpanPart2 = new AbsoluteSizeSpan(textSizePart2, true);
                        builder.setSpan(sizeSpanPart2, textPart1.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        String textPart3 = "脂肪 : " + String.valueOf(consumedFat) + " g\t\t鈉 : " + String.valueOf(consumedSodium) + " mg";  //食物物件中的第三行顯示食物熱量(單份熱量 * 數量)
                        builder.append(textPart3);
                        int textSizePart3 = 15;
                        AbsoluteSizeSpan sizeSpanPart3 = new AbsoluteSizeSpan(textSizePart3, true);
                        builder.setSpan(sizeSpanPart3, textPart2.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        InformationTextView.setText(builder);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        LinearLayout.LayoutParams InformationTextView_layoutParams = new LinearLayout.LayoutParams( //設置textview之間的間距以及textview寬度
                getResources().getDimensionPixelSize(R.dimen.calendar_item_width),  //設置textview寬度
                getResources().getDimensionPixelSize(R.dimen.calendar_item_height)
        );
        //InformationTextView_layoutParams.setMargins(30, 0, 0, 0);  //設置textview之間的間距
        InformationTextView.setLayoutParams(InformationTextView_layoutParams);

        InformationTextView.setPadding(50,15,15,15); //設置textView內文字和外框的間距

        int drawableResourceId = R.drawable.diary_fooditem_border; //設置邊框
        InformationTextView.setBackground(ContextCompat.getDrawable(requireContext(), drawableResourceId));

        InformationTextView.setOnClickListener(new View.OnClickListener() { //點選指定textView能跳至特定日期的diary畫面
            @Override
            public void onClick(View v) {
                selectedDate.getInstance().setDateString(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day)); //設置指定日期

                Bundle bundle = new Bundle();
                bundle.putString("fragmentToShow", "diary_frag");
                Intent intent = new Intent();
                intent.setClass(getActivity(), MainActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        RowContainer.addView(InformationTextView);
        dynamicLayout.addView(RowContainer);
    }


}