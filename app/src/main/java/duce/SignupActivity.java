package duce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import duce.adapters.LanguagesAdapter;
import duce.models.Languages;
import duce.models.UserLanguages;
import io.github.muddz.styleabletoast.StyleableToast;

import static com.duce.R.color.mtrl_navigation_bar_colored_item_tint;
import static com.duce.R.color.signup_red;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText mEtUsername;
    private TextView mTvUsernameHelp;
    private EditText mEtPassword;
    private TextView mTvPasswordHelp;
    private TextView mTvFirstLanguage;
    private TextView mTvInterests;
    private Button mBtnSignUp;
    private LanguagesAdapter mLanAdapter;
    private boolean[] mIsLanguageSelected;
    private ArrayList<Integer> mSelectedLanguages;
    private ArrayList<Integer> mInterestLanguages;

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
        mTvFirstLanguage = binding.tvFirstLanguage;
        mTvInterests = binding.tvInterests;
        mBtnSignUp = binding.btnSignUp;
        mLanAdapter = new LanguagesAdapter();
        mSelectedLanguages = new ArrayList<Integer>();
        mInterestLanguages = new ArrayList<Integer>();

        mTvUsernameHelp.setVisibility(View.INVISIBLE);

        mTvFirstLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlertDialog("myLanguage");
            }
        });

        mTvInterests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlertDialog("interest");
            }
        });

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
                registerUserLanguages();
                StyleableToast.makeText(
                    SignupActivity.this,
                    getString(R.string.signup_welcome) + " " + username + "!",
                    Toast.LENGTH_SHORT,
                    R.style.welcome_to_duce
                    )
                    .show();
                goMainActivity();
            }
        });
    }

    // Registers the relationships between the user and the languages selected in the Database
    private void registerUserLanguages() {
        // Set the languages put as Proficient Languages
        for (int i = 0; i < mSelectedLanguages.size(); i++) {
            int index = mSelectedLanguages.get(i);
            Languages languageObject = mLanAdapter.getLanguageObject(index);
            if (languageObject != null) {
                if (!languageObject.equals("Original")) {
                    UserLanguages userLanguages = new UserLanguages();
                    userLanguages.setUser(ParseUser.getCurrentUser());
                    userLanguages.setLanguage(languageObject);
                    userLanguages.setMyLanguage(true);
                    userLanguages.setInterestedIn(false);
                    userLanguages.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "Could not register languages");
                            }
                        }
                    });
                }
            }
        }

        // Set the languages put as Interest
        for (int i = 0; i < mInterestLanguages.size(); i++) {
            int index = mInterestLanguages.get(i);
            Languages languageObject = mLanAdapter.getLanguageObject(index);
            if (languageObject != null) {
                if (!languageObject.getLanguageName().equals("Original")) {
                    UserLanguages userLanguages = new UserLanguages();
                    userLanguages.setUser(ParseUser.getCurrentUser());
                    userLanguages.setLanguage(languageObject);
                    userLanguages.setMyLanguage(false);
                    userLanguages.setInterestedIn(true);
                    userLanguages.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.i(TAG, "Could not register languages");
                            }
                        }
                    });
                }
            }
        }
    }

    public void setAlertDialog(String languageType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle(R.string.select_languages);
        builder.setCancelable(false);

        mIsLanguageSelected = new boolean[mLanAdapter.getItemCount()];

        builder.setMultiChoiceItems(mLanAdapter.getLanguages(),
                                    mIsLanguageSelected,
                                    new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
               if (languageType.equals("myLanguage")) {
                   if (isChecked) {
                       mSelectedLanguages.add(which);
                   } else {
                       mSelectedLanguages.remove(Integer.valueOf(which));
                   }
               } else if (languageType.equals("interest")) {
                   if (isChecked) {
                       mInterestLanguages.add(which);
                   } else {
                       mInterestLanguages.remove(Integer.valueOf(which));
                   }
               }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (languageType.equals("myLanguage")) {
                    Collections.sort(mSelectedLanguages);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < mSelectedLanguages.size(); j++) {
                        String language = mLanAdapter.getLanguageName(mSelectedLanguages.get(j));
                        stringBuilder.append(language);

                        if (j != mSelectedLanguages.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    mTvFirstLanguage.setText(stringBuilder.toString());
                } else if (languageType.equals("interest")) {
                    Collections.sort(mInterestLanguages);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < mInterestLanguages.size(); j++) {
                        String language = mLanAdapter.getLanguageName(mInterestLanguages.get(j));
                        stringBuilder.append(language);

                        if (j != mInterestLanguages.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    mTvInterests.setText(stringBuilder.toString());
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int j = 0; j < mIsLanguageSelected.length; j++) {
                    mIsLanguageSelected[j] = false;
                    if (languageType.equals("myLanguage")) {
                        mSelectedLanguages.clear();
                        mTvFirstLanguage.setText("");
                    } else if (languageType.equals("interest")) {
                        mInterestLanguages.clear();
                        mTvInterests.setText("");
                    }
                }
            }
        });

        builder.show();
    }

    protected void goMainActivity() {
        Intent toMainActivity = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(toMainActivity);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}