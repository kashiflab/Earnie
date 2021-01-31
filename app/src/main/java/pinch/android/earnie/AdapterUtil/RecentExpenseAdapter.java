package pinch.android.earnie.AdapterUtil;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pinch.android.earnie.Expense;
import pinch.android.earnie.R;

public class RecentExpenseAdapter extends RecyclerView.Adapter<RecentExpenseAdapter.ViewHolder> {

    private Context context;
    private List<Expense> expenses;

    public RecentExpenseAdapter(Context context, List<Expense> expenses){
        this.context = context;
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_expense_item,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense expense = expenses.get(position);

        String date = expense.getDate();

        holder.title.setText(expense.getPurpose());
        holder.price.setText(expense.getAmount());
        holder.timeAgo.setText(expense.getStartDate());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, price, timeAgo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeAgo = itemView.findViewById(R.id.timeAgo);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);

        }
    }



}