package duce.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User_Languages")
public class UserLanguages extends ParseObject {

    public static final String TAG = "UserLanguages";

    public static final String KEY_USER = "user_id";
    public static final String KEY_LANGUAGE = "language_id";
    public static final String PROFICIENCY = "proficiency";
    public static final String INTERESTED_IN = "interested_in";

    public UserLanguages() {
        super();
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getLanguage() {
        return getString(KEY_LANGUAGE);
    }

    public void setLanguage(String language) {
        put(KEY_LANGUAGE, language);
    }

    public void setLanguageObject(Languages languageObject) {
        put(KEY_LANGUAGE, languageObject);
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
