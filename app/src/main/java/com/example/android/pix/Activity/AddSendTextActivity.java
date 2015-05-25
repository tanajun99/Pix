package com.example.android.pix.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.pix.ParseConstants;
import com.example.android.pix.R;
import com.rey.material.widget.Button;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddSendTextActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_add_text_send);

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
        final String fleType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE_SEND);
        getGetUri = getIntent().getData();
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTitle==null){
                    Toast.makeText(AddSendTextActivity.this, R.string.add_texts,Toast.LENGTH_LONG).show();
                }
                else if (mComment==null){
                    Toast.makeText(AddSendTextActivity.this, R.string.add_texts,Toast.LENGTH_LONG).show();

                }
                else{
                    final String title = mTitle.getText().toString().trim();
                    final String comment = mComment.getText().toString().trim();

                    Intent intent = new Intent(AddSendTextActivity.this, MessageRecipientActivity.class);
                    intent.putExtra(ParseConstants.KEY_SEND_TITLE,title);
                    intent.putExtra(ParseConstants.KEY_SEND_COMMENT, comment);
                    intent.setData(getGetUri);
                    intent.putExtra(ParseConstants.KEY_FILE_TYPE_SEND,fleType);
                    startActivity(intent);
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSendTextActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
