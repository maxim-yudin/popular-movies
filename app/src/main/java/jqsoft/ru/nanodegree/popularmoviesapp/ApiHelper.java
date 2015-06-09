package jqsoft.ru.nanodegree.popularmoviesapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    public static String getMovieList(String sortOrder) {
        return getJsonFromUrl(UrlBuilder.getMovieList(sortOrder));
    }

    private static String getJsonFromUrl(String sourceUrl) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {
            URL url = new URL(sourceUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder sbJson = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                sbJson.append(line);
            }

            if (sbJson.length() == 0) {
                return null;
            }
            jsonStr = sbJson.toString();
        } catch (IOException e) {
            Log.e("ApiHelper", "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ApiHelper", "Error closing stream", e);
                }
            }
        }
        return jsonStr;
    }
}
