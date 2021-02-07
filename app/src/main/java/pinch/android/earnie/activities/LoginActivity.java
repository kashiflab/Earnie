package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signupTxt, forgetPassword;
    EditText email, password;
    Button signin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signupTxt = findViewById(R.id.signup_text);
        signin = findViewById(R.id.loginbtn);
        email = findViewById(R.id.login_email);
        forgetPassword = findViewById(R.id.forget_password);
        password = findViewById(R.id.login_password);
        signupTxt.setOnClickListener(this);
        signin.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signup_text)
            startActivity(new Intent(this, SignUpActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (view.getId() == R.id.loginbtn) {
            if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText())) {

                Utils.initpDialog(this,"Please wait...");
                Utils.showpDialog();
                LoginUser(email.getText().toString(),password.getText().toString());

            }
            else Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
        if (view.getId() == R.id.forget_password)
            Toast.makeText(this, "to be done", Toast.LENGTH_SHORT).show();
    }

    private void LoginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                SharedPreferences preferences = getSharedPreferences("pinch.android.earnie",MODE_PRIVATE);
                boolean isIncomeAdded = preferences.getBoolean("isIncomeAdded",false);


                if(task.isSuccessful()){
                    isSalarySet(auth.getCurrentUser().getUid());

                }
                else {
                    Utils.hidepDialog();
                    Toast.makeText(LoginActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void isSalarySet(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Utils.hidepDialog();
                if(!snapshot.child("saved").getValue().equals("")){
                    startActivity(new Intent(LoginActivity.this, MainDrawerActivity.class));
                    finish();
                    finishAffinity();

                }else {
                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                    finish();
                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}