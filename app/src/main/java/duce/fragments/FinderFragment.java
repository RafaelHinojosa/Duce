package duce.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.duce.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import duce.adapters.ChatsAdapter;
import duce.adapters.FoundUsersAdapter;
import duce.models.CustomUser;
import es.dmoral.toasty.Toasty;

import static com.duce.R.layout.chats_fragment;
import static com.duce.R.layout.finder_fragment;

public class FinderFragment extends Fragment {

    public static final String TAG = "FinderFragment";

    private List<CustomUser> mUsers = new ArrayList<>();
    private EditText mEtSearch;
    private LottieAnimationView mAvWorldSpinning;
    private RecyclerView mRvUsers;
    private FoundUsersAdapter mUsersAdapter;

    public static FinderFragment newInstance() {
        return new FinderFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(finder_fragment, container, false);

        mEtSearch = view.findViewById(R.id.etSearch);
        mAvWorldSpinning = view.findViewById(R.id.avWorldSpinning);
        mRvUsers = view.findViewById(R.id.rvUsers);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        mUsers = new ArrayList<>();
        mUsersAdapter = new FoundUsersAdapter(getContext(), mUsers);
        mRvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvUsers.setAdapter(mUsersAdapter);

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                mAvWorldSpinning.setVisibility(View.INVISIBLE);
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String text = textView.getText().toString();
                    getSearchedUsers(text);
                }
                return false;
            }
        });
    }

    public void getSearchedUsers(String search) {
        ParseQuery<ParseUser> usersWithString = ParseUser.getQuery();
        usersWithString.whereStartsWith("username", search);

        usersWithString.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }

                mUsersAdapter.clear();
                for (ParseUser user : users) {
                    CustomUser newUser = new CustomUser(user);
                    mUsers.add(newUser);
                }
                mUsersAdapter.notifyDataSetChanged();

                if (users.size() == 0) {
                    Toasty.normal(
                        getContext(),
                        R.string.no_users_found,
                        Toast.LENGTH_SHORT
                        )
                        .show();
                }
            }
        });
    }
}