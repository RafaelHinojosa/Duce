package duce.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.databinding.FriendRequestItemBinding;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import duce.MainActivity;
import duce.fragments.FriendsTabFragment;
import duce.models.CustomUser;
import duce.models.Friends;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    public static final String TAG = "RequestsAdapter";

    private CustomUser mUser;
    private FriendRequestItemBinding mBinding;
    private final Context mContext;
    List<CustomUser> mRequestsUsers;
    List<Friends> mRequests;

    public RequestsAdapter(Context context, List<CustomUser> requestsUsers,
                           List<Friends> requests, CustomUser user) {
        this.mContext = context;
        this.mRequestsUsers = requestsUsers;
        this.mRequests = requests;
        this.mUser = user;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mBinding = FriendRequestItemBinding.inflate(LayoutInflater.from(mContext),
                parent, false);
        return new RequestsAdapter.ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RequestsAdapter.ViewHolder holder, int position) {
        CustomUser sender = mRequestsUsers.get(position);
        holder.bind(sender);
    }

    @Override
    public int getItemCount() {
        return mRequestsUsers.size();
    }

    public void addAll(List<CustomUser> newList) {
        mRequestsUsers.addAll(newList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvProfilePicture;
        private TextView mTvUsername;
        private ImageButton mBtnAccept;
        private ImageButton mBtnReject;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mIvProfilePicture = mBinding.ivProfilePicture;
            mTvUsername = mBinding.tvUsername;
            mBtnAccept = mBinding.btnAccept;
            mBtnReject = mBinding.btnReject;

            itemView.setOnClickListener(this);
        }

        public void bind(CustomUser sender) {
            Glide.with(mContext)
                    .load(sender.getProfilePicture().getUrl())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(mIvProfilePicture);
            mTvUsername.setText(sender.getUsername());

            mBtnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Friends request = mRequests.get(position);
                        mRequestsUsers.remove(position);
                        notifyDataSetChanged();

                        request.setAreFriends(true);
                        request.saveInBackground();
                    }
                }
            });

            mBtnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Friends request = mRequests.get(position);

                        mRequestsUsers.remove(position);
                        notifyDataSetChanged();

                        request.deleteInBackground();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CustomUser customUser = mRequestsUsers.get(position);

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("user", Parcels.wrap(customUser.getCustomUser()));
                mContext.startActivity(intent);
            }
        }
    }
}
