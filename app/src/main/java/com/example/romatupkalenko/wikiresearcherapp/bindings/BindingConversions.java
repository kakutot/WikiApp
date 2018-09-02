package com.example.romatupkalenko.wikiresearcherapp.bindings;

import android.databinding.BindingConversion;

import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;

import java.util.List;

public final class BindingConversions {

    /*@BindingConversion
    public static boolean convertNetworkLiveDataToBoolean(NetworkLiveData data) {
        return data.getValue().booleanValue();
    }*/
    @BindingConversion
    public static boolean convertListArticleEntryToBoolean(final List<ArticleEntry> list) {
        return list.size()>0;
    }
}
