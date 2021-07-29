package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.annotation.Annotation;
import java.util.Date;

public class CustomUser {

    public static final String TAG = "CustomUser";

    private ParseUser mParseUser;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROFILE_PICTURE = "profilePicture";
    public static final String SELF_DESCRIPTION = "selfDescription";
    public static final String COUNTRY_ID = "countryId";
    public static final String BIRTHDATE = "birthdate";
    public static final String LAST_CONNECTION = "lastConnection";
    public static final String ONLINE = "online";

    public CustomUser() {
        this.mParseUser = new ParseUser();
    }

    public CustomUser(ParseUser user) {
        this.mParseUser = user;
    }

    public void setCustomUser(ParseUser user) {
        this.mParseUser = user;
    }

    public ParseUser getCustomUser() { return mParseUser; }

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

    public Date getBirthdate() {
        return (Date) mParseUser.getDate(BIRTHDATE);
    }

    public void setBirthdate(Date birthdate) {
        mParseUser.put(BIRTHDATE, birthdate);
    }

    public void setLastConnection(Date lastConnection) {
        mParseUser.put(LAST_CONNECTION, lastConnection);
    }

    public Date getLastConnection() {
        return (Date) mParseUser.getDate(LAST_CONNECTION);
    }

    public void setOnline(boolean isOnline) {
        mParseUser.put(ONLINE, isOnline);
    }

    public boolean getOnline() {
        return (boolean) mParseUser.getBoolean(ONLINE);
    }
}
