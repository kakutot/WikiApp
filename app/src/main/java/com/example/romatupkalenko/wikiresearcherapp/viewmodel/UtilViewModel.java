package com.example.romatupkalenko.wikiresearcherapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

public class UtilViewModel extends AndroidViewModel {
    private  Context mContext;
    private NetworkLiveData networkState;
    private boolean isNetworkLiveDataInit;

    public NetworkLiveData getNetworkState() {
        initNetworkState();
        return networkState;
    }


    public UtilViewModel(@NonNull Application application) {
        super(application);
        mContext = application;

        networkState = new NetworkLiveData();

    }
    private void initNetworkState(){
        if(isNetworkLiveDataInit)
            return;
        isNetworkLiveDataInit = true;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    networkState.setValue(true);
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    networkState.setValue(true);
        }
    }
    public  class NetworkLiveData extends MutableLiveData<Boolean> {
        public NetworkLiveData(){super();}
        private BroadcastReceiver mBroadcastReceiver;
        @Override
        protected void onActive() {
            mBroadcastReceiver = new InternetReceiver();

            mContext.registerReceiver(mBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }

        @Override
        protected void onInactive() {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }


        public class InternetReceiver extends BroadcastReceiver {
            public InternetReceiver(){
                super();
            }
            @Override
            public void onReceive(Context context, Intent intent) {
                setValue(isOnline(context));
            }

            private boolean isOnline(Context context) {
                try {
                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    //should check null because in airplane mode it will be null
                    return (netInfo != null && netInfo.isConnected());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
    }
}
