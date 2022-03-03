package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CircleImageView ivProfile;    //declaration
    private StorageReference mRootStorage;

    Uri localFileUri,serverFileUri;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setHomeButtonEnabled(true);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);

        mRootStorage =FirebaseStorage.getInstance().getReference();
        etName=findViewById(R.id.etName);

        mAuth= FirebaseAuth.getInstance();
        ivProfile=findViewById(R.id.ivProfile); //intialization
        user=mAuth.getCurrentUser();


        if(user!=null)
        {
            etName.setText(user.getDisplayName());
            Uri photoUri = user.getPhotoUrl();
            if(photoUri!=null)
            {
                      Glide.with(this)
                              .load(photoUri)
                              .placeholder(R.drawable.profile)
                              .error(R.drawable.profile)
                              .into(ivProfile);

            }

        }

    }
    private void updateOnlyName()
    {
        UserProfileChangeRequest request= new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString())
                .build();
        user.updateProfile(request).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProfileActivity.this, "task is succesful", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ProfileActivity.this, "Failed to update profile"
                            +task.getException(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updateNameandPhoto()
    {
        pd= ProgressDialog.show(this,"","Updating Profile",true);

        String file_name=user.getUid() + ".jpg";
        final  StorageReference fileRef= mRootStorage.child("images/"+ file_name);
        fileRef.putFile(localFileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        pd.dismiss();

                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                serverFileUri =uri;

                                UserProfileChangeRequest request= new UserProfileChangeRequest.Builder()
                                        .setDisplayName(etName.getText().toString())
                                        .setPhotoUri(serverFileUri)
                                        .build();


                                user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(ProfileActivity.this, "task is succesful", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(ProfileActivity.this, "Failed to update profile"
                                                    +task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        });
                    }
                });

    }
    public  void btnSaveClick(View view)
    {
        if(etName.getText().toString().trim().equals(""))
        {
            etName.setError("Enter NAme");
        }
        else
        {

            if(localFileUri!=null)
            {
                updateNameandPhoto();
            }
            else {
                updateOnlyName();
            }
        }
    }


    public void pickImage(View view)
    {
        Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101)
        {
            if(resultCode==RESULT_OK)
            {
                localFileUri =data.getData();
                ivProfile.setImageURI(localFileUri);
            }
        }
    }

    public void btnLogOut(View view)
    {
        mAuth.signOut();
        startActivity( new Intent(this,MainActivity.class));
        finish();
    }
}
