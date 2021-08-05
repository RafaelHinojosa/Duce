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
import duce.adapters.RequestsAdapter;
import duce.models.CustomUser;
import duce.models.Friends;
import duce.models.Messages;
import kotlin.io.LineReader;

import static com.duce.R.layout.my_profile_tab_fragment;

public class FriendsTabFragment extends Fragment {

    public static final String TAG = "FriendsTabFragment";

    private CustomUser mUser;
    private List<CustomUser> mFriendsUsers;
    private List<CustomUser> mRequestsUsers;
    private List<Friends> mFriends;
    private List<Friends> mRequests;
    private FriendsAdapter mFriendsAdapter;
    private RequestsAdapter mRequestsAdapter;
    private RecyclerView mRvFriends;
    private RecyclerView mRvRequests;
    private TextView mTvFriendsTitle;
    private TextView mTvRequestsTitle;

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

        if (mUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            mTvRequestsTitle = view.findViewById(R.id.tvRequestsTitle);
            mRvRequests = view.findViewById(R.id.rvRequests);
            mRvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

            mTvRequestsTitle.setText(R.string.requests_title);
            mTvRequestsTitle.setVisibility(View.VISIBLE);
            mRvRequests.setVisibility(View.VISIBLE);

            mRequests = new ArrayList<>();
            mRequestsUsers = new ArrayList<>();
            mRequestsAdapter = new RequestsAdapter(getContext(), mRequestsUsers, mRequests, mUser);

            mRvRequests.setAdapter(mRequestsAdapter);

            getRequests();
        }

        getFriends(0);
    }

    public void getFriends(int skipper) {
        ParseQuery<Friends> friendsQuery = getFriendsQuery();
        friendsQuery.include(Friends.USER_ONE);
        friendsQuery.include(Friends.USER_TWO);
        friendsQuery.whereEqualTo(Friends.ARE_FRIENDS, true);
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

                    if (!friendOne.getObjectId().equals(mUser.getObjectId())) {
                        mFriendsUsers.add(friendOne);
                    } else {
                        mFriendsUsers.add(friendTwo);
                    }
                    mFriendsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Get the requests made to current User.
    // User One is the sender of the request and User Two is the current User and receiver
    public void getRequests() {
        ParseQuery<Friends> requestsQuery = ParseQuery.getQuery("Friends");
        requestsQuery.whereEqualTo(Friends.USER_TWO, mUser.getCustomUser());
        requestsQuery.include(Friends.USER_ONE);
        requestsQuery.whereEqualTo(Friends.ARE_FRIENDS, false);

        requestsQuery.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> requests, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " +  e.getMessage());
                    return;
                }

                Log.i(TAG, String.valueOf(requests.size()));

                CustomUser sender;
                for (Friends request : requests) {
                    Log.i(TAG, request.getUserOne().getUsername());
                    sender = new CustomUser(request.getUserOne());

                    mRequestsUsers.add(sender);
                    mRequests.add(request);
                    mRequestsAdapter.notifyDataSetChanged();
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

