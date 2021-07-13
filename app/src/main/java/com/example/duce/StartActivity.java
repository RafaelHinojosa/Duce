package com.example.duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button btnToLogIn;
    Button btnToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activity);

        btnToLogIn = findViewById(R.id.btnToLogIn);
        btnToSignUp = findViewById(R.id.btnToSignUp);

        btnToLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toLogIn = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(toLogIn);
            }
        });

        btnToSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(toSignUp);
            }
        });
    }
}