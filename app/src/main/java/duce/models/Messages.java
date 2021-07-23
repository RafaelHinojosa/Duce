package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Messages")
public class Messages extends ParseObject {

    public static final String TAG = "Messages";

    public static final String CHATS_ID = "chatsId";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String DESCRIPTION = "description";
    public static final String LAST_MESSAGE = "lastMessage";
    public static final String CREATED_AT = "createdAt";

    public Chats getChatsId() {
        return (Chats) getParseObject(CHATS_ID);
    }

    public void setChatsId(Chats chat) {
        put(CHATS_ID, chat);
    }

    public ParseUser getSender() {
        return getParseUser(SENDER);
    }

    public void setSender(ParseUser sender) {
        put(SENDER, sender);
    }

    public ParseUser getReceiver() {
        return getParseUser(RECEIVER);
    }

    public void setReceiver(ParseUser receiver) {
        put(RECEIVER, receiver);
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
