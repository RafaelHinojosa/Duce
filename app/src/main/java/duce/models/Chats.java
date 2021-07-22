package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chats")
public class Chats extends ParseObject {

    public static final String TAG = "Chats";

    public static final String USER_ONE = "user_one";
    public static final String USER_TWO = "user_two";

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
}
