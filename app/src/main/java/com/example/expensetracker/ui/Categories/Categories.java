package com.example.expensetracker.ui.Categories;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.home;
import com.example.expensetracker.ui.LentBorrowed.LentBorrowedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class Categories extends Fragment {

    private CategoriesViewModel mViewModel;
    private FirebaseFirestore fDb;
    private Spinner Categories;
    private Button btn_show_category, btn_add_Category;
    private TextView CategoriesHeader;
    private LinearLayout categoriesView;

    private Dialog myDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(CategoriesViewModel.class);
        View root =  inflater.inflate(R.layout.categories_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_categories);
        Categories = root.findViewById(R.id.spinner);
        btn_show_category = root.findViewById(R.id.show_category);
        CategoriesHeader = root.findViewById(R.id.Category_list);
        categoriesView = root.findViewById(R.id.CategoriesView);
        btn_add_Category = root.findViewById(R.id.addCategory);
        myDialog = new Dialog(getActivity());
        mViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final List<String> ls_categories = new ArrayList<>();
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ls_categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Categories.setAdapter(dataAdapter);
        fDb = FirebaseFirestore.getInstance();
        fDb.collection("Categories")
                .whereEqualTo("user_id", "Any")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(! ls_categories.contains(document.get("category_for").toString())){
                                    ls_categories.add(document.get("category_for").toString());
                                }
                            }
                            dataAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        btn_show_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoriesHeader.setVisibility(View.VISIBLE);
                btn_add_Category.setVisibility(View.VISIBLE);
                String Section = Categories.getSelectedItem().toString();
                fetchCategory(Section);
            }
        });

        btn_add_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        AdapterView.OnItemSelectedListener categorySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                CategoriesHeader.setVisibility(View.INVISIBLE);
                btn_add_Category.setVisibility(View.INVISIBLE);
                if(categoriesView.getChildCount() > 0)
                    categoriesView.removeAllViews();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        };
        Categories.setOnItemSelectedListener(categorySelectedListener);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);// Spinner Drop down elements

    }

    public void createTextView(String text) {
        final TextView textView_item_name = new TextView(getActivity());
        final LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView_item_name.setLayoutParams(_params);
        textView_item_name.setTextSize(20);
        textView_item_name.setGravity(Gravity.CENTER);
        textView_item_name.setPadding(10, 10, 10, 10);
        textView_item_name.setText(text);

        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.addView(textView_item_name);
        categoriesView.addView(ll);
    }

    private void fetchCategory(String Section){
        fDb.collection("Categories")
                .whereEqualTo("user_id", "Any")
                .whereEqualTo("category_for", Section)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                    createTextView(document.get("category_name").toString());
                                }
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addCategory(){
        TextView txtClose, txtSection;
        myDialog.setContentView(R.layout.category_popup);
        txtClose = myDialog.findViewById(R.id.close_txt);
        txtSection = myDialog.findViewById(R.id.section_label);
        txtSection.setText("Add Category under section "+Categories.getSelectedItem().toString());
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

}
