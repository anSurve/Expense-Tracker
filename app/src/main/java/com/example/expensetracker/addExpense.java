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

public class addExpense extends AppCompatActivity {

    private Button btn_submit;
    private EditText amount, given_to, description;
    private DatePicker time;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fDb;
    private ProgressBar mPBar;
    private Spinner Category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getSupportActionBar().setTitle("Add Expenditure");
        time = findViewById(R.id.timestamp);
        amount = findViewById(R.id.amount);
        Category = findViewById(R.id.new_category_name);
        given_to = findViewById(R.id.given_to);
        description = findViewById(R.id.description);
        btn_submit = findViewById(R.id.submit);
        mPBar = findViewById(R.id.loading);
        fAuth = FirebaseAuth.getInstance();
        fDb = FirebaseFirestore.getInstance();

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
                .whereEqualTo("category_for","Expense")
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

        Category.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selectedItem = parent.getSelectedItem().toString();
                if (selectedItem.equals("Given to Someone")){
                    given_to.setVisibility(View.VISIBLE);
                }else{
                    given_to.setText("");
                    given_to.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent){
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String amtString = amount.getText().toString();
                final String givenTo = given_to.getText().toString();
                final String descriptionText = description.getText().toString();
                final String category = Category.getSelectedItem().toString();

                if (amtString.isEmpty()) {
                    amount.setError("Amount is required");
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
                transaction.put("to",givenTo);
                transaction.put("description",descriptionText);
                transaction.put("category",category);
                transaction.put("time",timestamp.getTime());
                transaction.put("transaction_type","Expenditure");
                transaction.put("user_id",fAuth.getUid());

                mPBar.setVisibility(View.VISIBLE);

                fDb.collection("Transactions").add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(addExpense.this, "Record added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addExpense.this, "Could not add record to the db", Toast.LENGTH_LONG).show();
                        clearFields();
                    }
                });
            }
        });
    }

    private void clearFields(){
        Calendar c = Calendar.getInstance();
        time.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),null);
        amount.setText("");
        given_to.setText("");
        given_to.setVisibility(View.GONE);
        description.setText("");
        mPBar.setVisibility(View.INVISIBLE);
    }
}
