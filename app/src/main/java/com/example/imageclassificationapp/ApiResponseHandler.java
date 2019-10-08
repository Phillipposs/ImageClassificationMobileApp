package com.example.imageclassificationapp;

public interface ApiResponseHandler {
    void onResponse(Object response);
    void onError(int code);
    void onFailure(Throwable t);
}