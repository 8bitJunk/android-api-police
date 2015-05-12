package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class listActivityFragment extends Fragment {
    private ArrayList<Crime> crimes = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String url = this.getActivity().getIntent().getStringExtra("requestUrl");
        Log.d("URL", url);
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
                    // for all results in array, deserialise into crime objects
                    JSONObject arrayObject = jsonResult.getJSONObject(i);
                    Crime crime = new Crime();
                    crime.setId(arrayObject.getString("persistent_id"));
                    crimes.add(crime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // populate list with newly deserialised objects
            populateList();
        }

        // turns JSON into clazz object
        private <T> T decodeJson(Class<T> clazz, String json) {
            Object obj = new Gson().fromJson(json, clazz);
            return (T) obj;
        }
    }

    private void populateList() {
        Log.d("CRIMES FOUND", this.crimes.toString());
    }
}
