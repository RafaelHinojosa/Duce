package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserChats")
public class UserChats extends ParseObject {

    public static final String TAG = "UserChats";

    public static final String CHATS_ID = "chatsId";
    public static final String USER_ID = "userId";
    public static final String OTHER_USER_ID =  "otherUserId";
    public static final String LANGUAGE_ID = "languageId";

    public Chats getChat() {
        return (Chats) getParseObject(CHATS_ID);
    }

    public void setChat(Chats chat) {
        put(CHATS_ID, chat);
    }

    public ParseUser getUser() {
        return getParseUser(USER_ID);
    }

    public void setUser(ParseUser user) {
        put(USER_ID, user);
    }

    public ParseUser getOtherUser() {
        return getParseUser(OTHER_USER_ID);
    }

    public void setOtherUser(ParseUser otherUser) {
        put(OTHER_USER_ID, otherUser);
    }

    public Languages getLanguage() {
        return (Languages) getParseObject(LANGUAGE_ID);
    }

    public void setLanguage(Languages languageId) {
        put(LANGUAGE_ID, languageId);
    }
}
