package com.example.romatupkalenko.wikiresearcherapp.data.repository;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.AppExecutors;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleDao;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.data.network.ArticleNetworkDataSource;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.PageKeyedArticleDataSource;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.PageKeyedArticleDataSourceFactory;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.PositionalArticleDataSourceFactory;

import java.util.List;
import java.util.concurrent.Executors;

public class Repository {

    private static final String LOG_TAG = Repository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static Repository sInstance;
    private final ArticleDao mArticleDao;
    private final ArticleNetworkDataSource mArticleNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private Repository(ArticleDao ArticleDao,
                       ArticleNetworkDataSource ArticleNetworkDataSource,
                       AppExecutors executors) {
        mArticleDao = ArticleDao;
        mArticleNetworkDataSource = ArticleNetworkDataSource;
        mExecutors = executors;

        // Observes random articles ,that are cached into db
        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        // It is the latest cached(downloaded) data from wiki server
        LiveData<ArticleEntry[]> networkData = mArticleNetworkDataSource.getCurrentArticles();

       //The most recent  network data is saved into db
        networkData.observeForever(newAriclesFromNetwork -> {
            mExecutors.diskIO().execute(() -> {
                // Deletes old historical data
                // Insert our new Article
                Log.i(LOG_TAG,"New articles from network:");
                int i = 0;
                for (ArticleEntry entry:
                        newAriclesFromNetwork) {
                    Log.i(LOG_TAG,"Articles["+ ++i + "] : " + entry);
                }
                mArticleDao.bulkInsert(newAriclesFromNetwork);
                Log.d(LOG_TAG, "New values inserted");
            });
        });


        mArticleDao.getAllArticles().observeForever((articles)->{
            if(articles.size()>0) mInitialized = true; }
        );
    }

    public synchronized static Repository getInstance(
            ArticleDao articleDao, ArticleNetworkDataSource articleNetworkDataSource,
           AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Repository(articleDao, articleNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */

    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        Log.i("Repository","initialization");

        if (mInitialized ) return;

        mArticleNetworkDataSource.scheduleRecurringFetchWeatherSync();

        mInitialized = true;
        mExecutors.diskIO().execute(() -> {
            Log.i("Repository","data fetching started");
                startFetchArticleService();
        });
    }

    /**
     * Database related operations
     **/

    public LiveData<List<ArticleEntry>> getCurrentArticles() {
        Log.i("Repository","Getting not fav articles");
        initializeData();
        return mArticleDao.getAllNotFavArticles();
    }



    public LiveData<List<ArticleEntry>> getAllFavArticles(){
         return mArticleDao.getAllFavArticles();
    }

    public void makeFavArticle(ArticleEntry entry){
            if(entry!=null&&entry.getId()<=0){
                entry.setFaved(true);
                mExecutors.diskIO().execute(()-> {
                    mArticleDao.insertArticle(entry);
                    Log.i(LOG_TAG,"New article is inserted with fav = true");
                });
            }
            else {
                mExecutors.diskIO().execute(()-> {
                    mArticleDao.favArticle(entry.getId());
                    Log.i(LOG_TAG,"Existing article is updated with fav = true");
                        });
            }

    }

    public void unFavArticle(int pageId){
        mExecutors.diskIO().execute(()-> {
            mArticleDao.unfavArticle(pageId);
                });
              }

    public void deleteArticle(int pageId){
        mExecutors.diskIO().execute(()-> {
            mArticleDao.deleteArticleByPageId(pageId);
                });

    }

    public void addArticle(ArticleEntry article){
        mExecutors.diskIO().execute(()-> {
            mArticleDao.insertArticle(article);
                });

    }

    public void deleteAllArticles(){
        mExecutors.diskIO().execute(() -> {
            Log.i("Repository","clear initial random article data ");
            mArticleDao.deleteAllNotFaved();
            Log.d(LOG_TAG, "Old Articles deleted");
        });
    }
    /**
     * Network related operation
     */

    private void startFetchArticleService() {
        mArticleNetworkDataSource.startFetchArticleService();
    }

    /**
     * Paging stuff
     */
    public Listing<ArticleEntry> getSearchArticleData(Context context,String title, int pageSize){
        Log.i(LOG_TAG,"GetSearchArticleData");
        if(title.isEmpty())
            return null;
        PageKeyedArticleDataSourceFactory factory = new PageKeyedArticleDataSourceFactory(context,title);

        PagedList.Config config =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(pageSize)
                        .setPageSize(pageSize).build();

        LiveData<PagedList<ArticleEntry>> pagedListLiveData = new LivePagedListBuilder<>(factory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .build();

        LiveData<DataLoadingState> dataState = Transformations.switchMap(factory.getLiveDataSource(),data->data.getNetworkState());
        LiveData<DataLoadingState> inDataState = Transformations.switchMap(factory.getLiveDataSource(),data->data.getInitialLoadingNetworkState());

        return new Listing<>(pagedListLiveData,dataState,inDataState);
    }

    public Listing<ArticleEntry> getRandomArticleData(Context context){
        PositionalArticleDataSourceFactory factory = new PositionalArticleDataSourceFactory(context);

        PagedList.Config config =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setPageSize(5).setInitialLoadSizeHint(10).build();

        LiveData<DataLoadingState> dataState = Transformations.switchMap(factory.getLiveDataSource(),data->data.getNetworkState());
        LiveData<DataLoadingState> inDataState = Transformations.switchMap(factory.getLiveDataSource(),data->data.getInitialLoadingNetworkState());

        LiveData<PagedList<ArticleEntry>> pagedListLiveData = new LivePagedListBuilder<>(factory, config)
                .setFetchExecutor(Executors.newSingleThreadExecutor())
                .setBoundaryCallback(new PagedList.BoundaryCallback() {
                    @SuppressLint("WrongThread")
                    @Override
                    public void onItemAtEndLoaded(@NonNull Object itemAtEnd) {
                        //super.onItemAtEndLoaded(itemAtEnd);
                        factory.getLiveDataSource().getValue().getNetworkState().postValue(DataLoadingState.LOADING);

                        factory.getDataSource().invalidate();

                        mArticleNetworkDataSource.fetchArticles(null,true);
                       // factory.getLiveDataSource().getValue().getNetworkState().postValue(DataLoadingState.LOADING);

                    }
                })
                .build();



        return new Listing<>(pagedListLiveData,dataState,inDataState);

    }
}
