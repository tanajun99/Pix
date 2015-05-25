package com.tanajun99.android.pix.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tanajun99.android.pix.Adapter.TimeLineCardAdapter;
import com.tanajun99.android.pix.ParseConstants;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.tanajun99.android.pix.R;

import java.util.List;


public class TimeLineRecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    protected List<ParseObject> mPosts;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    public static TimeLineRecyclerViewFragment newInstance() {
        return new TimeLineRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
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

        timeLine();

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    private void timeLine() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        ParseQuery query = new ParseQuery(ParseConstants.CLASS_POST);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT_POST);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> post, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    // We found messages!
                    mPosts = post;

                    String[] usernames = new String[mPosts.size()];
                    int i = 0;
                    for (ParseObject posts : mPosts) {
                        usernames[i] = posts.getString(ParseConstants.KEY_POST_NAME);
                        i++;
                    }
                    mAdapter = new RecyclerViewMaterialAdapter(new TimeLineCardAdapter(mPosts));
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            timeLine();
        }
    };


}