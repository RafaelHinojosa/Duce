package duce.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Countries")
public class Countries extends ParseObject {

    public static final String TAG = "Countries";

    public static final String COUNTRY_NAME = "country_name";
    public static final String FLAG = "flag_emoji_code";

    public String getCountryName() {
        return getString(COUNTRY_NAME);
    }

    public void setCountryName(String countryName) {
        put(COUNTRY_NAME, countryName);
    }

    public String getFlag() {
        return getString(FLAG);
    }

    public void setFlag(String flag) {
        put(FLAG, flag);
    }
}
