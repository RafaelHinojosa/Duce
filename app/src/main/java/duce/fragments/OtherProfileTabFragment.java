package duce.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.google.android.flexbox.FlexboxLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import duce.StartActivity;
import duce.adapters.LanguagesAdapter;
import duce.models.CustomUser;
import duce.models.Friends;
import duce.models.UserLanguages;

import static com.duce.R.layout.other_profile_tab_fragment;

public class OtherProfileTabFragment extends Fragment {

    public static final String TAG = "OtherProfileTabFragment";

    private ImageView mIvProfilePicture;
    private TextView mTvUsername;
    private TextView mTvAge;
    private TextView mTvSelfDescription;
    private Button mBtnFriends;
    private Button mBtnAddFriend;
    private Button mBtnCancelRequest;
    private RelativeLayout mRlActions;
    private Button mBtnAcceptRequest;
    private Button mBtnRejectRequest;
    private com.google.android.flexbox.FlexboxLayout mFlMyLanguages;
    private com.google.android.flexbox.FlexboxLayout mFlMyInterests;
    private CustomUser mUser;
    private Friends mFriendship;
    private LanguagesAdapter mLanAdapter;



    public static OtherProfileTabFragment newInstance() {
        OtherProfileTabFragment fragment = new OtherProfileTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(other_profile_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receives the user to display
        Bundle bundle = this.getArguments();
        assert bundle != null;
        mUser = new CustomUser(Parcels.unwrap(bundle.getParcelable("user")));

        mLanAdapter = new LanguagesAdapter();

        mIvProfilePicture = view.findViewById(R.id.ivProfilePicture);
        mTvUsername = view.findViewById(R.id.tvUsername);
        mTvAge = view.findViewById(R.id.tvAge);
        mTvSelfDescription = view.findViewById(R.id.tvDescription);
        mBtnFriends = view.findViewById(R.id.btnFriends);
        mBtnAddFriend = view.findViewById(R.id.btnAddFriend);
        mBtnCancelRequest = view.findViewById(R.id.btnCancelRequest);
        mRlActions = view.findViewById(R.id.rlActions);
        mBtnAcceptRequest = view.findViewById(R.id.btnAcceptRequest);
        mBtnRejectRequest = view.findViewById(R.id.btnRejectRequest);

        mFlMyLanguages = view.findViewById(R.id.flMyLanguages);
        mFlMyInterests = view.findViewById(R.id.flMyInterests);

        getFriendship();
        bind();
    }

    private void getFriendship() {
        ParseQuery<Friends> friendshipQuery = getFriendsQuery();
        friendshipQuery.setLimit(1);

        friendshipQuery.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> friendship, ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                    return;
                }

                if (friendship.size() > 0) {
                    mFriendship = friendship.get(0);
                }

                if (friendship.size() == 0) {
                    mBtnAddFriend.setVisibility(View.VISIBLE);
                } else if (mFriendship.areFriends()) {
                    mBtnFriends.setVisibility(View.VISIBLE);
                } else if (mFriendship.getUserOne().getObjectId()
                           .equals(ParseUser.getCurrentUser().getObjectId())) {
                    mBtnCancelRequest.setVisibility(View.VISIBLE);
                } else {
                    mRlActions.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bind() {
        Glide.with(getContext())
                .load(mUser.getProfilePicture().getUrl())
                .centerCrop()
                .transform(new CircleCrop())
                .into(mIvProfilePicture);

        mTvUsername.setText(mUser.getUsername());

        // Set age
        String age = "";
        Date birthdate = mUser.getBirthdate();
        if (birthdate != null) {
            int year = birthdate.getYear() + 1900;
            int month = birthdate.getMonth() + 1;
            int day = birthdate.getDate();
            age = getAge(year, month, day);
            if (age != "-1") {
                mTvAge.setText(age);
            }
        }
        if (age.equals("-1") || birthdate == null) {
            mTvAge.setVisibility(View.GONE);
        }

        mTvSelfDescription.setText(mUser.getSelfDescription());

        mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendship = new Friends();
                mFriendship.setUserOne(ParseUser.getCurrentUser());
                mFriendship.setUserTwo(mUser.getCustomUser());
                mFriendship.setAreFriends(false);
                mFriendship.saveInBackground();
                mBtnAddFriend.setVisibility(View.INVISIBLE);
                mBtnCancelRequest.setVisibility(View.VISIBLE);
            }
        });

        mBtnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendship.deleteInBackground();
                mBtnCancelRequest.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setVisibility(View.VISIBLE);
            }
        });

        mBtnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendship.setAreFriends(true);
                mFriendship.saveInBackground();
                mRlActions.setVisibility(View.INVISIBLE);
                mBtnFriends.setVisibility(View.VISIBLE);
            }
        });

        mBtnRejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendship.deleteInBackground();
                mRlActions.setVisibility(View.INVISIBLE);
                mBtnAddFriend.setVisibility(View.VISIBLE);
            }
        });

        mBtnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_friend)
                    .setMessage(R.string.delete_friendship_confirmation)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mFriendship.deleteInBackground();
                            mBtnFriends.setVisibility(View.INVISIBLE);
                            mBtnAddFriend.setVisibility(View.VISIBLE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
            }
        });

        setLanguages();
    }

    public ParseQuery<Friends> getFriendsQuery() {
        ParseQuery<Friends> userOne = ParseQuery.getQuery("Friends");
        userOne.whereEqualTo(Friends.USER_ONE, ParseUser.getCurrentUser());
        userOne.whereEqualTo(Friends.USER_TWO, mUser.getCustomUser());

        ParseQuery<Friends> userTwo = ParseQuery.getQuery("Friends");
        userTwo.whereEqualTo(Friends.USER_ONE, mUser.getCustomUser());
        userTwo.whereEqualTo(Friends.USER_TWO, ParseUser.getCurrentUser());

        List<ParseQuery<Friends>> queries = new ArrayList<ParseQuery<Friends>>();
        queries.add(userOne);
        queries.add(userTwo);

        ParseQuery<Friends> mainQuery = ParseQuery.or(queries);

        return mainQuery;
    }

    public String getAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birthdate.set(year, month, day);

        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        if (age < 0) {
            return "Invalid birthdate";
        }

        Integer ageNumber = new Integer(age);
        String ageString = ageNumber.toString() + " years old";
        return ageString;
    }


    public void setLanguages() {
        mFlMyLanguages.removeAllViews();
        mFlMyInterests.removeAllViews();

        ParseQuery<UserLanguages> userLanguagesQuery = ParseQuery.getQuery("UserLanguages");
        userLanguagesQuery.whereEqualTo("userId", mUser.getCustomUser());
        userLanguagesQuery.include("languageId");
        userLanguagesQuery.addDescendingOrder("createdAt");

        userLanguagesQuery.findInBackground(new FindCallback<UserLanguages>() {
            @Override
            public void done(List<UserLanguages> userLanguages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                for (UserLanguages userLanguage : userLanguages) {
                    Log.i(TAG, userLanguage.toString());
                    TextView language = setLanguageTV(userLanguage.getLanguage().getLanguageName());
                    TextView languageCopy = setLanguageTV(userLanguage.getLanguage().getLanguageName());

                    if (userLanguage.getMyLanguage()) {
                        mFlMyLanguages.addView(language);
                    }

                    if (userLanguage.getInterestedIn()) {
                        mFlMyInterests.addView(languageCopy);
                    }
                }
            }
        });
    }

    public TextView setLanguageTV(String languageName) {
        TextView textView = new TextView(getContext());
        textView.setText(languageName);
        textView.setIncludeFontPadding(true);
        textView.setPadding(20,10,20,10);
        textView.setCompoundDrawablePadding(5);
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.language_box));
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);

        return textView;
    }
}
