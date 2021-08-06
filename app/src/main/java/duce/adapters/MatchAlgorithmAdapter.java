package duce.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.duce.R;
import com.duce.databinding.MatchedUserItemBinding;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import duce.models.MatchingUser;

public class MatchAlgorithmAdapter extends RecyclerView.Adapter<MatchAlgorithmAdapter.ViewHolder> {

    public static final String TAG = "MatchingAlgorithmAdapter";

    private MatchedUserItemBinding mBinding;
    private List<MatchingUser> mMatchingUsers;
    private Context mContext;

    public MatchAlgorithmAdapter(List<MatchingUser> users, Context context) {
        this.mContext = context;
        this.mMatchingUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = MatchedUserItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new MatchAlgorithmAdapter.ViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchingUser user = mMatchingUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mMatchingUsers.size();
    }

    public void addList(List<MatchingUser> users) {
        mMatchingUsers.addAll(users);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mIvProfilePicture;
        TextView mTvUsername;
        TextView mTvAge;
        TextView mTvLanguages;
        TextView mTvInterests;
        TextView mTvScore;
        TextView mTvAvailability;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvProfilePicture = mBinding.ivProfilePicture;
            mTvUsername = mBinding.tvUsername;
            mTvAge = mBinding.tvAge;
            mTvLanguages = mBinding.tvLanguages;
            mTvInterests = mBinding.tvInterests;
            mTvScore = mBinding.tvScore;
            mTvAvailability = mBinding.tvAvailability;
        }

        @SuppressLint("SetTextI18n")
        public void bind(MatchingUser user) {
            Glide.with(mContext)
                    .load(user.getUser().getProfilePicture().getUrl())
                    .fitCenter()
                    .into(mIvProfilePicture);

            mTvUsername.setText(user.getUser().getUsername());
            if (user.getAge() >= 15) {
                mTvAge.setText(String.valueOf(user.getAge()) + " years");
            } else {
                mTvAge.setVisibility(View.GONE);
            }
            mTvLanguages.setText(mContext.getString(R.string.spoken_languages) + " "
                    + user.commonLanguagesToString() + "\n");
            mTvInterests.setText(mContext.getString(R.string.languages_you_speak) +
                    " " + user.getUser().getUsername() + ": " + user.crossedLanguagesToString() + "\n");
            mTvAvailability.setText("Available: " + user.getLastConnection());
        }
    }
}