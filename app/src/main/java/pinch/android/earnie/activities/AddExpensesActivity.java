package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pinch.android.earnie.AdapterUtil.MonthlyExpenseAdapter;
import pinch.android.earnie.Expense;
import pinch.android.earnie.R;

public class AddExpensesActivity extends AppCompatActivity {

    TextView add_monthly_expense_tv, add_one_time_expense;
    ImageView back_press_expenses;

    FirebaseAuth auth;

    private List<Expense> monthlyExpense = new ArrayList<>();

    private RecyclerView expenseRecyclerView;
    private MonthlyExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        auth = FirebaseAuth.getInstance();

        add_one_time_expense = findViewById(R.id.add_one_time_expense);
        add_monthly_expense_tv=(TextView)findViewById(R.id.add_monthly_expense_tv);
        back_press_expenses=(ImageView)findViewById(R.id.back_press_expenses);

        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseRecyclerView.setHasFixedSize(true);

        back_press_expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==back_press_expenses)
                {
                    finish();
                }
            }
        });

        add_one_time_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddExpensesActivity.this,AddRecurringExpenseActivity.class));
            }
        });

        add_monthly_expense_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view==add_monthly_expense_tv)
                {
                    startActivity(new Intent(AddExpensesActivity.this,AddMonthlyExpenseActivity.class));
                }
            }
        });

        getMonthlySavingsId();

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

                        getMonthlyExpenses();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthlyExpenses() {
        monthlyExpense = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlyExpense");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(monthlyExpense.size()>0){
                    monthlyExpense.clear();
                }
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    monthlyExpense.add(new Expense(dataSnapshot.child("id").getValue().toString(),
                            dataSnapshot.child("amount").getValue().toString(),
                            dataSnapshot.child("purpose").getValue().toString(),
                            dataSnapshot.child("startDate").getValue().toString(),
                            dataSnapshot.child("endDate").getValue().toString(),
                            dataSnapshot.child("date").getValue().toString(),
                            dataSnapshot.child("month").getValue().toString(),
                            dataSnapshot.child("year").getValue().toString(),
                            Boolean.parseBoolean(dataSnapshot.child("isOneTimeExp").getValue().toString()),
                            Boolean.parseBoolean(dataSnapshot.child("deducted").getValue().toString())

                    ));
                }
                adapter = new MonthlyExpenseAdapter(AddExpensesActivity.this,monthlyExpense);
                expenseRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}