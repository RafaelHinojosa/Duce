package duce;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import duce.models.Chats;
import duce.models.Countries;
import duce.models.Languages;
import duce.models.Messages;
import duce.models.User;
import duce.models.UserLanguages;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Languages.class);
        ParseObject.registerSubclass(UserLanguages.class);
        ParseObject.registerSubclass(Chats.class);
        ParseObject.registerSubclass(Messages.class);
        ParseObject.registerSubclass(Countries.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("PEmnelg3TPFPH0JCtuwAYt7xIs48Jgr7tLxrBY4N")
                .clientKey("2tZPBPm2p3akpABkUB2ZBrFuYnOSFlX2W9qp8ivx")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
