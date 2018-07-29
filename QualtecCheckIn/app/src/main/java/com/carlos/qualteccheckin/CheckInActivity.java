package com.carlos.qualteccheckin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener{

    private final int CHECKING_IN = 1;
    private final int CHEKING_OUT = 2;
    private final int REQUEST_LOCATION = 3;

    private Button enterButton;
    private Button exitButton;
    private TextView logoutText;
    private ImageButton configButton;
    private TextView usernameTextView;

    private  int statusSaver = 0;

    private FirebaseUser user;

    private String username;
    private String emailToSend = "";

    private FusedLocationProviderClient locationProviderClient;
    private Calendar date;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        //Declare custom action bar
        ActionBar actionBar = getSupportActionBar();

        //Find IDs for buttons
        enterButton = (Button) findViewById(R.id.check_in_enter);
        exitButton = (Button) findViewById(R.id.check_in_exit);
        logoutText = (TextView) findViewById(R.id.check_in_logout);
        usernameTextView = (TextView) findViewById(R.id.check_in_username);

        //Get the firebase current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Configure custom action bar
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.action_bar_check_in,null);

        //Find ID of config button in action bar
        configButton = (ImageButton) customView.findViewById(R.id.action_bar_check_in_imgbutton);

        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);

            actionBar.setCustomView(customView);
            actionBar.setDisplayShowCustomEnabled(true);
        }
        else {
            Toast.makeText(CheckInActivity.this, "Whoops", Toast.LENGTH_SHORT).show();
        }

        if (user != null) {
            username = user.getDisplayName();

            if (username != null) {
                usernameTextView.setText(username);
            }
            else {
                usernameTextView.setText("Sin Nombre");
            }
        }

        enterButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        logoutText.setOnClickListener(this);
        configButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.check_in_enter:
                if (user != null) {
                    Toast.makeText(CheckInActivity.this, "Entrada", Toast.LENGTH_SHORT).show();
                    checkIn(CHECKING_IN);
                }
                else {
                    //User not correctly authenticated
                    Toast.makeText(CheckInActivity.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.check_in_exit:
                if (user != null) {
                    Toast.makeText(CheckInActivity.this, "Salida", Toast.LENGTH_SHORT).show();
                    checkIn(CHEKING_OUT);
                }
                else {
                    //User not correctly authenticated
                    Toast.makeText(CheckInActivity.this, "Error de autenticacion", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.check_in_logout:
                FirebaseAuth.getInstance().signOut();

                intent = new Intent(CheckInActivity.this, MainActivity.class);
                startActivity(intent);

                break;

            case R.id.action_bar_check_in_imgbutton:
                intent = new Intent(CheckInActivity.this, ConfigurationActivity.class);
                startActivity(intent);

                break;
        }
    }

    private void checkIn(final int status) {

        int result = PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        statusSaver = status;

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
        else {
            progressDialog.dismiss();
            Toast.makeText(CheckInActivity.this, "Servicios de ubicacion desactivados", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(CheckInActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        emailToSend = sharedPreferences.getString("pref_email", "");

        String subject = "Reportando...";

        SendMail sendMail = new SendMail(CheckInActivity.this, emailToSend, subject, message);

        sendMail.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkIn(statusSaver);
                }
                else {
                    Toast.makeText(CheckInActivity.this, "Servicio no disponible sin acceso a ubicacion", Toast.LENGTH_LONG).show();
                }

                break;
            }
        }
    }
}
