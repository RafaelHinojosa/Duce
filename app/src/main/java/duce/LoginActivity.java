package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.duce.R;

public class LoginActivity extends AppCompatActivity {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mEtUsername = findViewById(R.id.etUsername);
        mEtPassword = findViewById(R.id.etPassword);
        mBtnLogIn = findViewById(R.id.btnLogIn);

        mBtnLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    protected void loginUser() {
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();

        Toast.makeText(this, "Username = " + username + " Password = " + password, Toast.LENGTH_SHORT).show();

        Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(toMainActivity);
    }
}