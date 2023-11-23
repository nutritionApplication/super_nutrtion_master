package com.example.super_nutrtion_master;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class user_fragment extends Fragment {
    private TextView genderView, ageView, heightView, weightView, exerciseView;
    private Button edit_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_fragment, container, false);
        BindById(view);
        setViewText();
        edit();
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
    }

    public void setViewText(){
        Bundle bundle = this.getArguments();
        if(bundle!= null) {
            genderView.setText(bundle.getString("gender str"));
            ageView.setText(Integer.toString(bundle.getInt("age")));
            heightView.setText(Integer.toString(bundle.getInt("height")));
            weightView.setText(Integer.toString(bundle.getInt("weight")));
            exerciseView.setText(bundle.getString("exercise str"));
        }
    }

    public void edit(){
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserdataActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //不會重新開始生命週期
                startActivity(intent);
            }
        });
    }
}