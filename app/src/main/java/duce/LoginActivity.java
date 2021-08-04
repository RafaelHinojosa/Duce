package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.duce.R;
import com.duce.databinding.LoginActivityBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.joda.time.Seconds;

import java.util.concurrent.TimeUnit;

import duce.models.CustomUser;
import es.dmoral.toasty.Toasty;

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
            @SuppressLint("ResourceType")
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toasty.custom(
                        LoginActivity.this,
                        (CharSequence) getString(R.string.incorrect_credentials),
                        android.R.drawable.ic_menu_close_clear_cancel,
                        R.color.imperial_red,
                        Toast.LENGTH_SHORT,
                        true,
                        true
                        )
                        .show();
                    return;
                }
                CustomUser mUser = new CustomUser(ParseUser.getCurrentUser());
                mUser.setOnline(true);
                mUser.getCustomUser().saveInBackground();

                Toasty.custom(
                    LoginActivity.this,
                    (CharSequence) getString(R.string.login_welcome) + " " + mUser.getUsername(),
                    R.drawable.person_outline,
                    R.color.celadon_blue,
                    Toast.LENGTH_SHORT,
                    false,
                    true
                    )
                    .show();

                Intent toMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(toMainActivity);
            }
        });
    }
}