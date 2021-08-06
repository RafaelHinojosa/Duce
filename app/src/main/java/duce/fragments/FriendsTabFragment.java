package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.duce.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import duce.EndlessRecyclerViewScrollListener;
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
    private TextView mTvNoFriends;
    private TextView mTvNoFriendsOther;
    private LottieAnimationView mAvNoFriendsMe;
    private LottieAnimationView mAvNoFriendsOther;
    private SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener mScrollListener;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mTvFriendsTitle = view.findViewById(R.id.tvFriendsTitle);
        mTvFriendsTitle.setText(R.string.friends_title);
        mTvNoFriends = view.findViewById(R.id.tvNoFriends);
        mAvNoFriendsMe = view.findViewById(R.id.avNoFriendsMe);
        mTvNoFriendsOther = view.findViewById(R.id.tvNoFriendsOther);
        mAvNoFriendsOther = view.findViewById(R.id.avSendRequest);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mRvFriends = view.findViewById(R.id.rvFriends);
        mRvFriends.setLayoutManager(linearLayoutManager);
        mFriendsAdapter = new FriendsAdapter(getContext(), mFriendsUsers, mUser);

        mRvFriends.setAdapter(mFriendsAdapter);

        if (mUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            mTvRequestsTitle = view.findViewById(R.id.tvRequestsTitle);
            mRvRequests = view.findViewById(R.id.rvRequests);
            mRvRequests.setLayoutManager(new LinearLayoutManager(getContext()));

            mTvRequestsTitle.setText(R.string.requests_title);

            mRequests = new ArrayList<>();
            mRequestsUsers = new ArrayList<>();
            mRequestsAdapter = new RequestsAdapter(getContext(), mRequestsUsers, mRequests, mUser);

            mRvRequests.setAdapter(mRequestsAdapter);

            getRequests();
        }

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager,
            EndlessRecyclerViewScrollListener.ScrollDirection.DOWN) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int skipper = mFriendsAdapter.getItemCount();
                getFriends(skipper, false);
            }
        };

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFriends(0, true);
                mSwipeContainer.setRefreshing(false);
            }
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getFriends(0, false);
    }

    public void getFriends(int skipper, boolean refresh) {
        ParseQuery<Friends> friendsQuery = getFriendsQuery();
        friendsQuery.include(Friends.USER_ONE);
        friendsQuery.include(Friends.USER_TWO);
        friendsQuery.whereEqualTo(Friends.ARE_FRIENDS, true);
        friendsQuery.addDescendingOrder(Friends.CREATED_AT);
        friendsQuery.setSkip(skipper);
        friendsQuery.setLimit(25);

        friendsQuery.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> friends, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " +  e.getMessage());
                    return;
                }

                if (refresh) {
                    mFriends.clear();
                    mFriendsUsers.clear();
                    mFriendsAdapter.clear();
                    mScrollListener.resetState();
                }

                if (friends.size() > 0) {
                    mTvFriendsTitle.setVisibility(View.VISIBLE);
                    mRvFriends.setVisibility(View.VISIBLE);
                } else if (mUser.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    mAvNoFriendsMe.setVisibility(View.VISIBLE);
                    mTvNoFriends.setText(getString(R.string.connect_with_people));
                    mTvNoFriends.setVisibility(View.VISIBLE);
                } else {
                    mAvNoFriendsOther.setVisibility(View.VISIBLE);
                    String text = "";
                    text += getString(R.string.looks_like);
                    text += " " + mUser.getUsername() + " ";
                    text += getString(R.string.has_no_friends) + "\n";
                    text += getString(R.string.send_a_request);
                    mTvNoFriendsOther.setText(text);
                    mTvNoFriendsOther.setVisibility(View.VISIBLE);
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
                if (requests.size() > 0) {
                    mTvRequestsTitle.setVisibility(View.VISIBLE);
                    mRvRequests.setVisibility(View.VISIBLE);
                }

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

