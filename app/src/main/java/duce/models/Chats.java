package duce.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Chats")
public class Chats extends ParseObject {

    public static final String TAG = "Chats";

    public static final String USER_ONE = "userOne";
    public static final String USER_TWO = "userTwo";
    public static final String LANGUAGE = "language";

    public ParseUser getUserOne() {
        return getParseUser(USER_ONE);
    }

    public void setUserOne(ParseUser userOne) {
        put(USER_ONE, userOne);
    }

    public ParseUser getUserTwo() {
        return getParseUser(USER_TWO);
    }

    public void setUserTwo(ParseUser userTwo) {
        put(USER_TWO, userTwo);
    }

    public Languages getLanguage() {
        return (Languages) getParseObject(LANGUAGE);
    }

    public void setLanguage(Languages languageId) {
        put(LANGUAGE, languageId);
    }
}
