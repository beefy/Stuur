package com.stuur.stuur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Evan on 3/12/2016.
 */
public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

    WeakReference<ImageView> imageViewReference;

    public BitmapWorkerTask(ImageView imageView){
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(File... params) {
        return BitmapFactory.decodeFile(params[0].getAbsolutePath());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(bitmap != null && imageViewReference != null){
            ImageView viewImage = imageViewReference.get();
            if (viewImage != null){
                viewImage.setImageBitmap(bitmap);
            }
        }

    }


}
