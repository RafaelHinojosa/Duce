package duce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duce.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import duce.fragments.ConversationSettingsDialogFragment;

public class ConversationActivity extends AppCompatActivity implements ConversationSettingsDialogFragment.ConversationSettingsDialogListener {

    public static final String TAG = "ConversationActivity";

    private ImageView mIvProfilePicture;
    private TextView mTvFlag;
    private TextView mTvUsername;
    private FloatingActionButton mBtnSettings;
    private RecyclerView mRvMessages;
    private EditText mEtCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_activity);

        mBtnSettings = findViewById(R.id.btnSettings);
        mBtnSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ConversationSettingsDialogFragment settingsDialog = ConversationSettingsDialogFragment.newInstance("Select Language");
        settingsDialog.show(fm, "conversation_settings_dialog_fragment");
    }

    // Extract data put in the interface in the Settings Dialog
    @Override
    public void onFinishSettingsDialog(String language) {
        Toast.makeText(this, "Language Selected: " + language, Toast.LENGTH_SHORT).show();
    }
}