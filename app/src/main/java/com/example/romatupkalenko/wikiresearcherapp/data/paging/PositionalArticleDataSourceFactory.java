package com.example.romatupkalenko.wikiresearcherapp.data.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.Context;

public class PositionalArticleDataSourceFactory  extends DataSource.Factory {
    private MutableLiveData<PositionalArticleDataSource> liveDataSource;
    private PositionalArticleDataSource dataSource;
    private Context context;

    public PositionalArticleDataSourceFactory(Context context){
        liveDataSource = new MutableLiveData<>();
        this.context = context;
    }
    @Override
    public DataSource create() {
        dataSource = new PositionalArticleDataSource(context);
        liveDataSource.postValue(dataSource);
        return dataSource;
    }
    public PositionalArticleDataSource getDataSource() {
        return dataSource;
    }
    public  MutableLiveData<PositionalArticleDataSource> getLiveDataSource() {
        return liveDataSource;
    }
}
