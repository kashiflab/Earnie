package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;

public class WelcomeActivity extends AppCompatActivity {

    private TextView add_income_tv;
    private TextView incomeEt;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        auth = FirebaseAuth.getInstance();

        incomeEt = findViewById(R.id.incomeEt);
        add_income_tv=(TextView)findViewById(R.id.add_income_tv);

        add_income_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(incomeEt.getText().toString())){
                    Toast.makeText(WelcomeActivity.this, "Please enter monthly income", Toast.LENGTH_SHORT).show();
                }else {
                    Utils.initpDialog(WelcomeActivity.this,"Adding Income");
                    Utils.showpDialog();
                    setIncome(incomeEt.getText().toString());

                }
            }
        });
    }

    private void setIncome(String income) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid());

        HashMap<String,Object> map = new HashMap<>();
        map.put("income",income);
        map.put("saved",income);

//        map.put("saved",income);

        reference.updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utils.hidepDialog();
                if(task.isSuccessful()){
                    setMonthlyIncome(income);

                    Log.i("income","Income Added");

                }else{
                    Log.e("income","error");
                }
            }
        });
    }

    private void setMonthlyIncome(String income) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid()).child("MonthlySavings");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate2 = df2.format(c);

        String id = UUID.randomUUID().toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("saved",income) ;
        map.put("month",formattedDate2.split("-")[1]);
        map.put("isSalraySet",true);
        map.put("year",formattedDate2.split("-")[2]);

        reference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    SharedPreferences preferences = getSharedPreferences("pinch.android.earnie",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isIncomeAdded",true);
                    editor.putBoolean("isSet",true);
                    editor.apply();
                    startActivity(new Intent(WelcomeActivity.this,MainDrawerActivity.class));
                    finish();
                    finishAffinity();
                }
            }
        });

    }
}