package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Messages")
public class Messages extends ParseObject {

    public static final String TAG = "Messages";

    public static final String USER_ONE = "user_one";
    public static final String USER_TWO = "user_two";
    public static final String CHATS_ID = "chats_id";
    public static final String DESCRIPTION = "description";
    public static final String CREATED_AT = "created_at";

    public Chats getChatsId() {
        return (Chats) getParseObject(CHATS_ID);
    }

    public void setChatsId(Chats chat) {
        put(CHATS_ID, chat);
    }

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

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public Date getCreatedAt() {
        return getDate(CREATED_AT);
    }

    public void setCreatedAt(Date createdAt) {
        put(CREATED_AT, createdAt);
    }
}
