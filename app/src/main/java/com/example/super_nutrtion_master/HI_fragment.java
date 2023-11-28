package com.example.super_nutrtion_master;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HI_fragment extends Fragment {

    private int BMR, LFEM, Vegetables, OFNS, oils_and_fats, nuts_and_seeds, sodium;
    private double BMI, TDEE, protein, fat, Whole_grains, Dairy, Fruits;
    private TextView BMI_view, BMR_view, TDEE_view, Whole_grains_view, LFEM_view, Dairy_view, Vegetables_view, Fruits_view, OFNS_view, protein_view, fat_view, sodium_view;

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
        getData();
        setViewText();
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
        Whole_grains_view = view.findViewById(R.id.Whole_grains_textView);
        LFEM_view = view.findViewById(R.id.LFEM_textView);
        Dairy_view = view.findViewById(R.id.Dairy_textView);
        Vegetables_view = view.findViewById(R.id.Vegetables_textView);
        Fruits_view = view.findViewById(R.id.Fruits_textView);
        OFNS_view = view.findViewById(R.id.OFNS_textView);
        protein_view = view.findViewById(R.id.protein_textView);
        fat_view = view.findViewById(R.id.fat_textView);
        sodium_view = view.findViewById(R.id.sodium_textView);
    }

    public void getData(){
        Bundle bundle = this.getArguments();
        if(bundle!= null) {
            BMI = bundle.getDouble("BMI");
            BMR = bundle.getInt("BMR");
            TDEE = bundle.getDouble("TDEE");
            Whole_grains = bundle.getDouble("Whole_grains");
            LFEM = bundle.getInt("LFEM");
            Dairy = bundle.getDouble("Dairy");
            Vegetables = bundle.getInt("Vegetables");
            Fruits = bundle.getDouble("Fruits");
            OFNS = bundle.getInt("OFNS");
            oils_and_fats = bundle.getInt("oils_and_fats");
            nuts_and_seeds = bundle.getInt("nuts_and_seeds");
            protein = bundle.getDouble("protein");
            fat = bundle.getDouble("fat");
            sodium = bundle.getInt("Sodium");
        }
    }

    public void setViewText(){
        BMI_view.setText("BMI : " + Double.toString(BMI));
        BMR_view.setText("BMR(基礎代謝率) : " + Integer.toString(BMR));
        TDEE_view.setText("TDEE(每日總熱量消耗) : " + Double.toString(TDEE));
        Whole_grains_view.setText("全榖雜糧類 : " + Double.toString(Whole_grains) + "份");
        LFEM_view.setText("豆魚蛋肉類 : " + Integer.toString(LFEM) + "份");
        Dairy_view.setText("乳品類 : " + Double.toString(Dairy) + "份");
        Vegetables_view.setText("蔬菜類 : " + Integer.toString(Vegetables) + "份");
        Fruits_view.setText("水果類 : " + Double.toString(Fruits) + "份");
        OFNS_view.setText("油脂與堅果種子類 : " + Integer.toString(OFNS) + "份  (油脂類 : " + Integer.toString(oils_and_fats) + "份 + 堅果種子類 : " + Integer.toString(nuts_and_seeds) + "份)");
        protein_view.setText("蛋白質 : " + Double.toString(protein) + "g");
        fat_view.setText("脂肪 : " + Double.toString(fat) + "g");
        sodium_view.setText("鈉 : " + Integer.toString(sodium) + "mg");
    }
}