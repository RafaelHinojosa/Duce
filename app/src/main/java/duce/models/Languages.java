package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Languages")
public class Languages extends ParseObject {

    public static final String TAG = "Languages";

    public static final String KEY_LANGUAGE = "language_id";
    public static final String LANGUAGE_NAME = "language_name";

    public Languages() {
        super();
    }

    public String getKeyLanguage() {
        return getString(KEY_LANGUAGE);
    }

    public void setKeyLanguage(String keyLanguage) {
        put(KEY_LANGUAGE, keyLanguage);
    }

    public String getLanguageName() {
        return getString(LANGUAGE_NAME);
    }

    public void setLanguageName(String languageName) {
        put(LANGUAGE_NAME, languageName);
    }
}