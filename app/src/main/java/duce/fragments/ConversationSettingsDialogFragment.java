package duce.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.duce.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

import duce.adapters.LanguagesAdapter;

public class ConversationSettingsDialogFragment extends DialogFragment {

    public static final String TAG = "ConversationSettingsDialogFragment";
    public static final String TITLE = "title";

    private TextView mSelectLanguage;
    private Button mBtnCancel;
    private AlertDialog.Builder mLanSelector;
    private String mLanSelected;
    private LanguagesAdapter mLanAdapter;
    private ConversationSettingsDialogListener mListener;

    public interface ConversationSettingsDialogListener {
        void onFinishSettingsDialog(String language);
    }

    public static ConversationSettingsDialogFragment newInstance(String title) {
        ConversationSettingsDialogFragment fragment = new ConversationSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conversation_settings_dialog_fragment,
            container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);

        mBtnCancel = (Button) view.findViewById(R.id.btnCancel);
        mSelectLanguage = (TextView) view.findViewById(R.id.tvSelectLanguage);
        mLanSelector = new AlertDialog.Builder(getContext());
        mLanAdapter = new LanguagesAdapter();
        mLanSelected = "";
        mListener = (ConversationSettingsDialogListener) getActivity();

        String title = getArguments().getString(TITLE, String.valueOf(R.string.select_language));
        getDialog().setTitle(title);

        mSelectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDialog();
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationSettingsDialogListener listener = (ConversationSettingsDialogListener) getActivity();
                listener.onFinishSettingsDialog("");
                dismiss();
            }
        });
    }

    public void setupDialog() {
        mLanSelector.setTitle(R.string.incoming_messages_title);
        mLanSelector.setCancelable(false);

        String[] languages = (String[]) mLanAdapter.getLanguages();

        // Arguments: (List, index of selection, click listener)
        mLanSelector.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i >= 0 && i < languages.length) {
                    mLanSelected = languages[i];
                }
            }
        });

        mLanSelector.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ConversationSettingsDialogListener listener = (ConversationSettingsDialogListener) getActivity();
                listener.onFinishSettingsDialog(mLanSelected);
                dismiss();
            }
        });

        mLanSelector.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLanSelected = "";
                dismiss();
            }
        });

        mLanSelector.show();
    }
}