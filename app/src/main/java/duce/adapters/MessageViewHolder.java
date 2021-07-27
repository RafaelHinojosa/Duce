package duce.adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.duce.R;
import com.duce.databinding.IncomingMessageBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import duce.ConversationActivity;
import duce.models.CustomUser;
import duce.models.Messages;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "MessageViewHolder";

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void bindMessage(Messages message);

    public static class IncomingMessageViewHolder extends MessageViewHolder {
        private Context mContext;
        private ImageView mIvProfileOther;
        private TextView mTvDescription;
        private TextView mTvUsername;
        private ImageButton mIbDuce;    // Translate button
        private CustomUser mSender;

        public IncomingMessageViewHolder(View itemView, Context context) {
            super(itemView);

            mContext = context;
            mIvProfileOther = (ImageView) itemView.findViewById(R.id.ivProfileOther);
            mTvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            mTvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            mIbDuce = (ImageButton) itemView.findViewById(R.id.ibDuce);
            mIbDuce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get message description and position
                        Log.i(TAG, mTvDescription.getText().toString());
                        //Log.i(TAG, "El mensaje es " + ConversationAdapter.getMessage(position).toString());
                        Log.i(TAG, "Position " + String.valueOf(position));
                    }
                }
            });
            mSender = new CustomUser();
        }

        @Override
        void bindMessage(Messages message) {
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
                        Glide.with(mContext)
                                .load(mSender.getProfilePicture().getUrl())
                                .circleCrop()
                                .into(mIvProfileOther);
                        mTvUsername.setText(mSender.getUsername());
                        mTvDescription.setText(message.getDescription());
                    }
                }
            });
        }
    }

    public static class OutgoingMessageViewHolder extends MessageViewHolder {
        private Context mContext;
        private ImageView mIvProfileMe;
        private TextView mTvDescription;
        private ImageButton mIbDuce;
        private CustomUser mSender;

        public OutgoingMessageViewHolder(View itemView, Context context) {
            super(itemView);

            mContext = context;
            mIvProfileMe = (ImageView) itemView.findViewById(R.id.ivProfileMe);
            mTvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            mIbDuce = (ImageButton) itemView.findViewById(R.id.ibDuce);
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
                        Glide.with(mContext)
                                .load(mSender.getProfilePicture().getUrl())
                                .circleCrop()
                                .into(mIvProfileMe);
                        mTvDescription.setText(message.getDescription());
                    }
                }
            });
        }
    }
}