package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duce.R;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import duce.models.CustomUser;

// This Fragment will hold 2 fragments (tabs): My Profile and Friends. That's why it is Profile [MAIN] Fragment
public class ProfileMainFragment extends Fragment {

    public static final String TAG = "ProfileMainFragment";

    private CustomUser mUser;
    private Fragment mMyProfileTab;
    private Fragment mFriendsTab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        assert getArguments() != null;
        mUser = new CustomUser(Parcels.unwrap(getArguments().getParcelable("user")));
        Log.i(TAG, mUser.getObjectId());
        getCompleteUser();

        View view = inflater.inflate(R.layout.profile_main_fragment,container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    // Create and add Fragments to tabs
    private void setupViewPager(ViewPager viewPager) {
        ProfileTabsAdapter adapter = new ProfileTabsAdapter(getChildFragmentManager());

        mMyProfileTab = new MyProfileTabFragment();
        Bundle myProfileBundle = new Bundle();
        myProfileBundle.putParcelable("user", Parcels.wrap(mUser.getCustomUser()));
        mMyProfileTab.setArguments(myProfileBundle);
        adapter.addFragment(mMyProfileTab, "Profile");

        mFriendsTab = new FriendsTabFragment();
        Bundle friendsBundle = new Bundle();
        friendsBundle.putParcelable("user", Parcels.wrap(mUser.getCustomUser()));
        mMyProfileTab.setArguments(friendsBundle);
        adapter.addFragment(mFriendsTab, "Friends");

        viewPager.setAdapter(adapter);
    }

    protected void getCompleteUser() {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("objectId", mUser.getCustomUser().getObjectId());

        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }
                if (users.size() > 0) {
                    mUser.setCustomUser(users.get(0));
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}