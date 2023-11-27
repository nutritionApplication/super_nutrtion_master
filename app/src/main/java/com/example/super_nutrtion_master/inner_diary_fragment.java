package com.example.super_nutrtion_master;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class inner_diary_fragment extends Fragment {

    TextView dateView;
    String date_str;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference docRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inner_diary_fragment, container, false);
        BindById(view);
        getData();
        readDataFromDataBase(date_str);
        return view;
    }

    public void BindById(View view){
        dateView = view.findViewById(R.id.dateString);
    }

    public void getData(){
        Bundle args = getArguments();
        if (args != null) {
            date_str = args.getString("date str");
        }
    }

    public void readDataFromDataBase(String dateString){
        docRef = db.collection("date").document(dateString);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setViewText("exist");
                    } else {
                        setViewText("does not exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setViewText(String flag){
        dateView.setText(date_str + " " + flag);
    }
}