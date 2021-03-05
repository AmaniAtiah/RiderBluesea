package com.barmej.riderbluesea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.barmej.riderbluesea.domain.entity.Global;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import static com.barmej.riderbluesea.SignUpActivity.USER_REF_PATH;

public class EditProfileActivity extends AppCompatActivity {
    public static final String RIDER_DATA = "rider_data";
    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GET_PHOTO = 2;
    private TextInputEditText usernameTextInputEditText;
    private TextInputLayout usernameTextInputLayout;
    private ImageView userPhotoImageView;
    private Button saveButton;
    private FirebaseDatabase database;
    private Uri mUserPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usernameTextInputEditText = findViewById(R.id.edit_text_username);
        usernameTextInputLayout = findViewById(R.id.input_layout_username);
        userPhotoImageView = findViewById(R.id.user_image_view);
        saveButton = findViewById(R.id.save_button);
        database = FirebaseDatabase.getInstance();

        if (getIntent() != null && getIntent().getExtras() != null) {
            Global.CURRENT_USER = getIntent().getExtras().getParcelable(RIDER_DATA);
            if (Global.CURRENT_USER != null) {
                Glide.with(this).load(Global.CURRENT_USER.getPhoto()).placeholder(R.drawable.ic_account_circle_black_24dp).dontAnimate().into(userPhotoImageView);
                usernameTextInputEditText.setText(Global.CURRENT_USER.getUsername());
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        userPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission();
            }
        });
    }

    private void updateData() {
        if (TextUtils.isEmpty(usernameTextInputEditText.getText()) || mUserPhotoUri == null) {
            usernameTextInputLayout.setError(getString(R.string.error_msg_username));
            return;
        }
        uploadImageToFirebase();
        Global.CURRENT_USER.setUsername(usernameTextInputEditText.getText().toString());
        Global.CURRENT_USER.setPhoto(mUserPhotoUri.toString());
        finish();
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference(USER_REF_PATH).child(userId).setValue(Global.CURRENT_USER);
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
                                Toast.makeText(EditProfileActivity.this,"Uploading photo",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this,"not Uploading photo",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this,"not Uploading photo",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this,R.string.photo_selection_error,Toast.LENGTH_SHORT).show();
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