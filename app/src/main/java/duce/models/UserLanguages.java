package duce.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User_Languages")
public class UserLanguages extends ParseObject {

    public static final String TAG = "UserLanguages";

    public static final String USER_ID = "user_id";
    public static final String LANGUAGE_ID = "language_id";
    public static final String PROFICIENCY = "proficiency";
    public static final String INTERESTED_IN = "interested_in";

    public ParseUser getUser() {
        return getParseUser(USER_ID);
    }

    public void setUser(ParseUser user) {
        put(USER_ID, user);
    }

    public Languages getLanguage() {
        return (Languages) getParseObject(LANGUAGE_ID);
    }

    public void setLanguage(Languages language) {
        put(LANGUAGE_ID, language);
    }

    public int getProficiency() {
        return getInt(PROFICIENCY);
    }

    public void setProficiency(int proficiency) {
        put(PROFICIENCY, proficiency);
    }

    public boolean getInterestedIn() {
        return getBoolean(INTERESTED_IN);
    }

    public void setInterestedIn(boolean interestedIn) {
        put(INTERESTED_IN, interestedIn);
    }
}
