package com.carlos.qualteccheckin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CheckInActivity extends AppCompatActivity implements View.OnClickListener{

    private Button enterButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        enterButton = (Button) findViewById(R.id.check_in_enter);
        exitButton = (Button) findViewById(R.id.check_in_exit);

        enterButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_in_enter:
                Toast.makeText(CheckInActivity.this, "Entrada", Toast.LENGTH_SHORT).show();
                break;

            case R.id.check_in_exit:
                Toast.makeText(CheckInActivity.this, "Salida", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
