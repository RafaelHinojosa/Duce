package duce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duce.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import duce.ConversationActivity;
import duce.models.CustomUser;
import duce.models.Languages;
import duce.models.Messages;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MessageViewHolder> {

    public static final String TAG = "ConversationAdapter";

    private static final int OUTGOING_MESSAGE = 100;
    private static final int INCOMING_MESSAGE = 200;

    private List<Messages> mMessages;
    private Context mContext;

    public ConversationAdapter(Context context, List<Messages> messages) {
        mMessages = messages;
        mContext = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == INCOMING_MESSAGE) {
            View contactView = inflater.inflate(R.layout.incoming_message, parent, false);
            return new IncomingMessageViewHolder(contactView);
        } else if (viewType == OUTGOING_MESSAGE) {
            View contactView = inflater.inflate(R.layout.outgoing_messages, parent, false);
            return new OutgoingMessageViewHolder(contactView);
        } else {
            throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageViewHolder holder, int position) {
        Messages message = mMessages.get(position);
        holder.bindMessage(message);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    // Returns if the message is incoming or outcoming
    @Override
    public int getItemViewType(int position) {
        if (isMe(position)) {
            return OUTGOING_MESSAGE;
        } else {
            return INCOMING_MESSAGE;
        }
    }

    private boolean isMe(int position) {
        Messages message = mMessages.get(position);
        return message.getSender() != null && message.getSender().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    // Returns a message to the ViewHolder
    public Messages getMessage(int position) {
        if (position != RecyclerView.NO_POSITION) {
            return (Messages) mMessages.get(position);
        }
        return null;
    }

    // Changes the langauge of a message in the database
    public void changeLanguage(int position, String languageCode) {
        ParseQuery<Languages> languageQuery = ParseQuery.getQuery("Languages");
        languageQuery.whereEqualTo(Languages.TRANSLATE_CODE, languageCode);

        languageQuery.findInBackground(new FindCallback<Languages>() {
            @Override
            public void done(List<Languages> languages, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error: " + e.getMessage());
                    return;
                }
                if (languages.size() > 0) {
                    Languages language = languages.get(0);

                    Messages message = getMessage(position);
                    message.setLanguage(language);
                    message.saveInBackground();
                }
            }
        });
    }

    // Message View Holder inner class
    public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "MessageViewHolder";

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract void bindMessage(Messages message);
    }

    public class IncomingMessageViewHolder extends MessageViewHolder {
        private RelativeLayout mRlMessage;
        private ImageView mIvProfileOther;
        private TextView mTvDescription;
        private TextView mTvUsername;
        private TextView mTvDuce;    // Translate button
        private CustomUser mSender;

        public IncomingMessageViewHolder(View itemView) {
            super(itemView);

            mRlMessage = (RelativeLayout) itemView.findViewById(R.id.rlMessage);
            mIvProfileOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            mTvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            mTvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            mTvDuce = (TextView) itemView.findViewById(R.id.tvDuce);
            mTvDuce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Messages message = getMessage(position);

                        // Get current language of the message to change it
                        ParseQuery<Languages> languageQuery = ParseQuery.getQuery("Languages");
                        languageQuery.whereEqualTo("objectId", message.getLanguage().getObjectId());

                        languageQuery.findInBackground(new FindCallback<Languages>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void done(List<Languages> languages, ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error: " + e.getMessage());
                                    return;
                                }
                                if (languages.size() > 0) {
                                    Languages language = languages.get(0);
                                    String currentLanguage = language.getLanguageName();
                                    String currentCode = language.getTranslateCode();
                                    String targetCode = ConversationActivity.getTargetLanguage();
                                    Languages targetLanguageObj =
                                        ConversationActivity.getTargetLanguageObj();

                                    Log.i(TAG, "Current Code " + currentCode);
                                    Log.i(TAG, "Target Code " + targetCode);

                                    if (currentCode.equals("original") && !targetCode.equals("original")) {
                                        String translatedText = ConversationActivity.translate(message.getDescription(), targetCode);
                                        mTvDescription.setText(translatedText);
                                        mTvDuce.setText(mContext.getString(R.string.change_to)
                                                + " Original text");
                                        changeLanguage(position, targetCode);
                                    } else if (currentCode.equals(targetCode) && !targetCode.equals("original")) {
                                        mTvDescription.setText(message.getDescription());
                                        mTvDuce.setText(mContext.getString(R.string.translate_to)
                                                + " " + targetLanguageObj.getLanguageName());
                                        changeLanguage(position, "original");
                                    }
                                }
                            }
                        });
                    }
                }
            });
            mSender = new CustomUser();
        }

        @Override
        void bindMessage(Messages message) {
            String targetCode = ConversationActivity.getTargetLanguage();
            Languages targetCodeObj = ConversationActivity.getTargetLanguageObj();
            String userId = message.getSender().getObjectId();

            ParseQuery<ParseUser> sender = ParseUser.getQuery();
            sender.whereEqualTo("objectId", userId);

            sender.findInBackground(new FindCallback<ParseUser>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e != null) {
                        Log.d(TAG, "Error: " + e);
                        return;
                    }
                    if (users.size() > 0) {
                        mSender.setCustomUser(users.get(0));
                        Glide.with(mContext)
                                .load(mSender.getProfilePicture().getUrl())
                                .circleCrop()
                                .into(mIvProfileOther);
                        mTvUsername.setText(mSender.getUsername());
                        if (targetCode.equals("original")) {
                            mTvDescription.setText(message.getDescription());
                            mTvDuce.setText(mContext.getString(R.string.select_to_translate));
                        } else {
                            String translatedText = ConversationActivity.translate(message.getDescription(), targetCode);
                            Log.i(TAG, translatedText);
                            mTvDescription.setText(translatedText);
                            if (targetCodeObj != null) {
                                mTvDuce.setText(mContext.getString(R.string.change_to)
                                        + " original text");
                                message.setLanguage(targetCodeObj);
                                message.saveInBackground();
                            }
                        }
                        mRlMessage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public class OutgoingMessageViewHolder extends MessageViewHolder {
        private RelativeLayout mRlMessage;
        private TextView mTvDescription;
        private CustomUser mSender;

        public OutgoingMessageViewHolder(View itemView) {
            super(itemView);

            mTvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            mRlMessage = (RelativeLayout) itemView.findViewById(R.id.rlMessage);
            mSender = new CustomUser();
        }

        @Override
        public void bindMessage(Messages message) {
            String userId = message.getSender().getObjectId();

            ParseQuery<ParseUser> sender = ParseUser.getQuery();
            sender.whereEqualTo("objectId", userId);

            sender.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e != null) {
                        Log.d(TAG, "Error: " + e);
                        return;
                    }
                    if (users.size() > 0) {
                        mSender.setCustomUser(users.get(0));
                        mTvDescription.setText(message.getDescription());
                        mRlMessage.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}
