package com.example.android.pix.RecyclerView.RecyclerViewUtils;

import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;

import com.example.android.pix.R;


public class ItemClickSupport {

    public interface OnItemClickListener {

        void onItemClick(RecyclerView parent, View view, int position, long id);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(RecyclerView parent, View view, int position, long id);
    }

    private final RecyclerView mRecyclerView;
    private final TouchListener mTouchListener;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        mTouchListener = new TouchListener(recyclerView);
        recyclerView.addOnItemTouchListener(mTouchListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (!mRecyclerView.isLongClickable()) {
            mRecyclerView.setLongClickable(true);
        }

        mItemLongClickListener = listener;
    }

    public static ItemClickSupport addTo(RecyclerView recyclerView) {
        ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            itemClickSupport = new ItemClickSupport(recyclerView);
            //recyclerView.setTag(R.id.recyclerview_item_click_support, itemClickSupport);
        } else {
            // TODO: Log warning
        }

        return itemClickSupport;
    }

    public static void removeFrom(RecyclerView recyclerView) {
        final ItemClickSupport itemClickSupport = from(recyclerView);
        if (itemClickSupport == null) {
            // TODO: Log warning
            return;
        }

        recyclerView.removeOnItemTouchListener(itemClickSupport.mTouchListener);
        //recyclerView.setTag(R.id.recyclerview_item_click_support, null);
    }

    public static ItemClickSupport from(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return null;
        }

        //return (ItemClickSupport) recyclerView.getTag(R.id.recyclerview_item_click_support);
        return null;
    }

    private class TouchListener extends ClickItemTouchListener {
        TouchListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        boolean performItemClick(RecyclerView parent, View view, int position, long id) {
            if (mItemClickListener != null) {
                view.playSoundEffect(SoundEffectConstants.CLICK);
                mItemClickListener.onItemClick(parent, view, position, id);
                return true;
            }

            return false;
        }

        @Override
        boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
            if (mItemLongClickListener != null) {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return mItemLongClickListener.onItemLongClick(parent, view, position, id);
            }

            return false;
        }
    }
}