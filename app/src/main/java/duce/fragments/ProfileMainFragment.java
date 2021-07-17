package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duce.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

// This Fragment will hold 2 fragments (tabs): My Profile and Friends. That's why it is Profile [MAIN] Fragment
public class ProfileMainFragment extends Fragment {

    public static final String TAG = "ProfileMainFragment";

    public ProfileMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_main_fragment,container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        ProfileTabsAdapter adapter = new ProfileTabsAdapter(getChildFragmentManager());
        adapter.addFragment(new MyProfileTabFragment(), "My Profile");
        adapter.addFragment(new FriendsTabFragment(), "Friends");

        viewPager.setAdapter(adapter);
    }
}