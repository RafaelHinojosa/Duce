package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duce.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import duce.adapters.FriendsAdapter;
import duce.models.CustomUser;
import duce.models.Friends;
import duce.models.Messages;

import static com.duce.R.layout.my_profile_tab_fragment;

public class FriendsTabFragment extends Fragment {

    public static final String TAG = "FriendsTabFragment";

    private CustomUser mUser;
    private List<CustomUser> mFriendsUsers;
    private List<Friends> mFriends;
    private FriendsAdapter mFriendsAdapter;
    private RecyclerView mRvFriends;
    private TextView mTvFriendsTitle;

    public static FriendsTabFragment newInstance() {
        FriendsTabFragment fragment = new FriendsTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        mUser = new CustomUser(Parcels.unwrap(bundle.getParcelable("friend")));
        mFriendsUsers = new ArrayList<>();
        mFriends = new ArrayList<>();

        mTvFriendsTitle = view.findViewById(R.id.tvFriendsTitle);
        mTvFriendsTitle.setText(R.string.friends_title);
        mRvFriends = view.findViewById(R.id.rvFriends);
        mRvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendsAdapter = new FriendsAdapter(getContext(), mFriendsUsers, mUser);

        mRvFriends.setAdapter(mFriendsAdapter);

        getFriends(0);
    }

    public void getFriends(int skipper) {
        ParseQuery<Friends> friendsQuery = getFriendsQuery();
        friendsQuery.include(Friends.USER_ONE);
        friendsQuery.include(Friends.USER_TWO);
        friendsQuery.setSkip(skipper);
        friendsQuery.setLimit(50);

        friendsQuery.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> friends, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " +  e.getMessage());
                    return;
                }

                Log.i(TAG, String.valueOf(friends.size()));
                mFriends.addAll(friends);

                CustomUser friendOne;
                CustomUser friendTwo;
                for (Friends friend : friends) {
                    Log.i(TAG, friend.getUserOne().getUsername());
                    friendOne = new CustomUser(friend.getUserOne());
                    friendTwo = new CustomUser(friend.getUserTwo());

                    if (friend.areFriends()) {
                        if (!friendOne.getObjectId().equals(mUser.getObjectId())) {
                            mFriendsUsers.add(friendOne);
                        } else {
                            mFriendsUsers.add(friendTwo);
                        }
                        mFriendsAdapter.notifyDataSetChanged();
                    }
                    // Todo: If not friends, then save it in friend requests
                }
            }
        });
    }

    public ParseQuery<Friends> getFriendsQuery() {
        ParseQuery<Friends> userOne = ParseQuery.getQuery("Friends");
        userOne.whereEqualTo(Friends.USER_ONE, mUser.getCustomUser());

        ParseQuery<Friends> userTwo = ParseQuery.getQuery("Friends");
        userTwo.whereEqualTo(Friends.USER_TWO, mUser.getCustomUser());

        List<ParseQuery<Friends>> queries = new ArrayList<ParseQuery<Friends>>();
        queries.add(userOne);
        queries.add(userTwo);

        ParseQuery<Friends> mainQuery = ParseQuery.or(queries);

        return mainQuery;
    }

}

