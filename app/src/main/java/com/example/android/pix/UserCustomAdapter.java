package com.example.android.pix;

/**
 * Created by tanakajunnari on 5/9/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

public class UserCustomAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;

    public UserCustomAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            //holder.iconImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        ParseUser user = mUsers.get(position);

    //    if (user.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
      //      holder.iconImageView.setImageResource(R.mipmap.ic_action_picture);
        //}
        //else {
          //  holder.iconImageView.setImageResource(R.mipmap.ic_action_play_over_video);
        //}
        holder.nameLabel.setText(user.getUsername());

        return convertView;
    }

    private static class ViewHolder {
        //ImageView iconImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}

