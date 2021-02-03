package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import io.grpc.okhttp.internal.Util;
import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;

public class AddMonthlyExpenseActivity extends AppCompatActivity {

    ImageView back_press_expenses;
    private EditText amount, purpose, startDate, endDate;
    private ImageView oneTimeExpense;
    private TextView addExpense;
    private boolean OTE = false;

    private Calendar myCalendar;
    private Calendar myCalendar2;

    private boolean ed = false, sd = false;
    private FirebaseAuth auth;

    private boolean isEdit, deducted;
    private String eAmount, ePurpose, eStartDate, eEndDate, id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_monthly_expense);

        auth = FirebaseAuth.getInstance();

        getMonthlySavingsId();

        amount = findViewById(R.id.amount);
        purpose = findViewById(R.id.purpose);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        oneTimeExpense = findViewById(R.id.add_one_time_expense);
        addExpense = findViewById(R.id.addExpense);
        back_press_expenses=(ImageView)findViewById(R.id.back_press_expenses);

        if(getIntent().hasExtra("isEdit")){
            isEdit = getIntent().getBooleanExtra("isEdit",false);
            eAmount = getIntent().getStringExtra("amount");
            ePurpose = getIntent().getStringExtra("purpose");
            eEndDate = getIntent().getStringExtra("endDate");
            eStartDate = getIntent().getStringExtra("startDate");
            id = getIntent().getStringExtra("id");

//            deducted = getIntent().getBooleanExtra("deducted",false);

            amount.setText(eAmount);
            purpose.setText(ePurpose);
            startDate.setText(eStartDate);
            endDate.setText(eEndDate);
        }else{
            isEdit = false;
        }


        back_press_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==back_press_expenses)
                {
                    finish();
                }
            }
        });

        oneTimeExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oneTimeExpense.getAlpha()==0.4f){
                    startDate.setVisibility(View.VISIBLE);
                    endDate.setVisibility(View.VISIBLE);
                    oneTimeExpense.setAlpha(1.0f);
                    OTE = true;
                }else {
                    startDate.setVisibility(View.GONE);
                    endDate.setVisibility(View.GONE);
                    oneTimeExpense.setAlpha(0.4f);
                    OTE = false;
                }
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());
                String formattedDate = df.format(c);

                startDate.setText(formattedDate);

                if (TextUtils.isEmpty(amount.getText().toString()) || TextUtils.isEmpty(purpose.getText().toString())) {
                    Toast.makeText(AddMonthlyExpenseActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    if(!isEdit) {
                        addMonthlyExpense(amount.getText().toString(), purpose.getText().toString(),
                                startDate.getText().toString(), endDate.getText().toString(),false);
                    }else{
                        updateMonthlyExpense(amount.getText().toString(), purpose.getText().toString(),
                                startDate.getText().toString(), endDate.getText().toString());
                    }
                }
            }
        });

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        myCalendar2 = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sd = true;
                ed = false;
                // TODO Auto-generated method stub
                new DatePickerDialog(AddMonthlyExpenseActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ed = true;
                sd = false;
                new DatePickerDialog(AddMonthlyExpenseActivity.this, date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateMonthlyExpense(String amount2, String purpose2, String startDate2, String endDate2) {
        Utils.initpDialog(AddMonthlyExpenseActivity.this,"Adding Expense...");
        Utils.showpDialog();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String date = formattedDate.split("-")[0];
        String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MothlySavings").child(monthlySavingsId).child("MonthlyExpense")
                .child(id);

        HashMap<String,Object> map = new HashMap<>();
        map.put("amount",amount2);
        map.put("purpose",purpose2);
        map.put("startDate",startDate2);
        map.put("endDate",endDate2);
        map.put("date",date);
        map.put("month",month);
        map.put("year",year);
        map.put("isOneTimeExp",OTE);

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utils.hidepDialog();
                startDate.setText("");
                endDate.setText("");
                amount.setText("");
                purpose.setText("");
                if(task.isSuccessful()){
                    AddMonthlyExpenseActivity.super.onBackPressed();
                    Toast.makeText(AddMonthlyExpenseActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    Log.i("SUCCESS","SUCCESS");
                }else {
                    Log.e("ERROR","ERROR");
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        startDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabel2() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        endDate.setText(sdf.format(myCalendar2.getTime()));
    }

    private void addMonthlyExpense(String amount2, String purpose2, String startDate2, String endDate2, boolean deducted) {


        Utils.initpDialog(AddMonthlyExpenseActivity.this,"Adding Expense...");
        Utils.showpDialog();
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String date = formattedDate.split("-")[0];
        String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings").child(monthlySavingsId).child("MonthlyExpense");

        String id = UUID.randomUUID().toString();

        HashMap<String,Object> map = new HashMap<>();
        map.put("id",id);
        map.put("amount",amount2);
        map.put("purpose",purpose2);
        map.put("startDate",startDate2);
        map.put("endDate",endDate2);
        map.put("date",date);
        map.put("month",month);
        map.put("year",year);
        map.put("isOneTimeExp",OTE);
        map.put("deducted",deducted);

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utils.hidepDialog();
                startDate.setText("");
                endDate.setText("");
                amount.setText("");
                purpose.setText("");
                if(task.isSuccessful()){
                    AddMonthlyExpenseActivity.super.onBackPressed();
                    Toast.makeText(AddMonthlyExpenseActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    Log.i("SUCCESS","SUCCESS");
                }else {
                    Log.e("ERROR","ERROR");
                }
            }
        });
    }

    String monthlySavingsId = "";

    private void getMonthlySavingsId() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        String month = formattedDate.split("-")[1];
        String year = formattedDate.split("-")[2];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid())
                .child("MonthlySavings");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(dataSnapshot.child("month").getValue().toString().equals(month) &&
                            dataSnapshot.child("year").getValue().toString().equals(year)){
                        monthlySavingsId = dataSnapshot.getKey().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}