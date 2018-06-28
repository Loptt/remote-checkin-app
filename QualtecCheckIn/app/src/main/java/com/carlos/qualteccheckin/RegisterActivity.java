package com.carlos.qualteccheckin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressDialog progressDialog;

    private FirebaseUser user;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton = (Button) findViewById(R.id.register_register_button);

        nameEditText = (EditText)  findViewById(R.id.register_name);
        emailEditText = (EditText)  findViewById(R.id.register_email);
        passwordEditText = (EditText)  findViewById(R.id.register_password);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Registrando");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    //Successful registration
                    Log.d("Checking", "Updating profile");
                    updateProfile(name);
                }
                else {
                    //Failed Registration
                    Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfile(String name) {
        user = firebaseAuth.getCurrentUser();

        if (user != null) {

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            Log.d("Checking", "Before update profile");

            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Checking", "Update profile task successful");
                        Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, CheckInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {
                        Log.d("Checking", "Update profile task failed");
                        Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.d("Checking", "After update profile");
        }
        else {
            Log.d("Checking", "User not recognized");
        }

    }
}
