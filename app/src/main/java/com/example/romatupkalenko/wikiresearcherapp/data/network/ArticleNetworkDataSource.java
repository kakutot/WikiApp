package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.AppExecutors;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class ArticleNetworkDataSource {
    // The number of days we want our API to return, set to 14 days or two weeks
    private static final String LOG_TAG = ArticleNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the Article. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String WIKI_RESEARCH_SYNC_TAG = "wikiresearch-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static ArticleNetworkDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded Articles
    private final MutableLiveData<ArticleEntry[]> mDownloadedArticles;
    private final AppExecutors mExecutors;

    private String mCategory;

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    private ArticleNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedArticles = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static ArticleNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.i(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new ArticleNetworkDataSource(context.getApplicationContext(), executors);
                Log.i(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<ArticleEntry[]> getCurrentArticles() {
        return mDownloadedArticles;
    }

    /**
     * Starts an intent service to fetch the Article.
     */
    public void startFetchArticleService() {
        Intent intentToFetch = new Intent(mContext,ArticleIntentService.class);
       // intentToFetch.putExtra(ArticleIntentService.CATEGORY,mCategory);
        mContext.startService(intentToFetch);
        Log.i(LOG_TAG, "Service created");
    }
    public void scheduleRecurringFetchWeatherSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync Sunshine
        Job syncSunshineJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync Sunshine's data */
                .setService(WikiResearchJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(WIKI_RESEARCH_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncSunshineJob);
        Log.d(LOG_TAG, "Job scheduled");
    }
   public void fetchArticles(final String category,Boolean isRandom) {
        Log.i(LOG_TAG, "Fetch articles started");
        mExecutors.networkIO().execute(() -> {
            try {
                //URL for articles

                URL articles;
                if(isRandom){
                    articles = NetworkUtils.buildUrlForRandomArticles();
                }
                else{
                    articles = NetworkUtils.buildUrlForCategory("Physics",10);
                }

                // Response from category query
                String jsonArticles = NetworkUtils.getResponseFromHttpUrl(articles);
                Log.i(LOG_TAG,"Response :" + jsonArticles);
                JsonParser parser = new JsonParser();
                //Articles have : pageId / title

                // Parse the JSON into a list of articles
                FetchedArticles fetchedArticles;
                if(isRandom){
                    fetchedArticles = new FetchedArticles(parser.parseRandomArticlesQuery(jsonArticles));
                }
                else {
                    fetchedArticles = new FetchedArticles(parser.parseCategoryQuery(jsonArticles));
                }

                for (ArticleEntry articleEntry : fetchedArticles.getmFetchedArticles()){
                    //URL for page
                    URL currPageUrl = NetworkUtils.buildUrlForOnePage(articleEntry.getPageId());

                    // Response from page query
                    String jsonPage = NetworkUtils.getResponseFromHttpUrl(currPageUrl);
                    Log.i(LOG_TAG,"Current Page Response :" + jsonPage);
                    //Article has : pageUrl / imageTitle
                    ArticleEntry tempEntry = parser.parsePageQuery(jsonPage,articleEntry.getPageId());

                    articleEntry.setPageUrl(tempEntry.getPageUrl());

                    //URL for image
                    URL jsonImageUrl = NetworkUtils.buildUrlForOneImage(tempEntry.getImageTitle());

                    // Response from image query
                    String jsonImage = NetworkUtils.getResponseFromHttpUrl(jsonImageUrl);
                    //Article has : imageUrl
                    tempEntry = parser.parseImageQuery(jsonImage);

                    articleEntry.setImageUrl(tempEntry.getImageUrl());
                }

                Log.i(LOG_TAG, "JSON Parsing finished");


                // As long as there are weather forecasts, update the LiveData storing the most recent
                // weather forecasts. This will trigger observers of that LiveData, such as the
                // SunshineRepository.
                if (fetchedArticles != null && fetchedArticles.getmFetchedArticles().length != 0) {
                    Log.i(LOG_TAG, "JSON not null and has " + fetchedArticles.getmFetchedArticles().length
                            + " values");
                    // When you are off of the main thread and want to update LiveData, use postValue.
                    // It posts the update to the main thread.
                    mDownloadedArticles.postValue(fetchedArticles.getmFetchedArticles());

                    // If the code reaches this point, we have successfully performed our sync
                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }
        });
    }
}
