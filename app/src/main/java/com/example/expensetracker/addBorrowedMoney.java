package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class addBorrowedMoney extends AppCompatActivity {
    private Button btn_submit;
    private DatePicker time;
    private EditText amount, borrorwedFrom, description;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDb;
    private ProgressBar mPBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_borrowed_money);
        getSupportActionBar().setTitle("Add Borrowed Money");
        time = findViewById(R.id.timestamp);
        amount = findViewById(R.id.amount);
        borrorwedFrom = findViewById(R.id.borrowedFrom);
        description = findViewById(R.id.description);
        btn_submit = findViewById(R.id.submit);
        mPBar = findViewById(R.id.loading);
        fAuth = FirebaseAuth.getInstance();
        fDb = FirebaseFirestore.getInstance();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBorrowedMoneyToDB();
            }
        });
    }
    private void clearFields(){
        Calendar c = Calendar.getInstance();
        time.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),null);
        amount.setText("");
        borrorwedFrom.setText("");
        description.setText("");
        mPBar.setVisibility(View.INVISIBLE);
    }

    private void addBorrowedMoneyToDB(){
        final String amtString = amount.getText().toString();
        final String descriptionText = description.getText().toString();
        final String borrowed = borrorwedFrom.getText().toString();

        if (amtString.isEmpty()) {
            amount.setError("Amount is required");
            return;
        }
        if(borrowed.isEmpty()){
            borrorwedFrom.setError("Whom did you borrowed money from ?");
            return;
        }
        final Double amt = Double.parseDouble(amount.getText().toString());
        //Get Time
        int day = time.getDayOfMonth();
        int month = time.getMonth();
        int year =  time.getYear();

        Calendar timestamp = Calendar.getInstance();
        timestamp.set(year, month, day, 0, 0 ,0);

        final Map<String, Object> transaction = new HashMap<>();
        transaction.put("amount", amt);
        transaction.put("description",descriptionText);
        transaction.put("borrowed_from",borrowed);
        transaction.put("time",timestamp.getTime());
        transaction.put("transaction_type","Borrow");
        transaction.put("user_id",fAuth.getUid());

        mPBar.setVisibility(View.VISIBLE);

        fDb.collection("Transactions").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(addBorrowedMoney.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addBorrowedMoney.this, "Could not add record to the db", Toast.LENGTH_LONG).show();
                clearFields();
            }
        });
    }
}
