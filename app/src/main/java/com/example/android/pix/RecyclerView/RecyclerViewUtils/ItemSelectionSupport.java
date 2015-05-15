package com.example.android.pix.RecyclerView.RecyclerViewUtils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Checkable;

import com.example.android.pix.R;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class ItemSelectionSupport {
    public static final int INVALID_POSITION = -1;

    public static enum ChoiceMode {
        NONE,
        SINGLE,
        MULTIPLE
    }

    private final RecyclerView mRecyclerView;
    private final TouchListener mTouchListener;

    private ChoiceMode mChoiceMode = ChoiceMode.NONE;
    private CheckedStates mCheckedStates;
    private CheckedIdStates mCheckedIdStates;
    private int mCheckedCount;

    private static final String STATE_KEY_CHOICE_MODE = "choiceMode";
    private static final String STATE_KEY_CHECKED_STATES = "checkedStates";
    private static final String STATE_KEY_CHECKED_ID_STATES = "checkedIdStates";
    private static final String STATE_KEY_CHECKED_COUNT = "checkedCount";

    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;

    private ItemSelectionSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        mTouchListener = new TouchListener(recyclerView);
        recyclerView.addOnItemTouchListener(mTouchListener);
    }

    private void updateOnScreenCheckedViews() {
        final int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = mRecyclerView.getChildAt(i);
            final int position = mRecyclerView.getChildPosition(child);
            setViewChecked(child, mCheckedStates.get(position));
        }
    }

    public int getCheckedItemCount() {
        return mCheckedCount;
    }

    public boolean isItemChecked(int position) {
        if (mChoiceMode != ChoiceMode.NONE && mCheckedStates != null) {
            return mCheckedStates.get(position);
        }

        return false;
    }

    public int getCheckedItemPosition() {
        if (mChoiceMode == ChoiceMode.SINGLE && mCheckedStates != null && mCheckedStates.size() == 1) {
            return mCheckedStates.keyAt(0);
        }

        return INVALID_POSITION;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (mChoiceMode != ChoiceMode.NONE) {
            return mCheckedStates;
        }

        return null;
    }

    public long[] getCheckedItemIds() {
        if (mChoiceMode == ChoiceMode.NONE
                || mCheckedIdStates == null || mRecyclerView.getAdapter() == null) {
            return new long[0];
        }

        final int count = mCheckedIdStates.size();
        final long[] ids = new long[count];

        for (int i = 0; i < count; i++) {
            ids[i] = mCheckedIdStates.keyAt(i);
        }

        return ids;
    }

    public void setItemChecked(int position, boolean checked) {
        if (mChoiceMode == ChoiceMode.NONE) {
            return;
        }

        final Adapter adapter = mRecyclerView.getAdapter();

        if (mChoiceMode == ChoiceMode.MULTIPLE) {
            boolean oldValue = mCheckedStates.get(position);
            mCheckedStates.put(position, checked);

            if (mCheckedIdStates != null && adapter.hasStableIds()) {
                if (checked) {
                    mCheckedIdStates.put(adapter.getItemId(position), position);
                } else {
                    mCheckedIdStates.delete(adapter.getItemId(position));
                }
            }

            if (oldValue != checked) {
                if (checked) {
                    mCheckedCount++;
                } else {
                    mCheckedCount--;
                }
            }
        } else {
            boolean updateIds = mCheckedIdStates != null && adapter.hasStableIds();

            // Clear all values if we're checking something, or unchecking the currently
            // selected item
            if (checked || isItemChecked(position)) {
                mCheckedStates.clear();

                if (updateIds) {
                    mCheckedIdStates.clear();
                }
            }

            // This may end up selecting the checked we just cleared but this way
            // we ensure length of mCheckStates is 1, a fact getCheckedItemPosition relies on
            if (checked) {
                mCheckedStates.put(position, true);

                if (updateIds) {
                    mCheckedIdStates.put(adapter.getItemId(position), position);
                }

                mCheckedCount = 1;
            } else if (mCheckedStates.size() == 0 || !mCheckedStates.valueAt(0)) {
                mCheckedCount = 0;
            }
        }

        updateOnScreenCheckedViews();
    }

    @TargetApi(HONEYCOMB)
    public void setViewChecked(View view, boolean checked) {
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        } else if (Build.VERSION.SDK_INT >= HONEYCOMB) {
            view.setActivated(checked);
        }
    }

    /**
     * Clears any choices previously set.
     */
    public void clearChoices() {
        if (mCheckedStates != null) {
            mCheckedStates.clear();
        }

        if (mCheckedIdStates != null) {
            mCheckedIdStates.clear();
        }

        mCheckedCount = 0;
        updateOnScreenCheckedViews();
    }

    public ChoiceMode getChoiceMode() {
        return mChoiceMode;
    }

    public void setChoiceMode(ChoiceMode choiceMode) {
        if (mChoiceMode == choiceMode) {
            return;
        }

        mChoiceMode = choiceMode;

        if (mChoiceMode != ChoiceMode.NONE) {
            if (mCheckedStates == null) {
                mCheckedStates = new CheckedStates();
            }

            final Adapter adapter = mRecyclerView.getAdapter();
            if (mCheckedIdStates == null && adapter != null && adapter.hasStableIds()) {
                mCheckedIdStates = new CheckedIdStates();
            }
        }
    }

    public void onAdapterDataChanged() {
        final Adapter adapter = mRecyclerView.getAdapter();
        if (mChoiceMode == ChoiceMode.NONE || adapter == null || !adapter.hasStableIds()) {
            return;
        }

        final int itemCount = adapter.getItemCount();

        // Clear out the positional check states, we'll rebuild it below from IDs.
        mCheckedStates.clear();

        for (int checkedIndex = 0; checkedIndex < mCheckedIdStates.size(); checkedIndex++) {
            final long currentId = mCheckedIdStates.keyAt(checkedIndex);
            final int currentPosition = mCheckedIdStates.valueAt(checkedIndex);

            final long newPositionId = adapter.getItemId(currentPosition);
            if (currentId != newPositionId) {
                // Look around to see if the ID is nearby. If not, uncheck it.
                final int start = Math.max(0, currentPosition - CHECK_POSITION_SEARCH_DISTANCE);
                final int end = Math.min(currentPosition + CHECK_POSITION_SEARCH_DISTANCE, itemCount);

                boolean found = false;
                for (int searchPos = start; searchPos < end; searchPos++) {
                    final long searchId = adapter.getItemId(searchPos);
                    if (currentId == searchId) {
                        found = true;
                        mCheckedStates.put(searchPos, true);
                        mCheckedIdStates.setValueAt(checkedIndex, searchPos);
                        break;
                    }
                }

                if (!found) {
                    mCheckedIdStates.delete(currentId);
                    mCheckedCount--;
                    checkedIndex--;
                }
            } else {
                mCheckedStates.put(currentPosition, true);
            }
        }
    }

    public Bundle onSaveInstanceState() {
        final Bundle state = new Bundle();

        state.putInt(STATE_KEY_CHOICE_MODE, mChoiceMode.ordinal());
        state.putParcelable(STATE_KEY_CHECKED_STATES, mCheckedStates);
        state.putParcelable(STATE_KEY_CHECKED_ID_STATES, mCheckedIdStates);
        state.putInt(STATE_KEY_CHECKED_COUNT, mCheckedCount);

        return state;
    }

    public void onRestoreInstanceState(Bundle state) {
        mChoiceMode = ChoiceMode.values()[state.getInt(STATE_KEY_CHOICE_MODE)];
        mCheckedStates = state.getParcelable(STATE_KEY_CHECKED_STATES);
        mCheckedIdStates = state.getParcelable(STATE_KEY_CHECKED_ID_STATES);
        mCheckedCount = state.getInt(STATE_KEY_CHECKED_COUNT);

        // TODO confirm ids here
    }

    public static ItemSelectionSupport addTo(RecyclerView recyclerView) {
        ItemSelectionSupport itemSelectionSupport = from(recyclerView);
        if (itemSelectionSupport == null) {
            itemSelectionSupport = new ItemSelectionSupport(recyclerView);
            //recyclerView.setTag(R.id.recyclerview_item_selection_support, itemSelectionSupport);
        } else {
            // TODO: Log warning
        }

        return itemSelectionSupport;
    }

    public static void removeFrom(RecyclerView recyclerView) {
        final ItemSelectionSupport itemSelection = from(recyclerView);
        if (itemSelection == null) {
            // TODO: Log warning
            return;
        }

        itemSelection.clearChoices();

        recyclerView.removeOnItemTouchListener(itemSelection.mTouchListener);
        //recyclerView.setTag(R.id.recyclerview_item_selection_support, null);
    }

    public static ItemSelectionSupport from(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return null;
        }

        //return (ItemSelectionSupport) recyclerView.getTag(R.id.recyclerview_item_selection_support);
        return null;
    }

    private static class CheckedStates extends SparseBooleanArray implements Parcelable {
        private static final int FALSE = 0;
        private static final int TRUE = 1;

        public CheckedStates() {
            super();
        }

        private CheckedStates(Parcel in) {
            final int size = in.readInt();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    final int key = in.readInt();
                    final boolean value = (in.readInt() == TRUE);
                    put(key, value);
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            final int size = size();
            parcel.writeInt(size);

            for (int i = 0; i < size; i++) {
                parcel.writeInt(keyAt(i));
                parcel.writeInt(valueAt(i) ? TRUE : FALSE);
            }
        }

        public static final Creator<CheckedStates> CREATOR
                = new Creator<CheckedStates>() {
            @Override
            public CheckedStates createFromParcel(Parcel in) {
                return new CheckedStates(in);
            }

            @Override
            public CheckedStates[] newArray(int size) {
                return new CheckedStates[size];
            }
        };
    }

    private static class CheckedIdStates extends LongSparseArray<Integer> implements Parcelable {
        public CheckedIdStates() {
            super();
        }

        private CheckedIdStates(Parcel in) {
            final int size = in.readInt();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    final long key = in.readLong();
                    final int value = in.readInt();
                    put(key, value);
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            final int size = size();
            parcel.writeInt(size);

            for (int i = 0; i < size; i++) {
                parcel.writeLong(keyAt(i));
                parcel.writeInt(valueAt(i));
            }
        }

        public static final Creator<CheckedIdStates> CREATOR
                = new Creator<CheckedIdStates>() {
            @Override
            public CheckedIdStates createFromParcel(Parcel in) {
                return new CheckedIdStates(in);
            }

            @Override
            public CheckedIdStates[] newArray(int size) {
                return new CheckedIdStates[size];
            }
        };
    }

    private class TouchListener extends ClickItemTouchListener {
        TouchListener(RecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        boolean performItemClick(RecyclerView parent, View view, int position, long id) {
            final Adapter adapter = mRecyclerView.getAdapter();
            boolean checkedStateChanged = false;

            if (mChoiceMode == ChoiceMode.MULTIPLE) {
                boolean checked = !mCheckedStates.get(position, false);
                mCheckedStates.put(position, checked);

                if (mCheckedIdStates != null && adapter.hasStableIds()) {
                    if (checked) {
                        mCheckedIdStates.put(adapter.getItemId(position), position);
                    } else {
                        mCheckedIdStates.delete(adapter.getItemId(position));
                    }
                }

                if (checked) {
                    mCheckedCount++;
                } else {
                    mCheckedCount--;
                }

                checkedStateChanged = true;
            } else if (mChoiceMode == ChoiceMode.SINGLE) {
                boolean checked = !mCheckedStates.get(position, false);
                if (checked) {
                    mCheckedStates.clear();
                    mCheckedStates.put(position, true);

                    if (mCheckedIdStates != null && adapter.hasStableIds()) {
                        mCheckedIdStates.clear();
                        mCheckedIdStates.put(adapter.getItemId(position), position);
                    }

                    mCheckedCount = 1;
                } else if (mCheckedStates.size() == 0 || !mCheckedStates.valueAt(0)) {
                    mCheckedCount = 0;
                }

                checkedStateChanged = true;
            }

            if (checkedStateChanged) {
                updateOnScreenCheckedViews();
            }

            return false;
        }

        @Override
        boolean performItemLongClick(RecyclerView parent, View view, int position, long id) {
            return true;
        }
    }
}
