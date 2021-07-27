package duce.models;

import com.google.cloud.translate.Language;
import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Languages")
public class Languages extends ParseObject {

    public static final String TAG = "Languages";

    public static final String OBJECT_ID = "objectId";
    public static final String LANGUAGE_NAME = "languageName";
    public static final String TRANSLATE_CODE = "translateCode";

    public String getLanguageName() {
        return getString(LANGUAGE_NAME);
    }

    public void setLanguageName(String languageName) {
        put(LANGUAGE_NAME, languageName);
    }

    public String getTranslateCode() {
        return getString(TRANSLATE_CODE);
    }

    public void setTranslateCode(String languageName) {
        put(TRANSLATE_CODE, languageName);
    }

}