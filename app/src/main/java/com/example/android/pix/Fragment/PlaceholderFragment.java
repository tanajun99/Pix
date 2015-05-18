package com.example.android.pix.Fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.pix.Activity.MainActivity;
import com.example.android.pix.Activity.TimeLineActivity;
import com.example.android.pix.Activity.ViewerPhotoActivity;
import com.example.android.pix.Adapter.MessageCustomAdapter;
import com.example.android.pix.Adapter.PostsCustomAdapter;
import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanakajunnari on 5/14/15.
 */
public class PlaceholderFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    protected List<ParseObject> mPosts;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_line,
                container, false);

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
    public void onResume() {
        super.onResume();

        getActivity().setProgressBarIndeterminateVisibility(true);

        retrievePost();
    }

    private void retrievePost() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    // We found post!
                    mPosts = posts;

                    String[] usernames = new String[mPosts.size()];
                    int i = 0;
                    for(ParseObject post : mPosts) {
                        usernames[i] = post.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if (getListView().getAdapter() == null) {
                        PostsCustomAdapter adapter = new PostsCustomAdapter(
                                getListView().getContext(),
                                mPosts);
                        setListAdapter(adapter);
                    }
                    else {
                        ((PostsCustomAdapter)getListView().getAdapter()).refill(mPosts);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject post = mPosts.get(position);
        String messageType = post.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = post.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            // view the image
            Intent intent = new Intent(getActivity(), ViewerPhotoActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        }
        else {
            // view the video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // Delete it!
        List<String> ids = post.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if (ids.size() == 1) {
            // last recipient - delete the whole thing!
            post.deleteInBackground();
        }
        else {
            // remove the recipient and save
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            post.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            post.saveInBackground();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrievePost();
        }
    };
}
