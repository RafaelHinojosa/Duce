package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.duce.R;
import com.duce.databinding.SignupActivityBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import duce.models.User;

import static com.duce.R.color.signup_red;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText mEtUsername;
    private TextView mTvUsernameHelp;
    private EditText mEtPassword;
    private TextView mTvPasswordHelp;
    private Spinner mSpFirstLanguage;
    private Spinner mSpInterests;
    private Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SignupActivityBinding binding = SignupActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mEtUsername = binding.etUsername;
        mTvUsernameHelp = binding.tvUsernameHelp;
        mEtPassword = binding.etPassword;
        mTvPasswordHelp = binding.tvPasswordHelp;
        mSpFirstLanguage = binding.spFirstLanguage;
        mSpInterests = binding.spInterests;
        mBtnSignUp = binding.btnSignUp;

        mTvUsernameHelp.setVisibility(View.INVISIBLE);

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();

                signupUser(username, password);
            }
        });
    }

    private void signupUser(String username, String password) {
        if (username.isEmpty()) {
            mTvUsernameHelp.setVisibility(View.VISIBLE);
            mTvUsernameHelp.setText(R.string.must_fill);
            return;
        }
        if (password.length() < 8) {
            mTvPasswordHelp.setTextColor(getResources().getColor(signup_red));
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    mTvUsernameHelp.setVisibility(View.VISIBLE);
                    mTvUsernameHelp.setText(R.string.username_help);
                    return;
                }
                Toast.makeText(SignupActivity.this,  "Welcome " + username, Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    protected void goMainActivity() {
        Intent toMainActivity = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(toMainActivity);
    }
}