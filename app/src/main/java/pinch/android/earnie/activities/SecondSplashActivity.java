package pinch.android.earnie.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import pinch.android.earnie.R;

public class SecondSplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_splash);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(auth.getCurrentUser()!=null) {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SecondSplashActivity.this, MainDrawerActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Intent mainIntent = new Intent(SecondSplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, 2000);
    }
}