package com.barmej.riderbluesea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.barmej.riderbluesea.domain.entity.Rider;
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
    private ProgressBar progressBar;
    private  Button dontHaveAnAccount;

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
        progressBar = findViewById(R.id.progress_bar);
        dontHaveAnAccount = findViewById(R.id.dont_have_an_account_button);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignUpActivity.getStartIntent(LoginActivity.this));

            }
        });

        if (firebaseUser != null) {

            final String userId = firebaseAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference(USER_REF_PATH).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Global.CURRENT_USER = snapshot.getValue(Rider.class);
                    startActivity(HomeActivity.getStartIntent(LoginActivity.this));
                    finish();
                    hideForm(true);
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

        hideForm(true);

        firebaseAuth.signInWithEmailAndPassword(emailTextInputEditText.getText().toString(), passwordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(HomeActivity.getStartIntent(LoginActivity.this));
                    finish();
                   // hideForm(true);

                } else {
                    Toast.makeText(LoginActivity.this,R.string.log_in_failed,Toast.LENGTH_SHORT).show();
                    hideForm(false);
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void hideForm(boolean hide) {
        if (hide) {
            progressBar.setVisibility(View.VISIBLE);
            passwordTextInputLayout.setVisibility(View.INVISIBLE);
            emailTextInputLayout.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.INVISIBLE);

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            passwordTextInputLayout.setVisibility(View.VISIBLE);
            emailTextInputLayout.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);

        }
    }
}
