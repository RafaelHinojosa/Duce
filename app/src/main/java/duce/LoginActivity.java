package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duce.R;
import com.duce.databinding.LoginActivityBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import duce.models.CustomUser;

public class LoginActivity extends AppCompatActivity {

    public static final String  TAG = "LoginActivity";

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginActivityBinding binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mEtUsername = binding.etUsername;
        mEtPassword = binding.etPassword;
        mBtnLogIn = binding.btnLogIn;

        mBtnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    protected void loginUser(String username, String password) {

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Username/password are not correct!", Toast.LENGTH_SHORT).show();
                    return;
                }
                CustomUser mUser = new CustomUser(ParseUser.getCurrentUser());
                mUser.setOnline(true);
                mUser.getCustomUser().saveInBackground();

                Toast.makeText(LoginActivity.this, "Welcome back " + username, Toast.LENGTH_SHORT).show();
                Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(toMainActivity);
            }
        });
    }
}