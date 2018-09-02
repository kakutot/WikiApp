package com.example.romatupkalenko.wikiresearcherapp.data.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils.MyImageInfoResponse;
import com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils.MyPageInfoResponse;
import com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils.MySearchResponse;
import com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils.WikiApi;
import com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils.WikiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageKeyedArticleDataSource extends PageKeyedDataSource<Integer, ArticleEntry> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private WikiService wikiService;

    private MutableLiveData<DataLoadingState> networkState;

    private MutableLiveData<DataLoadingState> initialLoadingNetworkState;

    private String searchTitle;

    private int currentOffset;

    private int lastLimit;

    public MutableLiveData<DataLoadingState> getNetworkState() {
        return networkState;
    }

    public void setNetworkState(MutableLiveData<DataLoadingState> networkState) {
        this.networkState = networkState;
    }

    public MutableLiveData<DataLoadingState> getInitialLoadingNetworkState() {
        return initialLoadingNetworkState;
    }

    public void setInitialLoadingNetworkState(MutableLiveData<DataLoadingState> initialLoadingNetworkState) {
        this.initialLoadingNetworkState = initialLoadingNetworkState;
    }



    public PageKeyedArticleDataSource(String searchTitle){

    wikiService = WikiApi.getWikiService();

    initialLoadingNetworkState = new MutableLiveData<>();

    networkState = new MutableLiveData<>();

    this.searchTitle = searchTitle;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, ArticleEntry> callback) {
        Log.i(LOG_TAG,"Initial loadrange : " + params.requestedLoadSize);

        initialLoadingNetworkState.postValue(DataLoadingState.LOADING);
        networkState.postValue(DataLoadingState.LOADING);

        lastLimit = params.requestedLoadSize;
        currentOffset = params.requestedLoadSize;

        Call<MySearchResponse> call = wikiService.getInitSearchResponse(searchTitle,params.requestedLoadSize);
        // 1 : Asynchronous query to get initial list of articles,which contain articles TITLE,PAGEID
        call.enqueue(new Callback<MySearchResponse> (){
        List<ArticleEntry> articleResponses;
            @Override
            public void onResponse(Call<MySearchResponse> call, Response<MySearchResponse> response) {
                if (response.isSuccessful()) {

                articleResponses = response.body().getArticles();

                // 2 : Asynchronous queries  to get articles images
                    final int []counter = new int[1];
                    for(final ArticleEntry articleResponse : articleResponses){

                        Call<MyPageInfoResponse> myPageInfoResponse = wikiService.getPageInfo(articleResponse.getPageId());

                        myPageInfoResponse.enqueue(new Callback<MyPageInfoResponse>() {
                            @Override
                            public void onResponse(Call<MyPageInfoResponse> call, Response<MyPageInfoResponse> response) {
                                if (response.isSuccessful()) {
                                    ArticleEntry responseArticleResponse = response.body().getArticle();

                                    articleResponse.setPageUrl(responseArticleResponse.getPageUrl());

                                    Call<MyImageInfoResponse> myImageInfoResponse = wikiService.getImageInfo(
                                            responseArticleResponse.getImageTitle());
                                    // 3 : Asynchronous queries  to get article image url
                                    myImageInfoResponse.enqueue(new Callback<MyImageInfoResponse>() {
                                        @Override
                                        public void onResponse(Call<MyImageInfoResponse> call, Response<MyImageInfoResponse> response) {
                                          if(response.isSuccessful()){
                                              initialLoadingNetworkState.postValue(DataLoadingState.LOADED);
                                              networkState.postValue(DataLoadingState.LOADED);

                                              String imageUrl = response.body().getImageUrl();

                                              articleResponse.setImageUrl(imageUrl);
                                          }
                                          else {
                                              initialLoadingNetworkState.postValue(DataLoadingState.FAILED);
                                              networkState.postValue(DataLoadingState.FAILED);
                                          }
                                            if(counter[0]++==articleResponses.size()-1){
                                                Log.i(LOG_TAG,"Initial load MyPageInfoResponse size: "+ counter[0] +
                                                        " | articles :  " + articleResponses);
                                                callback.onResult(articleResponses,null,currentOffset);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyImageInfoResponse> call, Throwable t) {
                                            initialLoadingNetworkState.postValue(DataLoadingState.FAILED);
                                            networkState.postValue(DataLoadingState.FAILED);
                                            Log.i(LOG_TAG,"MyImageInfoResponseError :  " + t.getMessage());
                                        }
                                    });
                                }
                                else {
                                    initialLoadingNetworkState.postValue(DataLoadingState.FAILED);
                                    networkState.postValue(DataLoadingState.FAILED);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyPageInfoResponse> call, Throwable t) {
                                initialLoadingNetworkState.postValue(DataLoadingState.FAILED);
                                networkState.postValue(DataLoadingState.FAILED);
                                Log.i(LOG_TAG,"MyPageInfoResponseError :  " + t.getMessage());
                            }
                        });

                    }
                }
                else{
                    initialLoadingNetworkState.postValue(DataLoadingState.FAILED);
                    networkState.postValue(DataLoadingState.FAILED);
                }
            }

            @Override
            public void onFailure(Call<MySearchResponse> call, Throwable t) {
                Log.i(LOG_TAG,"Initial load MySearchResponse ERROR :  " + t.getMessage());
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ArticleEntry> callback) {
        //no need to implement this one
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, ArticleEntry> callback) {
        Log.i(LOG_TAG, "AfterLoad Initial loadrange : " + params.requestedLoadSize);

        networkState.postValue(DataLoadingState.LOADING);

        lastLimit = params.requestedLoadSize;
        currentOffset += params.requestedLoadSize;
        Log.i(LOG_TAG, "AfterLoad Currrent ofsset : " + currentOffset);
        Log.i(LOG_TAG, "AfterLoad Limit : " + lastLimit);
        Call<MySearchResponse> call = wikiService.getNextSearchResponse(searchTitle, currentOffset, params.requestedLoadSize);
        // 1 : Asynchronous query to get initial list of articles,which contain articles TITLE,PAGEID
        call.enqueue(new Callback<MySearchResponse> (){
            List<ArticleEntry> articleResponses;
            @Override
            public void onResponse(Call<MySearchResponse> call, Response<MySearchResponse> response) {
                if (response.isSuccessful()) {

                    articleResponses = response.body().getArticles();

                    // 2 : Asynchronous queries  to get articles images
                    final int []counter = new int[1];
                    for(final ArticleEntry articleResponse : articleResponses){

                        Call<MyPageInfoResponse> myPageInfoResponse = wikiService.getPageInfo(articleResponse.getPageId());

                        myPageInfoResponse.enqueue(new Callback<MyPageInfoResponse>() {
                            @Override
                            public void onResponse(Call<MyPageInfoResponse> call, Response<MyPageInfoResponse> response) {
                                if (response.isSuccessful()) {
                                    ArticleEntry responseArticleResponse = response.body().getArticle();

                                    articleResponse.setPageUrl(responseArticleResponse.getPageUrl());

                                    Call<MyImageInfoResponse> myImageInfoResponse = wikiService.getImageInfo(
                                            responseArticleResponse.getImageTitle());
                                    // 3 : Asynchronous queries  to get article image url
                                    myImageInfoResponse.enqueue(new Callback<MyImageInfoResponse>() {
                                        @Override
                                        public void onResponse(Call<MyImageInfoResponse> call, Response<MyImageInfoResponse> response) {
                                            if(response.isSuccessful()){

                                                networkState.postValue(DataLoadingState.LOADED);

                                                String imageUrl = response.body().getImageUrl();

                                                articleResponse.setImageUrl(imageUrl);
                                            }
                                            else {
                                                networkState.postValue(DataLoadingState.FAILED);
                                            }
                                            if(counter[0]++==articleResponses.size()-1){
                                                Log.i(LOG_TAG,"Afterload MyPageInfoResponse size: "+ counter[0] +
                                                        " | articles :  " + articleResponses);
                                                callback.onResult(articleResponses,currentOffset);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyImageInfoResponse> call, Throwable t) {
                                            networkState.postValue(DataLoadingState.FAILED);
                                            Log.i(LOG_TAG,"MyImageInfoResponseError :  " + t.getMessage());
                                        }
                                    });
                                }
                                else {
                                    networkState.postValue(DataLoadingState.FAILED);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyPageInfoResponse> call, Throwable t) {

                                networkState.postValue(DataLoadingState.FAILED);
                                Log.i(LOG_TAG,"MyPageInfoResponseError :  " + t.getMessage());
                            }
                        });

                    }
                }
                else{

                    networkState.postValue(DataLoadingState.FAILED);
                }
            }

            @Override
            public void onFailure(Call<MySearchResponse> call, Throwable t) {
                Log.i(LOG_TAG,"Initial load MySearchResponse ERROR :  " + t.getMessage());
            }
        });
    }
}
