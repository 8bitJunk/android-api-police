package com.alacityfoundation.statistick;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ryan on 08/05/2015.
 */
class RequestTask extends AsyncTask<String, String, String> {
    private OnTaskCompleted listener;
    private Object returnObjectType;

    /**
     * Creates a new instance of the RequestTask object.
     * @param listener the entity which will deal with the completed request
     * @param obj the type of object we expect to get back as represented by JSON
     */
    public RequestTask(OnTaskCompleted listener, Object obj) {
        this.listener = listener;
        this.returnObjectType = obj;
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        String result = "";
        try {
            result = this.downloadUrl(urls[0]);
        } catch (IOException e) {
            Log.d("OH DEAR", "Something went wrong");
            e.printStackTrace();
        }
        return result;
    }

    private String downloadUrl(String myurl) throws IOException {
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
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String line = null;

        StringBuilder responseData = new StringBuilder();
        while((line = in.readLine()) != null) {
            responseData.append(line);
        }
        in.close();
        return responseData.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // process results using registered listener method from the listener entity
        listener.onTaskCompleted(this.returnObjectType, result);
        return;
    }
}
