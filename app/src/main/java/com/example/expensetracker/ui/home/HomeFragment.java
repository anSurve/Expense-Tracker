package com.example.expensetracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.expensetracker.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel;
        ImageButton btn_add_expense,btn_add_earned, btn_add_investment, btn_add_loan, btn_add_LM, btn_add_BM;

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        btn_add_expense = root.findViewById(R.id.btn_add_expense);
        btn_add_earned = root.findViewById(R.id.btn_add_earning);
        btn_add_investment = root.findViewById(R.id.btn_add_investment);
        btn_add_loan = root.findViewById(R.id.btn_add_loan);
        btn_add_BM = root.findViewById(R.id.btn_add_borrowed_money);
        btn_add_LM = root.findViewById(R.id.btn_add_lent_money);

        btn_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addExpense.class));
            }
        });

        btn_add_earned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addEarned.class));
            }
        });

        btn_add_investment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addInvestment.class));
            }
        });

        btn_add_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addLoan.class));
            }
        });

        btn_add_LM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addLentMoney.class));
            }
        });

        btn_add_BM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),com.example.expensetracker.addBorrowedMoney.class));
            }
        });
        return root;
    }
}