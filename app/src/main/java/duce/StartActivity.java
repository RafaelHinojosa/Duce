package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.duce.R;
import com.duce.databinding.StartActivityBinding;
import com.parse.ParseUser;

public class StartActivity extends AppCompatActivity {

    private TextView mTvLogIn;
    private TextView mTvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            Intent toMainActivity = new Intent(StartActivity.this, MainActivity.class);
            startActivity(toMainActivity);
        }

        StartActivityBinding binding = StartActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mTvLogIn = binding.tvToLogIn;
        mTvSignUp = binding.tvToSignUp;

        mTvLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toLogIn = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(toLogIn);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mTvSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(toSignUp);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}