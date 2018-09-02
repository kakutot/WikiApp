package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import android.media.Image;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class MyPageInfoResponseDeserializer implements JsonDeserializer<MyPageInfoResponse> {
    @Override
    public MyPageInfoResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
       // Log.i("MyPageInfoResponse","INITIAL OBJECT :" + jsonObject.toString());
        JsonObject query = jsonObject.get("query").getAsJsonObject();
       // Log.i("MyPageInfoResponse","QUERY OBJECT :" + query.toString());
        JsonObject pages = query.get("pages").getAsJsonObject();
      //  Log.i("MyPageInfoResponse","PAGES OBJECT :" + pages.toString());
        Set<Map.Entry<String, JsonElement>> entries = pages.entrySet();
        String key = null;
        for (Map.Entry<String,JsonElement> entry : entries){
            key = entry.getKey();
            break;
        }
       // Log.i("MyPageInfoResponse","Key :"+key);

        JsonObject keyArticle = pages.get(key).getAsJsonObject();
        ArticleEntry article = context.deserialize(keyArticle,ArticleEntry.class);
        if(null!=keyArticle.get("images")){
            JsonArray images = keyArticle.get("images").getAsJsonArray();

            JsonObject image = images.get(0).getAsJsonObject();

            String imageTitle = image.get("title").getAsString();

            article.setImageTitle(imageTitle);
        }


       // Log.i("MyPageInfoResponse","Article :"+ article);
        MyPageInfoResponse response = new MyPageInfoResponse();

        response.setArticle(article);

        return response;
    }
}
