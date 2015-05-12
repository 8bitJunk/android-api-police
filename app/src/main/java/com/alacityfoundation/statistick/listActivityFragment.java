package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class listActivityFragment extends Fragment {
    private ArrayList<Crime> crimes = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String url = this.getActivity().getIntent().getStringExtra("requestUrl");
        new RequestTask().execute(url);

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    // inner class for dealing with the getting and deserialising of crime and force objects from API
    public class RequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            String result = "";
            try {
                result = new UrlDownloader().downloadUrl(urls[0]);
            } catch (IOException e) {
                Log.d("OH DEAR", "Something went wrong");
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("RESULTS", result);
            try {
                // deserialise the json objects and add to the appropriate instance variable
                JSONArray jsonResult = new JSONArray(result);
                for(int i = 0; i < jsonResult.length(); i++) {
                    // for all results in array, deserialise into returnObjectType objects
                    Crime resultObject = this.decodeJson(Crime.class, jsonResult.getString(i));
                    crimes.add(resultObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // populate spinners with new ly deserialised objects
            populateList();
        }

        // turns JSON into clazz object
        private <T> T decodeJson(Class<T> clazz, String json) {
            Object obj = new Gson().fromJson(json, clazz);
            return (T) obj;
        }
    }

    private void populateList() {
        Log.d("CRIMES FOUND", this.crimes.get(0).toString());
    }
}
