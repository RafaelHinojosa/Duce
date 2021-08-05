package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Friends")
public class Friends extends ParseObject {

    public static final String TAG = "Friends";

    public static final String USER_ONE = "userOne";
    public static final String USER_TWO = "userTwo";
    public static final String ARE_FRIENDS = "areFriends";

    public ParseUser getUserOne() {
        return (ParseUser) getParseUser(USER_ONE);
    }

    public void setUserOne(ParseUser userOne) {
        put(USER_ONE, userOne);
    }

    public ParseUser getUserTwo() {
        return (ParseUser) getParseUser(USER_TWO);
    }

    public void setUserTwo(ParseUser userTwo) {
        put(USER_TWO, userTwo);
    }

    public boolean areFriends() {
        return (boolean) getBoolean(ARE_FRIENDS);
    }

    public void setAreFriends(boolean areFriends) {
        put(ARE_FRIENDS, areFriends);
    }
}
