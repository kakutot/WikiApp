package com.example.romatupkalenko.wikiresearcherapp.data.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.example.romatupkalenko.wikiresearcherapp.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

@Entity(tableName = "article", indices = {
        @Index(value = {"title"}, unique = true),
        @Index(value = {"page_url"}, unique = true)
})
public class ArticleEntry {

    public static final String tableName = "article";


    @Expose(deserialize = false)
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("pageid")
    @ColumnInfo(name = "page_id")
    private int pageId;

    private String title;

    @SerializedName("fullurl")
    @ColumnInfo(name = "page_url")
    private String pageUrl;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @Expose(deserialize = false)
    @ColumnInfo(name = "is_faved")
    private boolean isFaved;

    @Ignore
    private String imageTitle;

    @Ignore
    public ArticleEntry() {
        pageId = -1;
        title = null;
        pageUrl = null;
        imageUrl = null;
        imageTitle = null;
        isFaved = false;
    }


    public ArticleEntry(int pageId, String title, String pageUrl,String imageUrl,boolean isFaved) {
     //   this.id = id;
        this.pageId = pageId;
        this.title = title;
        this.pageUrl = pageUrl;
        this.imageUrl = imageUrl;
        this.isFaved = isFaved;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public boolean isFaved() {
        return isFaved;
    }

    public void setFaved(boolean faved) {
        isFaved = faved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id").append("=").append(getId()).
                append(" | PageId").append("=").append(getPageId()).
        append(" | Title").append("=").append(getTitle()).
        append(" | Image Url").append("=").append(getImageUrl()).
        append(" | ImageTitle").append("=").append(getImageTitle()).
       // append(" | Id").append("=").append(getId()).
        append(" | PageUrl").append("=").append(getPageUrl()).
        append(" | Is Faved").append("=").append(isFaved());
        return stringBuilder.toString();
    }

    @BindingAdapter({"app:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext()).load(imageUrl).
                resize(400,150).
                centerCrop().
                placeholder(R.drawable.error_image).
                into(view);
    }
}
