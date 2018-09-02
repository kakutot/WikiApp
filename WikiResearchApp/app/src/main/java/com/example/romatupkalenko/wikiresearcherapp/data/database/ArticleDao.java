package com.example.romatupkalenko.wikiresearcherapp.data.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ArticleDao {
@Query("SELECT * FROM " + ArticleEntry.tableName + " WHERE 1")
LiveData<List<ArticleEntry>> getAllArticles();

@Query("SELECT * FROM " + ArticleEntry.tableName + " WHERE is_faved = 1")
LiveData<List<ArticleEntry>> getAllFavArticles();

@Query("SELECT * FROM " + ArticleEntry.tableName + " WHERE is_faved = 0")
LiveData<List<ArticleEntry>> getAllNotFavArticles();


@Insert(onConflict = OnConflictStrategy.REPLACE)
void insertArticle(ArticleEntry articleEntry);

@Query("DELETE FROM " + ArticleEntry.tableName + " WHERE page_id = :pageId ")
void deleteArticleByPageId(int pageId);

@Insert
void bulkInsert(ArticleEntry... articles);

@Query("DELETE FROM " + ArticleEntry.tableName)
void deleteAll();

@Query("DELETE FROM " + ArticleEntry.tableName + " WHERE is_faved = 0")
void deleteAllNotFaved();

@Query("UPDATE " + ArticleEntry.tableName + " SET is_faved = 1 WHERE id = :id")
void favArticle(int id);

@Query("UPDATE " + ArticleEntry.tableName + " SET is_faved = 0 WHERE id = :id")
void unfavArticle(int id);
}