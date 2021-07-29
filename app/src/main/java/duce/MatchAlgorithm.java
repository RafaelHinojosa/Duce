package duce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.duce.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.MatchingUser;
import duce.models.UserLanguages;

public class MatchAlgorithm extends AppCompatActivity {

    public static final String TAG = "MatchAlgorithm";

    private List<Languages> mMyLanguages;
    private List<Languages> mMyInterests;
    private List<MatchingUser> mMatchingUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_algorithm_activity);

        mMyLanguages = new ArrayList<>();
        mMyInterests = new ArrayList<>();
        mMatchingUsers = new ArrayList<>();

        setLanguages();
        getUsers();
    }

    public void setLanguages() {
        mMyLanguages.clear();
        mMyInterests.clear();

        ParseQuery<UserLanguages> proficientQuery = ParseQuery.getQuery("UserLanguages");
        proficientQuery.include("languageId");
        proficientQuery.whereEqualTo("userId", ParseUser.getCurrentUser());

        proficientQuery.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                if (userLanguages.size() == 0) {
                    return;
                }

                for (UserLanguages userLanguage : userLanguages) {
                    if (userLanguage.getMyLanguage()) {
                        Languages language = userLanguage.getLanguage();
                        Log.i(TAG, language.getLanguageName());
                        mMyLanguages.add(language);
                    }

                    if (userLanguage.getInterestedIn()) {
                        Languages language = userLanguage.getLanguage();
                        Log.i(TAG, language.getLanguageName());
                        mMyInterests.add(language);
                    }
                }
            }
        });
    }

    public void getUsers() {
        ParseQuery<ParseUser> usersQuery = ParseUser.getQuery();
        usersQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }
                for (ParseUser user : users) {
                    gradeUser(user);
                }

                mergeUsers();
            }
        });
    }

    public void gradeUser(ParseUser user) {
        MatchingUser matchingUser = new MatchingUser(user);
        // TODO: Compare my interests with matchingUsers' languages
        // TODO: Compare my languages with matchingUsers' interests
        // TODO: Get Age and grade
        // TODO: Last Connection
        // TODO: Save User
    }

    public void mergeUsers() {
    }
}