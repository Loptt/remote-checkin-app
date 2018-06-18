package com.carlos.qualteccheckin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.carlos.qualteccheckin.MailAPI.SendMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckInActivity extends AppCompatActivity{

    private Button enterButton;
    private Button exitButton;

    private TextView usernameTextView;

    private FirebaseUser user;

    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        enterButton = (Button) findViewById(R.id.check_in_enter);
        exitButton = (Button) findViewById(R.id.check_in_exit);

        usernameTextView = (TextView) findViewById(R.id.check_in_username);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            username = user.getDisplayName();
            email = user.getEmail();

            if (username.isEmpty()) {
                usernameTextView.setText("Sin Nombre");
            }
            else {
                usernameTextView.setText(username);
            }
        }

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Toast.makeText(CheckInActivity.this, "Entrada", Toast.LENGTH_SHORT).show();
                    sendEmail("Entrando");
                }
                else {
                    //User not correctly authenticated
                    Toast.makeText(CheckInActivity.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Toast.makeText(CheckInActivity.this, "Salida", Toast.LENGTH_SHORT).show();
                    sendEmail("Saliendo");
                }
                else {
                    //User not correctly authenticated
                    Toast.makeText(CheckInActivity.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail(String message) {

        String email = "carlos.deg02@hotmail.com";
        String subject = "Reportando...";

        SendMail sendMail = new SendMail(CheckInActivity.this, email, subject, message);

        sendMail.execute();
    }
}
