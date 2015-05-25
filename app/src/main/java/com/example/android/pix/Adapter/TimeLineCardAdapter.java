package com.example.android.pix.Adapter;

    import android.content.Context;
    import android.net.Uri;
    import android.support.v7.widget.RecyclerView;
    import android.text.format.DateUtils;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.example.android.pix.ParseConstants;
    import com.example.android.pix.R;
    import com.parse.ParseFile;
    import com.parse.ParseObject;
    import com.squareup.picasso.Picasso;

    import java.util.Date;
    import java.util.List;

public class TimeLineCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    protected Context mContext;
    protected List<ParseObject> mPosts;

    public TimeLineCardAdapter(Context context, List<ParseObject> messages) {
        mContext = context;
        mPosts = messages;
    }

    public TimeLineCardAdapter(List<ParseObject> contents) {
        this.mPosts = contents;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder;

        View view;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_card_item, parent, false);
        holder = new ViewHolder();
        holder.iconImageView = (ImageView) view.findViewById(R.id.post_photo);
        //holder.nameLabel = (TextView) view.findViewById(R.id.senderLabel);
        //holder.timeLabel = (TextView) view.findViewById(R.id.timeLabel);
        holder.titleLabel = (TextView)view.findViewById(R.id.post_title);
        holder.commentLabel = (TextView)view.findViewById(R.id.post_comment);
        view.setTag(holder);

        ParseObject post = mPosts.get(viewType);
        Date createdAt = post.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();

        //holder.timeLabel.setText(convertedDate);

        ParseFile file = mPosts.get(viewType).getParseFile(ParseConstants.KEY_FILE_POST);
        Uri fileUri = Uri.parse(file.getUrl());
        Picasso.with(mContext).load(fileUri.toString()).into(holder.iconImageView);

        //holder.nameLabel.setText(post.getString(ParseConstants.KEY_POST_NAME));
        holder.titleLabel.setText(post.getString(ParseConstants.KEY_POST_TITLE));
        holder.commentLabel.setText(post.getString(ParseConstants.KEY_POST_COMMENT));
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    private static class ViewHolder{
        ImageView iconImageView;
        //TextView nameLabel;
        //TextView timeLabel;
        TextView titleLabel;
        TextView commentLabel;
    }

    public void refill(List<ParseObject> messages) {
        mPosts.clear();
        mPosts.addAll(messages);
        notifyDataSetChanged();
    }
}


