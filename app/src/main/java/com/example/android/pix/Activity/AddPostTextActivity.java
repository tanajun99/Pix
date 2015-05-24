package com.example.android.pix.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.pix.FileManager;
import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddPostTextActivity extends ActionBarActivity {

    ImageView mPhoto;
    EditText mTitle;
    EditText mComment;
    Button mPost;
    Button mCancel;
    Uri getUri;
    Uri getGetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text_post);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap=null;
        InputStream inputStream = null;
        mPhoto = (ImageView)findViewById(R.id.preview_image);
        mTitle = (EditText)findViewById(R.id.add_title);
        mComment = (EditText)findViewById(R.id.add_comment);
        mPost = (Button)findViewById(R.id.post_add_text);
        mCancel = (Button)findViewById(R.id.cancel_add_text);
        getUri = getIntent().getData();

        try {
            inputStream = getContentResolver().openInputStream(getUri);
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bitmap != null){
            mPhoto.setImageBitmap(bitmap);
        }

        getGetUri = getIntent().getData();

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTitle==null){
                    Toast.makeText(AddPostTextActivity.this, R.string.add_texts, Toast.LENGTH_LONG);
                }
                else if (mComment==null){
                    Toast.makeText(AddPostTextActivity.this, R.string.add_texts,Toast.LENGTH_LONG);

                }
                else{
                    ParseObject post = createPost();
                    post(post);
                    Intent intent = new Intent(AddPostTextActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPostTextActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ParseObject createPost() {
        ParseObject post = new ParseObject(ParseConstants.CLASS_POST);
        ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());

        String title = mTitle.getText().toString().trim();
        String comment = mComment.getText().toString().trim();
        String fleType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE_POST);

        postACL.setPublicReadAccess(true);
        post.put(ParseConstants.KEY_POST_ID, ParseUser.getCurrentUser().getObjectId());
        post.put(ParseConstants.KEY_POST_NAME, ParseUser.getCurrentUser().getUsername());
        post.setACL(postACL);
        post.put(ParseConstants.KEY_FILE_TYPE_POST,fleType);
        post.put(ParseConstants.KEY_POST_TITLE,title);
        post.put(ParseConstants.KEY_POST_COMMENT, comment);

        byte[] fileBytes = FileManager.getByteArrayFromFile(this, getGetUri);

        if (fileBytes == null) {
            return null;
        }
        else {
            if (fleType.equals(ParseConstants.TYPE_IMAGE_SEND)) {
                fileBytes = FileManager.reduceImageForUpload(fileBytes);
            }

            String fileName = FileManager.getFileName(this, getUri, fleType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            post.put(ParseConstants.KEY_FILE_POST, file);

            return post;
        }
    }

    protected void post(ParseObject post) {
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // success!
                    Toast.makeText(AddPostTextActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddPostTextActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPostTextActivity.this);
                    builder.setMessage(R.string.error_sending_message)
                            .setTitle(R.string.error_selecting_file_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}

