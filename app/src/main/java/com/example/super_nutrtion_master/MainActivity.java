package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottom_menu;
    private user_fragment user_fragment = new user_fragment();
    private HI_fragment HI_fragment = new HI_fragment();
    private diary_fragment diary_fragment = new diary_fragment();
    private food_fragment food_fragment = new food_fragment();
    private recipe_fragment recipe_fragment = new recipe_fragment();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindById();
        fragToShow();
        change_page();
    }

    public void BindById(){
        bottom_menu = findViewById(R.id.bottomNavigationView);
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragmentPage,fragment).commit();
    }

    public void fragToShow(){ //進入主畫面時預設的fragment
        if(getIntent().hasExtra("fragmentToShow")){
            String fragTag = getIntent().getStringExtra("fragmentToShow");
            if(fragTag.equals("user_frag")){
                bottom_menu.setSelectedItemId(R.id.user); //bottomMenu的選項設為user
                setFragment(user_fragment);
            }
            else if(fragTag.equals("diary_frag")){
                bottom_menu.setSelectedItemId(R.id.diary); //bottomMenu的選項設為diary
                setFragment(diary_fragment);
            }
            else if(fragTag.equals("food_frag")){
                bottom_menu.setSelectedItemId(R.id.food); //bottomMenu的選項設為food
                setFragment(food_fragment);
            }
            else if(fragTag.equals("recipe_frag")){
                bottom_menu.setSelectedItemId(R.id.recipe); //bottomMenu的選項設為recipe
                setFragment(recipe_fragment);
            }
        }
    }

    public void change_page(){ //按下按鈕時切換到對應的fragment
        bottom_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user:
                        setFragment(user_fragment);
                        return true;
                    case R.id.HI:
                        setFragment(HI_fragment);
                        return true;
                    case R.id.diary:
                        setFragment(diary_fragment);
                        return true;
                    case R.id.food:
                        setFragment(food_fragment);
                        return true;
                    case R.id.recipe:
                        setFragment(recipe_fragment);
                        return true;
                }
                return false;
            }
        });
    }
}