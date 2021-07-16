package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.duce.R;

public class StartActivity extends AppCompatActivity {

    private Button mBtnToLogIn;
    private Button mBtnToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        mBtnToLogIn = findViewById(R.id.btnToLogIn);
        mBtnToSignUp = findViewById(R.id.btnToSignUp);

        mBtnToLogIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toLogIn = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(toLogIn);
            }
        });

        mBtnToSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(StartActivity.this, SignupActivity.class);
                startActivity(toSignUp);
            }
        });
    }
}