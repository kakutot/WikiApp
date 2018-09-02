package com.example.romatupkalenko.wikiresearcherapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;


import com.example.romatupkalenko.wikiresearcherapp.data.repository.Listing;
import com.example.romatupkalenko.wikiresearcherapp.data.repository.Repository;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;

import java.util.List;

public class MainViewModel extends ViewModel {
    //Data repository - responsible for the data provision
    private final Repository mRepository;

    //The concrete data needed to be persisted through the UI states changes
    private  LiveData<List<ArticleEntry>> mArticles;

    private LiveData<List<ArticleEntry>> mFavArticles;

    //Paging stuff
    private MutableLiveData<String> currentTitle;

    private LiveData<DataLoadingState> dataLoadingState;

    private LiveData<Listing<ArticleEntry>> repoResult;

    private LiveData<PagedList<ArticleEntry>> articleList = new MutableLiveData<>();;

    public LiveData<DataLoadingState> getDataLoadingState() {
        return dataLoadingState;
    }

    public LiveData<Listing<ArticleEntry>> getRepoResult() {
        return repoResult;
    }
    public String getCurrentTitle() {
        return currentTitle.getValue();
    }

    public void setCurrentTitle(String currentTitle) {
        this.currentTitle.postValue(currentTitle);
    }


    public MainViewModel(Repository repository) {
        mRepository = repository;
        mArticles = mRepository.getCurrentArticles();
        mFavArticles = mRepository.getAllFavArticles();
        currentTitle = new MutableLiveData<>();
        repoResult = Transformations.map(currentTitle,title->mRepository.getArticleData(title,10));
        articleList = Transformations.switchMap(repoResult,res->res.getPagedListLiveData());
        dataLoadingState = new MutableLiveData<>();
        dataLoadingState = Transformations.switchMap(repoResult,res->res.getDataLoadingStateLiveData());
    }
    //Db operations on data
    public LiveData<List<ArticleEntry>> getArticles() {
        return mArticles;
    }

    public void makeFavArticle(ArticleEntry article){
        mRepository.makeFavArticle(article);
    }

    public void unFavArticle(int pageId){
        mRepository.unFavArticle(pageId);
    }

    public void deleteArticle(int pageId){
        mRepository.deleteArticle(pageId);
    }

    public LiveData<List<ArticleEntry>> getFavArticles() {
        return mFavArticles;
    }


}
