package com.example.expensetracker.ui.Categories;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.home;
import com.example.expensetracker.signup;
import com.example.expensetracker.ui.LentBorrowed.LentBorrowedViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories extends Fragment {

    private CategoriesViewModel mViewModel;
    private FirebaseFirestore fDb;
    private Spinner Categories;
    private Button btn_show_category, btn_add_Category;
    private TextView CategoriesHeader;
    private LinearLayout categoriesView;
   // private ListView CategoriesList;

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
       // CategoriesList = root.findViewById(R.id.ViewCategory);
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
                clearAllData();
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
                clearAllData();
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
                                    createTextView(document.get("category_name").toString(), document.getId());
                                }
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        fDb.collection("Categories")
                .whereEqualTo("user_id", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("category_for", Section)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                createTextView(document.get("category_name").toString(), document.getId());
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void createTextView(String text, String docpath) {
        //final TableRow categoryRow = new TableRow(getActivity());
        final TextView textView_item_name = new TextView(getActivity());
        final View nView = new View(getActivity());
        final Button btnDel = new Button(getActivity());
        final LinearLayout.LayoutParams _viewparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final LinearLayout.LayoutParams _paramsline = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2);
        final LinearLayout.LayoutParams _btnparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int[] attrs = {android.R.attr.background};
        TypedArray ta = getActivity().obtainStyledAttributes(R.style.Divider, attrs);
        // Fetch the text from your style like this.
        //String Bgvalue = ta.getString(0);
        nView.setLayoutParams(_paramsline);
        nView.setBackground(ta.getDrawable(0));


        //categoryRow.setLayoutParams(_viewparams);
        //_btnparams.weight = 1.0f;
        _btnparams.gravity=Gravity.END;
        btnDel.setLayoutParams(_btnparams);
       // btnDel.setL
        btnDel.setGravity(Gravity.CENTER);
        btnDel.setBackgroundColor(Color.RED);
        btnDel.setText("Del");
        btnDel.setTextColor(Color.WHITE);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDel.setBackgroundColor(Color.rgb(255,150,150));
                DeleteCategory(docpath);
                clearAllData();
                CategoriesHeader.setVisibility(View.VISIBLE);
                btn_add_Category.setVisibility(View.VISIBLE);
                String Section = Categories.getSelectedItem().toString();
                fetchCategory(Section);
            }
        });

        _params.weight = 1.0f;
        _params.gravity=Gravity.START;
        textView_item_name.setLayoutParams(_params);
        textView_item_name.setTextSize(20);
        textView_item_name.setGravity(Gravity.CENTER);
        textView_item_name.setPadding(10, 10, 10, 10);
        textView_item_name.setText(text);

        //categoryRow.addView(textView_item_name);
        //categoryRow.addView(btnDel);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setLayoutParams(_viewparams);
        LinearLayout ll1 = new LinearLayout(getActivity());
        ll1.setLayoutParams(_viewparams);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll1.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(30, 30, 30, 30);
        ll.addView(textView_item_name);
        ll.addView(btnDel);
        //ll.setOnLongClickListener(speakHoldListener);
        ll1.addView(ll);
        ll1.addView(nView);
        categoriesView.addView(ll1);
    }

    private void addCategory(){
        TextView txtClose, txtSection;
        EditText categoryName;
        Button addCategory;
        myDialog.setContentView(R.layout.category_popup);
        txtClose = myDialog.findViewById(R.id.close_txt);
        txtSection = myDialog.findViewById(R.id.section_label);
        addCategory = myDialog.findViewById(R.id.btn_add_category);
        categoryName = myDialog.findViewById(R.id.new_category_name);

        String categorySectionString = Categories.getSelectedItem().toString();
        txtSection.setText("Add Category under section "+categorySectionString);
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categorNameString = categoryName.getText().toString();
                if(categorNameString.isEmpty()){
                    categoryName.setError("Please enter category name");
                    return;
                }
                addCategoryToDB(categorNameString,categorySectionString);
                clearAllData();
                CategoriesHeader.setVisibility(View.VISIBLE);
                btn_add_Category.setVisibility(View.VISIBLE);
                String Section = Categories.getSelectedItem().toString();
                fetchCategory(Section);
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    private void addCategoryToDB(String catName, String CatSection){
        final Map<String, Object> Category = new HashMap<>();
        Category.put("category_name", catName);
        Category.put("category_for", CatSection);
        Category.put("user_id", FirebaseAuth.getInstance().getUid());
        fDb.collection("Categories").add(Category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(), "Category Added successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DeleteCategory(String docpath){
        DocumentReference doc = fDb.collection("Categories").document(docpath);
        doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Category Deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearAllData(){
        CategoriesHeader.setVisibility(View.INVISIBLE);
        btn_add_Category.setVisibility(View.INVISIBLE);
        if(categoriesView.getChildCount() > 0)
            categoriesView.removeAllViews();
    }

    private View.OnLongClickListener speakHoldListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View pView) {
            // Do something when your hold starts here.
            addCategory();
            return true;
        }
    };
}
