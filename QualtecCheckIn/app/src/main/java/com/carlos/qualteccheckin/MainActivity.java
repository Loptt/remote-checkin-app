package com.carlos.qualteccheckin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView registerButton;

    private EditText emailEditText;
    private EditText passwordEditText;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        loginButton = (Button) findViewById(R.id.main_login_button);
        registerButton = (TextView) findViewById(R.id.main_register);

        emailEditText = (EditText) findViewById(R.id.main_email);
        passwordEditText = (EditText) findViewById(R.id.main_password);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInUser() {
        String username = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            emailEditText.setError("Se requiere un email");
            emailEditText.requestFocus();

            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Se requiere una contraseña");
            passwordEditText.requestFocus();

            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Autenticando");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d("Checking", "Sign in action ended");
                progressDialog.dismiss();

                if (task.isSuccessful()) {

                    Log.d("Checking", "Successful login");
                    //Successful login

                    Log.d("Checking", "Before intent");
                    Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    //Failed login
                    Toast.makeText(MainActivity.this, "Login Failed",
                            Toast.LENGTH_SHORT).show();

                    //TODO: Add actions on failed login
                }
            }
        });
    }
}

