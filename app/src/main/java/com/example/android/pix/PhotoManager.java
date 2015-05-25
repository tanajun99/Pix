package com.example.android.pix;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

public class PhotoManager {

    public static Bitmap resizeImage(byte[] imageData, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);

        options.inJustDecodeBounds = false;

        Bitmap reducedBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(reducedBitmap, targetWidth, targetHeight, false);

        return resizedBitmap;
    }

    public static Bitmap resizeImageMaintainAspectRatio(byte[] imageData, int shorterSideTarget) {
        Pair<Integer, Integer> dimensions = getDimensions(imageData);

        int imageWidth = dimensions.first;
        int imageHeight = dimensions.second;
        float ratio = (float) dimensions.first / dimensions.second;

        int targetWidth;
        int targetHeight;

        if (imageWidth > imageHeight) {
            targetHeight = shorterSideTarget;
            targetWidth = Math.round(shorterSideTarget * ratio);
        }
        else {
            targetWidth = shorterSideTarget;
            targetHeight = Math.round(shorterSideTarget / ratio);
        }

        return resizeImage(imageData, targetWidth, targetHeight);
    }

    public static Pair<Integer, Integer> getDimensions(byte[] imageData) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        return new Pair<Integer, Integer>(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}