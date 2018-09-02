package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.InjectorUtils;

public class ArticleIntentService extends IntentService {
    private static final String LOG_TAG = ArticleIntentService.class.getSimpleName();
    public static final String CATEGORY = "category";
    public ArticleIntentService() {
        super(ArticleIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(LOG_TAG, "Intent service started");
        ArticleNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
       //String category = intent.getExtras().getString(CATEGORY);
       networkDataSource.fetchArticles(null,true);
    }
}
