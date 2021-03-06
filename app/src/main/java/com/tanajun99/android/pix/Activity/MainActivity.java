package com.tanajun99.android.pix.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.tanajun99.android.pix.Fragment.InboxRecyclerViewFragment;
import com.tanajun99.android.pix.Fragment.MembersFragment;
import com.tanajun99.android.pix.Fragment.TimeLineRecyclerViewFragment;
import com.tanajun99.android.pix.ParseConstants;

import com.github.clans.fab.FloatingActionButton;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.parse.ParseUser;
import com.tanajun99.android.pix.R;


public class MainActivity extends ActionBarActivity {

    private MaterialViewPager mViewPager;
    private Toolbar toolbar;
    String postOrSend;

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10 MB

    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which) {
//                        case 0: // Take picture
//                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//
//                            if (mMediaUri == null) {
//                                // display an error
//                                Toast.makeText(MainActivity.this, R.string.error_external_storage,
//                                        Toast.LENGTH_LONG).show();
//                            }
//                            else {
//                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//                                startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
//                            }
//                            break;
//                        case 1: // Take video
//                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//                            if (mMediaUri == null) {
//                                // display an error
//                                Toast.makeText(MainActivity.this, R.string.error_external_storage,
//                                        Toast.LENGTH_LONG).show();
//                            }
//                            else {
//                                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//                                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
//                                videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
//                                startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
//                            }
//                            break;
                        case 0:
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                            break;
                        case 1:
                            Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVideoIntent.setType("video/*");
                            Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                            break;
                    }
                }

                private Uri getOutputMediaFileUri(int mediaType) {
                    if (isExternalStorageAvailable()) {

                        String appName = MainActivity.this.getString(R.string.app_name);
                        File mediaStorageDir = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                appName);

                        if (! mediaStorageDir.exists()) {
                            if (! mediaStorageDir.mkdirs()) {
                                Log.e(TAG, "Failed to create directory.");
                                return null;
                            }
                        }

                        File mediaFile;
                        Date now = new Date();
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                        String path = mediaStorageDir.getPath() + File.separator;
                        if (mediaType == MEDIA_TYPE_IMAGE) {
                            mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                        }
                        else if (mediaType == MEDIA_TYPE_VIDEO) {
                            mediaFile = new File(path + "VID_" + timestamp + ".mp4");
                        }
                        else {
                            return null;
                        }

                        Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                        // 5. Return the file's URI
                        return Uri.fromFile(mediaFile);
                    }
                    else {
                        return null;
                    }
                }

                private boolean isExternalStorageAvailable() {
                    String state = Environment.getExternalStorageState();

                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        toolbar = mViewPager.getToolbar();
        //mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setHomeButtonEnabled(false);
            }
        }

//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
//        mDrawer.setDrawerListener(mDrawerToggle);

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            int oldPosition = -1;

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return InboxRecyclerViewFragment.newInstance();
                    case 1:
                        return TimeLineRecyclerViewFragment.newInstance();
                    case 2:
                        return MembersFragment.newInstance();
                    default:
                        return TimeLineRecyclerViewFragment.newInstance();
                }
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);

                //only if position changed
                if (position == oldPosition)
                    return;
                oldPosition = position;

                int color = 0;
                String imageUrl = "";
                switch (position) {
                    case 0:
                        imageUrl = "http://tanajun99.com/wp-content/uploads/2015/05/background1.png";
                        color = getResources().getColor(R.color.blue);
                        break;
                    case 1:
                        imageUrl = "";
                        color = getResources().getColor(R.color.green);
                        break;
                    case 2:
                        imageUrl = "";
                        color = getResources().getColor(R.color.cyan);
                        break;
                    case 3:
                        imageUrl = "";
                        color = getResources().getColor(R.color.red);
                        break;
                }
                final int fadeDuration = 400;
                //mViewPager.setImageUrl(imageUrl,fadeDuration);
                //mViewPager.setColor(color,fadeDuration);

            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "INBOX";
                    case 1:
                        return "TIMELINE";
                    case 2:
                        return "FRIENDS";
                }
                return "";
            }
        });
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        mViewPager.getViewPager().setCurrentItem(1);

        final FloatingActionButton fabSend = (FloatingActionButton)findViewById(R.id.menu_send);
        fabSend.setColorNormal(R.color.actionButton);
        fabSend.setColorNormalResId(R.color.actionButton);
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOrSend = "Send";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final FloatingActionButton fabFriends = (FloatingActionButton)findViewById(R.id.menu_friends);
        fabFriends.setColorNormal(R.color.actionButton);
        fabFriends.setColorNormalResId(R.color.actionButton);
        fabFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditMembersActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton fabPost = (FloatingActionButton)findViewById(R.id.menu_post);
        fabPost.setColorNormal(R.color.actionButton);
        fabPost.setColorNormalResId(R.color.actionButton);
        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postOrSend = "Post";
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri = data.getData();
                }
                Log.i(TAG, "Media URI: " + mMediaUri);
                if (requestCode == PICK_VIDEO_REQUEST) {
                    // make sure the file is less than 10 MB
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) { /* Intentionally blank */ }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            if(postOrSend=="Send") {
                Intent recipientsIntent = new Intent(this, AddSendTextActivity.class);
                recipientsIntent.setData(mMediaUri);
                String fileType;
                if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                    fileType = ParseConstants.TYPE_IMAGE_SEND;
                }
                else {
                    fileType = ParseConstants.TYPE_VIDEO_SEND;
                }

                recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE_SEND, fileType);
                startActivity(recipientsIntent);
            }
            else{
                Intent postIntent = new Intent(this, AddPostTextActivity.class);
                postIntent.setData(mMediaUri);
                String fileType;
                if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                    fileType = ParseConstants.TYPE_IMAGE_POST;
                }
                else {
                    fileType = ParseConstants.TYPE_VIDEO_POST;
                }

                postIntent.putExtra(ParseConstants.KEY_FILE_TYPE_POST, fileType);
                startActivity(postIntent);
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
