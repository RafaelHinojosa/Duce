package duce.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.duce.databinding.FoundUserItemBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import duce.ConversationActivity;
import duce.MainActivity;
import duce.models.Chats;
import duce.models.CustomUser;
import duce.models.MatchingUser;
import duce.models.Messages;
import duce.models.UserChats;
import duce.models.UserLanguages;

public class FoundUsersAdapter extends RecyclerView.Adapter<FoundUsersAdapter.ViewHolder> {

    public static final String TAG = "FoundUsersAdapter";

    private FoundUserItemBinding mBinding;
    public final Context mContext;
    List<CustomUser> mUsers;

    public FoundUsersAdapter(Context context, List<CustomUser> users) {
        this.mContext = context;
        this.mUsers = users;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FoundUserItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new FoundUsersAdapter.ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        CustomUser user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<CustomUser> newList) {
        mUsers.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfilePicture;
        private TextView mTvUsername;
        private ImageButton mBtnMessage;
        private TextView mTvMyLanguages;
        private TextView mTvMyInterests;
        private LinearLayout mLlMyLanguages;
        private LinearLayout mLlMyInterests;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mIvProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            mTvUsername = itemView.findViewById(R.id.tvUsername);
            mBtnMessage = itemView.findViewById(R.id.btnMessage);
            mTvMyLanguages = itemView.findViewById(R.id.tvMyLanguages);
            mTvMyInterests = itemView.findViewById(R.id.tvMyInterests);
            mLlMyLanguages = itemView.findViewById(R.id.llMyLanguages);
            mLlMyInterests = itemView.findViewById(R.id.llMyInterests);
            itemView.setOnClickListener(this);

            mBtnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpConversation();
                }
            });
        }

        public void bind(CustomUser user) {
            if (user == null) {
                Log.i(TAG, "Null User");
                return;
            }
            Log.i(TAG, user.toString());
            Glide.with(mContext)
                    .load(user.getProfilePicture().getUrl())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mIvProfilePicture);
            mTvUsername.setText(user.getUsername());
            mTvMyLanguages.setText(R.string.my_languages);
            mTvMyInterests.setText(R.string.my_interests);

            // Clean the linear layouts
            mLlMyLanguages.removeAllViews();
            mLlMyInterests.removeAllViews();
            setLanguages(user);
        }

        public void setLanguages(CustomUser customUser) {
            CustomUser user = customUser;
            ParseQuery<UserLanguages> userLanguagesQuery = ParseQuery.getQuery("UserLanguages");
            userLanguagesQuery.whereEqualTo("userId", user.getCustomUser());
            // IMPORTANT: include the object pointer to have access to the full row with all its data
            userLanguagesQuery.include("languageId");
            userLanguagesQuery.setLimit(10);
            userLanguagesQuery.addDescendingOrder("createdAt");

            userLanguagesQuery.findInBackground(new FindCallback<UserLanguages>() {
                @Override
                public void done(List<UserLanguages> userLanguages, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error: " + e.getMessage());
                        return;
                    }

                    int myLanguagesCount = 0;
                    int interestCount = 0;

                    for (UserLanguages userLanguage : userLanguages) {
                        Log.i(TAG, userLanguage.toString());
                        TextView language = setLanguageTV(userLanguage.getLanguage().getLanguageName());
                        TextView languageCopy = setLanguageTV(userLanguage.getLanguage().getLanguageName());

                        if (userLanguage.getMyLanguage() && myLanguagesCount < 3) {
                            mLlMyLanguages.addView(language);
                            myLanguagesCount++;
                        }

                        if (userLanguage.getInterestedIn() && interestCount < 3) {
                            mLlMyInterests.addView(languageCopy);
                            interestCount++;
                        }
                    }
                }
            });
        }

        public TextView setLanguageTV(String languageName) {
            TextView textView = new TextView(mContext);
            textView.setText(languageName);
            textView.setIncludeFontPadding(true);
            textView.setPadding(20,10,20,10);
            textView.setCompoundDrawablePadding(5);
            textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.language_box));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textView.setLayoutParams(params);

            return textView;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CustomUser customUser = mUsers.get(position);

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(customUser.getCustomUser()));
                mContext.startActivity(intent);
            }
        }

        public void setUpConversation() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CustomUser user = mUsers.get(position);

                ParseQuery<UserChats> userChatsQuery = ParseQuery.getQuery("UserChats");
                userChatsQuery.whereEqualTo("userId", ParseUser.getCurrentUser());
                userChatsQuery.whereEqualTo("otherUserId", user.getCustomUser());

                userChatsQuery.findInBackground(new FindCallback<UserChats>() {
                    @Override
                    public void done(List<UserChats> userChats, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error: " + e.getMessage());
                            return;
                        }
                        if (userChats.size() == 0) {
                            createChat(user.getCustomUser());
                        } else {
                            UserChats userChat = userChats.get(0);
                            Chats chat = userChat.getChat();
                            goToMessages(chat, ParseUser.getCurrentUser(), user.getCustomUser());
                        }
                    }
                });
            }
        }

        public void createChat(ParseUser otherUser) {
            Chats newChat = new Chats();
            newChat.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error: " + e.getMessage());
                        return;
                    }
                    ParseQuery<Chats> lastChat = ParseQuery.getQuery("Chats");
                    lastChat.setLimit(1);
                    lastChat.addDescendingOrder("createdAt");
                    lastChat.findInBackground(new FindCallback<Chats>() {
                        @Override
                        public void done(List<Chats> chats, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error: " + e.getMessage());
                                return;
                            }
                            if (chats.size() > 0) {
                                Chats lastChat = chats.get(0);

                                // My conversation copy
                                UserChats userChats = new UserChats();
                                userChats.setChat(lastChat);
                                userChats.setUser(ParseUser.getCurrentUser());
                                userChats.setOtherUser(otherUser);
                                userChats.saveInBackground();

                                // The other's profile copy
                                UserChats otherUserCopy = new UserChats();
                                otherUserCopy.setChat(lastChat);
                                otherUserCopy.setUser(otherUser);
                                otherUserCopy.setOtherUser(ParseUser.getCurrentUser());
                                otherUserCopy.saveInBackground();

                                goToMessages(lastChat, ParseUser.getCurrentUser(), otherUser);
                            }
                        }
                    });
                }
            });
        }

        public void goToMessages(Chats chat, ParseUser sender, ParseUser receiver) {
            Messages message = new Messages();
            message.setChatsId(chat);
            message.setOwnerUser(sender);
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setDescription("Hello");

            Intent toMessages = new Intent(mContext, ConversationActivity.class);
            toMessages.putExtra("conversation", Parcels.wrap(message));
            mContext.startActivity(toMessages);
        }
    }
}
