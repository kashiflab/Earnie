package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

import pinch.android.earnie.R;
import pinch.android.earnie.Users;
import pinch.android.earnie.Utils.Utils;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView back_press;
    private EditText number, country_code, name, password, confirmPassword, oldPassword, salary;
    private Button saveChanges;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();

        salary = findViewById(R.id.salary);
        oldPassword = findViewById(R.id.oldPassword);
        number = findViewById(R.id.number);
        country_code = findViewById(R.id.country_code);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        name = findViewById(R.id.fullname);
        saveChanges = findViewById(R.id.saveChanges);
        back_press=(ImageView)findViewById(R.id.back_press);

        getUserData(auth.getCurrentUser().getUid());

        back_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view==back_press)
                {
                    EditProfileActivity.super.onBackPressed();
                }
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(number.getText()) || TextUtils.isEmpty(country_code.getText())
                || TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(salary.getText())){
                    Toast.makeText(EditProfileActivity.this, "Number and name is required", Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().length()>0){
                    if(password.getText().toString().equals(confirmPassword.getText().toString())){
                        updatePassword(country_code.getText().toString(),number.getText().toString(),
                                name.getText().toString(), password.getText().toString(), oldPassword.getText().toString(),
                                salary.getText().toString());
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Utils.initpDialog(EditProfileActivity.this,"Updating...");
                    Utils.showpDialog();
                    updateUserData(country_code.getText().toString(),number.getText().toString(),
                            name.getText().toString(),salary.getText().toString());
                }
            }
        });
    }

    private Users users;

    private void getUserData(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);

                number.setText(users.getNumber());
                name.setText(users.getFullname());
                country_code.setText(users.getCountryCode());
                salary.setText(users.getIncome());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePassword(String countryCode, String number, String name, String password,
                                String oldPassword, String salary) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(auth.getCurrentUser().getEmail(), oldPassword);

        // Prompt the user to re-provide their sign-in credentials

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateUserData(countryCode,number,name, salary);
                                        Log.d("TAG", "Password updated");
                                    } else {
                                        Log.d("TAG", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("TAG", "Error auth failed");
                        }
                    }
                });
    }

    private void updateUserData(String countryCode,String number, String name, String salary) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getUid());

        HashMap<String,Object> map = new HashMap<>();
        map.put("fullname",name);
        map.put("number",number);
        map.put("countryCode",countryCode);
        map.put("salary",salary);

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    EditProfileActivity.super.onBackPressed();
                    Toast.makeText(EditProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}