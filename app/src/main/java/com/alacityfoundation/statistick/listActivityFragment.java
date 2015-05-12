package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String url = this.getActivity().getIntent().getStringExtra("requestUrl");
        this.getActivity().getActionBar().setTitle(this.getActivity().getIntent().getStringExtra("forceName"));
        Log.d("URL", url);
        progressDialog = ProgressDialog.show(this.getActivity(), "Retrieving Data", "Please Wait");
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
                    crime.setPersistent_id(arrayObject.getString("persistent_id"));
                    crime.setPersistent_id(arrayObject.getString("id"));
                    crime.setCategory(arrayObject.getString("category"));
                    crime.setMonth(arrayObject.getString("month"));
                    // sort out possible nulls
                    if(arrayObject.isNull("location")) {
                        crime.setLocation("No information available");
                    } else {
                        crime.setLocation(arrayObject.getString("location"));
                    }

                    if(arrayObject.isNull("outcome_status")) {
                        crime.setOutcome_status("No information available");
                    } else {
                        crime.setOutcome_status(arrayObject.getJSONObject("outcome_status").getString("category"));
                    }

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
        final listAdapter adapter = new listAdapter(this.getActivity(), R.layout.crime_list, crimes.toArray());
        ListView crimeList = (ListView) this.getActivity().findViewById(R.id.crimeList);
        crimeList.setAdapter(adapter);
        progressDialog.dismiss();
    }
}
