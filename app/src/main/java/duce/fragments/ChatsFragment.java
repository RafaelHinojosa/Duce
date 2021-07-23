package duce.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.duce.R;
import com.duce.databinding.ChatsFragmentBinding;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import duce.ConversationActivity;
import duce.MainActivity;
import duce.adapters.ChatsAdapter;
import duce.models.Chats;
import duce.models.Countries;
import duce.models.Messages;

import static com.duce.R.layout.chats_fragment;

public class ChatsFragment extends Fragment {

    public static final String TAG = "ChatsFragment";

    private List<Messages> mLastMessages = new ArrayList<>();
    private RecyclerView mRvChats;
    private EditText mEtSearch;
    private ChatsAdapter mChatsAdapter;
    protected SwipeRefreshLayout mSwipeContainer;

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

        mLastMessages = new ArrayList<>();
        mChatsAdapter = new ChatsAdapter(getContext(), mLastMessages);
        mRvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvChats.setAdapter(mChatsAdapter);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastMessages(true);
                mSwipeContainer.setRefreshing(false);
            }
        });
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getLastMessages(false);
    }

    // Select the last messages from conversations where the user appears
    public void getLastMessages(boolean refreshChats) {
        ParseQuery<Messages> sender = ParseQuery.getQuery("Messages");
        sender.whereEqualTo(Messages.SENDER, ParseUser.getCurrentUser());

        ParseQuery<Messages> receiver = ParseQuery.getQuery("Messages");
        receiver.whereEqualTo(Messages.RECEIVER, ParseUser.getCurrentUser());

        List<ParseQuery<Messages>> queries = new ArrayList<ParseQuery<Messages>>();
        queries.add(sender);
        queries.add(receiver);

        ParseQuery<Messages> mainQuery = ParseQuery.or(queries);
        mainQuery.whereEqualTo(Messages.LAST_MESSAGE, true);
        mainQuery.addDescendingOrder(Messages.CREATED_AT);
        mainQuery.setLimit(20);

        mainQuery.findInBackground(new FindCallback<Messages>() {
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }
                Log.i(TAG, String.valueOf(messages.size()));
                for (Messages message : messages) {
                    Log.i(TAG, String.valueOf(message));
                }
                if (refreshChats) {
                    mChatsAdapter.clear();
                    mChatsAdapter.addAll(messages);
                } else {
                    mLastMessages.addAll(messages);
                    mChatsAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
