package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addInvestment extends AppCompatActivity {

    private Button btn_submit;
    private EditText amount, description,newTerm;
    private DatePicker time;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDb;
    private ProgressBar mPBar;
    private Spinner Category, Maturity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_investment);
        getSupportActionBar().setTitle("Add Investment");
        time = findViewById(R.id.timestamp);
        amount = findViewById(R.id.amount);
        Category = findViewById(R.id.category_name);
        Maturity = findViewById(R.id.maturityafter);
        newTerm = findViewById(R.id.newTerm);
        description = findViewById(R.id.description);
        btn_submit = findViewById(R.id.submit);
        mPBar = findViewById(R.id.loading);
        fAuth = FirebaseAuth.getInstance();
        fDb = FirebaseFirestore.getInstance();
        loadMaturityTerm();
        loadCategories();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInvestmentToDB();
            }
        });

        Maturity.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selectedItem = parent.getSelectedItem().toString();
                if (selectedItem.equals("Other")){
                    newTerm.setVisibility(View.VISIBLE);
                }else{
                    newTerm.setText("");
                    newTerm.setVisibility(View.GONE);
                }
            }
            public void onNothingSelected(AdapterView<?> parent){
            }
        });
    }

    private void loadMaturityTerm() {
        final List<String> ls_terms = new ArrayList<>();
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ls_terms);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Maturity.setAdapter(dataAdapter);
        ls_terms.add("1 Month");
        ls_terms.add("6 Months");
        ls_terms.add("1 Year");
        ls_terms.add("5 Years");
        ls_terms.add("10 Years");
        ls_terms.add("Other");
        dataAdapter.notifyDataSetChanged();
    }

    private void loadCategories(){
        final List<String> ls_categories = new ArrayList<>();
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ls_categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        Category.setAdapter(dataAdapter);
        fDb = FirebaseFirestore.getInstance();
        fDb.collection("Categories")
                .whereEqualTo("user_id", "Any")
                .whereEqualTo("category_for","Investment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(! ls_categories.contains(document.get("category_name").toString())){
                                    ls_categories.add(document.get("category_name").toString());
                                }
                            }
                            dataAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        fDb.collection("Categories")
                .whereEqualTo("user_id", fAuth.getUid())
                .whereEqualTo("category_for","Investment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if(! ls_categories.contains(document.get("category_name").toString())){
                                    ls_categories.add(document.get("category_name").toString());
                                }
                            }
                            dataAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void clearFields(){
        Calendar c = Calendar.getInstance();
        time.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),null);
        amount.setText("");
        newTerm.setText("");
        newTerm.setVisibility(View.GONE);
        description.setText("");
        mPBar.setVisibility(View.INVISIBLE);
    }

    private void addInvestmentToDB(){
        final String amtString = amount.getText().toString();
        final String descriptionText = description.getText().toString();
        final String category = Category.getSelectedItem().toString();
        String maturity = Maturity.getSelectedItem().toString();

        if (amtString.isEmpty()) {
            amount.setError("Amount is required");
            return;
        }
        if (maturity == "Other") {
            maturity = newTerm.getText().toString();
            if(maturity.isEmpty()){
                newTerm.setError("Maturity term is required");
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
        transaction.put("description",descriptionText);
        transaction.put("category",category);
        transaction.put("maturity",maturity);
        transaction.put("time",timestamp.getTime());
        transaction.put("user_id",fAuth.getUid());

        mPBar.setVisibility(View.VISIBLE);

        fDb.collection("Investments").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(addInvestment.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addInvestment.this, "Could not add record to the db", Toast.LENGTH_LONG).show();
                clearFields();
            }
        });
    }
}
