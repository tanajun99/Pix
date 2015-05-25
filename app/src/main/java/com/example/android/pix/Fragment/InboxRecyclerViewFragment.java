package com.example.android.pix.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.pix.Activity.MainActivity;
import com.example.android.pix.Adapter.InboxRecyclerViewAdapter;
import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class InboxRecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    public static InboxRecyclerViewFragment newInstance() {
        return new InboxRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.Refresh1,
                R.color.Refresh2,
                R.color.Refresh3,
                R.color.Refresh4
        );
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        retrieveMessage();

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    private void retrieveMessage() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.edit_member_unfollower, Toast.LENGTH_LONG).show();

            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT_SEND);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    mMessages = messages;

                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    mAdapter = new RecyclerViewMaterialAdapter(new InboxRecyclerViewAdapter(mMessages));
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }
//    @Override
//    public void onListItemClick(RecyclerView l, View v, int position, long id) {
//        super(l,v,position,id);
//        ParseObject message = mMessages.get(position);
//        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE_SEND);
//        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE_SEND);
//        Uri fileUri = Uri.parse(file.getUrl());
//
//        if (messageType.equals(ParseConstants.TYPE_IMAGE_SEND)) {
//            // view the image
//            Intent intent = new Intent(getActivity(), ViewerPhotoActivity.class);
//            intent.setData(fileUri);
//            startActivity(intent);
//        }
//        else {
//            // view the video
//            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
//            intent.setDataAndType(fileUri, "video/*");
//            startActivity(intent);
//        }
//
//        // Delete it!
//        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
//
//        if (ids.size() == 1) {
//            // last recipient - delete the whole thing!
//            message.deleteInBackground();
//        }
//        else {
//            // remove the recipient and save
//            ids.remove(ParseUser.getCurrentUser().getObjectId());
//
//            ArrayList<String> idsToRemove = new ArrayList<String>();
//            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
//
//            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
//            message.saveInBackground();
//        }
//    }
//
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessage();
        }
    };
}
