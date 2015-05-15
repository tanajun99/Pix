package com.example.android.pix.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.pix.MD5Utils;
import com.example.android.pix.R;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

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
            holder.userImageView = (ImageView) convertView.findViewById(R.id.user_image_view);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            holder.checkmarkImageView = (ImageView)convertView.findViewById(R.id.user_image_checkmark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.userImageView.setImageResource(R.mipmap.ic_person_grey600_48dp);
        } else{
            String hash = MD5Utils.md5Hex(email);
            String gravatalUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatalUrl)
                    .placeholder(R.mipmap.ic_person_grey600_48dp)
                    .into(holder.userImageView);
        }
        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView)parent;
        if(gridView.isItemChecked(position)){
            holder.checkmarkImageView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkmarkImageView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImageView;
        ImageView checkmarkImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}

