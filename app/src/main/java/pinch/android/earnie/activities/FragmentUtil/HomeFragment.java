package pinch.android.earnie.activities.FragmentUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import pinch.android.earnie.AdapterUtil.MonthlyExpenseAdapter;
import pinch.android.earnie.AdapterUtil.RecentExpenseAdapter;
import pinch.android.earnie.Expense;
import pinch.android.earnie.MonthlySavings;
import pinch.android.earnie.MyService;
import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;
import pinch.android.earnie.activities.AddExpensesActivity;


public class HomeFragment extends Fragment {

    TextView add_expense_tv;

    private TextView saved_amountTv;
    FirebaseAuth auth;

    private RecyclerView recyclerView;
    private RecentExpenseAdapter adapter;
    private List<Expense> monthlyExpense;


    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;

    private String savingId;
    private String savedAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();

        add_expense_tv=(TextView)view.findViewById(R.id.add_expense_tv);
        saved_amountTv = view.findViewById(R.id.saved_amount);

        barChart = view.findViewById(R.id.barchart);

        recyclerView = view.findViewById(R.id.expenseRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        getIncome();
        getMonthlySavings();


        add_expense_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==add_expense_tv)
                {
                    startActivity(new Intent(getActivity(), AddExpensesActivity.class));
                }
            }
        });

        return view;

    }

    double value = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateEverySecondDeduction(List<Expense> expenses) {
        value = 0;



        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        for(int i=0;i<expenses.size();i++){
            if(expenses.get(i).getMonth().equals(formattedDate.split("-")[1])
            && expenses.get(i).getYear().equals(formattedDate.split("-")[2])){
                // Get the number of days in that month
                LocalDate date = LocalDate.of(Integer.parseInt(formattedDate2.split("-")[2]),
                        Integer.parseInt(formattedDate2.split("-")[1]),
                        Integer.parseInt(formattedDate2.split("-")[0]));
                int days = date.lengthOfMonth();

                double cal = Double.parseDouble(expenses.get(i).getAmount())/(days*24*60*60);
                value = value + cal;
            }
        }

        Timer timer = new Timer();
        TimerTask timerTask;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                startService(String.valueOf(value));
                //refresh your textview
            }
        };
        timer.schedule(timerTask, 10000, 60000);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getMonthlyExpenses() {
        monthlyExpense = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlyExpense");

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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
                adapter = new RecentExpenseAdapter(getActivity(),monthlyExpense);
                recyclerView.setAdapter(adapter);

                if(monthlyExpense.size()>0) {
                    calculateEverySecondDeduction(monthlyExpense);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startService(String value) {
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        serviceIntent.putExtra("inputExtra", value);
        serviceIntent.putExtra("saved",savedAmount);
        serviceIntent.putExtra("savingId",savingId);
        ContextCompat.startForegroundService(getActivity(), serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        getActivity().stopService(serviceIntent);
    }

    private void getIncome() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Date c = Calendar.getInstance().getTime();

                    SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate2 = df2.format(c);

                    if(formattedDate2.split("-")[1].equals(dataSnapshot.child("month").getValue().toString())) {
                        saved_amountTv.setText(new DecimalFormat(".#").format(Double.
                                parseDouble(dataSnapshot.child("saved").getValue().toString())));
                        savedAmount = dataSnapshot.child("saved").getValue().toString();
                    }
                }

                getMonthlyExpenses();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private List<MonthlySavings> savings;

    private ArrayList<String> labelName;
    boolean isShown = false;

    public void getMonthlySavings(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barEntries = new ArrayList<>();
                savings = new ArrayList<>();
                labelName = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    savings.add(new MonthlySavings(dataSnapshot.child("saved").getValue().toString(),
                            dataSnapshot.child("month").getValue().toString()));
                    savingId = dataSnapshot.getKey().toString();
                }
                for (int i=0;i<savings.size();i++) {

                    barEntries.add(new BarEntry(i, Float.parseFloat(savings.get(i).getSaved())));
                    labelName.add(savings.get(i).getMonth());
                }

                if(isShown) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setGraphData();
                        }
                    }, 30000);
                }else{
                    setGraphData();
                    isShown = true;
                }


//                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                barDataSet.setValueTextColor(Color.BLACK);
//                barDataSet.setValueTextSize(16f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setGraphData(){
        barDataSet = new BarDataSet(barEntries,"Savings");

        Description descriptions = new Description();
        descriptions.setText("Months");
        barChart.setDescription(descriptions);

        barData = new BarData(barDataSet);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelName.size());
        xAxis.setLabelRotationAngle(90);
        barChart.animateY(2000);
        barChart.invalidate();
    }

}