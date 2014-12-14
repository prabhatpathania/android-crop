package com.soundcloud.android.crop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.soundcloud.android.crop.util.VisibleForTesting;

/**
 * Builder for crop Intents and utils for handling result
 */
public class Crop {

    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;

    static interface Extra {
        String ASPECT_X = "aspect_x";
        String ASPECT_Y = "aspect_y";
        String MAX_X = "max_x";
        String MAX_Y = "max_y";
        String OUT_X = "out_x";
        String OUT_Y = "out_y";
        String ERROR = "error";
    }

    private Intent cropIntent;

    /**
     * Create a crop Intent builder with source image
     *
     * @param source Source image URI
     */
    public Crop(Uri source) {
        cropIntent = new Intent();
        cropIntent.setData(source);
    }

    /**
     * Set output URI where the cropped image will be saved
     *
     * @param output Output image URI
     */
    public Crop output(Uri output) {
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, output);
        return this;
    }

    /**
     * Set fixed aspect ratio for crop area
     *
     * @param x Aspect X
     * @param y Aspect Y
     */
    public Crop withAspect(int x, int y) {
        cropIntent.putExtra(Extra.ASPECT_X, x);
        cropIntent.putExtra(Extra.ASPECT_Y, y);
        return this;
    }

    /**
     * Crop area with fixed 1:1 aspect ratio
     */
    public Crop asSquare() {
        cropIntent.putExtra(Extra.ASPECT_X, 1);
        cropIntent.putExtra(Extra.ASPECT_Y, 1);
        return this;
    }

    /**
     * Set maximum crop size
     *
     * @param width Max width
     * @param height Max height
     */
    public Crop withMaxSize(int width, int height) {
        cropIntent.putExtra(Extra.MAX_X, width);
        cropIntent.putExtra(Extra.MAX_Y, height);
        return this;
    }

    /**
     * Set the out size of the image
     *
     * @param width Out bitmap width
     * @param height Out bitmap height
     */
    public Crop withOutputSize(int width, int height) {
        cropIntent.putExtra(Extra.OUT_X, width);
        cropIntent.putExtra(Extra.OUT_Y, height);
        return this;
    }

    /**
     * Send the crop Intent!
     *
     * @param activity Activity that will receive result
     */
    public void start(Activity activity) {
        activity.startActivityForResult(getIntent(activity), REQUEST_CROP);
    }

    /**
     * Send the crop Intent!
     *
     * @param activity Activity that will receive result
     * @param requestCode Activity request code
     */
    public void start(Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the crop Intent!
     *
     * @param context Context
     * @param fragment Fragment that will receive result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(Context context, Fragment fragment) {
        fragment.startActivityForResult(getIntent(context), REQUEST_CROP);
    }

    /**
     * Send the crop Intent!
     *
     * @param context Context
     * @param fragment Fragment that will receive result
     * @param requestCode Activity request code
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(Context context, Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    @VisibleForTesting
    Intent getIntent(Context context) {
        cropIntent.setClass(context, CropImageActivity.class);
        return cropIntent;
    }

    /**
     * Retrieve URI for cropped image, as set in the Intent builder
     *
     * @param result Output Image URI
     */
    public static Uri getOutput(Intent result) {
        return result.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
    }

    /**
     * Retrieve error that caused crop to fail
     *
     * @param result Result Intent
     * @return Throwable handled in CropImageActivity
     */
    public static Throwable getError(Intent result) {
        return (Throwable) result.getSerializableExtra(Extra.ERROR);
    }

    /**
     * Utility method that starts an image picker since that often precedes a crop
     *
     * @param activity Activity that will receive result
     */
    public static void pickImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        try {
            activity.startActivityForResult(intent, REQUEST_PICK);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.crop__pick_error, Toast.LENGTH_SHORT).show();
        }
    }

}
