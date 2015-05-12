package com.alacityfoundation.statistick;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ryan on 12/05/2015.
 */
public class UrlDownloader {

    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DEBUG", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = this.readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // read HTTP response and return as string
    private String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String line = null;

        StringBuilder responseData = new StringBuilder();
        while((line = in.readLine()) != null) {
            responseData.append(line);
        }
        in.close();
        return responseData.toString();
    }
}
