package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.ICancelToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    TextView loginText;
    EditText number, email, name, password, confirmPassword, countryCode;
    Button signup;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loginText = findViewById(R.id.logintext);
        number = findViewById(R.id.phone_number);
        email = findViewById(R.id.signup_email);
        name = findViewById(R.id.signup_name);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        countryCode = findViewById(R.id.country_code);
        signup = findViewById(R.id.signup_btn);
        loginText.setOnClickListener(this);
        signup.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logintext)
            startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (view.getId() == R.id.signup_btn) {
            if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText())
            && !TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(number.getText())
            && !TextUtils.isEmpty(confirmPassword.getText()) && !TextUtils.isEmpty(countryCode.getText())) {

                if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                    Utils.initpDialog(this,"Please wait...");
                    Utils.showpDialog();
                    String phone = countryCode.getText().toString()+number.getText().toString();
                    RegisterUser(email.getText().toString(), password.getText().toString()
                            , phone , name.getText().toString());

                }else {
                    Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
            }
            else Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void RegisterUser(String email, String password, String phone, String name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id",auth.getCurrentUser().getUid());
                    map.put("fullname",name);
                    map.put("email",email);
                    map.put("number",phone);
                    map.put("income","");
                    map.put("saved","");

                    reference.child(auth.getCurrentUser().getUid()).setValue(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(SignUpActivity.this, WelcomeActivity.class));
                                finish();
                                Toast.makeText(SignUpActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SignUpActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
                            }
                            Utils.hidepDialog();
                        }
                    });

                }
            }
        });
    }
}