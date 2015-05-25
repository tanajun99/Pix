package com.example.android.pix.Activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.android.pix.FileManager;
import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;
import com.example.android.pix.Adapter.UserCustomAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageRecipientActivity extends Activity {

    public static final String TAG = MessageRecipientActivity.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;
    protected String mTitle;
    protected String mComment;
    GridView mGridView;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid_recip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myawesometoolbar);
        setActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.friendsGrid);
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);
        Intent intent = getIntent();
        mMediaUri = intent.getData();
        mFileType = intent.getExtras().getString(ParseConstants.KEY_FILE_TYPE_SEND);
        mTitle = intent.getExtras().getString(ParseConstants.KEY_SEND_TITLE);
        mComment = intent.getExtras().getString(ParseConstants.KEY_SEND_COMMENT);
        Log.d(ParseConstants.KEY_SEND_TITLE, mTitle);
        Log.d(ParseConstants.KEY_SEND_COMMENT, mComment);

        final FloatingActionButton fabSend = (FloatingActionButton) findViewById(R.id.send_add_text);
        fabSend.setColorNormal(R.color.actionButton);
        fabSend.setColorNormalResId(R.color.actionButton);
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject message = createMessage();
                open(v);
                if (message == null) {
                    // error
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageRecipientActivity.this);
                    builder.setMessage(R.string.error_selecting_file)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    finish();
                }
            }
        });

        final FloatingActionButton fabFriends = (FloatingActionButton) findViewById(R.id.cancel_add_text);
        fabFriends.setColorNormal(R.color.actionButton);
        fabFriends.setColorNormalResId(R.color.actionButton);
        fabFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageRecipientActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION_SEND);
        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME_SEND);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserCustomAdapter adapter = new UserCustomAdapter(MessageRecipientActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserCustomAdapter) mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageRecipientActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE_SEND, mFileType);
        message.put(ParseConstants.KEY_SEND_TITLE, mTitle);
        message.put(ParseConstants.KEY_SEND_COMMENT, mComment);

        byte[] fileBytes = FileManager.getByteArrayFromFile(this, mMediaUri);

        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE_SEND)) {
                fileBytes = FileManager.reduceImageForUpload(fileBytes);
            }

            String fileName = FileManager.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE_SEND, file);

            return message;
        }
    }

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    protected void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(MessageRecipientActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MessageRecipientActivity.this, MainActivity.class);
                    sendPushNotifications();
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageRecipientActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID_SEND, getRecipientIds());

        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.push_message, ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = (ImageView) view.findViewById(R.id.user_image_checkmark);
            if (mGridView.isItemChecked(position)) {
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void open(View view) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Sending photo　:)  ");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        final int totalProgressTime = 100;

        final Thread t = new Thread() {

            @Override
            public void run() {
                int jumpTime = 0;
                while (jumpTime < totalProgressTime) {
                    try {
                        sleep(200);
                        jumpTime += 5;
                        mProgressDialog.setProgress(jumpTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}



