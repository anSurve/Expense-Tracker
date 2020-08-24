package com.example.expensetracker.ui.Categories;

import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriesViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public CategoriesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Select a section :");
    }
    public LiveData<String> getText() {
        return mText;
    }
}
