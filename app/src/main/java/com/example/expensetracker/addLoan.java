package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addLoan extends AppCompatActivity {

    private Button btn_submit;
    private EditText amount, description, newTenure, from;
    private DatePicker time;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDb;
    private ProgressBar mPBar;
    private Spinner Tenure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loan);
        getSupportActionBar().setTitle("Add Loan");
        time = findViewById(R.id.timestamp);
        amount = findViewById(R.id.amount);
        from = findViewById(R.id.from);
        Tenure = findViewById(R.id.tenure);
        newTenure = findViewById(R.id.newTenure);
        description = findViewById(R.id.description);
        btn_submit = findViewById(R.id.submit);
        mPBar = findViewById(R.id.loading);
        fAuth = FirebaseAuth.getInstance();
        fDb = FirebaseFirestore.getInstance();
        loadTenure();
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLoanToDB();
            }
        });
    }

    private void loadTenure() {
        final List<String> ls_terms = new ArrayList<>();
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ls_terms);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Tenure.setAdapter(dataAdapter);
        ls_terms.add("6 Months");
        ls_terms.add("1 Year");
        ls_terms.add("5 Years");
        ls_terms.add("10 Years");
        ls_terms.add("Other");
        dataAdapter.notifyDataSetChanged();
    }
    private void clearFields(){
        Calendar c = Calendar.getInstance();
        time.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),null);
        amount.setText("");
        newTenure.setText("");
        newTenure.setVisibility(View.GONE);
        description.setText("");
        mPBar.setVisibility(View.INVISIBLE);
    }

    private void addLoanToDB(){
        final String amtString = amount.getText().toString();
        final String fromString = from.getText().toString();
        final String descriptionText = description.getText().toString();
        String tenure = Tenure.getSelectedItem().toString();

        if (amtString.isEmpty()) {
            amount.setError("Amount is required");
            return;
        }
        if (fromString.isEmpty()){
            from.setError("Where did you take loan from ?");
            return;
        }
        if (tenure == "Other") {
            tenure = newTenure.getText().toString() + " Months";
            if(tenure.isEmpty()){
                newTenure.setError("Maturity term is required");
                return;
            }
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
        transaction.put("from",fromString);
        transaction.put("description",descriptionText);
        transaction.put("tenure",tenure);
        transaction.put("time",timestamp.getTime());
        transaction.put("user_id",fAuth.getUid());

        mPBar.setVisibility(View.VISIBLE);

        fDb.collection("Loans").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(addLoan.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addLoan.this, "Could not add record to the db", Toast.LENGTH_LONG).show();
                clearFields();
            }
        });
    }
}
