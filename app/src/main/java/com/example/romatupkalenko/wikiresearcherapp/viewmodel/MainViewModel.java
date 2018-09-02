package com.example.romatupkalenko.wikiresearcherapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.content.Context;


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

    public MutableLiveData<String> getCurrentTitle() {
        return currentTitle;
    }

    //Paging stuff
    private MutableLiveData<String> currentTitle;

    private LiveData<DataLoadingState> dataLoadingState;

    private LiveData<Listing<ArticleEntry>> searchRepoResult;

    private Listing<ArticleEntry> randomRepoResult;


    public Listing<ArticleEntry> getRandomRepoResult() {
        return randomRepoResult;
    }

    public LiveData<Listing<ArticleEntry>> getSearchRepoResult() {
        return searchRepoResult;
    }


    public void setCurrentTitle(String currentTitle) {
        this.currentTitle.postValue(currentTitle);
    }

    private Context context;
    public void setContext(Context context) {
        this.context = context;
    }


    public MainViewModel(Repository repository) {
        mRepository = repository;
        mArticles = mRepository.getCurrentArticles();
        mFavArticles = mRepository.getAllFavArticles();
        //Search paging (data from server)
        currentTitle = new MutableLiveData<>();
        searchRepoResult = Transformations.map(currentTitle, title->mRepository.getSearchArticleData(context,title,10));
        randomRepoResult = mRepository.getRandomArticleData(context);
       // articleList = Transformations.switchMap(searchRepoResult, res->res.getPagedListLiveData());

       // dataLoadingState = Transformations.switchMap(searchRepoResult, res->res.getDataLoadingStateLiveData());
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
