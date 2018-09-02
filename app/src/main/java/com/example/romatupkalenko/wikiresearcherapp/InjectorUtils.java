package com.example.romatupkalenko.wikiresearcherapp;

import android.content.Context;

import com.example.romatupkalenko.wikiresearcherapp.data.repository.Repository;
import com.example.romatupkalenko.wikiresearcherapp.data.database.WikiResearchDatabase;
import com.example.romatupkalenko.wikiresearcherapp.data.network.ArticleNetworkDataSource;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModelFactory;

public class InjectorUtils {
    public static Repository provideRepository(Context context) {
        WikiResearchDatabase database =  WikiResearchDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        ArticleNetworkDataSource networkDataSource =
                ArticleNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return Repository.getInstance(database.articleDao(), networkDataSource, executors);
    }

    public static ArticleNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return ArticleNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }
    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        Repository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }
}
