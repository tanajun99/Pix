package com.example.android.pix;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.rey.material.widget.TextView;

/**
 * Created by tanakajunnari on 5/24/15.
 */
public class RecycleViewClicked extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    public TextView title;
    public ImageView icon;

    private ClickListener clickListener;

    public RecycleViewClicked(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        icon = (ImageView) itemView.findViewById(R.id.icon);

        // We set listeners to the whole item view, but we could also
        // specify listeners for the title or the icon.
        itemView.setOnClickListener(this);
    }


    /* Interface for handling clicks - both normal and long ones. */
    public interface ClickListener {

        /**
         * Called when the view is clicked.
         *
         * @param v view that is clicked
         * @param position of the clicked item
         * @param isLongClick true if long click, false otherwise
         */
        public void onClick(View v, int position, boolean isLongClick);

    }

    /* Setter for listener. */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {

        clickListener.onClick(v, getPosition(), false);

    }
}
