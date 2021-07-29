package duce.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MatchingUser {

    public static final String TAG = "MatchingUser";

    private int mScore;
    private int mAge;
    // Time since last connection
    private Calendar mLastConnection;
    private List<Languages> mProficientLanguages;
    private List<Languages> mInterests;
    private List<Languages> mCommonLanguages;
    private CustomUser mUser;

    public MatchingUser(ParseUser user) {
        mUser = new CustomUser(user);
        initializeVariables();
        setLanguages();
    }

    public void initializeVariables() {
        mScore = 0;
        mAge = 0;
        // Calendar
        mProficientLanguages = new ArrayList<>();
        mInterests = new ArrayList<>();
        mCommonLanguages = new ArrayList<>();
    }

    public CustomUser getUser() {
        return mUser;
    }

    public void setUser(CustomUser user) {
        mUser = user;
    }

    public int getScore() {
        return mScore;
    }

    public void updateScore(int points) {
        mScore += points;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge() {
        Date birthdate = mUser.getBirthdate();
        if (birthdate == null) {
            mAge = -1;
            return;
        }

        int year = birthdate.getYear() + 1900;
        int month = birthdate.getMonth() + 1;
        int day = birthdate.getDate();

        Calendar calBirthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        calBirthdate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - calBirthdate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < calBirthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            mAge = -1;
            return;
        }

        Integer ageNumber = new Integer(age);
        mAge = ageNumber;
    }

    // Last connection

    public void setLanguages() {
        mProficientLanguages.clear();
        mInterests.clear();

        ParseQuery<UserLanguages> proficientQuery = ParseQuery.getQuery("UserLanguages");
        proficientQuery.include("languageId");
        proficientQuery.whereEqualTo("userId", mUser.getCustomUser());

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
                        mProficientLanguages.add(language);
                    }

                    if (userLanguage.getInterestedIn()) {
                        Languages language = userLanguage.getLanguage();
                        mInterests.add(language);
                    }
                }
            }
        });
    }

    public List<Languages> getProficientLanguages() {
        return mProficientLanguages;
    }

    public List<Languages> getInterests() {
        return mInterests;
    }

    // Checks the common languages between the interests of a user and the proficiency of the other one
    public void setCommonLanguages(List<Languages> userInterests) {
        for (Languages userInterest : userInterests) {
            String languageName = userInterest.getLanguageName();
            for (Languages proficientLanguage : mProficientLanguages) {
                if (languageName.equals(proficientLanguage.getLanguageName())) {
                    Log.i(TAG, "Common Language Found: " + languageName);
                    mCommonLanguages.add(userInterest);
                    break;
                }
            }
        }
    }

    public List<Languages> getCommonLanguages() {
        return mCommonLanguages;
    }

    public int getCommonLanNumber() {
        return mCommonLanguages.size();
    }
}
