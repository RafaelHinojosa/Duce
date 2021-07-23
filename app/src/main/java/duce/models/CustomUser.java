package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.annotation.Annotation;

public class CustomUser {

    public static final String TAG = "CustomUser";

    private ParseUser mParseUser;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String SELF_DESCRIPTION = "selfDescription";
    public static final String COUNTRY_ID = "countryId";

    public CustomUser(ParseUser user) {
        mParseUser = user;
    }

    public String getObjectId() {
        return mParseUser.getObjectId();
    }

    public String getUsername() {
        return mParseUser.getUsername();
    }

    public void setUsername(String username) {
        mParseUser.put(USERNAME, username);
    }

    public void setPassword(String password) {
        mParseUser.put(PASSWORD, password);
    }

    public ParseFile getProfilePicture() {
        return mParseUser.getParseFile(PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile profilePicture) {
        mParseUser.put(PROFILE_PICTURE, profilePicture);
    }

    public String getSelfDescription() {
        return mParseUser.getString(SELF_DESCRIPTION);
    }

    public void setSelfDescription(String selfDescription) {
        mParseUser.put(SELF_DESCRIPTION, selfDescription);
    }

    public Countries getCountryId() {
        return (Countries) mParseUser.getParseObject(COUNTRY_ID);
    }

    public void setCountryId(String countryId) {
        mParseUser.put(COUNTRY_ID, countryId);
    }
}
