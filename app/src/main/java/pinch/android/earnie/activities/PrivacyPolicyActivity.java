package pinch.android.earnie.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pinch.android.earnie.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ImageView back_press_privacy;

    private TextView googlePlayServices, contactus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        contactus = findViewById(R.id.contactus);
        back_press_privacy=(ImageView)findViewById(R.id.back_press_privacy);
        googlePlayServices = findViewById(R.id.googlePlayServices);

        googlePlayServices.setText(Html.fromHtml("<u>(https://www.google.com/policies/privacy/)</u>"));

        contactus.setText(Html.fromHtml("If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me at <u>benjamin.istace@gmail.com.</u>"));

        googlePlayServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/policies/privacy/"));
                startActivity(browserIntent);
            }
        });


        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "benjamin.istace@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

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