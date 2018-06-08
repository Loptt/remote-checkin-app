package com.carlos.qualteccheckin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginButton;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.main_login_button);
        usernameEditText = (EditText) findViewById(R.id.main_username);
        passwordEditText = (EditText) findViewById(R.id.main_password);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(this);
    }

    private void signInUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("Se requiere un email");
            usernameEditText.requestFocus();

            //Toast.makeText(MainActivity.this, "Por favor, introduce un usuario",
              //      Toast.LENGTH_SHORT).show();

            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Se requiere una contraseña");
            passwordEditText.requestFocus();

            //Toast.makeText(MainActivity.this, "Por favor introduce una contraseña",
            //        Toast.LENGTH_SHORT).show();

            return;
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Autenticando");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    //Successful login
                    Toast.makeText(MainActivity.this, "Login Successful",
                            Toast.LENGTH_SHORT).show();

                    //TODO: Add actions on successful login
                    Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                } else {
                    //Failed login
                    Toast.makeText(MainActivity.this, "Login Failed",
                            Toast.LENGTH_SHORT).show();

                    //TODO: Add actions on failed login
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_login_button:
                signInUser();
                break;
        }
    }
}

