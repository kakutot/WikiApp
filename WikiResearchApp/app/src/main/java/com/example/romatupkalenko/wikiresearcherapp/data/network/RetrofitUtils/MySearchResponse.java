package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;

import java.util.List;

public class MySearchResponse {
    public List<ArticleEntry> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleEntry> articles) {
        this.articles = articles;
    }

    private List<ArticleEntry> articles;

    public MySearchResponse(){
    }

}
