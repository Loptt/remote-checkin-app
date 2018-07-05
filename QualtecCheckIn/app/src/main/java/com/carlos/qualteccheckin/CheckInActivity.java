package com.carlos.qualteccheckin;

import android.Manifest;
import android.app.ProgressDialog;
import android.location.Location;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.Properties;
import java.util.Calendar;
import java.util.concurrent.CancellationException;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.carlos.qualteccheckin.MailAPI.SendMail;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckInActivity extends AppCompatActivity{

    private final int CHECKING_IN = 1;
    private final int CHEKING_OUT = 2;

    private Button enterButton;
    private Button exitButton;
    private TextView usernameTextView;

    private FirebaseUser user;

    private String username, email;

    private FusedLocationProviderClient locationProviderClient;
    private Calendar date;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        enterButton = (Button) findViewById(R.id.check_in_enter);
        exitButton = (Button) findViewById(R.id.check_in_exit);

        usernameTextView = (TextView) findViewById(R.id.check_in_username);

        user = FirebaseAuth.getInstance().getCurrentUser();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (user != null) {
            username = user.getDisplayName();
            email = user.getEmail();

            if (username != null) {
                usernameTextView.setText(username);
            }
            else {
                usernameTextView.setText("Sin Nombre");
            }
        }

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    Toast.makeText(CheckInActivity.this, "Entrada", Toast.LENGTH_SHORT).show();
                    checkIn(CHECKING_IN);
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
                    checkIn(CHEKING_OUT);
                }
                else {
                    //User not correctly authenticated
                    Toast.makeText(CheckInActivity.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkIn(final int status) {

        int result = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        progressDialog = new ProgressDialog(CheckInActivity.this);
        progressDialog.setMessage("Solicitud en progreso");
        progressDialog.show();

        if (result != PermissionChecker.PERMISSION_DENIED) {
            locationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {
                        date = Calendar.getInstance();

                        prepareAndSendEmail(status, date, location);
                    }
                    else {
                        Toast.makeText(CheckInActivity.this, "Error de Checkin", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.dismiss();
                }
            });
        }
    }

    private void prepareAndSendEmail(int status, Calendar date, Location location) {
        String message;

        String year = String.valueOf(date.get(Calendar.YEAR));
        String month = String.valueOf(date.get(Calendar.MONTH));
        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));

        String hour = String.valueOf(date.get(Calendar.HOUR));
        String minutes = String.valueOf(date.get(Calendar.MINUTE));

        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        if (status == CHECKING_IN) {
            message = "Check in \n" + "Date: " + day + "/" + month + "/" + year + " \n" + "Time: " + hour + ":" + minutes
                    + " \n" + "Latitude: " + latitude + " \n" + "Longitude: " + longitude + "\n";
        }
        else {
            message = "Check out \n" + "Date: " + day + "/" + month + "/" + year + " \n" + "Time: " + hour + ":" + minutes
                    + " \n" + "Latitude: " + latitude + " \n" + "Longitude: " + longitude + "\n";
        }

        sendEmail(message);
    }

    private void sendEmail(String message) {

        String email = "carlos.deg02@hotmail.com";
        String subject = "Reportando...";

        SendMail sendMail = new SendMail(CheckInActivity.this, email, subject, message);

        sendMail.execute();
    }
}
