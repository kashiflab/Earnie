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

import java.util.ArrayList;
import java.util.List;

import pinch.android.earnie.AdapterUtil.MonthlyExpenseAdapter;
import pinch.android.earnie.Expense;
import pinch.android.earnie.R;

public class AddExpensesActivity extends AppCompatActivity {

    TextView add_monthly_expense_tv;
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

        add_monthly_expense_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view==add_monthly_expense_tv)
                {
                    startActivity(new Intent(AddExpensesActivity.this,AddMonthlyExpenseActivity.class));
                }
            }
        });

        getMonthlyExpenses();

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
                            Boolean.parseBoolean(dataSnapshot.child("isOneTimeExp").getValue().toString())
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