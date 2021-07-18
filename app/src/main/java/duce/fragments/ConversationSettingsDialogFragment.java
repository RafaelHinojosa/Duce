package duce.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.duce.R;

public class ConversationSettingsDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "ConversationSettingsDialogFragment";

    private Spinner mSpSelectLanguage;
    private Button mBtnSave;

    public ConversationSettingsDialogFragment() {
        // Required empty public constructor
    }

    public interface ConversationSettingsDialogListener {
        void onFinishSettingsDialog(String language);
    }

    public static ConversationSettingsDialogFragment newInstance(String title) {
        ConversationSettingsDialogFragment fragment = new ConversationSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.conversation_settings_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        mSpSelectLanguage = (Spinner) view.findViewById(R.id.spSelectLanguage);
        mBtnSave = (Button) view.findViewById(R.id.btnSave);

        String title = getArguments().getString("title", "Select Language (frag)");
        getDialog().setTitle(title);
        mSpSelectLanguage.requestFocus();

        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ConversationSettingsDialogListener listener = (ConversationSettingsDialogListener) getActivity();
        listener.onFinishSettingsDialog(mSpSelectLanguage.getSelectedItem().toString());
        // Close dialog and return to Conversation Activity (parent)
        dismiss();
    }
}