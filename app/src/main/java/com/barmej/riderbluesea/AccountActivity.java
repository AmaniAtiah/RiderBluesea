package com.barmej.riderbluesea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.barmej.riderbluesea.domain.entity.Global;
import com.barmej.riderbluesea.domain.entity.Rider;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.barmej.riderbluesea.SignUpActivity.USER_REF_PATH;

public class AccountActivity extends AppCompatActivity {
    private ImageView userImageView;
    private TextView userNameTv;
    private TextView userEmailTv;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Button changeProfileButton;
    private Button resetPasswordButton;
    private Button logoutButton;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, AccountActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userNameTv = findViewById(R.id.username_text_view);
        userImageView = findViewById(R.id.user_image_view);
        userEmailTv = findViewById(R.id.email_textview);
        changeProfileButton = findViewById(R.id.change_profile_button);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        logoutButton = findViewById(R.id.logout_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        changeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                intent.putExtra(EditProfileActivity.RIDER_DATA, Global.CURRENT_USER);
                startActivity(intent);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(LoginActivity.getStartIntent(AccountActivity.this));
                finish();
            }
        });
        String userId = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase.getReference(USER_REF_PATH).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Global.CURRENT_USER = snapshot.getValue(Rider.class);
                if (Global.CURRENT_USER != null) {
                    userNameTv.setText(Global.CURRENT_USER.getUsername());
                    userEmailTv.setText(Global.CURRENT_USER.getEmail());
                    Glide.with(getApplicationContext()).load(Global.CURRENT_USER.getPhoto()).placeholder(R.drawable.ic_account_circle_black_24dp).into(userImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}