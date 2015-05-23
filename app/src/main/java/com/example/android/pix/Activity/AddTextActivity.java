package com.example.android.pix.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddTextActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_add_text);

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

        final String fleType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        getGetUri = getIntent().getData();

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTitle==null){
                    Toast.makeText(AddTextActivity.this, R.string.add_texts,Toast.LENGTH_LONG);
                }
                else if (mComment==null){
                    Toast.makeText(AddTextActivity.this, R.string.add_texts,Toast.LENGTH_LONG);

                }
                else{
                    final String title = mTitle.getText().toString().trim();
                    final String comment = mComment.getText().toString().trim();

                    Intent intent = new Intent(AddTextActivity.this, MessageRecipientActivity.class);
                    intent.putExtra(ParseConstants.KEY_SEND_TITLE,title);
                    intent.putExtra(ParseConstants.KEY_SEND_COMMENT, comment);
                    intent.setData(getGetUri);
                    intent.putExtra(ParseConstants.KEY_FILE_TYPE,fleType);
                    startActivity(intent);
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTextActivity.this, MainActivity.class);
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
}
