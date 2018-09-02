package com.example.romatupkalenko.wikiresearcherapp.data.network;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import com.example.romatupkalenko.wikiresearcherapp.AppExecutors;
import com.example.romatupkalenko.wikiresearcherapp.InjectorUtils;

public class WikiResearchJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ArticleNetworkDataSource dataSource = InjectorUtils.provideNetworkDataSource(this);
        dataSource.fetchArticles(null,true);

        jobFinished(jobParameters, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
