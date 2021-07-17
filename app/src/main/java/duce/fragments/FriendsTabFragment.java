package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duce.R;

public class FriendsTabFragment extends Fragment {

    public FriendsTabFragment() {
        // Required empty public constructor
    }

    public static FriendsTabFragment newInstance(String param1, String param2) {
        FriendsTabFragment fragment = new FriendsTabFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.friends_tab_fragment, container, false);
    }
}