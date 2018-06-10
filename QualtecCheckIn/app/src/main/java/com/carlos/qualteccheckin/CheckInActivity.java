package com.carlos.qualteccheckin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.carlos.qualteccheckin.MailAPI.SendMail;
import com.google.firebase.auth.FirebaseAuth;

public class CheckInActivity extends AppCompatActivity{

    private Button enterButton;
    private Button exitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        enterButton = (Button) findViewById(R.id.check_in_enter);
        exitButton = (Button) findViewById(R.id.check_in_exit);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CheckInActivity.this, "Entrada", Toast.LENGTH_SHORT).show();
                sendEmail("Entrando");
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CheckInActivity.this, "Salida", Toast.LENGTH_SHORT).show();
                sendEmail("Saliendo");
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
