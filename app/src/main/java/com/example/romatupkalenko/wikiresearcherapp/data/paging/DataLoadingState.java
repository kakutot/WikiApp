package com.example.romatupkalenko.wikiresearcherapp.data.paging;

public class DataLoadingState {
    private final Status status;
    private final String msg;

    public static final DataLoadingState LOADED;
    public static final DataLoadingState LOADING;
    public static final DataLoadingState FAILED;
    public DataLoadingState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED = new DataLoadingState(Status.LOADED,"Loaded");
        LOADING=  new DataLoadingState(Status.LOADING,"Loading");
        FAILED =  new DataLoadingState(Status.FAILED,"Failed");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }


    public enum Status{
        LOADING,LOADED,FAILED;
    }

}
