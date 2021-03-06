package duce.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.duce.R;
import com.duce.databinding.ChatsFragmentBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bolts.Task;
import duce.ConversationActivity;
import duce.EndlessRecyclerViewScrollListener;
import duce.LoginActivity;
import duce.MainActivity;
import duce.MatchAlgorithm;
import duce.adapters.ChatsAdapter;
import duce.models.Chats;
import duce.models.Countries;
import duce.models.Messages;
import es.dmoral.toasty.Toasty;

import static com.duce.R.layout.chats_fragment;

public class ChatsFragment extends Fragment {

    public static final String TAG = "ChatsFragment";

    private List<Messages> mLastMessages = new ArrayList<>();
    private RecyclerView mRvChats;
    private EditText mEtSearch;
    private FloatingActionButton mBtnMatch;
    private ChatsAdapter mChatsAdapter;
    protected SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private boolean mLoadChats;

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(chats_fragment, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        mRvChats = (RecyclerView) view.findViewById(R.id.rvChats);
        mEtSearch = (EditText) view.findViewById(R.id.etSearch);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mLastMessages = new ArrayList<>();
        mChatsAdapter = new ChatsAdapter(getContext(), mLastMessages);
        mRvChats.setLayoutManager(linearLayoutManager);
        mBtnMatch = view.findViewById(R.id.btnMatchGen);
        mRvChats.setAdapter(mChatsAdapter);
        mLoadChats = true;

        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager,
            EndlessRecyclerViewScrollListener.ScrollDirection.DOWN) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int skipper = mChatsAdapter.getItemCount();
                if (mLoadChats) {
                    getLastMessages(skipper, false);
                }
            }
        };

        mRvChats.addOnScrollListener(mScrollListener);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastMessages(0, true);
                mSwipeContainer.setRefreshing(false);
            }
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mBtnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMatch = new Intent(getContext(), MatchAlgorithm.class);
                startActivity(toMatch);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mLoadChats = false;
                    String search = textView.getText().toString();
                    getSearchedChats(search);
                }
                return false;
            }
        });

        getLastMessages(0, false);
        setLiveChats();
    }

    // Select the last messages from conversations where the user appears
    public void getLastMessages(int skipper, boolean refreshChats) {
        ParseQuery<Messages> mainQuery = queryMyMessages();
        mainQuery.addDescendingOrder(Messages.CREATED_AT);
        mainQuery.setSkip(skipper);
        mainQuery.setLimit(20);

        mainQuery.findInBackground(new FindCallback<Messages>() {
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }
                Log.i(TAG, String.valueOf(messages.size()));
                for (Messages message : messages) {
                    Log.i(TAG, String.valueOf(message) + " mensajes");
                }
                if (refreshChats) {
                    mLastMessages.clear();
                    mChatsAdapter.clear();
                    mChatsAdapter.addAll(messages);
                    mScrollListener.resetState();
                } else {
                    mLastMessages.addAll(messages);
                    mChatsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Sets a Live Query for Messages
    public void setLiveChats() {
        String webSocketUrl = "wss://duce.b4a.io/";

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(webSocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<Messages> messagesQuery = queryMyMessages();

        assert parseLiveQueryClient != null;
        SubscriptionHandling<Messages> subscriptionHandling = parseLiveQueryClient.subscribe(messagesQuery);

        // Listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, (query, message) -> {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getLastMessages(0, true);
                }
            });
        });
    }

    public ParseQuery<Messages> queryMyMessages() {
        ParseQuery<Messages> sender = ParseQuery.getQuery("Messages");
        sender.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());
        sender.whereEqualTo(Messages.SENDER, ParseUser.getCurrentUser());

        ParseQuery<Messages> receiver = ParseQuery.getQuery("Messages");
        receiver.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());
        receiver.whereEqualTo(Messages.RECEIVER, ParseUser.getCurrentUser());

        List<ParseQuery<Messages>> queries = new ArrayList<ParseQuery<Messages>>();
        queries.add(sender);
        queries.add(receiver);

        ParseQuery<Messages> mainQuery = ParseQuery.or(queries);
        mainQuery.whereEqualTo(Messages.LAST_MESSAGE, true);

        return mainQuery;
    }

    public void getSearchedChats(String search) {
        // Get the my User and the list of users whose username matches the list
        ParseQuery<ParseUser> myQuery = ParseUser.getQuery();
        myQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseUser> usersWithString = ParseUser.getQuery();
        usersWithString.whereStartsWith("username", search);

        // 1. Where I am the sender and the receiver's username matches with the searched user
        ParseQuery<Messages> senderIsMe = ParseQuery.getQuery("Messages");
        senderIsMe.whereEqualTo("lastMessage", true);
        senderIsMe.whereMatchesKeyInQuery("ownerUser", "objectId", myQuery);
        senderIsMe.whereMatchesKeyInQuery("sender", "objectId", myQuery);
        senderIsMe.whereMatchesKeyInQuery("receiver", "objectId", usersWithString);

        // 2. Where I am the receiver and the sender's username matches with the searched user
        ParseQuery<Messages> receiverIsMe = ParseQuery.getQuery("Messages");
        receiverIsMe.whereEqualTo("lastMessage", true);
        receiverIsMe.whereMatchesKeyInQuery("ownerUser", "objectId", myQuery);
        receiverIsMe.whereMatchesKeyInQuery("receiver", "objectId", myQuery);
        receiverIsMe.whereMatchesKeyInQuery("sender", "objectId", usersWithString);

        // 3. Join both lists where I am either a sender or receiver and the other user's username matches with the search
        List<ParseQuery<Messages>> listMessagesMe = new ArrayList<ParseQuery<Messages>>();
        listMessagesMe.add(senderIsMe);
        listMessagesMe.add(receiverIsMe);

        ParseQuery<Messages> searchedChats = ParseQuery.or(listMessagesMe);
        searchedChats.addDescendingOrder("createdAt");

        searchedChats.findInBackground(new FindCallback<Messages>() {
            @Override
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }
                Log.i(TAG, "Chats retrieved = " + messages.size());
                for (Messages message : messages) {
                    Log.i(TAG, message.getDescription());
                }

                mLastMessages.clear();
                mChatsAdapter.clear();
                mChatsAdapter.addAll(messages);
                mScrollListener.resetState();

                if (messages.size() == 0) {
                    Toasty.normal(
                        getContext(),
                        R.string.no_chats_found,
                        Toast.LENGTH_SHORT
                        )
                        .show();
                }
            }
        });
    }

    // Deletes all the messages from a Conversation where I am the "owner" -> my own copy of the messages so that the other user does not lose them
    public static void deleteChat(Chats chatId, int position) {
        ParseQuery<Messages> messagesToDelete = ParseQuery.getQuery("Messages");
        messagesToDelete.whereEqualTo(Messages.CHATS_ID, chatId);
        messagesToDelete.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());

        messagesToDelete.findInBackground(new FindCallback<Messages>() {
            @Override
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }

                Messages.deleteAllInBackground(messages, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Error: " + e.getMessage());
                            return;
                        }
                    }
                });
            }
        });
    }
}
