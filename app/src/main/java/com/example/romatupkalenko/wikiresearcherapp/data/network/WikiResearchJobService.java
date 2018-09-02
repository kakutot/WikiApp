package com.example.romatupkalenko.wikiresearcherapp.data.network;

import android.util.Log;

import com.example.romatupkalenko.wikiresearcherapp.data.repository.Repository;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import com.example.romatupkalenko.wikiresearcherapp.AppExecutors;
import com.example.romatupkalenko.wikiresearcherapp.InjectorUtils;

public class WikiResearchJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ArticleNetworkDataSource dataSource = InjectorUtils.provideNetworkDataSource(this);
        Repository repository = InjectorUtils.provideRepository(this);
        repository.deleteAllArticles();
        dataSource.fetchArticles(null,true);
        jobFinished(jobParameters, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
