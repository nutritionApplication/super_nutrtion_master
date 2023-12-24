package com.example.super_nutrtion_master;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.BaseAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import android.widget.ListView;
import android.content.Context;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link recipe_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recipe_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button search_button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private LinearLayout dynamicLayout;

    private TextView recipeView;

    private ListView recipe_item;
    private ImageView food_picture;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFragment.
     */
    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_fragment, container, false);
        View item_view = inflater.inflate(R.layout.fragment_inner_recipe_fragment, container, false);

        BindById(view, item_view);
        search();
        getDataFromDataBase();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public void BindById(View view, View item_view){
        search_button = view.findViewById(R.id.search);
        recipe_item = view.findViewById(R.id.recipe_item);
        food_picture = item_view.findViewById(R.id.recipe_image);
    }

    public void search(){ //點選搜尋後跳至 recipeSearchActivity
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "recipe_search");

                Intent intent = new Intent();
                intent.setClass(getActivity(), RecipeSearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView subtitleTextView;
    }

    public class RecipeAdapter extends BaseAdapter{
        private Context context;
        private int resource;
        private List<RecipeItem> recipe_obj;


        RecipeAdapter (Context context, int resource, List<RecipeItem> recipe_obj){
            this.context = context;
            this.resource = resource;
            this.recipe_obj = recipe_obj;
        }

        @Override
        public int getCount() {
            return recipe_obj.size();
        }
        @Override
        public Object getItem(int position) {
            return recipe_obj.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.fragment_inner_recipe_fragment, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.imageView = view.findViewById(R.id.recipe_image);
                viewHolder.titleTextView = view.findViewById(R.id.otitle);
                viewHolder.subtitleTextView = view.findViewById(R.id.ttitle);

                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            // 獲取當前位置的資料項目
            RecipeItem currentItem = recipe_obj.get(position);
            viewHolder.imageView.setImageDrawable(currentItem.getFoodImg().getDrawable());
            viewHolder.titleTextView.setText(currentItem.getTitle());

            StringBuilder recipeText = new StringBuilder();
            List<Object> recipeList = currentItem.getRecipe();

            // 處理陣列 逐行印出 印前5行
            int linesToShow = 5;
            for (int i = 0; i < Math.min(linesToShow, recipeList.size()); i++) {
                recipeText.append(recipeList.get(i).toString()).append("\n");
            }

            if (recipeList.size() > linesToShow) {
                recipeText.append("...\n查看更多");
            }
            viewHolder.subtitleTextView.setText(recipeText.toString().trim());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowRecipeActivity.class);
                    intent.putExtra("source", "recipe_frag");
                    intent.putExtra("food_name", currentItem.getTitle());

                    context.startActivity(intent);
                }
            });

            return view;
        }
    }
    public class RecipeItem {
        private String title;
        private List<Object> recipe;
        private ImageView food_img;

        public RecipeItem(String title, ImageView food_img, List<Object> recipe) {
            this.title = title;
            this.food_img = food_img;
            this.recipe = recipe;
        }

        public String getTitle() {
            return title;
        }
        public ImageView getFoodImg() {
            return food_img;
        }
        public List<Object> getRecipe() {
            return recipe;
        }
    }

    public void getDataFromDataBase () {
        db.collection("recipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<RecipeItem> recipeItemList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String food_name = document.getId();
                        List<Object> food_recipe = (List<Object>) document.get("recipe");

                        getStorageImg(food_name, new OnImageLoadedListener() {
                            @Override
                            public void onImageLoaded(ImageView imageView) {
                                RecipeItem food_recipe_obj = new RecipeItem(food_name, imageView,
                                        food_recipe);
                                recipeItemList.add(food_recipe_obj);

                                if (recipeItemList.size() == task.getResult().size()) { // 所有圖片加載完成
                                    RecipeAdapter recipeAdapter = new RecipeAdapter(getContext(),
                                            R.layout.fragment_inner_recipe_fragment,
                                            recipeItemList);
                                    recipe_item.setAdapter(recipeAdapter);
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(ImageView imageView);
    }

    public void getStorageImg(String food_name, OnImageLoadedListener listener) {
        storage.getReference().child(food_name + ".jpg").getBytes(Long.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() { // 設置食物圖片

            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if (task.isSuccessful()) {
                    byte[] imageData = task.getResult();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, Objects.requireNonNull(imageData).length);

                    ImageView food_picture = new ImageView(getContext());
                    food_picture.setImageBitmap(bitmap);

                    if (listener != null) {
                        listener.onImageLoaded(food_picture);
                    }
                }
                else{
                    Log.e("Storage", "Error getting image", task.getException());
                }
            }
        });
    }
}
