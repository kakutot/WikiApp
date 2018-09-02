package com.example.romatupkalenko.wikiresearcherapp.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;

public class Listing<T> {
    private LiveData<PagedList<T>> pagedListLiveData;
    private LiveData<DataLoadingState> dataLoadingStateLiveData;

    public LiveData<DataLoadingState> getInitialdataLoadingStateLiveData() {
        return initialdataLoadingStateLiveData;
    }

    public void setInitialdataLoadingStateLiveData(LiveData<DataLoadingState> initialdataLoadingStateLiveData) {
        this.initialdataLoadingStateLiveData = initialdataLoadingStateLiveData;
    }

    private LiveData<DataLoadingState> initialdataLoadingStateLiveData;

    public LiveData<PagedList<T>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public void setPagedListLiveData(LiveData<PagedList<T>> pagedListLiveData) {
        this.pagedListLiveData = pagedListLiveData;
    }

    public LiveData<DataLoadingState> getDataLoadingStateLiveData() {
        return dataLoadingStateLiveData;
    }

    public void setDataLoadingStateLiveData(LiveData<DataLoadingState> dataLoadingStateLiveData) {
        this.dataLoadingStateLiveData = dataLoadingStateLiveData;
    }

    public Listing(LiveData<PagedList<T>> pagedListLiveData, LiveData<DataLoadingState> dataLoadingStateLiveData,
                   LiveData<DataLoadingState> initialdataLoadingStateLiveData) {
        this.pagedListLiveData = pagedListLiveData;
        this.dataLoadingStateLiveData = dataLoadingStateLiveData;
        this.initialdataLoadingStateLiveData = initialdataLoadingStateLiveData;

    }


}
