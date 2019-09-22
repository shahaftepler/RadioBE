package com.example.radiobe.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;
import java.net.URL;

public class DownloadFacebookProfileImage extends AsyncTask<Void, Void, Bitmap> {
    private String uriString;
    private FacebookProfilePictureDownloadedListener listener;


    DownloadFacebookProfileImage(String uriString, FacebookProfilePictureDownloadedListener listener){
        this.uriString = uriString;
        this.listener = listener;
    }
    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(uriString);
            InputStream in = (InputStream) url.getContent();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(listener != null){
            listener.done(bitmap);
        }

    }
}

