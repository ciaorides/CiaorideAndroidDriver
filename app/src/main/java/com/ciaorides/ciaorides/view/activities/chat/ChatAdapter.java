package com.ciaorides.ciaorides.view.activities.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ciaorides.ciaorides.R;
import com.ciaorides.ciaorides.model.response.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatAdapter extends FirebaseRecyclerAdapter<Message, ChatAdapter.ViewHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message model) {
        holder.message.setText(model.getText());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_send, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_recieve, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getUserId().equals("5264"))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sender, message, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //sender = itemView.findViewById(R.id.sendername);
            message = itemView.findViewById(R.id.txtMessage);
            //timestamp = itemView.findViewById(R.id.timestamp);

        }
    }

}