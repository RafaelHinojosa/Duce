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
    private String lastConnection;
    private List<Languages> mProficientLanguages;
    private List<Languages> mInterests;
    private List<Languages> mCommonLanguages;
    private List<Languages> mCrossedLanguages;
    private CustomUser mUser;

    public MatchingUser(ParseUser user) {
        this.mUser = new CustomUser(user);
        initializeVariables();
    }

    public void initializeVariables() {
        this.mScore = 0;
        setAge();
        lastConnection = "";
        this.mProficientLanguages = new ArrayList<>();
        this.mInterests = new ArrayList<>();
        this.mCommonLanguages = new ArrayList<>();
        this.mCrossedLanguages = new ArrayList<>();
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

    private void setAge() {
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

    public void setLastConnection() {
        if (mUser.getOnline()) {
            lastConnection = "Online";
        } else {
            lastConnection = calculateTimeAgo(mUser.getLastConnection());
        }
    }

    public String getLastConnection() {
        return lastConnection;
    }

    // Get & Save the proficient and interest languages of the user
    public void setLanguages() {
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
                    Log.i(TAG, "Si es 0");
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
        //Log.i(TAG, "Common languages between " + mUser.getUsername() + " and " + ParseUser.getCurrentUser().getUsername());
        for (Languages userInterest : userInterests) {
            String languageName = userInterest.getLanguageName();
            //Log.i(TAG, languageName + " pairing");
            //Log.i(TAG, String.valueOf(mProficientLanguages.size()));
            for (Languages proficientLanguage : mProficientLanguages) {
                //Log.i(TAG, proficientLanguage.getLanguageName());
                if (languageName.equals(proficientLanguage.getLanguageName())) {
                    //Log.i(TAG, "Common Language Found: " + languageName);
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

    // Checks the common languages between the interests of a user and the proficiency of the other one
    public void setCrossedLanguages(List<Languages> userLanguages) {
        Log.i(TAG, "Common languages between " + mUser.getUsername() + " and " + ParseUser.getCurrentUser().getUsername());
        for (Languages userLanguage : userLanguages) {
            String languageName = userLanguage.getLanguageName();
            Log.i(TAG, languageName + " pairing");
            Log.i(TAG, String.valueOf(mProficientLanguages.size()));
            for (Languages interest : mInterests) {
                Log.i(TAG, interest.getLanguageName());
                if (languageName.equals(interest.getLanguageName())) {
                    Log.i(TAG, "Common Language Found: " + languageName);
                    mCrossedLanguages.add(userLanguage);
                    break;
                }
            }
        }
    }

    public List<Languages> getCrossedLanguages() {
        return mCrossedLanguages;
    }

    public int getCrossedLanNumber() {
        return mCrossedLanguages.size();
    }

    public void addLanguage(Languages language) {
        mProficientLanguages.add(language);
    }

    public void addInterest(Languages language) {
        mInterests.add(language);
    }

    public static String calculateTimeAgo(Date lastConnection) {
        if (lastConnection == null) {
            return "Disconnected";
        }

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            long time = lastConnection.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "Disconnected";
    }
}
