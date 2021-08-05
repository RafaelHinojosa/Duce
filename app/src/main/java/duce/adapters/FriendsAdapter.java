package duce.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.duce.databinding.ChatItemBinding;
import com.duce.databinding.FriendItemBinding;
import com.duce.databinding.FriendsTabFragmentBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import duce.ConversationActivity;
import duce.fragments.ChatsFragment;
import duce.models.CustomUser;
import duce.models.Friends;
import duce.models.Messages;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public static final String TAG = "FriendsAdapter";

    private CustomUser mUser;
    private FriendItemBinding mBinding;
    private final Context mContext;
    List<CustomUser> mFriendsUsers;

    public FriendsAdapter(Context context, List<CustomUser> friends, CustomUser user) {
        this.mContext = context;
        this.mFriendsUsers = friends;
        this.mUser = user;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FriendItemBinding.inflate(LayoutInflater.from(mContext),
                parent, false);
        return new FriendsAdapter.ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FriendsAdapter.ViewHolder holder, int position) {
        CustomUser friend = mFriendsUsers.get(position);
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return mFriendsUsers.size();
    }

    public void addAll(List<CustomUser> newList) {
        mFriendsUsers.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView mIvProfilePicture;
        private TextView mTvUsername;
        private ImageButton mIbMessage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mIvProfilePicture = mBinding.ivProfilePicture;
            mTvUsername = mBinding.tvUsername;
            mIbMessage = mBinding.btnMessage;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(CustomUser friend) {
            Glide.with(mContext)
                    .load(friend.getProfilePicture().getUrl())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mIvProfilePicture);
            mTvUsername.setText(friend.getUsername());
        }

        @Override
        public void onClick(View v) {
            return;
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
