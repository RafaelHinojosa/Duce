package duce.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duce.R;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import duce.ConversationActivity;
import duce.models.Messages;

public class ConversationAdapter extends RecyclerView.Adapter<MessageViewHolder> {

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
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == INCOMING_MESSAGE) {
            View contactView = inflater.inflate(R.layout.incoming_message, parent, false);
            return new MessageViewHolder.IncomingMessageViewHolder(contactView, parent.getContext());   // DIFERENCIAY QUE ES STATIC
        } else if (viewType == OUTGOING_MESSAGE) {
            View contactView = inflater.inflate(R.layout.outgoing_messages, parent, false);
            return new MessageViewHolder.OutgoingMessageViewHolder(contactView, parent.getContext());
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
}
