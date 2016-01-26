package com.zooop.sunshine.openweathermap;

import android.os.AsyncTask;
import android.util.Log;

import com.zooop.sunshine.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stephenokennedy on 25/01/2016.
 */
public class FetchWeatherTask extends AsyncTask<Void,Void,String>{

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
    @Override
    protected String doInBackground(Void... params) {
        return getWeatherData();
    }

    public String getWeatherData() {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            String baseUrl = "http://api.openweathermap.org/data/2.5/find?q=London&units=metric";
            String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
            URL url = new URL(baseUrl.concat(apiKey));

            //Create request to OpenWeatherMap, and then open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
                return null;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            //Not Necessary
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debug
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0)
                return null;//Stream was empty
            return buffer.toString();
        } catch (IOException e) {
            Log.e("ForecastFragment", "Error", e);
            return "Error";
        }finally {
            if(urlConnection !=null)
                urlConnection.disconnect();
            if (reader !=null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

    }
}
