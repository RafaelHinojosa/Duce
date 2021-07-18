package duce.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duce.R;

import duce.ConversationActivity;
import duce.MainActivity;

public class ChatsFragment extends Fragment implements View.OnClickListener{

    private TextView mTvLastMessage;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance() {
        ChatsFragment fragment = new ChatsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chats_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        mTvLastMessage = (TextView) view.findViewById(R.id.tvLastMessageMessi);
        mTvLastMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Intent from this fragment to Conversation activity
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        startActivity(intent);
    }
}