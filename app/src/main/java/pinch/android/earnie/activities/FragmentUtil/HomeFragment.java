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
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

//import java.security.Timestamp;
import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.time.
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import pinch.android.earnie.AdapterUtil.MonthlyExpenseAdapter;
import pinch.android.earnie.AdapterUtil.RecentExpenseAdapter;
import pinch.android.earnie.Expense;
import pinch.android.earnie.MonthlySavings;
import pinch.android.earnie.MyService;
import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;
import pinch.android.earnie.activities.AddExpensesActivity;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    TextView add_expense_tv;

    private TextView saved_amountTv;
    FirebaseAuth auth;

    private RecyclerView recyclerView;
    private RecentExpenseAdapter adapter;
    private List<Expense> monthlyExpense;


    Timer timer = new Timer();
    TimerTask timerTask;

    boolean isSet;


    String input;

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList<BarEntry> barEntries;
    ArrayList<BarEntry> lastDataSet;

    private String savingId;
    private String savedAmount;
    private  String incomeAmount = "0";

    @Override
    public void onPause() {
        super.onPause();

        timer.cancel();
        if(timerTask!=null) {
            try {
                timerTask.cancel();
            }catch (Exception e)
            {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getIncome();
            }
        },2000);
        getMonthlySavings();
    }

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

        getMonthlySavings();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getIncome();
            }
        },2000);


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


    //    @RequiresApi(api = Build.VERSION_CODES.O)
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @RequiresApi(api = Build.VERSION_CODES.O)
    private void calculateEverySecondDeduction(List<Expense> expenses) {
        value = 0;
        Float fineNewValue = 0F;

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        LocalDate todaydate = LocalDate.now();
        LocalDate thisMonthStart = todaydate.withDayOfMonth(1);
        LocalDate thisMonthEnd = todaydate.withDayOfMonth(todaydate.getMonth().length(todaydate.isLeapYear()));

        Long currentMonthStartStamp = getStamp(Integer.parseInt(thisMonthStart.toString().split("-")[2]),Integer.parseInt(thisMonthStart.toString().split("-")[1])-1,Integer.parseInt(thisMonthStart.toString().split("-")[0]));
        Long currentMonthEndStamp = getStamp(Integer.parseInt(thisMonthEnd.toString().split("-")[2]),Integer.parseInt(thisMonthEnd.toString().split("-")[1])-1,Integer.parseInt(thisMonthEnd.toString().split("-")[0]));

        Long currentStamp = Calendar.getInstance().getTimeInMillis();

        for(int i=0;i<expenses.size();i++){

            if(expenses.get(i).isDeducted())
            {
                // aur kya len gy sir
            }
            else {

                Log.e("Hitting", "Hitting" + i);

                String startDate = expenses.get(i).getStartDate();
                Log.e("startDate", startDate.toString());
                int expenseStartDay = Integer.parseInt(startDate.split("/")[1]);
                int expenseStartMonth = Integer.parseInt(startDate.split("/")[0]) - 1;
                int expenseStartYear = Integer.parseInt(startDate.split("/")[2]) + 2000;
                Long startStamp = getStamp(expenseStartDay, expenseStartMonth, expenseStartYear); // start time of expense

                Long minusSeconds = 0L;
                Float spentExpense = 0F;


                Float expenseAmount = Float.parseFloat(String.valueOf(Long.parseLong(expenses.get(i).getAmount())));


                // if monthly
                if (expenses.get(i).isOneTimeExp()) {
                    String endDate = expenses.get(i).getEndDate();
                    int expenseEndDay = Integer.parseInt(endDate.split("/")[1]);
                    int expenseEndMonth = Integer.parseInt(endDate.split("/")[0]) - 1;
                    int expenseEndYear = Integer.parseInt(endDate.split("/")[2]) + 2000;
                    Long endStamp = getStamp(expenseEndDay, expenseEndMonth, expenseEndYear); // endstamp of expense

                    Log.e("thisis", "Monthly");
                    // monthly, we don't need any time condition here except that it started before this time
                    if (startStamp >= currentMonthStartStamp && endStamp < currentStamp) {
                        spentExpense = expenseAmount;
                    }
                } else {
                    Log.e("thisis", "One Time");
                    // one time
                    if (startStamp < currentStamp && startStamp >= currentMonthStartStamp) {

                        spentExpense = expenseAmount;
                        Log.e("expenseAmount", (expenses.get(i).getAmount()));


                        Log.e("minusSeconds", minusSeconds.toString());
                        Log.e("spentExpense", spentExpense.toString());
                    } else {
                        Log.e("failed", startStamp.toString());
                        Log.e("failedMonth", currentMonthStartStamp.toString());
                        Log.e("failedCurrent", currentStamp.toString());
                    }
                }

                fineNewValue += spentExpense;
//                value = value + cal;
            }
        }
        startShowingSalary(fineNewValue);
//        Float Salary = 0F;
//
//        Long allSeconds = currentMonthEndStamp - currentMonthStartStamp;
//
//
//        // millis to seconds
//        allSeconds = allSeconds / 1000;
//
//
//        // per second spent
//        Float spentExpensePerSecond = Float.parseFloat(incomeAmount) / Float.parseFloat(String.valueOf(allSeconds));
//        // TODO: implement next month logic-- DONE, YAY!!
//
//        // 1. seconds that are in past but are of this month
//        Long minusSeconds = (currentStamp / 1000) - (currentMonthStartStamp / 1000);
//
//
//        // multiply above seconds with per second spent value, we got our guy
//        Salary = Float.parseFloat(String.valueOf(minusSeconds)) * spentExpensePerSecond;
//
//        fineNewValue = Salary - fineNewValue;
//
//        Log.e("sendingReqToDeduct",fineNewValue.toString());
//        startRunningMyChild(String.valueOf(fineNewValue),incomeAmount,savingId);
    }

    private void startShowingSalary(Float fineNewValue) {

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        LocalDate todaydate = LocalDate.now();
        LocalDate thisMonthStart = todaydate.withDayOfMonth(1);
        LocalDate thisMonthEnd = todaydate.withDayOfMonth(todaydate.getMonth().length(todaydate.isLeapYear()));

        Long currentMonthStartStamp = getStamp(Integer.parseInt(thisMonthStart.toString().split("-")[2]),Integer.parseInt(thisMonthStart.toString().split("-")[1])-1,Integer.parseInt(thisMonthStart.toString().split("-")[0]));
        Long currentMonthEndStamp = getStamp(Integer.parseInt(thisMonthEnd.toString().split("-")[2]),Integer.parseInt(thisMonthEnd.toString().split("-")[1])-1,Integer.parseInt(thisMonthEnd.toString().split("-")[0]));

        Long currentStamp = Calendar.getInstance().getTimeInMillis();


        Float Salary = 0F;

        Long allSeconds = currentMonthEndStamp - currentMonthStartStamp;


        // millis to seconds
        allSeconds = allSeconds / 1000;


        // per second spent
        Float spentExpensePerSecond = Float.parseFloat(incomeAmount) / Float.parseFloat(String.valueOf(allSeconds));
        // TODO: implement next month logic-- DONE, YAY!!

        // 1. seconds that are in past but are of this month
        Long minusSeconds = (currentStamp / 1000) - (currentMonthStartStamp / 1000);


        // multiply above seconds with per second spent value, we got our guy
        Salary = Float.parseFloat(String.valueOf(minusSeconds)) * spentExpensePerSecond;

        fineNewValue = Salary - fineNewValue;

        Log.e("sendingReqToDeduct",fineNewValue.toString());
        startRunningMyChild(String.valueOf(fineNewValue),incomeAmount,savingId);
    }
    public void startPhuckingSeconds(List<Expense> expenses)
    {

        timerTask = new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                calculateEverySecondDeduction(expenses);
            }
        };
        try {
            timer.schedule(timerTask, 10000, 10000);
        }
        catch (Exception e)
        {
            timer = new Timer();

            try {
                timer.schedule(timerTask, 10000, 10000);
            }
            catch (Exception e4)
            {
                e4.printStackTrace();
            }
        }

    }
    public Long getStamp(int day, int month, int year)
    {
        Calendar myCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        myCal.set(Calendar.DAY_OF_MONTH, day);
        myCal.set(Calendar.MONTH, month);
        myCal.set(Calendar.YEAR, year);
        myCal.set(Calendar.HOUR_OF_DAY,1);
        myCal.set(Calendar.MINUTE,0);
        myCal.set(Calendar.SECOND,0);
        Long currentMonthStartStamp = myCal.getTimeInMillis();

        return currentMonthStartStamp;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    String monthlySavingsId = "";

    private void getMonthlySavingsId(String id) {
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
                            Boolean.parseBoolean(dataSnapshot.child("isOneTimeExp").getValue().toString()),
                            Boolean.parseBoolean(dataSnapshot.child("deducted").getValue().toString())
                    ));
                }
                adapter = new RecentExpenseAdapter(getActivity(),monthlyExpense);
                recyclerView.setAdapter(adapter);

                if(monthlyExpense.size()>0) {
//                    calculateEverySecondDeduction(monthlyExpense);
                    startPhuckingSeconds(monthlyExpense);
                }
                else{
                    startShowingSalary(0F);
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

    public void startLocal(String value) {
//        Intent serviceIntent = new Intent(getActivity(), MyService.class);
//        serviceIntent.putExtra("inputExtra", value);
//        serviceIntent.putExtra("saved",savedAmount);
//        serviceIntent.putExtra("savingId",savingId);
//        ContextCompat.startForegroundService(getActivity(), serviceIntent);
        startRunningMyChild(value,savedAmount,savingId);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        getActivity().stopService(serviceIntent);
    }

    private void getIncome() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        DatabaseReference referenceIncome = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("income");

        referenceIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("snapShot",snapshot.toString());
                incomeAmount = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Date c = Calendar.getInstance().getTime();

                    SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate2 = df2.format(c);

                    if(formattedDate2.split("-")[1].equals(dataSnapshot.child("month").getValue().toString())) {
                        saved_amountTv.setText(new DecimalFormat(".####").format(Double.
                                parseDouble(dataSnapshot.child("saved").getValue().toString())));
                        savedAmount = dataSnapshot.child("saved").getValue().toString();
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

    private List<MonthlySavings> savings;

    private ArrayList<String> labelName;
    boolean isShown = false;


    private ArrayList<String> isMonthly;
    public void isMonthExist(){

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isMonthly = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    isMonthly.add(dataSnapshot.child("month").getValue().toString());
                }
                for(int i=0;i<savings.size();i++) {
                    if (isMonthly.contains(formattedDate2.split("-")[1])) {
                        savingId = savings.get(i).getId();
                        break;
                    } else {
                        startNewMonth();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getMonthlySavings(){
        getOneTimeExpense();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean render = false;
                barEntries = new ArrayList<>();
                savings = new ArrayList<>();
                labelName = new ArrayList<>();
                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate2 = df2.format(c);
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    savings.add(new MonthlySavings(dataSnapshot.getKey(),dataSnapshot.child("saved").getValue().toString(),
                            dataSnapshot.child("month").getValue().toString(),dataSnapshot.child("year").getValue().toString(),
                            Boolean.parseBoolean(dataSnapshot.child("isSalarySet").getValue().toString())));


                }

                isMonthExist();

                for (int i=0;i<savings.size();i++) {

                    if(lastDataSet!=null)
                    {
                        try{
                            BarEntry barEntryOld = lastDataSet.get(i);
                            if(Math.round(Float.parseFloat(savings.get(i).getSaved())) <  barEntryOld.getY() + 5)
                            {

                            }
                            else{
                                render = true;
                            }
                        }
                        catch (Exception e)
                        {

                        }
                    }
                    barEntries.add(new BarEntry(i, Math.round(Float.parseFloat(savings.get(i).getSaved()))));
                    Log.e("currentEntry", String.valueOf(Math.round(Float.parseFloat(savings.get(i).getSaved()))));
                    labelName.add(savings.get(i).getMonth());
                }



                if(lastDataSet!=null && lastDataSet.equals(barEntries) && !render)
                {
                    // who are you? do I even know you?
                }
                else {

                    lastDataSet = barEntries;
                    if (isShown) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setGraphData();
                            }
                        }, 300000);
                    } else {
                        setGraphData();
                        isShown = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startNewMonth() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid())
                .child("MonthlySavings");
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("saved",incomeAmount);
        map.put("month",formattedDate2.split("-")[1]);
        map.put("isSalarySet",true);
        map.put("year",formattedDate2.split("-")[2]);

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }
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
        xAxis.setGranularityEnabled(false);
        xAxis.setLabelCount(labelName.size());
        xAxis.setLabelRotationAngle(90);
        barChart.animateY(2000);
        barChart.invalidate();
    }

    public void startRunningMyChild(String input, String incomeAmount, String savingId) {
//        Log.i("SERVICESSS","START");
        auth = FirebaseAuth.getInstance();

        getSavedAmount();
        deductExpenseEverySecond(Double.parseDouble(input), incomeAmount);

    }


    private void deductExpenseEverySecond(double value, String incomeAmount) {

        SharedPreferences preferences = this.getActivity().getSharedPreferences("pinch.android.earnie",MODE_PRIVATE);
        isSet = preferences.getBoolean("isSet",false);
        SharedPreferences.Editor editor = preferences.edit();

        double remaining =  value;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("saved",remaining);
        map.put("month",formattedDate2.split("-")[1]);

        if(savingId==null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSet) {
                        reference.child(savingId).updateChildren(map);
                    } else {
                        editor.putBoolean("isSet", true);
                        editor.putString("id", id);
                        editor.apply();
                        reference.child(id).setValue(map);
                    }
                }
            },4000);

        }else {
            if (isSet) {
                reference.child(savingId).updateChildren(map);
            } else {
                editor.putBoolean("isSet", true);
                editor.putString("id", id);
                editor.apply();
                reference.child(id).setValue(map);
            }
        }

    }

    List<Expense> oneTimeExpense;

    public void getOneTimeExpense(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid())
                .child("oneTimeExpense");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oneTimeExpense = new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    oneTimeExpense.add(new Expense(dataSnapshot.child("id").getValue().toString(),
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getSavedAmount(){
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(auth.getCurrentUser().getUid()).child("MonthlySaved");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Date c = Calendar.getInstance().getTime();
                        System.out.println("Current time => " + c);
                        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String formattedDate2 = df2.format(c);

                        if (formattedDate2.split("-")[1].equals(dataSnapshot.child("month").getValue().toString())) {
                            savedAmount = snapshot.child("saved").getValue().toString();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}