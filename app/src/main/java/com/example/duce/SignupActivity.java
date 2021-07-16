package com.example.duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Spinner spFirstLanguage;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spFirstLanguage = findViewById(R.id.spFirstLanguage);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });
    }

    protected void checkCredentials() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String firstLanguage = spFirstLanguage.getSelectedItem().toString();

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password must contain at least 1 character", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent toMainActivity = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(toMainActivity);
    }
}