package duce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.duce.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import duce.adapters.MatchAlgorithmAdapter;
import duce.models.Chats;
import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.MatchingUser;
import duce.models.Messages;
import duce.models.UserChats;
import duce.models.UserLanguages;

public class MatchAlgorithm extends AppCompatActivity {

    public static final String TAG = "MatchAlgorithm";

    private List<Languages> mMyLanguages;
    private List<Languages> mMyInterests;
    private List<MatchingUser> mMatchingUsers;
    private List<ParseUser> mUsers;
    private CustomUser mUser;
    private final int[] mMinRange = {9, 10, 15, 20, 15};
    private final int[] mMaxRange = {9, 15, 20, 15, 10};
    private final int[] mAgeRange = {16, 25, 40, 60, 75};
    private CardStackLayoutManager mCardManager;
    private MatchAlgorithmAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_algorithm_activity);

        mMyLanguages = new ArrayList<>();
        mMyInterests = new ArrayList<>();
        mMatchingUsers = new ArrayList<>();
        mUser = new CustomUser(ParseUser.getCurrentUser());
        mUsers = new ArrayList<>();
        mAdapter = new MatchAlgorithmAdapter(mMatchingUsers, MatchAlgorithm.this);

        CardStackView csCardStackView = findViewById(R.id.csCardStackView);
        setCardStack();
        csCardStackView.setLayoutManager(mCardManager);
        csCardStackView.setAdapter(mAdapter);
        csCardStackView.setItemAnimator(new DefaultItemAnimator());

        setLanguages();
        getUserLanguages();
    }

    private void setCardStack() {
        mCardManager = new CardStackLayoutManager(this, new CardStackListener() {

            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                int position = mCardManager.getTopPosition() - 1;
                Log.d(TAG, "onCardSwiped: p=" + mCardManager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Top) {
                    setUpConversation(position);
                    Toast.makeText(MatchAlgorithm.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
                    // TODO: go to profile
                    Toast.makeText(MatchAlgorithm.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });

        mCardManager.setStackFrom(StackFrom.None);
        mCardManager.setVisibleCount(3);            // Number of visible cards at the same time
        mCardManager.setTranslationInterval(8.0f); // Time for the next card to appear
        mCardManager.setScaleInterval(0.8f);      // Animates the next card to grow when a card is swiped
        mCardManager.setSwipeThreshold(0.3f);   // How much distance to swipe the card
        mCardManager.setMaxDegree(30.0f);       // Max degree of inclination of a card when it is swiped
        mCardManager.setDirections(Direction.FREEDOM);
        mCardManager.setCanScrollHorizontal(true);
        mCardManager.setSwipeableMethod(SwipeableMethod.Manual);
        mCardManager.setOverlayInterpolator(new LinearInterpolator());
    }

    // Saves the current user languages and interests
    public void setLanguages() {
        mMyLanguages.clear();
        mMyInterests.clear();

        ParseQuery<UserLanguages> proficientQuery = ParseQuery.getQuery("UserLanguages");
        proficientQuery.include("languageId");
        proficientQuery.whereEqualTo("userId", ParseUser.getCurrentUser());

        proficientQuery.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                if (userLanguages.size() == 0) {
                    return;
                }

                for (UserLanguages userLanguage : userLanguages) {
                    if (userLanguage.getMyLanguage()) {
                        Languages language = userLanguage.getLanguage();
                        mMyLanguages.add(language);
                    }

                    if (userLanguage.getInterestedIn()) {
                        Languages language = userLanguage.getLanguage();
                        mMyInterests.add(language);
                    }
                }
            }
        });
    }

    public void getUserLanguages() {
        ParseQuery<UserLanguages> userLanguagesQuery = ParseQuery.getQuery("UserLanguages");
        userLanguagesQuery.whereNotEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        userLanguagesQuery.addAscendingOrder("userId");
        userLanguagesQuery.include("userId");
        userLanguagesQuery.include("languageId");

        userLanguagesQuery.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                if (userLanguages.size() == 0) {
                    return;
                }

                // Get & Save the languages and interests of all users that have a language registered
                MatchingUser matchingUser = new MatchingUser(userLanguages.get(0).getUser());
                String userId = userLanguages.get(0).getUser().getObjectId();
                mMatchingUsers.add(matchingUser);
                int userIndex = 0;
                //Log.i(TAG, userId);
                for (int i = 1; i < userLanguages.size(); i++) {
                    //Log.i(TAG, userLanguages.get(i).getUser().getUsername());
                    if (userLanguages.get(i).getUser().getObjectId().equals(userId)) {
                        if (userLanguages.get(i).getMyLanguage()) {
                            Languages language = userLanguages.get(i).getLanguage();
                            mMatchingUsers.get(userIndex).addLanguage(language);
                        }

                        if (userLanguages.get(i).getInterestedIn()) {
                            Languages language = userLanguages.get(i).getLanguage();
                            mMatchingUsers.get(userIndex).addInterest(language);
                        }
                    } else {
                        matchingUser = new MatchingUser(userLanguages.get(i).getUser());
                        userId = matchingUser.getUser().getObjectId();
                        mMatchingUsers.add(matchingUser);
                        userIndex++;
                        i--;
                    }
                }

                // Set common languages between my interests and their known languages
                for (MatchingUser user : mMatchingUsers) {
                    user.setCommonLanguages(mMyInterests);
                    int common = user.getCommonLanNumber();

                    //Log.i(TAG, "Common Languages between: " + ParseUser.getCurrentUser().getUsername() + " and " + user.getUser().getUsername());

                    List<Languages> commonLanguages = user.getCommonLanguages();
                    user.updateScore(8 * common);
                    String commonLangS = "";
                    for (Languages language : commonLanguages) {
                        commonLangS += language.getLanguageName();
                    }
                    //Log.i(TAG, "Languages: " + commonLangS);
                }

                // Set common languages between my languages and their interests
                for (MatchingUser user : mMatchingUsers) {
                    user.setCrossedLanguages(mMyLanguages);
                    int common = user.getCrossedLanNumber();

                    //Log.i(TAG, "Common Languages between: " + ParseUser.getCurrentUser().getUsername() + " and " + user.getUser().getUsername());

                    List<Languages> crossedLanguages = user.getCrossedLanguages();
                    user.updateScore(5 * common);
                    String crossedLangS = "";
                    for (Languages language : crossedLanguages) {
                        crossedLangS += language.getLanguageName();
                    }
                    //Log.i(TAG, "Languages: " + crossedLangS);
                }

                // Set points for the age
                int myAge = setAge(mUser.getBirthdate());
                // Do only if myAge is > 15
                int myAgePos = 0;
                for (int i = 0; i < mAgeRange.length - 1; i++) {
                    if (mAgeRange[i] <= myAge && myAge < mAgeRange[i+1]) {
                        myAgePos = i;
                        break;
                    } else if (i == mAgeRange.length - 1) {
                        myAgePos = i + 1;
                    }
                }

                // Ranges the other user should be inside
                int myMin = myAge - mMinRange[myAgePos];
                int myMax = myAge + mMaxRange[myAgePos];
                if (myMin < 15) {
                    myMin = 15;
                }

                // See if other user and my user are in the same age range
                for (MatchingUser user : mMatchingUsers) {
                    int userAge = user.getAge();
                    int userAgePos = getAgeIndex(userAge);
                    int userMin = userAge - mMinRange[userAgePos];
                    int userMax = userAge + mMaxRange[userAgePos];
                    if (userMin < 15) {
                        userMin = 15;
                    }

                    if (userMin <= myAge && myAge <= userMax && myMin <= userAge && userAge <= myMax) {
                        //Log.i(TAG, "Ages are in range! " + mUser.getUsername() + " " + myAge +  "   " + user.getUser().getUsername() + " " + userAge);
                        user.updateScore(3);
                        //Log.i(TAG, "Score: " + String.valueOf(user.getScore()));
                    } // else {
                        // Log.i(TAG, mUser.getUsername() + " " + myAge + " and " + user.getUser().getUsername() + " " +  userAge + " are not in the same range");
                    // }
                }

                // Set points for recently connected people
                for (MatchingUser user : mMatchingUsers) {
                    user.setLastConnection();
                    String lastConnection = user.getLastConnection();
                    if (!(lastConnection.equals("yesterday") || lastConnection.charAt(lastConnection.length()-1) == 'd')) {
                        user.updateScore(2);
                    }
                    //Log.i(TAG, lastConnection);
                }

                // Order the users by their points
                Collections.sort(mMatchingUsers, new ScoreComparator());
                Collections.reverse(mMatchingUsers);
                for (MatchingUser user : mMatchingUsers) {
                    String userResume = "\n";
                    userResume += user.getUser().getUsername() + "\n";

                    // Languages the user know and you want to learn
                    List<Languages> commonLanguages = user.getCommonLanguages();
                    String commonLangS = "";
                    for (Languages language : commonLanguages) {
                        commonLangS += language.getLanguageName() + ", ";
                    }
                    userResume += user.getUser().getUsername() + " knows these languages you want to learn: " + commonLangS + "\n";

                    // Langauges the user wants to learn and you know
                    List<Languages> crossedLanguages = user.getCrossedLanguages();
                    String crossedLangS = "";
                    for (Languages language : crossedLanguages) {
                        crossedLangS += language.getLanguageName() + ", ";
                    }
                    userResume += "You know these languages " + user.getUser().getUsername() + " wants to learn: " + crossedLangS + "\n";

                    userResume += "AGES: Your age: " + setAge(mUser.getBirthdate()) + "; " + user.getUser().getUsername() + " age: " + user.getAge() + "\n";;
                    userResume += "Last Connection: " + user.getLastConnection() + "\n";
                    userResume += "POINTS: " + user.getScore() + " points\n";

                    Log.i(TAG, userResume);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private int setAge(Date birthdate) {
        if (birthdate == null) {
            return -1;
        }

        int year = birthdate.getYear() + 1900;
        int month = birthdate.getMonth() + 1;
        int day = birthdate.getDate();

        Calendar calBirthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        calBirthdate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - calBirthdate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < calBirthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            return -1;
        }

        Integer ageNumber = new Integer(age);
        return ageNumber;
    }

    private int getAgeIndex(int age) {
        int agePos = 0;
        for (int i = 0; i < mAgeRange.length - 1; i++) {
            if (mAgeRange[i] <= age && age < mAgeRange[i+1]) {
                return agePos;
            } else if (i == mAgeRange.length - 1) {
                agePos = i + 1;
            }
        }
        return agePos;
    }

    public void setUpConversation(int position) {
        if (position >= 0 && position <= mMatchingUsers.size()) {
            CustomUser user = mMatchingUsers.get(position).getUser();

            ParseQuery<UserChats> userChatsQuery = ParseQuery.getQuery("UserChats");
            userChatsQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
            userChatsQuery.whereEqualTo("otherUserId", user.getCustomUser());

            userChatsQuery.findInBackground(new FindCallback<UserChats>() {
                @Override
                public void done(List<UserChats> userChats, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error: " + e.getMessage());
                        return;
                    }
                    if (userChats.size() == 0) {
                        createChat(user.getCustomUser());
                    } else {
                        UserChats userChat = userChats.get(0);
                        Chats chat = userChat.getChat();
                        goToMessages(chat, ParseUser.getCurrentUser(), user.getCustomUser());
                    }
                }
            });
        }
    }

    public void createChat(ParseUser otherUser) {
        Chats newChat = new Chats();
        newChat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }
                ParseQuery<Chats> lastChat = ParseQuery.getQuery("Chats");
                lastChat.setLimit(1);
                lastChat.addDescendingOrder("createdAt");
                lastChat.findInBackground(new FindCallback<Chats>() {
                    @Override
                    public void done(List<Chats> chats, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error: " + e.getMessage());
                            return;
                        }
                        if (chats.size() > 0) {
                            Chats lastChat = chats.get(0);
                            goToMessages(lastChat, ParseUser.getCurrentUser(), otherUser);

                            // My conversation copy
                            UserChats userChats = new UserChats();
                            userChats.setChat(lastChat);
                            userChats.setUser(ParseUser.getCurrentUser());
                            userChats.setOtherUser(otherUser);
                            userChats.saveInBackground();

                            // The other's profile copy
                            UserChats otherUserCopy = new UserChats();
                            otherUserCopy.setChat(lastChat);
                            otherUserCopy.setUser(otherUser);
                            otherUserCopy.setOtherUser(ParseUser.getCurrentUser());
                            otherUserCopy.saveInBackground();
                        }
                    }
                });
            }
        });
    }

    public void goToMessages(Chats chat, ParseUser sender, ParseUser receiver) {
        Messages message = new Messages();
        message.setChatsId(chat);
        message.setOwnerUser(sender);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setDescription("Hello");

        Intent toMessages = new Intent(MatchAlgorithm.this, ConversationActivity.class);
        toMessages.putExtra("conversation", Parcels.wrap(message));
        startActivity(toMessages);
    }
}