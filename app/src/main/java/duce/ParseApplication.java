package duce;

import android.app.Application;

import com.parse.BuildConfig;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("PEmnelg3TPFPH0JCtuwAYt7xIs48Jgr7tLxrBY4N")
                .clientKey("2tZPBPm2p3akpABkUB2ZBrFuYnOSFlX2W9qp8ivx")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
