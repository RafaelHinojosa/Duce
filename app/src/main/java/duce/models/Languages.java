package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Languages")
public class Languages extends ParseObject {

    public static final String TAG = "Languages";

    public static final String LANGUAGE_NAME = "language_name";

    public String getLanguageName() {
        return getString(LANGUAGE_NAME);
    }

    public void setLanguageName(String languageName) {
        put(LANGUAGE_NAME, languageName);
    }
}