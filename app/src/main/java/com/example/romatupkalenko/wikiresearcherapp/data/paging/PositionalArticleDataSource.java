package com.example.romatupkalenko.wikiresearcherapp.data.paging;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.PositionalDataSource;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleDao;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.data.database.WikiResearchDatabase;

import java.util.List;

public class PositionalArticleDataSource extends PositionalDataSource<ArticleEntry> {
    private ArticleDao articleDao;

    private MutableLiveData<DataLoadingState> networkState;

    private MutableLiveData<DataLoadingState> initialLoadingNetworkState;


    private Context context;

    public PositionalArticleDataSource(Context context){
        networkState = new MutableLiveData<>();
        initialLoadingNetworkState = new MutableLiveData<>();
        this.context = context;
        articleDao = WikiResearchDatabase.getInstance(context).articleDao();
    }
    public MutableLiveData<DataLoadingState> getNetworkState() {
        return networkState;
    }

    public MutableLiveData<DataLoadingState> getInitialLoadingNetworkState() {
        return initialLoadingNetworkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ArticleEntry> callback) {
        initialLoadingNetworkState.postValue(DataLoadingState.LOADING);
        networkState.postValue(DataLoadingState.LOADING);

        Log.i(PositionalArticleDataSource.class.getSimpleName(),"Initial loadrange : " + params.requestedLoadSize
        +"init pos : " + params.requestedStartPosition);


       List<ArticleEntry> list = articleDao.getNotFavArticlesLimitOffset(params.requestedLoadSize,params.requestedStartPosition);
        Log.i("POSITION DATA SOURCE","INITIALY LOADED ARTICLES:");
        for (ArticleEntry articleEntry : list){
            Log.i("POSITION DATA SOURCE","Article : " + articleEntry);
        }

        initialLoadingNetworkState.postValue(DataLoadingState.LOADED);
        networkState.postValue(DataLoadingState.LOADED);

        callback.onResult(list,params.requestedStartPosition);


    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ArticleEntry> callback) {
        networkState.postValue(DataLoadingState.LOADING);
        Log.i(PositionalArticleDataSource.class.getSimpleName(),"After loadrange : " + params.loadSize
                +" | After init pos : " + params.startPosition);

        List<ArticleEntry> articles = articleDao.getNotFavArticlesLimitOffset
                (params.loadSize,params.startPosition);

        Log.i("POSITION DATA SOURCE","AFTER LOADED ARTICLES:");
        for (ArticleEntry articleEntry : articles){
            Log.i("POSITION DATA SOURCE","Article : " + articleEntry);
        }

        networkState.postValue(DataLoadingState.LOADED);
        callback.onResult(articles);


    }

}
