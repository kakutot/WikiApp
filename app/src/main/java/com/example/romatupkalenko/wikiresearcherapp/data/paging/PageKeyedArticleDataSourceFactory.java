package com.example.romatupkalenko.wikiresearcherapp.data.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.content.Context;

public class PageKeyedArticleDataSourceFactory extends DataSource.Factory {
    private MutableLiveData<PageKeyedArticleDataSource> liveDataSource;
    private PageKeyedArticleDataSource dataSource;
    private String searchTitle;
    private Context context;

    public PageKeyedArticleDataSourceFactory(Context context,String searchTitle){
        liveDataSource = new MutableLiveData<>();
        this.searchTitle = searchTitle;
        this.context = context;
    }
    @Override
    public DataSource create() {
        dataSource = new PageKeyedArticleDataSource(context,searchTitle);
        liveDataSource.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<PageKeyedArticleDataSource> getLiveDataSource() {
        return liveDataSource;
    }

}
