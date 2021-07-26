package duce;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import java.net.URI;
import java.net.URISyntaxException;

import duce.models.Chats;
import duce.models.Countries;
import duce.models.Languages;
import duce.models.Messages;
import duce.models.UserLanguages;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Languages.class);
        ParseObject.registerSubclass(UserLanguages.class);
        ParseObject.registerSubclass(Chats.class);
        ParseObject.registerSubclass(Messages.class);
        ParseObject.registerSubclass(Countries.class);

        // Http library that handles HTTP requests
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("WHjW7CrNBYYWkB2qv2nAnIdsdfoR3YkFrhjOVcNB")
                .clientKey("ahJ8pSvBBBsTPmHmY3xiS5qp359oOplmbDt2U8dY")
                .clientBuilder(builder)
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
