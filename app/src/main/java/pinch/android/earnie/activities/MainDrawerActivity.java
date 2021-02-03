package pinch.android.earnie.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import pinch.android.earnie.R;
import pinch.android.earnie.Users;
import pinch.android.earnie.activities.FragmentUtil.HomeFragment;

public class MainDrawerActivity extends AppCompatActivity {

    LinearLayout clk1, clk0;
    Toolbar toolbar;
    DrawerLayout drawer;

    FirebaseAuth auth;

    TextView fullname, email;
    ImageView close;
    Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activitydrawer);

        auth = FirebaseAuth.getInstance();
        getUserData(auth.getCurrentUser().getUid());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.emailTv);
        close = findViewById(R.id.close);

        clk0 = findViewById(R.id.click0);
        clk1 = findViewById(R.id.click1);


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Initializing();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    public void getUserData(String id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users = snapshot.getValue(Users.class);
                fullname.setText(users.getFullname());
                email.setText(users.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void ClickNavigation(View view) {
        Fragment fragment = null;

        switch (view.getId()) {
            case R.id.click0:
                   startActivity(new Intent(MainDrawerActivity.this, EditProfileActivity.class));
                break;
            case R.id.click1:
                startActivity(new Intent(MainDrawerActivity.this, PrivacyPolicyActivity.class));
                break;
            case R.id.logout_btn:
                auth.signOut();
                startActivity(new Intent(MainDrawerActivity.this,LoginActivity.class));
                finish();
                finishAffinity();
                break;
            default:
                fragment = new HomeFragment();
//                fragment = new HomeFragment();
//                toolbar.setTitle("Home");
                callfragment(fragment);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void callfragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.myfram, fragment)
                .commit();

    }

    public void Initializing() {
        Fragment fragment = null;
        fragment = new HomeFragment();
//        fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.myfram, fragment).commit();

    }


}