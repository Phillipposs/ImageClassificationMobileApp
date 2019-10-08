package com.example.imageclassificationapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.imageclassificationapp.model.User;

import java.io.File;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;


public class APICalls {

        public static void sendImageForClassification(String filePath, Context context, Callback<String> callback ) {
            Retrofit retrofit = NetworkClient.getRetrofitClient();
            RetrofitCalls retrofitCalls = retrofit.create(RetrofitCalls.class);
            //Create a file object using file path
            File file = new File(filePath);
            // Create a request body with file and image media type
            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
            // Create MultipartBody.Part using file request-body,file name and part name
            SharedPreferences mPrefs = context.getSharedPreferences("_", MODE_PRIVATE); //add key
            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            String userName = mPrefs.getString("userName", null);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
            //Create request body with text description and text media type
            RequestBody userNameBody = RequestBody.create(MediaType.parse("text/plain"), userName);
            //
            Call call = retrofitCalls.sendImageForClassification(part, userNameBody);
            call.enqueue(callback);
        }

    public static void getResult(Context context,Callback<String> callback ) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        SharedPreferences mPrefs = context.getSharedPreferences("_", MODE_PRIVATE); //add key
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        String userName = mPrefs.getString("userName", null);
        RetrofitCalls retrofitCalls = retrofit.create(RetrofitCalls.class);
        Call call = retrofitCalls.getResult(userName);
        call.enqueue(callback);
      /*  Call<String> call =retrofitCalls.getResult(userName);
       enqueueWithRetry(call, MY_RETRIES, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (handler != null) {
                    if (response.body() != null)
                        handler.onResponse(response.body());
                    else if (response.errorBody() != null) {
                        handler.onError(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                onFail(call, t, handler);
            }
        });*/
    }
    public static void userLogin(User user, Callback<String> callback ) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        RetrofitCalls retrofitCalls = retrofit.create(RetrofitCalls.class);
        Call call = retrofitCalls.userLogin(user);
        call.enqueue(callback);
    }
    private static void onFail(Call call, Throwable t, ApiResponseHandler handler) {
        if(!call.isCanceled() && handler != null) {
            t.printStackTrace();
            handler.onFailure(t);
        }
    }

    public static final int DEFAULT_RETRIES = 3;
    public static final int MY_RETRIES = 3;

    public static <T> void enqueueWithRetry(Call<T> call,  final int retryCount,final Callback<T> callback) {
        call.enqueue(new RetryableCallback<T>(call, retryCount) {

            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static boolean isCallSuccess(Response response) {
        int code = response.code();
        return (code >= 200 && code < 400);
    }
}
