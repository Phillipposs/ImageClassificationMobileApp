package com.example.imageclassificationapp;




import android.util.Log;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RetryableCallback<T> implements Callback<T> {

    private int totalRetries = 1000;
    private static final String TAG = RetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public RetryableCallback(Call<T> call, int totalRetries) {
        this.call = call;
        this.totalRetries = totalRetries;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!APICalls.isCallSuccess(response))
            if (retryCount++ < totalRetries) {
                log("Retrying API Call -  (" + retryCount + " / " + totalRetries + ") " + call.request().url());
                retry();
            } else {
                log("On final response:" + call.request().url());
                onFinalResponse(call, response);
            }
        else {
            log("On final response:" + call.request().url());
            onFinalResponse(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if(t!=null && t.getMessage()!=null)
            Log.e(TAG, t.getMessage());
        if (retryCount++ < totalRetries) {
            log("Retrying API Call -  (" + retryCount + " / " + totalRetries + ") " + call.request().url());
            retry();
        } else {
            log("On final failure:" + call.request().url());
            onFinalFailure(call, t);
        }
    }

    public void onFinalResponse(Call<T> call, Response<T> response) {

    }

    public void onFinalFailure(Call<T> call, Throwable t) {
    }

    private void retry() {
        call.clone().enqueue(this);
    }


    private void log(String message) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, message);
    }
}