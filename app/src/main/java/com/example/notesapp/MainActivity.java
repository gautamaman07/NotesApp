package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button signUPbtn= findViewById(R.id.signUPBtn);
        signUPbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                finish();
            }
        });

    }

    private void redirect() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void btnLoginClick(View view) {
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();


        if (email.equals("")) {

            etEmail.setError("Enter Email");
        } else if (etPassword.equals("")) {
            etPassword.setError("Enter Password");
        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                redirect();
                            } else {

                                Toast.makeText(MainActivity.this, "failed to log in"
                                                + task.getException()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            redirect();
        }
    }

    public void forgotpassword(View view) {

        email = etEmail.getText().toString().trim();


        if (email.equals("")) {

            etEmail.setError("Enter Email");
        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Sent email Successfully :" + email, Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(MainActivity.this, "failed to sent email"
                                                + task.getException()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
        }


    }


}