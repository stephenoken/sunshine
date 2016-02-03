package com.zooop.sunshine.openweathermap;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.zooop.sunshine.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by stephenokennedy on 25/01/2016.
 */
public class FetchWeatherTask extends AsyncTask<String,Void,String[]>{
    private ArrayAdapter<String> mForecastAdapter;

    public FetchWeatherTask(ArrayAdapter<String> mForecastAdapter){
        this.mForecastAdapter = mForecastAdapter;
    }
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    /*
        The date/time conversion code is going to be moved outside the async later,
        so for convience we're breaking it out into its own method now
     */
    private String getReadableString(long time){
        //Because the API returns a unix timestamp,
        // it must be converted to milliseconds in order to be converted to valid date
        return new SimpleDateFormat("EEE MMM dd").format(time);
    }

    /*
        Prepare the weather high/lows for presentation
     */
    private String formatHighLows(double high,double low){
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /*
        Take the string represeting the complete forecast in JSON format and
        pull tge data we need to construct the strings needed for the wireframes

     */

    private String[] getWeatherDataFromJson(String forecastJSONStr, int numDays) throws JSONException{

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJSON = new JSONObject(forecastJSONStr);
        JSONArray weatherArray = forecastJSON.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.
        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            String day;
            String desciption;
            String highAndLow;
            //Get JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);
            long dateTime;
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableString(dateTime);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            desciption = weatherObject.getString(OWM_DESCRIPTION);
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high,low);
            resultStrs[i] = day + " - " + desciption + " - " + highAndLow;
        }
        return resultStrs;
    }
    @Override
    protected String[] doInBackground(String... params) {
        return getWeatherData(params);
    }

    public String[] getWeatherData(String[] params) {
        //If there's no zip code, there's nothing to do.
        // TODO: 26/01/2016 Will need to change this coordinates
        if (params.length == 0)
            return null;
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String format ="json";
        String units ="metric";
        String forecastJsonStr = "";
        int numDays = 7;
        try {
//            String baseUrl = "http://api.openweathermap.org/data/2.5/find?q=London&units=metric";
//            String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
//            URL url = new URL(baseUrl.concat(apiKey));

            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String APPID_PARAM = "appid";
            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,params[0])
                    .appendQueryParameter(FORECAST_BASE_URL,format)
                    .appendQueryParameter(UNITS_PARAM,units)
                    .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM,BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
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
            forecastJsonStr =  buffer.toString();

            try {
                return getWeatherDataFromJson(forecastJsonStr,numDays);
            }catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            Log.e("ForecastFragment", "Error", e);
            return null;
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

    @Override
    protected void onPostExecute(String[] results) {
        if (results != null){
            mForecastAdapter.clear();
            for (String dayForecatStr: results) {
                mForecastAdapter.add(dayForecatStr);
            }
            //New data from the server Whoop!!!
        }
    }
}
