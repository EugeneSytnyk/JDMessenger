package com.jdcompany.jdmessenger.ui.adapters;

import android.net.Uri;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jdcompany.jdmessenger.R;
import com.jdcompany.jdmessenger.data.objects.Message;
import com.jdcompany.jdmessenger.domain.Chat;
import com.jdcompany.jdmessenger.domain.MessageAction;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    List<Message> data;
    Chat chat;
    SimpleDateFormat simpleDateFormat;
    private int position;

    public int getPosition(){
        return position;
    }

    public List<Message> getData(){
        return data;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public interface OnItemClickListener {
        void onUserItemClicked(Message messageModel);
    }

    private MessagesAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener (MessagesAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MessagesAdapter() {
        super();
        data = new ArrayList<>();
        simpleDateFormat = new SimpleDateFormat("HH:mm");
    }

    public void setChat(Chat chat){
        this.chat = chat;
    }

    public void setMessagesCollection(List<Message> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = data.get(position);
        holder.setLeftSide(chat.isLeftSide(message));
        holder.tvMessageTime.setText(simpleDateFormat.format(new Date(message.getTime())));

        if(message.getAction().equals(MessageAction.TEXT.toString())) {
            holder.setText(data.get(position).getBody());
            holder.ivMessageImage.setVisibility(View.GONE);
            //holder.tvMessage.setVisibility(View.VISIBLE);
        }
        else if(message.getAction().equals(MessageAction.IMAGE.toString())){
            holder.setText("");
            holder.ivMessageImage.setImageURI(Uri.parse(message.getBody()));
            holder.ivMessageImage.setVisibility(View.VISIBLE);
            //holder.tvMessage.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getAdapterPosition());
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            if (MessagesAdapter.this.onItemClickListener != null)
                MessagesAdapter.this.onItemClickListener.onUserItemClicked(data.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return chat == null ? 0 : data.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView tvMessage;
        TextView tvMessageTime;
        ImageView ivMessageImage;
        ConstraintLayout clMessageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            clMessageContainer = itemView.findViewById(R.id.flMessageContainer);
            ivMessageImage = itemView.findViewById(R.id.ivMessageImage);
            this.itemView.setOnCreateContextMenuListener(this);
        }

        void setText(String text) {
            tvMessage.setText(text);
        }

        void setLeftSide(boolean side) {
            FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (side) {
                frameLayoutParams.gravity = Gravity.LEFT;
                clMessageContainer.setBackgroundResource(R.drawable.message_left_drawable);
                frameLayoutParams.leftMargin = 20;
            } else {
                frameLayoutParams.gravity = Gravity.RIGHT;
                clMessageContainer.setBackgroundResource(R.drawable.message_right_drawable);
                frameLayoutParams.rightMargin = 20;
            }
            clMessageContainer.setLayoutParams(frameLayoutParams);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.optionDeleteMessageGlobally, Menu.NONE, R.string.optionDeleteMessageGlobally);
            menu.add(Menu.NONE, R.id.optionDeleteMessageLocally, Menu.NONE, R.string.optionDeleteMessageLocally);
            menu.add(Menu.NONE, R.id.optionEditTextMessage, Menu.NONE, R.string.optionEditText);
        }
    }
}
