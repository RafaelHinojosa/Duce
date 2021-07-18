package duce.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duce.R;

public class MyProfileTabFragment extends Fragment {

    public MyProfileTabFragment() {
        // Required empty public constructor
    }

    public static MyProfileTabFragment newInstance() {
        MyProfileTabFragment fragment = new MyProfileTabFragment();
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
        return inflater.inflate(R.layout.my_profile_tab_fragment, container, false);
    }
}