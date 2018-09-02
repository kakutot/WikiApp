package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.support.annotation.NonNull;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;

public class FetchedArticles {

    @NonNull
    private final ArticleEntry[] mFetchedArticles;

    public FetchedArticles(@NonNull final ArticleEntry[] fetchedArticles) {
        mFetchedArticles = fetchedArticles;
    }

    public ArticleEntry[] getmFetchedArticles() {
        return mFetchedArticles;
    }
}
