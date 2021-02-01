package pinch.android.earnie.AdapterUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import pinch.android.earnie.Expense;
import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;
import pinch.android.earnie.activities.AddMonthlyExpenseActivity;

public class MonthlyExpenseAdapter extends RecyclerView.Adapter<MonthlyExpenseAdapter.ViewHolder> {

    private Context context;
    private List<Expense> expenses;
    private FirebaseAuth auth;

    public MonthlyExpenseAdapter(Context context, List<Expense> expenses){
        this.context = context;
        this.expenses = expenses;

        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_item,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.title.setText(expense.getPurpose());
        holder.amount.setText(expense.getAmount());
        if(expense.isOneTimeExp()) {
            holder.renewDate.setText(expense.getDate() + " of Each Month");
        }else{
            holder.renewDate.setText("Non-renewable");
            holder.edit.setVisibility(View.GONE);
        }
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeMonthlyExpense(expense.getId());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, AddMonthlyExpenseActivity.class)
                .putExtra("isEdit",true).putExtra("amount",expense.getAmount())
                .putExtra("purpose",expense.getPurpose()).putExtra("startDate",expense.getStartDate())
                .putExtra("endDate",expense.getEndDate()).putExtra("id",expense.getId()));
            }
        });
    }

    private void removeMonthlyExpense(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlyExpense").child(id);

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, amount, renewDate, edit, remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            amount = itemView.findViewById(R.id.amount);
            renewDate = itemView.findViewById(R.id.renewDate);
            edit = itemView.findViewById(R.id.edit);
            remove = itemView.findViewById(R.id.remove);

        }
    }
}
