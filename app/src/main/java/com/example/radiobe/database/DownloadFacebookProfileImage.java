package com.example.radiobe.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFacebookProfileImage extends AsyncTask<Void, Void, Bitmap> {
    String uriString;
    FacebookProfilePictureDownloadedListener listener;


    public DownloadFacebookProfileImage(String uriString , FacebookProfilePictureDownloadedListener listener){
        this.uriString = uriString;
        this.listener = listener;
    }
    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        Uri uri = Uri.parse(uriString);
        try {

//            URL imgUrl = new URL("https://graph.facebook.com/{user-id}/picture?type=large");
//            InputStream in = (InputStream) imgUrl.getContent();
//            Bitmap  bitmap = BitmapFactory.decodeStream(in);


            URL url = new URL(uriString);
//            URLConnection con = url.openConnection();

//            InputStream in = con.getInputStream();

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

