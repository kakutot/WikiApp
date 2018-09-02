package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.app.DownloadManager;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JsonParser {
    private static final String LOG_TAG = JsonParser.class.getSimpleName();
    private static final String QUERY = "query";
    private static final String CATEGORY_MEMBERS = "categorymembers";
    private static final String PAGE_ID = "pageid";
    private static final String TITLE = "title";
    private static final String PAGES = "pages";
    private static final String URL ="url";
    private static final String URL_CANON ="canonicalurl";
    private static final String IMAGE_INFO = "imageinfo";
    private static final String IMAGES = "images";
    /**
     * Builds the URL used to get info about certain category
     *
     * @param categoryResponse jsonResponse from category query
     * @return ArticleEntry array ,entries have only pageId and title
     */
    public ArticleEntry[] parseCategoryQuery(String categoryResponse){
        ArticleEntry [] articleEntries = null;
        try {
            JSONObject root = new JSONObject(categoryResponse);

            JSONObject queryObject = root.getJSONObject(QUERY);

            JSONArray categoryMembers = queryObject.getJSONArray(CATEGORY_MEMBERS);

            articleEntries  = new ArticleEntry[categoryMembers.length()];

            for(int i = 0;i< categoryMembers.length(); i++){
                ArticleEntry entry = new ArticleEntry();

                JSONObject currentEntry = categoryMembers.getJSONObject(i);

                int cPageId = currentEntry.getInt(PAGE_ID);

                String cTitle = currentEntry.getString(TITLE);

                entry.setPageId(cPageId);
                entry.setTitle(cTitle);

                articleEntries[i] = entry;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG,"Current articles:");
        int i = 0;
        for (ArticleEntry entry:
             articleEntries) {
            Log.i(LOG_TAG,"Articles["+ ++i + "] : " + entry);
        }

    return articleEntries;

    }
    /**
     * Builds the URL used to get info about certain article
     *
     * @param pageResponse jsonResponse from category query
     * @param pageId page id used for the query creation
     * @return ArticleEntry  has pageURL and imageTitle
     */
    public ArticleEntry parsePageQuery(String pageResponse,int pageId){
    ArticleEntry articleEntry = new ArticleEntry();
        try {
            JSONObject root = new JSONObject(pageResponse);
                JSONObject queryObject = root.getJSONObject(QUERY);
                    JSONObject pages = queryObject.getJSONObject(PAGES);
                        JSONObject page = pages.getJSONObject(String.valueOf(pageId));
                            String pageUrl = page.getString(URL_CANON);
                            articleEntry.setPageUrl(pageUrl);
                            if(page.has(IMAGES)){
                                JSONArray images = page.getJSONArray(IMAGES);
                                JSONObject firstImage = images.getJSONObject(0);
                                String firstImageTitle = firstImage.getString(TITLE);
                                articleEntry.setImageTitle(firstImageTitle);
                            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  articleEntry;
    }
    /**
     * Builds the URL used to get info about certain image
     *
     * @param pageResponse jsonResponse from image query
     * @return ArticleEntry array ,entries have only imageURL
     */
    public ArticleEntry parseImageQuery(String pageResponse){
        ArticleEntry articleEntry = new ArticleEntry();
        try {
            JSONObject root = new JSONObject(pageResponse);
              JSONObject queryObject = root.getJSONObject(QUERY);
                  JSONObject pages = queryObject.getJSONObject(PAGES);
                  JSONObject page;
                    Iterator iterator = pages.keys();
                    String pageid ;
                    pageid = (String) iterator.next();
                    page = pages.getJSONObject(pageid);
                        if(page.has(IMAGE_INFO)){
                            JSONArray imageInfo = page.getJSONArray(IMAGE_INFO);
                            JSONObject imageInfoEl = imageInfo.getJSONObject(0);
                            articleEntry.setImageUrl(imageInfoEl.getString(URL));
                        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  articleEntry;
    }

    public ArticleEntry[] parseRandomArticlesQuery(String response){
        List<ArticleEntry> articleEntries = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(response);
                JSONObject queryObject = root.getJSONObject(QUERY);
                    JSONObject pagesObject = queryObject.getJSONObject(PAGES);
                    Iterator iterator = pagesObject.keys();
                    String pageid ;
                        JSONObject page;

                        while (iterator.hasNext()){
                            pageid = (String) iterator.next();
                            page = pagesObject.getJSONObject(pageid);
                            ArticleEntry entry = new ArticleEntry();
                            entry.setPageId(page.getInt(PAGE_ID));
                            entry.setTitle(page.getString(TITLE));
                            articleEntries.add(entry);
                        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArticleEntry[] arr = new ArticleEntry[articleEntries.size()];
        int i = 0;
        for(ArticleEntry entry : articleEntries){
            arr[i] = entry;
            i++;
        }
        return arr;
    }

}


