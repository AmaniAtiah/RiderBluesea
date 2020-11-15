package com.barmej.riderbluesea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.barmej.riderbluesea.SignUpActivity.USER_REF_PATH;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailTextInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordTextInputEditText;
    private Button loginButton;
    private FirebaseAuth firebaseAuth;



    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        emailTextInputLayout = findViewById(R.id.input_layout_email);
        emailTextInputEditText = findViewById(R.id.edit_text_email);
        passwordTextInputLayout = findViewById(R.id.input_layout_password);
        passwordTextInputEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });

        if (firebaseUser != null) {

            final String userId =firebaseAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference(USER_REF_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Global.CURRENT_USER = snapshot.getValue(Rider.class);
                    startActivity(MainActivity.getStartIntent(LoginActivity.this));
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
    }

    private void loginClicked() {
        if (!isValidEmail(emailTextInputEditText.getText())) {
            emailTextInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (passwordTextInputEditText.getText().length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.invalid_password));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(emailTextInputEditText.getText().toString(), passwordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    startActivity(MainActivity.getStartIntent(LoginActivity.this));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
