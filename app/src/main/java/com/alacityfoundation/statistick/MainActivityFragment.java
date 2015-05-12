package com.alacityfoundation.statistick;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class MainActivityFragment extends Fragment {
    private ArrayList<Force> forces = new ArrayList<>();
    private ArrayList<Crime> crimes = new ArrayList<>();
    private final ArrayList<String> months = new ArrayList<String>() {{
        add("January");
        add("February");
        add("March");
        add("April");
        add("May");
        add("June");
        add("July");
        add("August");
        add("September");
        add("October");
        add("November");
        add("December");
    }};
    private final ArrayList<Integer> years = new ArrayList<Integer>() {{
        for(int i = 2000; i < 2016; i++) {
            add(i);
        }
    }};

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // perform web request to get list of all police forces
        new RequestTask(new Force()).execute("https://data.police.uk/api/forces");

        // return inflated view
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    // called on button press
    public void getCrime(View view) {

    }

    private void populateSpinners() {
        // loop through results and add to spinner
        Spinner forceSpinner = (Spinner) this.getActivity().findViewById(R.id.forceSpinner);
        ArrayAdapter<String> forceAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.forces);
        forceSpinner.setAdapter(forceAdapter);

        // populate month & year spinners
        Spinner yearSpinner = (Spinner) this.getActivity().findViewById(R.id.yearSpinner);
        Spinner monthSpinner = (Spinner) this.getActivity().findViewById(R.id.monthSpinner);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.years);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter(this.getActivity().getBaseContext(), android.R.layout.simple_spinner_item, this.months);

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);
    }

    // inner class for dealing with the getting and deserialising of crime and force objects from API
    public class RequestTask extends AsyncTask<String, Void, String> {
        private Object returnObjectType;

        /**
         * Creates a new instance of the RequestTask object.
         * @param obj the type of object we expect to get back as represented by JSON
         */
        public RequestTask(Object obj) {
            this.returnObjectType = obj;
        }

        public RequestTask() {
            super();
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

        @Override
        protected void onPostExecute(String result) {
            Log.d("RESULTS", result);
            try {
                // deserialise the json objects and add to the appropriate instance variable
                JSONArray jsonResult = new JSONArray(result);
                for(int i = 0; i < jsonResult.length(); i++) {
                    // for all results in array, deserialise into returnObjectType objects
                    Object resultObject = this.decodeJson(this.returnObjectType.getClass(), jsonResult.getString(i));
                    // and add to appropriate instance variables depending on class
                    if(this.returnObjectType instanceof Force) {
                        forces.add((Force) resultObject);
                    } else if (this.returnObjectType instanceof Crime) {
                        crimes.add((Crime) resultObject);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // populate spinners with new ly deserialised objects
            populateSpinners();
        }

        // turns JSON into clazz object
        private <T> T decodeJson(Class<T> clazz, String json) {
            Object obj = new Gson().fromJson(json, clazz);
            return (T) obj;
        }
    }
}
