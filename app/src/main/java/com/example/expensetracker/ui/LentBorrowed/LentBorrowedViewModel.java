package com.example.expensetracker.ui.LentBorrowed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LentBorrowedViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public LentBorrowedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is lent-borrowed fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
