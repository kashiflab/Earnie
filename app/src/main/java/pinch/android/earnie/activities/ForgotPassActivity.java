package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import pinch.android.earnie.R;
import pinch.android.earnie.Utils.Utils;

public class ForgotPassActivity extends AppCompatActivity {

    private TextView signInTV;
    private Button sendEmail;
    private EditText email;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        signInTV = findViewById(R.id.signin_txt);
        sendEmail = findViewById(R.id.resetBtn);
        email = findViewById(R.id.login_email);

        auth = FirebaseAuth.getInstance();

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText())){
                    Utils.initpDialog(ForgotPassActivity.this,"Sending Email");
                    Utils.showpDialog();
                    Toast.makeText(ForgotPassActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }else{
                    sendResetEmail(email.getText().toString());
                }
            }
        });

    }

    private void sendResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utils.hidepDialog();
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                    ForgotPassActivity.super.onBackPressed();
                }else Toast.makeText(ForgotPassActivity.this, "Some error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}