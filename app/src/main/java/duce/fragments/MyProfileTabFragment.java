package duce.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.duce.R;
import com.duce.databinding.MyProfileTabFragmentBinding;
import com.parse.ParseUser;

import duce.StartActivity;

public class MyProfileTabFragment extends Fragment {

    private static final String TAG = "MyProfileTabFragment";

    private Button mBtnLogOut;

    public MyProfileTabFragment() {
        // Required empty public constructor
    }

    public static MyProfileTabFragment newInstance() {
        MyProfileTabFragment fragment = new MyProfileTabFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBtnLogOut = view.findViewById(R.id.btnLogOut);

        mBtnLogOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent toStartActivity = new Intent(getActivity(), StartActivity.class);
                startActivity(toStartActivity);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_profile_tab_fragment, container, false);
    }
}