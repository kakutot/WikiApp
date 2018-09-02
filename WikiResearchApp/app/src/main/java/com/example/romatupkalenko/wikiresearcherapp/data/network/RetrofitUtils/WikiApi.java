package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WikiApi {
    private final static String BASE_URL = "https://en.wikipedia.org/w/api.php/";
    private static WikiService instance;
    public static WikiService getWikiService(){
        if(instance==null)
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit.Builder retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).
                    addConverterFactory(createGson());
            instance = retrofit.build().create(WikiService.class);
        }

        return instance;
    }

    private static GsonConverterFactory createGson()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setLenient();
        // Adding custom deserializers
        gsonBuilder.registerTypeAdapter(MySearchResponse.class, new MySearchResponseDeserializer());
        gsonBuilder.registerTypeAdapter(MyPageInfoResponse.class,new MyPageInfoResponseDeserializer());
        gsonBuilder.registerTypeAdapter(MyImageInfoResponse.class,new MyImageInfoResponseDeserializer());
        Gson myGson = gsonBuilder.create();

        return GsonConverterFactory.create(myGson);
    }

}
