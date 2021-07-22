package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.annotation.Annotation;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String TAG = "User";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String SELF_DESCRIPTION = "self_description";
    public static final String COUNTRY_ID = "country_id";


    public String getUsername() {
        return getString(USERNAME);
    }

    public void setUsername(String username) {
        put(USERNAME, username);
    }

    public void setPassword(String password) {
        put(PASSWORD, password);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile profilePicture) {
        put(PROFILE_PICTURE, profilePicture);
    }

    public String getSelfDescription() {
        return getString(SELF_DESCRIPTION);
    }

    public void setSelfDescription(String selfDescription) {
        put(SELF_DESCRIPTION, selfDescription);
    }

    public Countries getCountryId() {
        return (Countries) getParseObject(COUNTRY_ID);
    }

    public void setCountryId(String countryId) {
        put(COUNTRY_ID, countryId);
    }
}
