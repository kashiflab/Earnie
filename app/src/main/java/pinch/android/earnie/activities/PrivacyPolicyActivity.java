package pinch.android.earnie.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import pinch.android.earnie.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ImageView back_press_privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        back_press_privacy=(ImageView)findViewById(R.id.back_press_privacy);
        back_press_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==back_press_privacy)
                {
                    finish();
                }
            }
        });
    }
}