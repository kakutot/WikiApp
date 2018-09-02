package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import android.media.Image;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class MyImageInfoResponseDeserializer implements JsonDeserializer<MyImageInfoResponse> {
    @Override
    public MyImageInfoResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MyImageInfoResponse response = new MyImageInfoResponse();
       if(json!=null){
           JsonObject jsonObject = json.getAsJsonObject();

           if(jsonObject.get("query")!=null){
               JsonObject query = jsonObject.get("query").getAsJsonObject();
               if(query.get("pages")!=null){
                   JsonObject pages = query.get("pages").getAsJsonObject();
                   Set<Map.Entry<String, JsonElement>> entries = pages.entrySet();
                   String key = null;
                   for (Map.Entry<String,JsonElement> entry : entries){
                       key = entry.getKey();
                       break;
                   }
                   // Log.i("MyImageInfoResponse","Key :"+key);

                   if(pages.get(key)!=null){
                       JsonObject numb = pages.get(key).getAsJsonObject();

                       JsonArray imageinfo = numb.get("imageinfo").getAsJsonArray();

                       if(imageinfo.get(0)!=null){
                           JsonObject imageinfoF= imageinfo.get(0).getAsJsonObject();
                           response.setImageUrl(imageinfoF.get("url").getAsString());
                       }

                   }

               }

           }
       }
        //Log.i("MyImageInfoResponse","Response :"+response);
        return response;
    }
}
