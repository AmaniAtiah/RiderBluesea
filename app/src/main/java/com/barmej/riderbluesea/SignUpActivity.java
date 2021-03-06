package com.barmej.riderbluesea;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.barmej.riderbluesea.domain.entity.Global;
import com.barmej.riderbluesea.domain.entity.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class SignUpActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GET_PHOTO = 2;
    public static final String USER_REF_PATH = "users";
    private ImageView userPhotoImageView;
    private ImageView downloadImageView;
    private TextInputLayout usernameTextInputLayout;
    private TextInputEditText usernameTextInputEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailTextInputEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordTextInputEditText;
    private Button signUpButton;
    private Button doYouHaveAnAccountButton;
    private Uri mUserPhotoUri;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private ProgressBar progressBar;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userPhotoImageView = findViewById(R.id.image_view_users);
        downloadImageView = findViewById(R.id.download_image_view);
        usernameTextInputLayout = findViewById(R.id.input_layout_username);
        usernameTextInputEditText = findViewById(R.id.edit_text_username);
        emailTextInputLayout = findViewById(R.id.input_layout_email);
        emailTextInputEditText = findViewById(R.id.edit_text_email);
        passwordTextInputLayout = findViewById(R.id.input_layout_password);
        passwordTextInputEditText = findViewById(R.id.edit_text_password);
        signUpButton = findViewById(R.id.sign_up_button);
        doYouHaveAnAccountButton = findViewById(R.id.do_you_have_an_account_button);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        userPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAccount();
            }
        });

        doYouHaveAnAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.getStartIntent(SignUpActivity.this));
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            hideForm(true);
            startActivity(HomeActivity.getStartIntent(SignUpActivity.this));
            finish();
        }
    }

    private void hideForm(boolean hide) {
        if (hide) {
            progressBar.setVisibility(View.VISIBLE);
            passwordTextInputLayout.setVisibility(View.INVISIBLE);
            emailTextInputLayout.setVisibility(View.INVISIBLE);
            usernameTextInputLayout.setVisibility(View.INVISIBLE);
            userPhotoImageView.setVisibility(View.INVISIBLE);
            downloadImageView.setVisibility(View.INVISIBLE);
            doYouHaveAnAccountButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            passwordTextInputLayout.setVisibility(View.VISIBLE);
            emailTextInputLayout.setVisibility(View.VISIBLE);
            usernameTextInputLayout.setVisibility(View.VISIBLE);
            userPhotoImageView.setVisibility(View.VISIBLE);
            downloadImageView.setVisibility(View.VISIBLE);
            doYouHaveAnAccountButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }

    private void createUserAccount() {

        if (TextUtils.isEmpty(usernameTextInputEditText.getText())) {
            usernameTextInputLayout.setError(getString(R.string.error_msg_username));
            return;
        }
        if (!isValidEmail(emailTextInputEditText.getText())) {
            emailTextInputLayout.setError(getString(R.string.invalid_email));
            return;
        }

        if (passwordTextInputEditText.getText().length() < 6) {
            passwordTextInputLayout.setError(getString(R.string.invalid_password));
            return;
        }
        if (mUserPhotoUri != null) {
            sendUserToHome();
        } else {
            Toast.makeText(this,R.string.add_image,Toast.LENGTH_SHORT).show();
        }

    }

    private void sendUserToHome() {
        firebaseAuth.createUserWithEmailAndPassword(emailTextInputEditText.getText().toString(),passwordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this,"create user",Toast.LENGTH_SHORT).show();
                    startActivity(HomeActivity.getStartIntent(SignUpActivity.this));
                    finish();
                    uploadImageToFirebase();

                    Global.CURRENT_USER = new Rider();
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    Global.CURRENT_USER.setUsername(usernameTextInputEditText.getText().toString());
                    Global.CURRENT_USER.setEmail(emailTextInputEditText.getText().toString());
                    Global.CURRENT_USER.setPhoto(mUserPhotoUri.toString());
                    Global.CURRENT_USER.setId(userId);

                    hideForm(true);

                    database.getReference(USER_REF_PATH).child(userId).setValue(Global.CURRENT_USER).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                hideForm(true);
                                Toast.makeText(SignUpActivity.this,"user added successfully",Toast.LENGTH_SHORT).show();

                            } else {
                                hideForm(false);
                                Toast.makeText(SignUpActivity.this,"user added failed",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {
                    hideForm(false);
                    Toast.makeText(SignUpActivity.this,"user added failed",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void uploadImageToFirebase() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference photoStorageRefernce = storageReference.child(UUID.randomUUID().toString());
        photoStorageRefernce.putFile(mUserPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    photoStorageRefernce.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this,"Uploading photo",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this,"not Uploading photo",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this,"not Uploading photo",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void requestExternalStoragePermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_READ_STORAGE);

        }else {
            launchGalleryIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGalleryIntent();
            } else {
                Toast.makeText(getApplicationContext(),R.string.read_permission_needed_to_access_files,Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_GET_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    mUserPhotoUri = data.getData();
                    userPhotoImageView.setImageURI(mUserPhotoUri);
                } catch (Exception e) {
                    Toast.makeText(SignUpActivity.this,R.string.photo_selection_error,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_photo)), REQUEST_GET_PHOTO);
    }
}
