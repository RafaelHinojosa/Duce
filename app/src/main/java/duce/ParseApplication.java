package duce;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import duce.models.Chats;
import duce.models.Countries;
import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.Messages;
import duce.models.UserLanguages;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Languages.class);
        ParseObject.registerSubclass(UserLanguages.class);
        ParseObject.registerSubclass(Chats.class);
        ParseObject.registerSubclass(Messages.class);
        ParseObject.registerSubclass(Countries.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("WHjW7CrNBYYWkB2qv2nAnIdsdfoR3YkFrhjOVcNB")
                .clientKey("ahJ8pSvBBBsTPmHmY3xiS5qp359oOplmbDt2U8dY")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
