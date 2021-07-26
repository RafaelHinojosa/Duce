package duce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.BuildConfig;
import com.duce.R;
import com.duce.databinding.ConversationActivityBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.services.translate.Translate;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import duce.adapters.ConversationAdapter;
import duce.fragments.ConversationSettingsDialogFragment;
import duce.models.CustomUser;
import duce.models.Messages;

public class ConversationActivity extends AppCompatActivity implements ConversationSettingsDialogFragment.ConversationSettingsDialogListener {

    public static final String TAG = "ConversationActivity";
    static final int MAX_MESSAGES_TO_SHOW = 20;

    private ImageView mIvProfilePicture;
    private TextView mTvFlag;
    private TextView mTvUsername;
    private FloatingActionButton mBtnSettings;
    private RecyclerView mRvMessages;
    private EditText mEtCompose;
    private ImageButton mIbSend;
    private Messages mConversation;
    private List<Messages> mMessages;
    private CustomUser mOtherUser;
    private ConversationAdapter mAdapter;
    com.google.cloud.translate.Translate translate;
    private String mTranslationLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConversationActivityBinding binding = ConversationActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mIvProfilePicture = binding.ivProfilePicture;
        mTvFlag = binding.tvFlag;
        mTvUsername = binding.tvUsername;
        mRvMessages = binding.rvMessages;
        mEtCompose = binding.etComposeMessage;
        mIbSend = binding.ibSend;
        mBtnSettings = findViewById(R.id.btnSettings);
        mConversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        mMessages = new ArrayList<>();
        mOtherUser = new CustomUser();
        mTranslationLanguage = "";

        mAdapter = new ConversationAdapter(ConversationActivity.this, mMessages);
        mRvMessages.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRvMessages.setLayoutManager(linearLayoutManager);

        mIbSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = mEtCompose.getText().toString();
                if (isBlank(description)) {
                    Toast.makeText(ConversationActivity.this, "Empty messages can't be sent", Toast.LENGTH_SHORT).show();
                    return;
                }
                // The original for me the sender
                uploadMessage("me", description);
                // The copy for the receiver
                uploadMessage("otherUser", description);

                mEtCompose.setText(null);
            }
        });

        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });

        bind();
        getMessages();
        setLiveMessages();
        getTranslateService();
        translate("Hola como estas?", "en");
    }

    protected void bind() {
        String senderId = mConversation.getSender().getObjectId();
        String receiverId = mConversation.getReceiver().getObjectId();

        String otherId = (senderId.equals(ParseUser.getCurrentUser().getObjectId()))
                ? receiverId
                : senderId;

        ParseQuery<ParseUser> getOtherUser = ParseQuery.getQuery("_User");
        getOtherUser.whereEqualTo("objectId", otherId);
        getOtherUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e.getMessage());
                    return;
                }
                if (objects.size() > 0) {
                    mOtherUser.setCustomUser(objects.get(0));
                    Glide.with(ConversationActivity.this)
                            .load(mOtherUser.getProfilePicture().getUrl())
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(mIvProfilePicture);
                    mTvUsername.setText(mOtherUser.getUsername());
                }
            }
        });
    }

    public void getMessages() {
        ParseQuery<Messages> messagesQuery = ParseQuery.getQuery("Messages");
        messagesQuery.whereEqualTo(Messages.CHATS_ID, mConversation.getChatsId());
        messagesQuery.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());
        messagesQuery.setLimit(MAX_MESSAGES_TO_SHOW);
        messagesQuery.orderByDescending("createdAt");

        messagesQuery.findInBackground(new FindCallback<Messages>() {
            @Override
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error: " + e);
                    return;
                }
                if (messages.size() > 0) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();
                    mRvMessages.scrollToPosition(0);
                }
            }
        });
    }

    public void uploadMessage(String owner, String description) {
        Messages message = new Messages();
        message.setChatsId(mConversation.getChatsId());
        if (owner.equals("me")) {
            message.setOwnerUser(ParseUser.getCurrentUser());
        } else if (owner.equals("otherUser")) {
            message.setOwnerUser(mOtherUser.getCustomUser());
        }
        message.setSender(ParseUser.getCurrentUser());
        message.setReceiver(mOtherUser.getCustomUser());
        message.setDescription(description);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Failed to save message " + e);
                    return;
                }
                if (owner.equals("me")) {
                    Log.i(TAG, "hey " + owner);
                    mMessages.add(0, message);
                    mAdapter.notifyDataSetChanged();
                    mRvMessages.scrollToPosition(0);
                }
                updateLastMessage(owner);
                Toast.makeText(ConversationActivity.this, R.string.message_save, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // When a message is submitted, we have to update the lastMessage property of the penutimate message
    public void updateLastMessage(String owner) {
        ParseQuery<Messages> penultimateMessage = ParseQuery.getQuery("Messages");
        penultimateMessage.whereEqualTo(Messages.CHATS_ID, mConversation.getChatsId());
        if (owner.equals("me")) {
            penultimateMessage.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());
        } else if (owner.equals("otherUser")){
            penultimateMessage.whereEqualTo(Messages.OWNER_USER, mOtherUser.getCustomUser());
        }
        penultimateMessage.addDescendingOrder("createdAt");
        penultimateMessage.setSkip(1);  // Skip the just submitted message
        penultimateMessage.setLimit(1); // Get the penultimate

        // Retrieve the object by id
        penultimateMessage.findInBackground(new FindCallback<Messages>() {
            @Override
            public void done(List<Messages> messages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get the penultimate message");
                    return;
                }
                if (messages.size() > 0) {
                    Messages message = messages.get(0);
                    message.put("lastMessage", false);
                    message.saveInBackground();
                }
            }
        });
    }

    // Sets a Live Query for Messages
    public void setLiveMessages() {
        String webSocketUrl = "wss://duce.b4a.io/";

        ParseLiveQueryClient parseLiveQueryClient = null;
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI(webSocketUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<Messages> messagesQuery = ParseQuery.getQuery("Messages");
        messagesQuery.whereEqualTo(Messages.CHATS_ID, mConversation.getChatsId());
        messagesQuery.whereEqualTo(Messages.OWNER_USER, ParseUser.getCurrentUser());
        messagesQuery.whereEqualTo(Messages.RECEIVER, ParseUser.getCurrentUser());

        assert parseLiveQueryClient != null;
        SubscriptionHandling<Messages> subscriptionHandling = parseLiveQueryClient.subscribe(messagesQuery);

        // Listen for CREATE events on the Message class
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, message) -> {
            mMessages.add(0, message);

            // RecyclerView updates need to be run on the UI thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    mRvMessages.scrollToPosition(0);
                }
            });
        });
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ConversationSettingsDialogFragment settingsDialog = ConversationSettingsDialogFragment.newInstance(String.valueOf(R.string.select_language));
        settingsDialog.show(fm, "conversation_settings_dialog_fragment");
    }

    // Extract data put in the interface in the Settings Dialog
    @Override
    public void onFinishSettingsDialog(String language) {
        Toast.makeText(this, "Language Selected: " + language, Toast.LENGTH_SHORT).show();
        mTranslationLanguage = language;
    }

    public boolean isBlank(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    public void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.duce_credentials)) {
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            // Set credentials and get translate service
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void translate(String originalText, String targetLanguage) {
        //Get input text to be translated
        Translation translation = translate.translate(
                originalText,
                com.google.cloud.translate.Translate.TranslateOption.targetLanguage(targetLanguage),
                com.google.cloud.translate.Translate.TranslateOption.model("base")
                );
        String translatedText = translation.getTranslatedText();
        Toast.makeText(ConversationActivity.this, translatedText, Toast.LENGTH_SHORT).show();
    }
}