package com.tanajun99.android.pix.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tanajun99.android.pix.ParseConstants;
import com.parse.ParseObject;
import com.tanajun99.android.pix.R;

import java.util.Date;
import java.util.List;

public class InboxRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<ParseObject> mMessages;
    private RecyclerView mRecycler;

    public InboxRecyclerViewAdapter(Context context, List<ParseObject> messages) {
        mContext = context;
        mMessages = messages;
    }

    public InboxRecyclerViewAdapter(List<ParseObject> contents) {
        this.mMessages = contents;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ViewHolder holder;
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_small_inbox, parent, false);
        holder = new ViewHolder();
        holder.iconImageView = (ImageView) view.findViewById(R.id.messageIcon);
        holder.nameLabel = (TextView) view.findViewById(R.id.senderLabel);
        holder.timeLabel = (TextView) view.findViewById(R.id.timeLabel);
        holder.titleLabel = (TextView)view.findViewById(R.id.titleLabel);
        holder.commentLabel = (TextView)view.findViewById(R.id.commentLabel);

        view.setTag(holder);

        ParseObject message = mMessages.get(viewType);
        Date createdAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();

        holder.timeLabel.setText(convertedDate);

        if (message.getString(ParseConstants.KEY_FILE_TYPE_SEND).equals(ParseConstants.TYPE_IMAGE_SEND)) {
            holder.iconImageView.setImageResource(R.mipmap.ic_action_picture);
        }
        else {
            holder.iconImageView.setImageResource(R.mipmap.ic_action_play_over_video);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.titleLabel.setText(message.getString(ParseConstants.KEY_SEND_TITLE));
        holder.commentLabel.setText(message.getString(ParseConstants.KEY_SEND_COMMENT));
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public static class ViewHolder{
        ImageView iconImageView;
        TextView nameLabel;
        TextView timeLabel;
        TextView titleLabel;
        TextView commentLabel;
    }

    public void refill(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}