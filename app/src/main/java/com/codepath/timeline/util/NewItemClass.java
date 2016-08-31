package com.codepath.timeline.util;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.codepath.timeline.R;

import java.io.File;
import java.util.UUID;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NewItemClass extends AppCompatActivity {

    public final String APP_TAG = "TimelineApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String randomPhotoFileName = "photo.jpg";
    public Uri takenPhotoUri;

    // on click attached to text view id="@+id/tvAddLocation"
    public void addLocation(View view) {
        if (Integer.parseInt(view.getTag().toString()) == 1) {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            view.setTag(2);
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            view.setTag(1);
        }
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
        NewItemClassPermissionsDispatcher.accessLocationWithCheck(this);
    }

    // Todo: add collaborators to the story
    // on click attached to text view id="@+id/tvAddPeople"
    public void addPeople(View view) {
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
    }

    // on click attached to frame layout id="@+id/flStoryPhoto"
    public void onLaunchCamera(View view) {
//        Snackbar.make(findViewById(android.R.id.content), "clicked", Snackbar.LENGTH_SHORT).show();
        NewItemClassPermissionsDispatcher.openCameraWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void accessLocation() {
        // Trigger the access to location here
       // Todo: access location http://guides.codepath.com/android/Retrieving-Location-with-LocationServices-API
    }

    // Todo: https://developer.android.com/training/camera/photobasics.html
    // https://developer.android.com/training/camera/cameradirect.html
    @NeedsPermission(Manifest.permission.CAMERA)
    void openCamera() {
        randomPhotoFileName = UUID.randomUUID().toString();

        // Trigger the opening of a camera here
//        Snackbar.make(findViewById(android.R.id.content), "here", Snackbar.LENGTH_SHORT).show();
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri()); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void readStorage() {
        // Trigger the reading storage
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void writeStorage() {
        // Trigger the writing to a storage
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri() {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            // Todo: remove permissions for writing/accessing the storage
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + randomPhotoFileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // Todo: all these permissions are must for API 23, put in a separate class if reused

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NewItemClassPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleToAccessStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_access_storage_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleToWriteStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_write_storage_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleAccessLocation(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_access_location_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.cancel();
                    }
                })
                .show();
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_camera_rationale)
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        request.cancel();
                    }
                })
                .show();
    }

    // Annotate a method which is invoked if the user doesn't grant the permissions
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showDeniedForLocation() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    // Annotates a method which is invoked if the user
    // chose to have the device "never ask again" about a permission
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showNeverAskForLocation() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_neverask, Snackbar.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showDeniedForCamera() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void showNeverAskForCamera() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_neverask, Snackbar.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForReadStorage() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForReadStorage() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_neverask, Snackbar.LENGTH_SHORT).show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForWriteStorage() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_denied, Snackbar.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForWriteStorage() {
        Snackbar.make(findViewById(android.R.id.content), R.string.permission_neverask, Snackbar.LENGTH_SHORT).show();
    }
}
