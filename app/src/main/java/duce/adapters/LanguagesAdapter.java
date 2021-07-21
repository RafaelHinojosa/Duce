package duce.adapters;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import duce.models.Languages;

public class LanguagesAdapter {

    public static final String TAG = "LanguagesAdapter";

    private final List<Languages> mLanguages;

    public LanguagesAdapter() {
        super();
        mLanguages = new ArrayList<Languages>();
        setLanguages();
    }

    public CharSequence[] getLanguages() {
        String[] languages = new String[getItemCount()];
        int i = 0;
        for (Languages language : mLanguages) {
            languages[i] = language.getLanguageName();
            i++;
        }
        return languages;
    }

    public void setLanguages() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Languages");
        query.include("object_id");
        query.include("language_name");
        query.addAscendingOrder("language_name");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Could not get the Languages");
                    return;
                }
                for (ParseObject object : objects) {
                    mLanguages.add((Languages) object);
                }
            }
        });
    }

    public Languages getLanguageObject(int index) {
        if (index >= 0 && index < mLanguages.size()) {
            return mLanguages.get(index);
        }
        return null;
    }

    // Used when language name is known
    public String getLanguageId(String language) {
        for (Languages languageObject : mLanguages) {
            if (languageObject.getLanguageName().equals(language)) {
                return languageObject.getObjectId();
            }
        }
        return "-1";
    }

    // Used when index is known
    public String getLanguageId(int index) {
        if (index >= 0 && index < mLanguages.size()) {
            return mLanguages.get(index).getObjectId();
        }
        return "-1";
    }

    public String getLanguageName(int index) {
        if (index >= 0 && index < mLanguages.size()) {
            return mLanguages.get(index).getLanguageName();
        }
        return "-1";
    }

    public int getItemCount() {
        return mLanguages.size();
    }
}
