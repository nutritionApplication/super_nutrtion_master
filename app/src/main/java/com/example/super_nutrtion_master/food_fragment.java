package com.example.super_nutrtion_master;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class food_fragment extends Fragment {

    Button search_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_fragment, container, false);
        BindById(view);
        search();
        return view;
    }

    public void BindById(View view){
        search_button = view.findViewById(R.id.search);
    }

    public void search(){
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "food_frag");

                Intent intent = new Intent();
                intent.setClass(getActivity(), FoodSearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}