package com.example.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.home;
import com.example.expensetracker.login;
import com.google.firebase.auth.FirebaseAuth;


public class SignOut extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseAuth.getInstance().signOut();
        return inflater.inflate(R.layout.fragment_sign_out, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        startActivity(new Intent(getActivity(), com.example.expensetracker.login.class));
        Toast.makeText(getActivity(), "Signing Out !!", Toast.LENGTH_LONG).show();
    }
}
