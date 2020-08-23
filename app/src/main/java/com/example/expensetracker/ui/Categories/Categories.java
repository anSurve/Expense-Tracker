package com.example.expensetracker.ui.Categories;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.expensetracker.R;
import com.example.expensetracker.ui.LentBorrowed.LentBorrowedViewModel;

public class Categories extends Fragment {

    private CategoriesViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(CategoriesViewModel.class);
        View root =  inflater.inflate(R.layout.categories_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_categories);
        mViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

}
