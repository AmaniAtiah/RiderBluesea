package com.barmej.riderbluesea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.barmej.riderbluesea.domain.entity.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputLayout currentPasswordTextInputLayout;
    private TextInputEditText currentPasswordTextInputEditText;
    private TextInputLayout newPasswordTextInputLayout;
    private TextInputEditText newPasswordTextInputEditText;
    private TextInputLayout confirmPasswordTextInputLayout;
    private TextInputEditText confirmPasswordTextInputEditText;
    private Button updatePasswordButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPasswordTextInputLayout = findViewById(R.id.input_layout_current_password);
        currentPasswordTextInputEditText = findViewById(R.id.edit_text_current_password);
        newPasswordTextInputLayout = findViewById(R.id.input_layout_new_password);
        newPasswordTextInputEditText = findViewById(R.id.edit_text_new_password);
        confirmPasswordTextInputLayout = findViewById(R.id.input_layout_confirm_password);
        confirmPasswordTextInputEditText = findViewById(R.id.edit_text_confirm_password);
        updatePasswordButton = findViewById(R.id.update_password_button);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPasswordTextInputLayout.setError(null);
                newPasswordTextInputLayout.setError(null);
                confirmPasswordTextInputLayout.setError(null);
                if (TextUtils.isEmpty(currentPasswordTextInputEditText.getText())) {
                    currentPasswordTextInputLayout.setError(getString(R.string.error_msg_current_password));
                } else if (TextUtils.isEmpty(newPasswordTextInputEditText.getText())) {
                    newPasswordTextInputLayout.setError(getString(R.string.error_msg_new_password));
                } else if (TextUtils.isEmpty(confirmPasswordTextInputEditText.getText())) {
                    confirmPasswordTextInputLayout.setError(getString(R.string.error_msg_confirm_password));
                } else  if (newPasswordTextInputEditText.getText().length() < 6) {
                    newPasswordTextInputLayout.setError(getString(R.string.invalid_password));
                } else if (newPasswordTextInputEditText.getText().toString().equals(confirmPasswordTextInputEditText.getText().toString())) {
                    updatePassword();
                }
            }
        });
    }

    private void updatePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Global.CURRENT_USER.getEmail(), currentPasswordTextInputEditText.getText().toString());
        user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPasswordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePasswordActivity.this,R.string.change_password_successfully,Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,R.string.password_has_not_been_changed,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this,R.string.password_has_not_been_changed,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}