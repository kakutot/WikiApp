package com.example.romatupkalenko.wikiresearcherapp.data.network.RetrofitUtils;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;

public class MyPageInfoResponse{
    public ArticleEntry getArticle() {
        return article;
    }

    public void setArticle(ArticleEntry article) {
        this.article = article;
    }

    public MyPageInfoResponse() {
    }

    private ArticleEntry article;

}
