package com.barmej.riderbluesea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.barmej.riderbluesea.domain.entity.Rider;
import com.barmej.riderbluesea.fragment.CurrentTripFragment;
import com.barmej.riderbluesea.fragment.TripListFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.barmej.riderbluesea.SignUpActivity.USER_REF_PATH;

public class HomeActivity extends AppCompatActivity {
    private TextView userNameTv;
    private ImageView userImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigationView;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameTv = findViewById(R.id.username_text_view);
        userImageView = findViewById(R.id.user_image_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        frameLayout = findViewById(R.id.frame_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        String userId = firebaseAuth.getCurrentUser().getUid();

        setFragment(new TripListFragment());
        setFragment(new CurrentTripFragment());

//        bottomNavigationView = findViewById(R.id.navigation_bottom);

//        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNav);
//        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TripListFragment()).commit();

        firebaseDatabase.getReference(USER_REF_PATH).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Global.CURRENT_USER = snapshot.getValue(Rider.class);
                userNameTv.setText(Global.CURRENT_USER.getUsername());
                Glide.with(getApplicationContext()).load(Global.CURRENT_USER.getPhoto()).into(userImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private BottomNavigationView.OnNavigationItemSelectedListener bottomNav =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                    Fragment selectedFragment = null;
//                    switch (item.getItemId()) {
//                        case R.id.nav_main:
//                            selectedFragment = new TripListFragment();
//                            break;
//
//                        case R.id.nav_reserve_trip:
//                            selectedFragment = new reservedTripFragment();
//                            break;
//
//                        case R.id.nav_current_trip:
//                            selectedFragment = new CurrentTripFragment();
//                            break;
//
//                        case R.id.nav_my_account:
//                            selectedFragment = new AccountFragment();
//                            break;
//                    }
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
//                    return true;
//                }
//            };

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
//        SpannableString s = new SpannableString("My red MenuItem");
//        s.setSpan(new ForegroundColorSpan(R.color.colorAccent), 0, s.length(), 0);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(LoginActivity.getStartIntent(HomeActivity.this));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStop() {
        super.onStop();

    }
}