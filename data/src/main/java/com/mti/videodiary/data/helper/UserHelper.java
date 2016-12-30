package com.mti.videodiary.data.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mti.videodiary.data.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import static android.graphics.Bitmap.Config.RGB_565;
import static android.provider.MediaStore.MediaColumns.DATA;
import static com.mti.videodiary.data.Constants.IMAGE_DIR;


/**
 * Created by Taras Matolinets on 15.11.14.
 * Class with help methods
 */
public class UserHelper {

    private static final String JPG = ".jpg";

    /**
     * hide keyboard under viewDivider
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no viewDivider has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Log.e(Constants.TAG, "exception " + e.toString());
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);

            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void copyFileUsingFileStreams(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "exception " + e.toString());
        } finally {

            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                Log.e(Constants.TAG, "exception " + e.toString());
            }
        }
    }

    public static String saveBitmapToSD(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();

        File imageDir = new File(root + IMAGE_DIR);

        boolean isExist = imageDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";

        if (!isExist) {
            final File file = new File(imageDir, fname + JPG);

            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                Log.e(Constants.TAG, "exception " + e.toString());
            }

            return file.getAbsolutePath();
        }
        return null;
    }
}
