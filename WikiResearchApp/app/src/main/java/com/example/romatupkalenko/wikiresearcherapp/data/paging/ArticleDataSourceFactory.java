package com.example.romatupkalenko.wikiresearcherapp.data.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class ArticleDataSourceFactory extends DataSource.Factory {
    private MutableLiveData<PageKeyedArticleDataSource> liveDataSource;
    private PageKeyedArticleDataSource dataSource;
    private String searchTitle;
    public ArticleDataSourceFactory(String searchTitle){
        liveDataSource = new MutableLiveData<>();
        this.searchTitle = searchTitle;
    }
    @Override
    public DataSource create() {
        dataSource = new PageKeyedArticleDataSource(searchTitle);
        liveDataSource.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<PageKeyedArticleDataSource> getLiveDataSource() {
        return liveDataSource;
    }

}
