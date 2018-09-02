package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikiService {

    //https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch=intitle:test&continue=-%7C%7C&format=json&srlimit=100
    @GET("?action=query&format=json&list=search&srwhat=text")
    Call<MySearchResponse> getInitSearchResponse(
            @Query("srsearch") /*intitle:*/ String srsearch,
            @Query("srlimit") int srlimit);

    @GET("?action=query&format=json&list=search&srwhat=text&continue=-||")
    Call<MySearchResponse> getNextSearchResponse(
            @Query("srsearch") String srsearch,
            @Query("sroffset") int sroffset,
            @Query("srlimit") int srlimit
    );

    @GET("?action=query&format=json&prop=info|images&inprop=url")
    Call<MyPageInfoResponse> getPageInfo(@Query("pageids") int pageid);

    @GET("?action=query&format=json&prop=imageinfo&iiprop=url")
    Call<MyImageInfoResponse> getImageInfo(@Query("titles") String title);
}
