package duce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.duce.databinding.ChatItemBinding;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import duce.ConversationActivity;
import duce.fragments.ChatsFragment;
import duce.models.Chats;
import duce.models.Countries;
import duce.models.CustomUser;
import duce.models.Messages;

import static android.provider.Settings.System.getString;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    public static final String TAG = "ChatsAdapter";

    private ChatItemBinding mBinding;
    private final Context mContext;
    List<Messages> mLastMessages;

    public ChatsAdapter(Context context, List<Messages> lastMessages) {
        this.mContext = context;
        this.mLastMessages = lastMessages;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = ChatItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ChatsAdapter.ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Messages message = mLastMessages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mLastMessages.size();
    }

    public void clear() {
        mLastMessages.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Messages> newList) {
        mLastMessages.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mIvProfilePicture;
        TextView mTvFlag;
        TextView mTvUsername;
        TextView mTvSender;
        TextView mTvDescription;
        TextView mTvDate;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mIvProfilePicture = mBinding.ivProfilePicture;
            mTvFlag = mBinding.tvFlag;
            mTvUsername = mBinding.tvUsername;
            mTvSender = mBinding.tvSender;
            mTvDescription = mBinding.tvDescription;
            mTvDate = mBinding.tvDate;
            itemView.setOnClickListener(this);
        }

        public void bind(Messages message) {

            String senderId = message.getSender().getObjectId();
            String receiverId = message.getReceiver().getObjectId();
            // If I am the sender, the other's the receiver. If I am the receiver, the other is the sender.
            String otherId = (senderId.equals(ParseUser.getCurrentUser().getObjectId()))
                ? receiverId
                : senderId;

            CustomUser senderUser = new CustomUser();
            CustomUser otherUser = new CustomUser();

            // Set other user's photo, username and sender name
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
                        otherUser.setCustomUser(objects.get(0));
                        // Set values to the chat Item
                        Glide.with(mContext)
                            .load(otherUser.getProfilePicture().getUrl())
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(mIvProfilePicture);
                        mTvUsername.setText(otherUser.getUsername());
                        if (senderId.equals(otherId)) {
                            mTvSender.setText(otherUser.getUsername() + ":");
                        } else {
                            mTvSender.setText(R.string.you);
                        }
                    }
                }
            });
            mTvDescription.setText(message.getDescription());
            Date createdAt = message.getCreatedAt();
            String timeAgo = Messages.calculateTimeAgo(createdAt);
            mTvDate.setText(timeAgo);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Messages message = mLastMessages.get(position);
                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("conversation", Parcels.wrap(message));
                mContext.startActivity(intent);
            }
        }
    }
}
