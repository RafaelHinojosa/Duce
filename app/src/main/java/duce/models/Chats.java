package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Chats")
public class Chats extends ParseObject {

    public static final String TAG = "Chats";

    public static final String CHAT_NAME = "conversationName";

    public void setChatName(String name) {
        put(CHAT_NAME, name);
    }

    public String getChatName() {
        return getString(CHAT_NAME);
    }

}
