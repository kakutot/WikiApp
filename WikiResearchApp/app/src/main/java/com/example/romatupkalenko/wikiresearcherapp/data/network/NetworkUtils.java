package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_QUERY_URL =
            "https://en.wikipedia.org/w/api.php?action=query";
    private static final String FORMAT = "format";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String FORMAT_VALUE_JSON = "json";
    //<--------------------------------------------------------------------------->
    private static final String LIST_PARAM = "list";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String LIST_PARAM_VALUE_CATEGORY_MEMBERS = "categorymembers";
    //<--------------------------------------------------------------------------->
    private static final String CATEGORY_TITLE_PARAM = "cmtitle";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String CATEGORY_TITLE_PARAM_INIT_VALUE = "Category:";
    //<--------------------------------------------------------------------------->
    private static final String LIMIT_PARAM = "cmlimit";

    private static final String PAGE_ID_PARAM = "pageids";

    private static final String PROP_PARAM = "prop";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String PROP_PARAM_VALUE_INFO = "info";
    private static final String PROP_PARAM_VALUE_IMAGES = "images";
    private static final String PROP_PARAM_VALUE_IMAGE_INFO = "imageinfo";
    //<--------------------------------------------------------------------------->

    private static final String INPROP_PARAM = "inprop";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String INPROP_VALUE_PARAM = "url";
    //<--------------------------------------------------------------------------->

    private static final String TITLES_PARAM = "titles";

    private static final String IIPROP_PARAM = "iiprop";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String IIPROP_PARAM_VALUE_URL = "url";
    //<--------------------------------------------------------------------------->
    private static final String QUERY_VALUE_DELIMETER = "|";

    private static final String GENERATOR_PARAM = "generator";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String GENERATOR_PARAM_VALUE_RANDOM = "random";
    //<--------------------------------------------------------------------------->
    private static final String GRNAMESPACE_PARAM = "grnnamespace";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String GRNAMESPACE_PARAM_ZERO_VALUE = "0";
    //<--------------------------------------------------------------------------->

    private static final String RVPROP_PARAM = "rvprop";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String RVPROP_PARAM_CONTENT = "content";
    //<--------------------------------------------------------------------------->
    private static final String GRNLIMIT_PARAM = "grnlimit";
    //POSSIBLE QUERY VALUES
    //<--------------------------------------------------------------------------->
    private static final String GRNLIMIT_PARAM_DEFAULT_VALUE = "10";
    //<--------------------------------------------------------------------------->
    /**
     * Builds the URL used to get articles from a certain category
     *
     * @return The URL to use to query the wiki server for category elements .
     */

    //https://en.wikipedia.org/w/api.php?format=json&action=query&generator=random&grnnamespace=0&prop=images&rvprop=content&grnlimit=10
    static URL buildUrlForRandomArticles(){
        Uri categoryQueryUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendQueryParameter(GENERATOR_PARAM,GENERATOR_PARAM_VALUE_RANDOM)
                .appendQueryParameter(GRNAMESPACE_PARAM,GRNAMESPACE_PARAM_ZERO_VALUE)
                .appendQueryParameter(PROP_PARAM,PROP_PARAM_VALUE_IMAGES)
                .appendQueryParameter(RVPROP_PARAM,RVPROP_PARAM_CONTENT)
                .appendQueryParameter(GRNLIMIT_PARAM,GRNLIMIT_PARAM_DEFAULT_VALUE)
                .appendQueryParameter(FORMAT, FORMAT_VALUE_JSON)
                .build();

        try {
            URL categoryQueryUrl = new URL(categoryQueryUri.toString());
            Log.i(TAG, "URL: " + categoryQueryUrl);
            return categoryQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    //https://en.wikipedia.org/w/api.php?action=query&list=categorymembers&cmtitle=Category:Physics&cmlimit=10h
    static URL buildUrlForCategory(String category, int limit) {
        Uri categoryQueryUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendQueryParameter(LIST_PARAM, LIST_PARAM_VALUE_CATEGORY_MEMBERS)
                .appendQueryParameter(CATEGORY_TITLE_PARAM, CATEGORY_TITLE_PARAM_INIT_VALUE + " " +category)
                .appendQueryParameter(LIMIT_PARAM, String.valueOf(limit))
                .appendQueryParameter(FORMAT, FORMAT_VALUE_JSON)
                .build();

        try {
            URL categoryQueryUrl = new URL(categoryQueryUri.toString());
            Log.i(TAG, "URL: " + categoryQueryUrl);
            return categoryQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Builds the URL used to get info about certain article
     *
     * @param pageid The id of article page
     * @return The URL to use to query the wiki server for the certain article.
     */

    //https://en.wikipedia.org/w/api.php?action=query&pageids=22939&prop=info|images&inprop=url
    static URL buildUrlForOnePage(int pageid) {
        Uri pageQueryUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendQueryParameter(PAGE_ID_PARAM, String.valueOf(pageid))
                .appendQueryParameter(PROP_PARAM, PROP_PARAM_VALUE_INFO + QUERY_VALUE_DELIMETER + PROP_PARAM_VALUE_IMAGES)
                .appendQueryParameter(INPROP_PARAM, IIPROP_PARAM_VALUE_URL)
                .appendQueryParameter(FORMAT, FORMAT_VALUE_JSON)
                .build();

        try {
            URL finalQuery = new URL(pageQueryUri.toString());
            Log.v(TAG, "URL: " + finalQuery);
            return finalQuery;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * Builds the URL used to get info about certain image
     *
     * @param imageTitle The image title (all titles are unique)
     * @return The URL to use to query the wiki server for the certain image.
     */

    //https://commons.wikimedia.org/w/api.php?action=query&titles=File:Albert%20Einstein%20Head.jpg&prop=imageinfo&iiprop=url
    static URL buildUrlForOneImage(String imageTitle) {
        Uri imageQueryUri = Uri.parse(BASE_QUERY_URL).buildUpon()
                .appendQueryParameter(TITLES_PARAM, imageTitle)
                .appendQueryParameter(PROP_PARAM, PROP_PARAM_VALUE_IMAGE_INFO)
                .appendQueryParameter(IIPROP_PARAM, IIPROP_PARAM_VALUE_URL)
                .appendQueryParameter(FORMAT, FORMAT_VALUE_JSON)
                .build();
        try {
            URL finalQuery = new URL(imageQueryUri.toString());
            Log.v(TAG, "URL: " + finalQuery);
            return finalQuery;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.i(TAG, "GETING RESPONSE");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = null;
        String response = null;
        try {
            int resCode = urlConnection.getResponseCode();
            if(resCode==200){
                in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();

                if (hasInput) {
                    response = scanner.next();
                }
                scanner.close();
            }
            else{
                throw new IOException("Bad response" + resCode);
            }
            return response;
        }

        finally {
            urlConnection.disconnect();
            in.close();
        }
    }

}
