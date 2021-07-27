package duce.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Messages")
public class Messages extends ParseObject {

    public static final String TAG = "Messages";

    public static final String CHATS_ID = "chatsId";
    public static final String OWNER_USER = "ownerUser";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String DESCRIPTION = "description";
    public static final String LAST_MESSAGE = "lastMessage";
    public static final String CREATED_AT = "createdAt";
    public static final String LANGUAGE = "language";

    public Chats getChatsId() {
            return (Chats) getParseObject(CHATS_ID);
    }

    public void setChatsId(Chats chat) {
        put(CHATS_ID, chat);
    }

    public ParseUser getOwnerUser() {
        return getParseUser(OWNER_USER);
    }

    public void setOwnerUser(ParseUser ownerUser) {
        put(OWNER_USER, ownerUser);
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

    public Languages getLanguage() {
        return (Languages) getParseObject(LANGUAGE);
    }

    public void setLanguage(Languages language) {
        put(LANGUAGE, language);
    }

    // Returns how much time ago was a given Date
    public static String calculateTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
