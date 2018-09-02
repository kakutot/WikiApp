package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;

public class MySearchResponseDeserializer implements JsonDeserializer<MySearchResponse> {

    @Override
    public MySearchResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();
       // Log.i("MySearchResponse","INITIAL OBJECT :" + jsonObject.toString());
            JsonObject query = jsonObject.get("query").getAsJsonObject();
     //   Log.i("MySearchResponse","Query OBJECT :" + query.toString());
            MySearchResponse response = new MySearchResponse();

            ArticleEntry[] articles = context.deserialize(query.get("search").getAsJsonArray(),ArticleEntry[].class);
            for(ArticleEntry articleEntry : articles){
              //  Log.i("MySearchResponse", articleEntry.toString());
            }

            response.setArticles(Arrays.asList(articles));
        return response;
    }
}
