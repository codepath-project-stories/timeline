package com.codepath.timeline.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.codepath.timeline.view.BitmapScaler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    // Return the byte[] representation for a given photoUri
    public static byte[] getImageData(String photoUri) {
        byte[] imageByte = null;

        try {
            Bitmap rawTakenImage = BitmapFactory.decodeFile(photoUri);
            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 700);
            // Configure byte output stream
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            // Compress the image further
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
            String path = photoUri + "_resized";
            Log.d(TAG, "Photo path: " + path);
            File resizedFile = new File(path);

            resizedFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(resizedFile);
            // Write the bytes of the bitmap to file
            imageByte = bytes.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "Exception from getImageData: " + e.getMessage());
        }

        return imageByte;
    }
}
